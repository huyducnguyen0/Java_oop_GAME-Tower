package com.hust.towerdefence.Model.Entities.Combat.Enemy;
/**
 * WarriorHacHoa (Dark Warrior)
 * Phiên bản hắc hóa của Warrior phe người chơi.
 * Đóng vai trò Tanker chính của phe địch với lượng máu và sát thương vượt trội Warrior thường.
 */
public class WarriorHacHoa extends Enemy {


    // Chỉ số nhỉnh hơn Warrior đồng minh (Warrior: 300, 650, 1200)
    private static final float[] HEALTH_DATA = {350f, 750f, 1500f};

    // Sát thương nhỉnh hơn Warrior đồng minh (Warrior: 15, 30, 55)
    private static final float[] DAMAGE_DATA = {20f, 45f, 75f};

    // Tầm đánh nhỉnh hơn Warrior một chút (Warrior: 75, 80, 85)
    private static final float[] RANGE_DATA = {1.2f, 1.3f, 1.4f};

    // Phần thưởng khi tiêu diệt (Xứng đáng với công sức hạ gục một Tanker)
    private static final int[] GOLD_REWARD_DATA = {40, 100, 250};
    private static final float[] EXP_REWARD_DATA = {60f, 150f, 350f};

    public WarriorHacHoa() {
        super();
        this.width = 0.9f;
        this.height = 0.9f; // Giữ chiều cao như Warrior thường để tạo sự tương phản về chỉ số mà không làm thay đổi hình ảnh quá nhiều
        applyLevelData();
    }

    /**
     * Cập nhật chỉ số dựa trên cấp độ.
     * Warrior hắc hóa có tốc độ đánh chậm nhưng mỗi đòn đánh đều rất nặng nề.
     */
    public void applyLevelData() {
        int index = this.level - 1;

        this.maxHealth = HEALTH_DATA[index];
        this.health = this.maxHealth;
        this.attackDamage = DAMAGE_DATA[index];
        this.attackRange = RANGE_DATA[index];

        this.goldReward = GOLD_REWARD_DATA[index];
        this.expReward = EXP_REWARD_DATA[index];

            // Tốc độ đánh chậm hơn Warrior thường để tạo cảm giác "nặng nề" và "đáng sợ"
            // Level 1: ~0.8 phát/giây, Level 3: ~1.1 phát/giây
        this.setAttackSpeed(0.8f + (index * 0.15f));
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
        this.width = 0.9f;
        this.height = 0.9f;
        applyLevelData();
    }
}
