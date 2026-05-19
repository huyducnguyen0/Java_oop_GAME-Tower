package com.hust.towerdefence.Model.Entities.Combat.Soldier;

/**
 * Miner
 * Đơn vị sản xuất vàng.
 * Sử dụng thông số "tấn công" để đại diện cho khả năng đào tài nguyên.
 */
public class Miner extends Soldier {

    // Thông số kinh tế cho 3 cấp độ
    private float miningTimer;
    private int carriedGold;
    private static final float[] HEALTH_DATA = {80f, 120f, 160f};  // Extracted: thợ mỏ yếu hơn Pawn
    private static final float[] GOLD_PER_MINE = {10f, 25f, 60f}; // Thay cho attackDamage
    private static final float[] RANGE_DATA = {0.5f, 0.5f, 0.5f};  // Standardize to world units
    private static final int[] UPGRADE_COST_DATA = {80, 200, 0};

    public Miner() {
        super();
        this.width = 0.8f;
        this.height = 0.8f;

        this.team = Team.SOLDIER; // Xác định phe
        this.currentState = State.GOING_TO_MINE; // Trạng thái mặc định
        applyLevelData();
    }

    /**
     * Chuyển đổi các thông số đào vàng vào khung xương của CombatEntity
     */
    public void applyLevelData() {
        // Không cần validation ở đây vì level đã được validate trong setLevel()
        int index = this.level - 1;

        // Health từ static array
        this.maxHealth = HEALTH_DATA[index];
        this.health = this.maxHealth;

        // Tái định nghĩa biến: Damage -> Vàng mỗi lần đào
        this.attackDamage = GOLD_PER_MINE[index];

        this.upgradeCost = UPGRADE_COST_DATA[index];

        // Tầm đào: Standardized to world units
        this.attackRange = RANGE_DATA[index];
    }

    @Override
    public void reset() {
        super.reset();
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
    public float getMiningTimer() { return miningTimer; }
    public void setMiningTimer(float time) { this.miningTimer = time; }
    public int getCarriedGold() { return carriedGold; }
    public void setCarriedGold(int gold) { this.carriedGold = gold; }
}
