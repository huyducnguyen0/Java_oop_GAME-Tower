package com.hust.towerdefence.Model.AI.States;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.hust.towerdefence.Model.Entities.SteerableEntity;

/**
 * COMBAT STATE - Hệ thống trạng thái AI điều khiển hành vi của thực thể (Entity).
 * Tuân thủ quy trình: Hành quân -> Phát hiện mục tiêu -> Truy đuổi -> Tấn công.
 * Sử dụng thư viện GDX AI State Machine để quản lý logic.
 */
public enum CombatState implements State<SteerableEntity> {

    /**
     * 1. TRẠNG THÁI NGHỈ / QUÉT TÌM MỤC TIÊU
     * Thực thể đứng yên tại chỗ để quét các mục tiêu trong tầm (Lính địch hoặc Nhà chính).
     */
    IDLE_SCAN() {
        @Override
        public void enter(SteerableEntity entity) {
            // Dừng hoàn toàn mọi lực đẩy và vận tốc cũ để đứng yên
            entity.setSteeringBehavior(null);
            entity.getLinearVelocity().setZero();
        }

        @Override
        public void update(SteerableEntity entity) {
            // Ưu tiên 1: Nếu thấy mục tiêu hợp lệ trong tầm quét -> Chuyển sang truy đuổi
            if (entity.isTargetExisting() && entity.isValidTarget()) {
                entity.getStateMachine().changeState(MOVE_CHASE);
            }
            // Ưu tiên 2: Nếu không có địch -> Quay lại nhiệm vụ hành quân theo đường mòn
            else {
                entity.getStateMachine().changeState(FOLLOW_ROAD);
            }
        }
    },

    /**
     * 2. TRẠNG THÁI HÀNH QUÂN (PATROL/FOLLOW PATH)
     * Thực thể di chuyển dọc theo lộ trình đã được định sẵn trong PathManager.
     */
    FOLLOW_ROAD() {
        @Override
        public void enter(SteerableEntity entity) {
            // Kích hoạt lại hành vi bám đường (FollowPath)
            if (entity.getFollowPathBehavior() != null) {
                entity.setSteeringBehavior(entity.getFollowPathBehavior());
            }
        }

        @Override
        public void update(SteerableEntity entity) {
            // Liên tục quét mục tiêu khi đang đi. Nếu thấy địch -> Bỏ đường để truy đuổi
            if (entity.isTargetExisting() && entity.isValidTarget()) {
                entity.getStateMachine().changeState(MOVE_CHASE);
            }
        }
    },

    /**
     * 3. TRẠNG THÁI TRUY ĐUỔI (CHASE)
     * Áp sát mục tiêu bằng Steering AI cho đến khi nằm trong tầm đánh (Attack Range).
     */
    MOVE_CHASE() {
        @Override
        public void update(SteerableEntity entity) {
            // Nếu mục tiêu biến mất (đã chết hoặc thoát khỏi tầm quét) -> Quay lại hành quân
            if (!entity.isTargetExisting()) {
                entity.getStateMachine().changeState(FOLLOW_ROAD);
                return;
            }

            // Nếu đã đủ gần mục tiêu -> Dừng lại để tấn công
            if (entity.isTargetInRange()) {
                entity.getStateMachine().changeState(STAY_ATTACK);
            }
        }
    },

    /**
     * 4. TRẠNG THÁI TẤN CÔNG (STAY & ATTACK)
     * Đứng yên thực hiện hành động gây sát thương dựa trên tốc độ đánh (Cooldown).
     */
    STAY_ATTACK() {
        @Override
        public void enter(SteerableEntity entity) {
            // Ngắt Steering để thực thể không bị "trượt" khi đang đánh
            entity.setSteeringBehavior(null);
            entity.getLinearVelocity().setZero();
        }

        @Override
        public void update(SteerableEntity entity) {
            // Kiểm tra mục tiêu: Nếu không còn mục tiêu -> Về hành quân
            if (!entity.isTargetExisting()) {
                entity.getStateMachine().changeState(FOLLOW_ROAD);
                return;
            }

            // Nếu mục tiêu chạy ra khỏi tầm đánh -> Tiếp tục đuổi
            if (!entity.isTargetInRange()) {
                entity.getStateMachine().changeState(MOVE_CHASE);
                return;
            }

            // Thực hiện hành động tấn công nếu Cooldown đã sẵn sàng
            if (entity.canInteract()) {
                entity.performAction(); // Gây sát thương/Tạo projectile
                entity.resetCooldown();  // Reset bộ đếm thời gian
            }
        }
    };

    // --- CÁC HÀM MẶC ĐỊNH CỦA INTERFACE STATE ---
    @Override public void enter(SteerableEntity entity) {}
    @Override public void exit(SteerableEntity entity) {}
    @Override public boolean onMessage(SteerableEntity entity, Telegram telegram) { return false; }
}
