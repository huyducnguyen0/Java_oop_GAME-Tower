package com.hust.towerdefence.Model.AI.States;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.hust.towerdefence.Model.Entities.DefenseTower;

/**
 * TOWER STATE - Quản lý logic hành vi của Trụ phòng thủ.
 * Trụ luôn đứng cố định, thực hiện vòng lặp: Quét kẻ thù -> Khai hỏa.
 */
public enum TowerState implements State<DefenseTower> {

    // Trạng thái quét tìm kẻ địch lọt vào tầm bắn
    STAY_SCAN() {
        @Override
        public void enter(DefenseTower entity) {
            // Khi bắt đầu quét mới, đảm bảo ngắt liên kết với mục tiêu cũ (nếu có)
            entity.setTarget(null);
        }

        @Override
        public void update(DefenseTower entity) {
            // Chuyển sang trạng thái bắn nếu phát hiện kẻ thù hợp lệ lọt vào tầm bắn (Range)
            if (entity.isTargetExisting() && entity.isTargetInRange() && entity.isValidTarget()) {
                entity.getStateMachine().changeState(STAY_FIRE);
            }
        }
    },

    // Trạng thái bắn liên tục vào mục tiêu cho đến khi địch chết hoặc ra khỏi tầm
    STAY_FIRE() {
        @Override
        public void update(DefenseTower entity) {
            // Kiểm tra tính hiện hữu của mục tiêu và khoảng cách mỗi frame
            if (!entity.isTargetExisting() || !entity.isTargetInRange()) {
                entity.getStateMachine().changeState(STAY_SCAN);
                return;
            }

            // Thực hiện bắn đạn/gây sát thương theo nhịp nạp (Cooldown)
            if (entity.isActionReady()) {
                entity.performAction();
                entity.resetCooldown();
            }
        }
    };

    // Các phương thức bổ trợ của GDX-AI (Để trống nếu không xử lý logic chuyển cảnh đặc biệt)
    @Override public void enter(DefenseTower entity) {}
    @Override public void exit(DefenseTower entity) {}
    @Override public boolean onMessage(DefenseTower entity, Telegram telegram) { return false; }
}
