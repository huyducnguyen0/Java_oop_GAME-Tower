package com.hust.towerdefence.Model.Entities.Combat.Soldier;

/**
 * Lancer
 * Đơn vị cận chiến tầm xa (Reach weapon).
 * Tầm đánh rộng hơn Pawn nhưng tốc độ đánh chậm hơn.
 */
public class Lancer extends Soldier {

    // Thông số cho 3 cấp độ (Level 1, 2, 3)

    private static final float[] HEALTH_DATA = {120f, 250f, 500f};
    private static final float[] DAMAGE_DATA = {25f, 55f, 120f}; // Sát thương mỗi nhát đâm khá cao
    private static final float[] RANGE_DATA = {1.5f, 1.6f, 1.8f}; // Gấp đôi hoặc gấp ba Pawn (64px)
    private static final int[] UPGRADE_COST_DATA = {70, 220, 0};

    public Lancer() {
        super();
        this.width = 0.7f; // Nhỏ hơn Pawn để trông thanh mảnh hơn, nhưng vẫn đủ lớn để nhận biết là lính cận chiến tầm xa
        this.height = 0.9f; // Cao hơn để tạo cảm giác cây giáo dài hơn
        applyLevelData();
    }

    /**
     * Thiết lập chỉ số dựa trên đặc tính: Đánh mạnh, tầm xa, nhưng chậm.
     */
    public void applyLevelData() {
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
        this.width = 0.7f;
        this.height = 0.9f;
        applyLevelData();
    }
}
