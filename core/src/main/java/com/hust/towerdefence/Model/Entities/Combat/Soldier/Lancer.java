package com.hust.towerdefence.Model.Entities.Combat.Soldier;

/**
 * Lancer
 * Đơn vị cận chiến tầm xa (Reach weapon).
 * Tầm đánh rộng hơn Pawn nhưng tốc độ đánh chậm hơn.
 */
public class Lancer extends Soldier {

    // Thông số cho 3 cấp độ (Level 1, 2, 3)
    private static final int MAX_LEVEL = 3;
    private static final float[] HEALTH_DATA = {120f, 250f, 500f};
    private static final float[] DAMAGE_DATA = {25f, 55f, 120f}; // Sát thương mỗi nhát đâm khá cao
    private static final float[] RANGE_DATA = {100f, 110f, 120f}; // Gấp đôi hoặc gấp ba Pawn (64px)
    private static final int[] UPGRADE_COST_DATA = {70, 220, 0};

    public Lancer() {
        super();
        this.width = 64;
        this.height = 64;
        applyLevelData();
    }

    /**
     * Thiết lập chỉ số dựa trên đặc tính: Đánh mạnh, tầm xa, nhưng chậm.
     */
    public void applyLevelData() {
        // Không cần validation ở đây vì level đã được validate trong setLevel()
        int index = this.level - 1;

        this.maxHealth = HEALTH_DATA[index];
        this.health = this.maxHealth;
        this.attackDamage = DAMAGE_DATA[index];
        this.attackRange = RANGE_DATA[index];
        this.upgradeCost = UPGRADE_COST_DATA[index];

        // Tốc độ đánh chậm (Ví dụ: 0.7 phát/giây, chậm hơn Pawn là 1.2)
        // Điều này tạo ra cảm giác cây giáo nặng nề cần thời gian thu hồi.
        this.setAttackSpeed(0.7f + (index * 0.1f));
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
}
