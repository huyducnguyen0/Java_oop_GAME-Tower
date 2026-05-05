package com.hust.towerdefence.Model.Entities.Combat.Soldier;

/**
 * Pawn
 * Đơn vị lính cận chiến cơ bản.
 * Có 3 cấp độ với chỉ số tăng dần.
 */
public class Pawn extends Soldier {

    // Thông số cho 3 cấp độ (Level 1, 2, 3)
    private static final int MAX_LEVEL = 3;
    private static final float[] HEALTH_DATA = {150f, 300f, 600f};
    private static final float[] DAMAGE_DATA = {15f, 35f, 80f};
    private static final float[] RANGE_DATA = {40f, 45f, 50f}; // Tầm đánh ngắn (cận chiến)
    private static final int[] UPGRADE_COST_DATA = {50, 150, 0};

    public Pawn() {
        super();
        // Thiết lập kích thước vật lý cho Pawn
        this.width = 32;
        this.height = 32;
        applyLevelData();
    }

    /**
     * Cập nhật các chỉ số chiến đấu dựa trên level hiện tại.
     * Hàm này sẽ được gọi khi khởi tạo hoặc sau khi Soldier được setLevel mới.
     * Giả định: level đã được validate trong khoảng [1, MAX_LEVEL]
     */
    public void applyLevelData() {
        // Không cần validation ở đây vì level đã được validate trong setLevel()
        int index = this.level - 1; // Level 1 tương ứng index 0

        // Gán dữ liệu vào các biến thừa kế từ CombatEntity
        this.maxHealth = HEALTH_DATA[index];
        this.health = this.maxHealth;
        this.attackDamage = DAMAGE_DATA[index];
        this.attackRange = RANGE_DATA[index];
        this.attackSpeed = 1.2f; // Tốc độ đánh cố định hoặc có thể cho vào mảng nếu muốn tăng theo level

        // Gán dữ liệu vào biến thừa kế từ Soldier
        this.upgradeCost = UPGRADE_COST_DATA[index];

        // Tính toán lại cooldown dựa trên attackSpeed mới
        this.setAttackSpeed(this.attackSpeed);
    }

    @Override
    public void setLevel(int level) {
        super.setLevel(level);  // Gọi parent validation (level >= 1)
        if (this.level > MAX_LEVEL) this.level = MAX_LEVEL;
        applyLevelData();  // 🆕 Tự động cập nhật stats khi level thay đổi
    }

    @Override
    public void reset() {
        super.reset();
        this.level = 1;
        applyLevelData();
    }
}
