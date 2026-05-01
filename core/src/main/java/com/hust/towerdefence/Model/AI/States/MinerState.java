package com.hust.towerdefence.Model.AI.States;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.hust.towerdefence.Model.Entities.SteerableEntity;

/**
 * MINER STATE - Điều khiển Thợ mỏ.
 * Quy trình: Tìm mỏ -> Di chuyển -> Đào vàng liên tục.
 */
public enum MinerState implements State<SteerableEntity> {

    // Quét tìm mỏ vàng trên bản đồ
    IDLE_SEARCH() {
        @Override
        public void enter(SteerableEntity entity) {
            // Đảm bảo ngắt động cơ và phanh tay khi đang tìm kiếm mỏ
            entity.setSteeringBehavior(null);
            entity.getLinearVelocity().setZero();
        }

        @Override
        public void update(SteerableEntity entity) {
            if (entity.isTargetExisting() && entity.isValidTarget()) {
                entity.getStateMachine().changeState(MOVE_TO_MINE);
            }
        }
    },

    // Di chuyển tới vị trí mỏ
    MOVE_TO_MINE() {
        @Override
        public void update(SteerableEntity entity) {
            if (!entity.isTargetExisting()) {
                entity.getStateMachine().changeState(IDLE_SEARCH);
                return;
            }

            if (entity.isTargetInRange()) {
                // Chuyển sang trạng thái STAY_MINING, việc dừng xe sẽ do enter() của trạng thái đó xử lý
                entity.getStateMachine().changeState(STAY_MINING);
            }
        }
    },

    // Đứng yên đào vàng cho đến khi mỏ cạn kiệt (Target mất)
    STAY_MINING() {
        @Override
        public void enter(SteerableEntity entity) {
            // Ngắt hành vi di chuyển ngay khi chạm vào tầm khai thác mỏ
            entity.setSteeringBehavior(null);
            entity.getLinearVelocity().setZero();
        }

        @Override
        public void update(SteerableEntity entity) {
            // Nếu mỏ vàng biến mất, quay lại tìm mỏ mới
            if (!entity.isTargetExisting()) {
                entity.getStateMachine().changeState(IDLE_SEARCH);
                return;
            }

            if (entity.isActionReady()) {
                entity.performAction(); // Logic cộng tiền (và takeDamage(0)) nằm ở đây trong class Miner
                entity.resetCooldown();
            }
        }
    };

    @Override public void enter(SteerableEntity entity) {}
    @Override public void exit(SteerableEntity entity) {}
    @Override public boolean onMessage(SteerableEntity entity, Telegram telegram) { return false; }
}
