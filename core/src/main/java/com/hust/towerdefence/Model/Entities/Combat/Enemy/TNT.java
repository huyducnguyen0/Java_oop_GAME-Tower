package com.hust.towerdefence.Model.Entities.Combat.Enemy;

/**
 * TNT (Enemy)
 * Kẻ địch ném bom có 3 cấp độ tương ứng với độ khó của màn chơi.
 */
public class TNT extends Enemy {

    // Thông số cho 3 cấp độ (Level 1, 2, 3)
    private static final float[] HEALTH_DATA = {60f, 150f, 400f};
    private static final float[] DAMAGE_DATA = {30f, 70f, 150f};
    private static final float[] RANGE_DATA = {3.0f, 3.8f, 4.5f};

    // Phần thưởng tăng dần để người chơi có động lực nâng cấp Soldier
    private static final int[] GOLD_REWARD_DATA = {20, 50, 120};
    private static final float[] EXP_REWARD_DATA = {40f, 100f, 250f};
    // Tốc độ bay của bom (World Unit/giây)
    private static final float[] BOMB_SPEED_DATA = {5.0f, 6.5f, 8.0f};
    protected float bombSpeed;

    public TNT() {
        super();
        this.width = 0.75f; // Kích thước vừa phải để trông như một kẻ ném bom, không quá lớn nhưng vẫn có thể nhận biết
        this.height = 0.75f;
        applyLevelData();
    }

    /**
     * Cập nhật chỉ số dựa trên level hiện tại (thường do WaveSystem thiết lập khi sinh quái)
     * Giả định: level đã được validate trong setLevel()
     */
    public void applyLevelData() {
        // Không cần validation ở đây vì level đã được validate trong setLevel()
        int index = this.level - 1;

        this.maxHealth = HEALTH_DATA[index];
        this.health = this.maxHealth;
        this.attackDamage = DAMAGE_DATA[index];
        this.attackRange = RANGE_DATA[index];

        this.goldReward = GOLD_REWARD_DATA[index];
        this.expReward = EXP_REWARD_DATA[index];
        this.bombSpeed = BOMB_SPEED_DATA[index];
        // Level càng cao ném bom càng nhanh (giảm giãn cách)
        this.setAttackSpeed(0.5f + (index * 0.2f));
    }

    @Override
    public void setLevel(int level) {
        super.setLevel(level);  // Gọi parent validation (level >= 1)
        if (this.level > MAX_LEVEL) this.level = MAX_LEVEL;
        applyLevelData();  // Tự động cập nhật chỉ số khi đổi level
    }

    @Override
    public void reset() {
        super.reset();
        this.width = 0.75f;
        this.height = 0.75f;
        applyLevelData();
    }

    public float getBombSpeed() { return bombSpeed; }

    public void setBombSpeed(float bombSpeed) {
        this.bombSpeed = Math.max(0, bombSpeed);
    }
}
