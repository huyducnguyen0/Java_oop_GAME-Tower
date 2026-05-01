package com.hust.towerdefence.Model.Entities;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.hust.towerdefence.Model.AI.States.MinerState;

/**
 * MINER - Thực thể Thợ mỏ.
 *
 * VAI TRÒ:
 * 1. Thừa hưởng khả năng di chuyển thông minh (Steerable) để tìm mỏ vàng.
 * 2. Tự vận hành dựa trên máy trạng thái (StateMachine): Tìm mỏ -> Di chuyển -> Khai thác.
 * 3. Thực hiện hành động cộng tiền trực tiếp vào hệ thống tài chính của người chơi.
 */
public class Miner extends SteerableEntity {

    /**
     * Khởi tạo Thợ mỏ với đầy đủ "cơ bắp" (Physics) và "bộ não" (AI).
     *
     * @param x, y: Vị trí xuất phát.
     * @param hp: Lượng máu.
     * @param teamId: Phe phái (Thường là 1 - Phe ta).
     * @param range: Khoảng cách để có thể bắt đầu khai thác.
     * @param value: Lượng vàng khai thác được mỗi chu kỳ.
     * @param cooldown: Thời gian nghỉ giữa 2 lần khai thác.
     * @param maxSpeed: Tốc độ di chuyển tối đa.
     */
    public Miner(float x, float y, int hp, int teamId,
                 float range, float value, float cooldown, float maxSpeed) {

        // 1. Khởi tạo thuộc tính vật lý và tương tác (Size mặc định 32x32)
        super(x, y, 32, 32, hp, teamId, range, value, cooldown, maxSpeed);

        /**
         * 2. Khởi tạo "Bộ não" (StateMachine).
         * Chúng ta đặt trạng thái ban đầu là IDLE_SEARCH để Miner tự động tìm mỏ ngay khi xuất hiện.
         */
        this.stateMachine = new DefaultStateMachine<>(this, MinerState.IDLE_SEARCH);
    }

    /**
     * Thực hiện hành động khai thác vàng.
     * Được gọi từ STAY_MINING trong MinerState khi đủ điều kiện.
     */
    @Override
    public void performAction() {
        // Kiểm tra an toàn: Mục tiêu còn đó và hết thời gian hồi chiêu
        if (!canInteract()) return;

        /**
         * --- TODO: KẾT NỐI VỚI CLASS QUẢN LÝ VÀNG TẠI ĐÂY ---
         * Vì Miner đã có interactValue (là số vàng mỗi lần cuốc),
         * bro chỉ cần gọi hàm cộng tiền từ class quản lý tài chính của bro.
         *
         * Ví dụ: YourGoldManager.addGold((int) this.interactValue);
         */

        // -----------------------------------------------------------

        // Đánh dấu thời điểm vừa hành động để tính cooldown cho lần sau
        resetCooldown();
    }

    /**
     * Quy định mục tiêu hợp lệ của Thợ mỏ.
     * Chỉ những thực thể thuộc Team 0 (Mỏ vàng) mới được Miner để mắt tới.
     */
    @Override
    public boolean isValidTarget() {
        return target != null && target.getTeamId() == 0;
    }

    /**
     * Cập nhật logic mỗi khung hình.
     */
    @Override
    public void update(float delta) {
        // super.update sẽ tự động chạy stateMachine.update() và steering AI
        // giúp thợ mỏ tự ra quyết định và di chuyển mượt mà.
        super.update(delta);
    }
}
