package com.hust.towerdefence.Model.Entities.Combat.Soldier;

/**
 * Pawn
 * Đơn vị lính cận chiến cơ bản.
 * Sử dụng đơn vị World Unit (1.0f tương ứng 1 ô gạch).
 */
public class Pawn extends Soldier {

    // Thông số cho 3 cấp độ (Level 1, 2, 3)

    private static final float[] HEALTH_DATA = {150f, 300f, 600f};
    private static final float[] DAMAGE_DATA = {15f, 35f, 80f};

    // Range cũng phải chuyển sang World Unit (Ví dụ: 0.8f đơn vị thay vì 50 pixel)
    private static final float[] RANGE_DATA = {0.6f, 0.7f, 0.8f};
    private static final int[] UPGRADE_COST_DATA = {50, 150, 0};

    public Pawn() {
        super();
        // Thiết lập kích thước theo World Unit
        // Nếu 1 ô gạch = 1.0f, thì lính nên nhỏ hơn 1 chút để trông thoáng (0.8f)
        this.width = 0.8f;
        this.height = 0.8f;

        this.team = Team.SOLDIER; // Xác định phe
        this.currentState = State.IDLE; // Trạng thái mặc định

        applyLevelData();
    }

    /**
     * Cập nhật các chỉ số chiến đấu dựa trên level hiện tại.
     */
    public void applyLevelData() {
        int index = this.level - 1;

        this.maxHealth = HEALTH_DATA[index];
        this.health = this.maxHealth;
        this.attackDamage = DAMAGE_DATA[index];
        this.attackRange = RANGE_DATA[index];

        // Tốc độ đánh (giữ nguyên vì đây là đơn vị thời gian, không ảnh hưởng bởi unit)
        this.setAttackSpeed(1.2f);

        this.upgradeCost = UPGRADE_COST_DATA[index];
    }

    @Override
    public void setLevel(int level) {
        super.setLevel(level);
        if (this.level > MAX_LEVEL) this.level = MAX_LEVEL;
        applyLevelData();
    }

    @Override
    public void reset() {
        super.reset();
        this.level = 1;
        this.width = 0.8f;
        this.height = 0.8f;
        applyLevelData();
    }
}
