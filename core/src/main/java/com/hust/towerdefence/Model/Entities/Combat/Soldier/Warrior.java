package com.hust.towerdefence.Model.Entities.Combat.Soldier;
import com.badlogic.gdx.math.Vector2;
public class Warrior extends Soldier {

    // Thông số cho 3 cấp độ (Level 1, 2, 3)
    // Máu của Warrior cao vượt trội so với các đơn vị khác
    private static final float[] HEALTH_DATA = {300f, 650f, 1200f};// máu tăng mạnh để tạo cảm giác "trâu bò" và có thể đứng vững lâu hơn trên chiến trường
    private static final float[] DAMAGE_DATA = {15f, 30f, 55f};
    // Tầm đánh: Pawn (40-50), Warrior (75-80)
    // Giúp Warrior có thể chạm vào địch sớm hơn một chút để "thu hút" sự chú ý.
    private static final float[] RANGE_DATA = {1.0f, 1.1f, 1.2f}; // cần chuyển sang World Unit (Ví dụ: 1.0f tương đương 100 pixel)
    private static final int[] UPGRADE_COST_DATA = {120, 300, 0};

    public Warrior(Vector2 playerCastlePosition) {
        super();
        this.width = 0.9f; // Lớn hơn Pawn để trông vững chãi hơn, nhưng vẫn nhỏ hơn 1.0f để không chiếm quá nhiều diện tích
        this.height = 0.9f; // Cùng chiều cao để tạo cảm giác đồng đều, nhưng có thể điều chỉnh nếu muốn tạo sự khác biệt rõ ràng hơn
        applyLevelData();
    }

    /**
     * Thiết lập chỉ số: Ưu tiên tối đa vào lượng máu và khả năng đứng vững.
     */
    public void applyLevelData() {
        // Không cần validation ở đây vì level đã được validate trong setLevel()
        int index = this.level - 1;

        this.maxHealth = HEALTH_DATA[index];
        this.health = this.maxHealth; // Hồi đầy máu khi nâng cấp

        this.attackDamage = DAMAGE_DATA[index];
        this.attackRange = RANGE_DATA[index];
        this.upgradeCost = UPGRADE_COST_DATA[index];

        // Tốc độ đánh: Chậm hơn Pawn (1.2) một chút để nhấn mạnh sự nặng nề của giáp trụ
        this.setAttackSpeed(0.9f + (index * 0.1f));
    }

    @Override
    public void reset() {
        super.reset();
        this.width = 0.9f;
        this.height = 0.9f;
        applyLevelData();
    }

    @Override
    public void setLevel(int level) {
        super.setLevel(level);  // Gọi parent validation (level >= 1)
        if (this.level > MAX_LEVEL) this.level = MAX_LEVEL;
        applyLevelData();  // Tự động cập nhật stats khi level thay đổi
    }
}
