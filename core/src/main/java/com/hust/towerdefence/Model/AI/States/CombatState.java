package com.hust.towerdefence.Model.AI.States;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.hust.towerdefence.Model.Entities.SteerableEntity;

/**
 * COMBAT STATE - Điều khiển Lính chiến đấu.
 * Tự động hóa: Tìm mục tiêu -> Hành quân -> Tấn công.
 */
public enum CombatState implements State<SteerableEntity> {

    // Đứng yên quét tìm mục tiêu (Lính địch hoặc Nhà chính)
    IDLE_SCAN() {
        @Override
        public void enter(SteerableEntity entity) {
            // Đảm bảo ngắt hoàn toàn mọi di chuyển cũ khi vào trạng thái nghỉ
            entity.setSteeringBehavior(null);
            entity.getLinearVelocity().setZero();
        }

        @Override
        public void update(SteerableEntity entity) {
            if (entity.isTargetExisting() && entity.isValidTarget()) {
                entity.getStateMachine().changeState(MOVE_CHASE);
            }
        }
    },

    // Di chuyển áp sát mục tiêu bằng Steering AI
    MOVE_CHASE() {
        @Override
        public void update(SteerableEntity entity) {
            if (!entity.isTargetExisting()) {
                entity.getStateMachine().changeState(IDLE_SCAN);
                return;
            }

            // Nếu đã vào tầm đánh, dừng di chuyển để chuẩn bị vả địch
            if (entity.isTargetInRange()) {
                // Logic dừng xe sẽ được xử lý triệt để trong enter() của STAY_ATTACK
                entity.getStateMachine().changeState(STAY_ATTACK);
            }
        }
    },

    // Đứng yên thực hiện đòn đánh theo Cooldown
    STAY_ATTACK() {
        @Override
        public void enter(SteerableEntity entity) {
            // Ngắt động cơ di chuyển để tập trung tấn công
            entity.setSteeringBehavior(null);
            entity.getLinearVelocity().setZero();
        }

        @Override
        public void update(SteerableEntity entity) {
            // Nếu địch chết hoặc chạy mất, quay lại trạng thái di chuyển/tìm kiếm
            if (!entity.isTargetExisting() || !entity.isTargetInRange()) {
                entity.getStateMachine().changeState(MOVE_CHASE);
                return;
            }

            if (entity.isActionReady()) {
                entity.performAction(); // Gây sát thương
                entity.resetCooldown();
            }
        }
    };

    @Override public void enter(SteerableEntity entity) {}
    @Override public void exit(SteerableEntity entity) {}
    @Override public boolean onMessage(SteerableEntity entity, Telegram telegram) { return false; }
}
