package com.hust.towerdefence.Model.Entities.Combat.Enemy;
/**
 * PawnHacHoa (Dark Pawn)
 * Phiên bản hắc hóa của Pawn phe người chơi.
 * Máu và sát thương cao hơn Pawn thường, nhưng tầm đánh giữ nguyên.
 */
public class PawnHacHoa extends Enemy {

    // Thông số cho 3 cấp độ (Level 1, 2, 3)
    // Đã BUFF MẠNH: Thống trị mặt bằng chung, HP cực trâu và ATK cực gắt
    private static final float[] HEALTH_DATA = {280f, 580f, 1100f};      // HP tăng vọt, Lv3 chạm mốc 1100f (gần bằng Tanker đại ca 1200f)
    private static final float[] DAMAGE_DATA = {32f, 68f, 115f};        // ATK siêu uy tín, Lv3 lên hẳn 115f (vượt mặt Sát thủ 110f, đứng top 2 game)

    // Giữ nguyên tầm đánh ngắn như yêu cầu (Pawn: 40, 45, 50)
    private static final float[] RANGE_DATA = {40f, 45f, 50f};

    // Phần thưởng khi tiêu diệt
    private static final int[] GOLD_REWARD_DATA = {15, 35, 80};
    private static final float[] EXP_REWARD_DATA = {30f, 70f, 150f};

    public PawnHacHoa() {
        super();
        this.width = 0.8f; // Giữ nguyên kích thước như Pawn thường để tạo sự tương phản về chỉ số mà không làm thay đổi hình ảnh quá nhiều
        this.height = 0.8f;
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
        this.width = 0.8f;
        this.height = 0.8f;
        applyLevelData();
    }
}
