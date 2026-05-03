package com.hust.towerdefence.Model.Entities;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.hust.towerdefence.Model.AI.States.CombatState;

/**
 * COMBAT - Thực thể chiến đấu đa năng.
 *
 * ĐIỂM ƯU VIỆT:
 * 1. Đa hình (Polymorphism): Dùng chung cho cả lính cận chiến (Melee) và đánh xa (Ranged).
 * 2. Tự vận hành (Autonomous): Tự tìm mục tiêu, tự đuổi theo, tự tấn công dựa trên CombatState.
 * 3. Tối ưu hệ thống: Cung cấp tín hiệu (Signal) để lớp System biết khi nào cần sinh đạn.
 * 4. Clean & Scalable: Dễ dàng mở rộng cho các loại lính đặc biệt (Tanker, Assassin).
 */
public class Combat extends SteerableEntity {

    // --- Đặc tính chiến đấu ---
    private boolean isRanged;       // True: Bắn đạn, False: Vung kiếm (trừ máu trực tiếp)
    private boolean fireSignal;     // "Cờ hiệu" báo cho lớp System biết lính vừa thực hiện bắn đạn

    /**
     * Constructor "Sạch": Khởi tạo lính với đầy đủ bộ não và cơ bắp.
     */
    public Combat(float x, float y, float width, float height, int hp, int teamId,
                  float range, float damage, float cooldown, float maxSpeed, boolean isRanged) {

        super(x, y, width, height, hp, teamId, range, damage, cooldown, maxSpeed);
        this.isRanged = isRanged;
        this.fireSignal = false;

        /**
         * KHỞI TẠO BỘ NÃO (AI):
         * Lính vừa sinh ra sẽ ở trạng thái IDLE_SCAN để tìm kiếm mục tiêu xung quanh.
         */
        this.stateMachine = new DefaultStateMachine<>(this, CombatState.IDLE_SCAN);
    }

    /**
     * THỰC THI HÀNH ĐỘNG CHIẾN ĐẤU.
     * Được gọi tự động từ trạng thái STAY_ATTACK khi đủ điều kiện canInteract().
     */
    @Override
    public void performAction() {
        if (!canInteract()) return;

        if (isRanged) {
            /**
             * LOGIC ĐÁNH XA:
             * Bật cờ fireSignal. Lớp System (Controller) sẽ check cờ này,
             * lấy thông tin từ getPosition() và getTarget() để tạo Projectile.
             */
            this.fireSignal = true;
        } else {
            /**
             * LOGIC CẬN CHIẾN:
             * Áp sát rồi thì trừ máu trực tiếp vào mục tiêu.
             */
            target.takeDamage((int) this.interactValue);
        }

        // Đánh xong thì bắt đầu tính thời gian hồi chiêu
        resetCooldown();
    }

    /**
     * ĐIỀU KIỆN MỤC TIÊU HỢP LỆ.
     * Quy định lính chỉ tấn công những kẻ "không cùng phe" và "không phải trung lập".
     */
    @Override
    public boolean isValidTarget() {
        if (target == null) return false;

        // Không đánh đồng đội (cùng teamId)
        // Không đánh mỏ vàng (team 0)
        return target.getTeamId() != this.teamId && target.getTeamId() != 0;
    }

    /**
     * Getter/Setter đặc thù cho cơ chế sinh đạn.
     */
    public boolean isRanged() { return isRanged; }

    /**
     * Lớp System sẽ gọi hàm này để kiểm tra xem lính có vừa bắn đạn không.
     * Sau khi check xong phải gọi consumeFireSignal() để hạ cờ xuống.
     */
    public boolean hasFired() { return fireSignal; }

    public void consumeFireSignal() { this.fireSignal = false; }

    @Override
    public void update(float delta) {
        // Thực thi toàn bộ chu trình: Base -> Interactable -> Steerable -> StateMachine
        super.update(delta);
    }
}
