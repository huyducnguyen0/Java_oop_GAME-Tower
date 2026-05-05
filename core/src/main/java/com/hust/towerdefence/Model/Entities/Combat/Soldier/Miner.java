package com.hust.towerdefence.Model.Entities.Combat.Soldier;

/**
 * Miner
 * Đơn vị sản xuất vàng.
 * Sử dụng thông số "tấn công" để đại diện cho khả năng đào tài nguyên.
 */
public class Miner extends Soldier {

    // Thông số kinh tế cho 3 cấp độ
    private static final int MAX_LEVEL = 3;
    private static final float[] GOLD_PER_MINE = {10f, 25f, 60f}; // Thay cho attackDamage
    private static final float[] MINING_SPEED = {0.5f, 0.8f, 1.2f}; // Lần đào mỗi giây
    private static final int[] UPGRADE_COST_DATA = {80, 200, 0};

    public Miner() {
        super();
        this.width = 64;
        this.height = 64;
        applyLevelData();
    }

    /**
     * Chuyển đổi các thông số đào vàng vào khung xương của CombatEntity
     */
    public void applyLevelData() {
        // Không cần validation ở đây vì level đã được validate trong setLevel()
        int index = this.level - 1;

        // Tái định nghĩa biến: Damage -> Vàng mỗi lần đào
        this.attackDamage = GOLD_PER_MINE[index];

        // Tái định nghĩa biến: Attack Speed -> Tốc độ đào
        this.setAttackSpeed(MINING_SPEED[index]);

        this.maxHealth = 80 + (index * 40); // Thợ mỏ thường yếu hơn Pawn
        this.health = this.maxHealth;
        this.upgradeCost = UPGRADE_COST_DATA[index];

        // Tầm đào (Range): Thợ mỏ thường đào tại chỗ hoặc tầm rất gần
        this.attackRange = 30f;
    }

    @Override
    public void reset() {
        super.reset();
        this.level = 1;
        applyLevelData();
    }

    @Override
    public void setLevel(int level) {
        super.setLevel(level);  // Gọi parent validation (level >= 1)
        if (this.level > MAX_LEVEL) this.level = MAX_LEVEL;
        applyLevelData();  // Tự động cập nhật stats khi level thay đổi
    }

    // Getter mang tính ngữ nghĩa để System dễ đọc code
    public float getGoldPerCycle() {
        return attackDamage;
    }
}
