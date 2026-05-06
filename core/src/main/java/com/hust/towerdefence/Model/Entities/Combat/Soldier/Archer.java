package com.hust.towerdefence.Model.Entities.Combat.Soldier;

/**
 * Archer
 * Đơn vị lính tầm xa.
 * Chuyên xử lý kẻ thù từ khoảng cách an toàn.
 */
public class Archer extends Soldier {

    // Thông số cho 3 cấp độ (Level 1, 2, 3)
    private static final float[] HEALTH_DATA = {80f, 150f, 300f};
    private static final float[] DAMAGE_DATA = {12f, 28f, 65f};
    private static final float[] RANGE_DATA = {4.0f, 5.0f, 6.0f}; // Tầm bắn xa
    private static final int[] UPGRADE_COST_DATA = {60, 180, 0};
    private static final float[] ARROW_SPEED_DATA = {8.0f, 10.0f, 14.0f}; // Tốc độ bay của mũi tên tăng theo cấp độ
    protected float arrowSpeed;

    public Archer() {
        super();
        this.width = 0.7f;
        this.height = 0.7f;
        applyLevelData();
    }

    /**
     * Cập nhật các chỉ số dựa trên cấp độ hiện tại.
     */
    public void applyLevelData() {
        // Không cần validation ở đây vì level đã được validate trong setLevel()
        int index = this.level - 1;

        this.maxHealth = HEALTH_DATA[index];
        this.health = this.maxHealth;
        this.attackDamage = DAMAGE_DATA[index];
        this.attackRange = RANGE_DATA[index];
        this.upgradeCost = UPGRADE_COST_DATA[index];
        this.arrowSpeed = ARROW_SPEED_DATA[index];
        // Tốc độ bắn (phát mỗi giây) - Archer thường bắn nhanh hơn Pawn
        this.setAttackSpeed(1.5f + (index * 0.2f));
    }

    @Override
    public void reset() {
        super.reset();
        this.arrowSpeed = 400f;
        this.width = 0.7f;
        this.height = 0.7f;
        applyLevelData();
    }

    @Override
    public void setLevel(int level) {
        super.setLevel(level);  // Gọi parent validation (level >= 1)
        if (this.level > MAX_LEVEL) this.level = MAX_LEVEL;
        applyLevelData();  // Tự động cập nhật stats khi level thay đổi
    }

    // ==================== Getters / Setters ====================

    public float getArrowSpeed() {
        return arrowSpeed;
    }

    public void setArrowSpeed(float arrowSpeed) {
        this.arrowSpeed = Math.max(0, arrowSpeed);
    }
}
