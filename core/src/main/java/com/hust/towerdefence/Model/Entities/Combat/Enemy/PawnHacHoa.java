package com.hust.towerdefence.Model.Entities.Combat.Enemy;
/**
 * PawnHacHoa (Dark Pawn)
 * Phiên bản hắc hóa của Pawn phe người chơi.
 * Máu và sát thương cao hơn Pawn thường, nhưng tầm đánh giữ nguyên.
 */
public class PawnHacHoa extends Enemy {

    // Thông số cho 3 cấp độ (Level 1, 2, 3)
    private static final int MAX_LEVEL = 3;
    // Chỉ số được thiết kế nhỉnh hơn Pawn (Pawn: 150, 300, 600)
    private static final float[] HEALTH_DATA = {180f, 380f, 750f};

    // Sát thương nhỉnh hơn Pawn (Pawn: 15, 35, 80)
    private static final float[] DAMAGE_DATA = {20f, 45f, 100f};

    // Giữ nguyên tầm đánh ngắn như yêu cầu (Pawn: 40, 45, 50)
    private static final float[] RANGE_DATA = {40f, 45f, 50f};

    // Phần thưởng khi tiêu diệt
    private static final int[] GOLD_REWARD_DATA = {15, 35, 80};
    private static final float[] EXP_REWARD_DATA = {30f, 70f, 150f};

    public PawnHacHoa() {
        super();
        this.width = 64;
        this.height = 64;
        this.level = 1;
        applyLevelData();
    }

    /**
     * Cập nhật chỉ số dựa trên cấp độ.
     * Tốc độ đánh được giữ ở mức khá để tạo áp lực liên tục.
     * Giả định: level đã được validate trong setLevel()
     */
    public void applyLevelData() {
        int index = this.level - 1;

        this.maxHealth = HEALTH_DATA[index];
        this.health = this.maxHealth;
        this.attackDamage = DAMAGE_DATA[index];
        this.attackRange = RANGE_DATA[index];

        this.goldReward = GOLD_REWARD_DATA[index];
        this.expReward = EXP_REWARD_DATA[index];

        // Tốc độ đánh tương đương hoặc nhanh hơn Pawn một chút để tăng sự hung hãn
        this.setAttackSpeed(1.2f + (index * 0.1f));
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
        this.level = 1;
        applyLevelData();
    }
}
