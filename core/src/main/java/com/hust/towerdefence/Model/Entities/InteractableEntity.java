package com.hust.towerdefence.Model.Entities;

/**
 * INTERACTABLE ENTITY - Tầng xử lý hành vi Tương tác (Tấn công/Khai thác).
 * Lớp này định nghĩa "tầm tay" và "tốc độ" thực hiện hành động của thực thể.
 * Phù hợp làm lớp cha cho: Trụ, Lính cận chiến, Lính đánh xa, và Thợ mỏ.
 */
public abstract class InteractableEntity extends BaseEntity {

    // --- Thông số Tương tác (Stats) ---
    protected float interactRange;    // Khoảng cách tối đa để thực hiện hành động
    protected float interactValue;    // Lượng giá trị tạo ra (Sát thương/Vàng/Hồi máu)
    protected float actionCooldown;   // Thời gian chờ giữa 2 lần hành động (giây)
    protected float lastActionTime;   // Lưu mốc stateTime của lần thực hiện cuối

    // --- Đối tượng nhắm tới ---
    protected BaseEntity target;      // Mục tiêu hiện tại (Lính địch/Mỏ vàng/Trụ)

    public InteractableEntity(float x, float y, float width, float height, int hp, int teamId,
                              float range, float value, float cooldown) {
        // Kế thừa định danh và vật lý từ BaseEntity
        super(x, y, width, height, hp, teamId);

        this.interactRange = range;
        this.interactValue = value;
        this.actionCooldown = cooldown;

        // Trạng thái sẵn sàng hành động ngay khi khởi tạo
        this.lastActionTime = -cooldown;
    }

    /**
     * Hành động cốt lõi của thực thể.
     * Được gọi bởi các lớp State (như AttackState) khi đủ điều kiện canInteract().
     */
    public abstract void performAction();

    /**
     * Logic kiểm tra mục tiêu có "xứng đáng" để tương tác hay không.
     * Ví dụ: Lính bỏ qua mỏ vàng (team 0), Thợ mỏ chỉ nhắm vào mỏ vàng.
     */
    public abstract boolean isValidTarget();

    /**
     * Tổng hợp các điều kiện: Mục tiêu hợp lệ? Trong tầm bắn? Hết cooldown?
     * @return true nếu thực thể đủ điều kiện "vào việc".
     */
    public boolean canInteract() {
        return isTargetExisting() && isValidTarget() && isTargetInRange() && isActionReady();
    }

    // --- Helper Methods (Hỗ trợ State xử lý logic) ---

    public boolean isTargetExisting() {
        // Mục tiêu phải tồn tại, đang hoạt động và không ở trạng thái chờ xóa
        return target != null && target.isActive() && !target.isRemoved();
    }

    public boolean isTargetInRange() {
        if (target == null) return false;
        // Tính khoảng cách giữa vị trí hiện tại và mục tiêu
        return position.dst(target.getPosition()) <= interactRange;
    }

    public boolean isActionReady() {
        // Dựa vào stateTime (tổng thời gian sống) để tính hồi chiêu
        return stateTime - lastActionTime >= actionCooldown;
    }

    /**
     * Cập nhật lại mốc thời gian sau khi thực hiện hành động thành công.
     */
    public void resetCooldown() {
        this.lastActionTime = stateTime;
    }

    // --- Giao tiếp dữ liệu (Getters & Setters) ---
    public void setTarget(BaseEntity target) { this.target = target; }
    public BaseEntity getTarget() { return target; }
    public float getInteractRange() { return interactRange; }
    public float getInteractValue() { return interactValue; }
}
