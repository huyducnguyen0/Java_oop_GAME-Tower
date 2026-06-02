package com.hust.towerdefence.Model.Entities.Combat.Soldier;

/**
 * Healer
 * Đơn vị hỗ trợ, hồi máu cho đồng đội trong phạm vi.
 */
public class Healer extends Soldier {

    // --- CẤU HÌNH THÔNG SỐ CHO 3 CẤP ĐỘ (LEVEL 1, 2, 3) ---
    private static final float[] HEAL_AMOUNT = {20f, 40f, 80f}; // Lượng máu hồi mỗi lần - cố định progression
    private static final float[] HEAL_RANGE = {2.5f, 3.0f, 3.5f}; // Phạm vi tìm đồng đội
    private static final int[] UPGRADE_COST_DATA = {100, 250, 0};

    /**
     * Hàm khởi tạo Tu sĩ hồi máu (Healer)
     */
    public Healer() {
        super();
        this.width = 0.7f;
        this.height = 0.7f;
        applyLevelData();
    }

    /**
     * Thiết lập chỉ số: Hồi máu mạnh dần theo level, giãn cách thời gian ổn định.
     */
    public void applyLevelData() {
        // Không cần validation ở đây vì level đã được validate trong setLevel()
        int index = this.level - 1;

        // Tái định nghĩa: Damage -> Heal Amount
        this.attackDamage = HEAL_AMOUNT[index];

        // Tái định nghĩa: Range -> Support Range
        this.attackRange = HEAL_RANGE[index];

        this.upgradeCost = UPGRADE_COST_DATA[index];

        // Giãn thời gian: Healer thường hồi máu khá chậm để cân bằng game
        // Level 1: ~2 giây/lần, Level 3: ~1.4 giây/lần
        this.setAttackSpeed(0.5f + (index * 0.1f));

        this.maxHealth = 70f + (index * 30f); // Healer thường có máu thấp
        this.health = this.maxHealth;
    }

    // ==================== HỆ THỐNG OVERRIDE LOGIC ====================

    @Override
    public void reset() {
        super.reset();
        this.width = 0.7f;
        this.height = 0.7f;
        applyLevelData();
    }

    @Override
    public void setLevel(int level) {
        super.setLevel(level);  // Gọi parent validation (level >= 1)
        if (this.level > MAX_LEVEL) this.level = MAX_LEVEL;
        applyLevelData();  // Tự động cập nhật stats khi level thay đổi
    }

    // ==================== GETTERS NGỮ NGHĨA HỆ THỐNG ====================

    public float getHealAmount() {
        return attackDamage;
    }

    // ========================================================
    // CÁC HÀM TRUY VẤN DỮ LIỆU TĨNH (STATIC GETTERS) CHO UI
    // ========================================================

    public static float getStaticMaxHealth(int level) {
        int idx = Math.min(Math.max(level, 1), 3) - 1;
        return 70f + (idx * 30f); // Thực hiện đồng bộ chính xác công thức toán gốc
    }

    public static float getStaticDamage(int level) {
        int idx = Math.min(Math.max(level, 1), 3) - 1;
        return HEAL_AMOUNT[idx];
    }

    public static float getStaticRange(int level) {
        int idx = Math.min(Math.max(level, 1), 3) - 1;
        return HEAL_RANGE[idx];
    }

    public static int getStaticUpgradeCost(int level) {
        int idx = Math.min(Math.max(level, 1), 3) - 1;
        return UPGRADE_COST_DATA[idx];
    }
}
