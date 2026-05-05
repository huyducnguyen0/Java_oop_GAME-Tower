package com.hust.towerdefence.Model.Entities;

/**
 * GOLD MINE - Thực thể Mỏ Vàng.
 *
 * VAI TRÒ TRONG GAME:
 * 1. Là một "điểm mốc" tĩnh trên bản đồ để Thợ mỏ (Miner) di chuyển tới.
 * 2. Cung cấp vị trí (X, Y) để Miner kiểm tra tầm khai thác (Range).
 * 3. Thuộc phe trung lập (Team 0) để phân biệt với lính ta và địch.
 *
 * GHI CHÚ: Thực thể này không cần quản lý máu hay di chuyển.
 */
public class GoldMine extends BaseEntity {

    /**
     * Khởi tạo mỏ vàng tại một vị trí cố định.
     *
     * @param x, y: Tọa độ đặt mỏ vàng trên bản đồ.
     * @param width, height: Kích thước vùng va chạm (để thợ mỏ không cần đứng chồng lên tâm mỏ).
     */
    public GoldMine(float x, float y, float width, float height) {
        /**
         * Gọi constructor lớp cha (BaseEntity):
         * - hp: Truyền 1 (Vì thực thể này không bao giờ bị tấn công nên 1 hay 1000 không quan trọng).
         * - teamId: 0 (Quy ước quốc tế cho phe Trung lập/Tài nguyên).
         */
        super(x, y, width, height, 1, 0);
    }

    /**
     * Mỏ vàng là vật thể tĩnh, không có logic thay đổi theo thời gian.
     */
    @Override
    public void update(float delta) {
        // Chỉ gọi super để cập nhật stateTime nếu sau này cần hiệu ứng lấp lánh (Animation).
        super.update(delta);
    }

    /**
     * Ghi đè hàm nhận sát thương để đảm bảo mỏ vàng "bất tử".
     * Lính hay trụ có bắn vào cũng không làm mỏ vàng biến mất.
     */
    @Override
    public void takeDamage(int amount) {
        // Để trống: Mỏ vàng không nhận sát thương.
    }

    /**
     * Hàm này được giữ lại để đảm bảo đúng cấu trúc Entity,
     * nhưng vì mỏ vàng không bao giờ chết nên logic bên trong sẽ không được gọi.
     */
    @Override
    public void die() {
        // Mỏ vàng tồn tại vĩnh viễn trong suốt trận đấu.
    }
}
