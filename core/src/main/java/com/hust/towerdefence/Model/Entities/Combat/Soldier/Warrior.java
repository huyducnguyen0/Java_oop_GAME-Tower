package com.hust.towerdefence.Model.Entities.Combat.Soldier;

import com.badlogic.gdx.math.Vector2;

public class Warrior extends Soldier {

    // --- HE THONG DU LIEU CHI SO CO DINH THEO CAP DO ---
    private static final float[] HEALTH_DATA = {300f, 650f, 1200f};       // Mau tang manh tao cam giac "trau bo"
    private static final float[] DAMAGE_DATA = {15f, 30f, 55f};          // Sat thuong can chien
    private static final float[] RANGE_DATA = {1.0f, 1.1f, 1.2f};         // Tam danh (World Unit)
    private static final int[] UPGRADE_COST_DATA = {120, 300, 0};         // Chi phi nang cap theo tung cap (Lv3 dat max = 0)

    /**
     * Ham khoi tao don vi Chien binh (Warrior) - Mac dinh o Level 1
     */
    public Warrior() {
        super();
        this.width = 0.9f;  // Lon hon Pawn de trong vung chai
        this.height = 0.9f; // Chieu cao can doi
        applyLevelData();
    }

    /**
     * Thiet lap chi so noi bo thoi gian thuc dua vao bien level cua thuc the
     */
    public void applyLevelData() {
        // Chuyen doi tu Level (1,2,3) sang Index mang (0,1,2)
        int index = this.level - 1;

        this.maxHealth = HEALTH_DATA[index];
        this.health = this.maxHealth; // Hoi day mau lap tuc khi thuc the duoc nang cap

        this.attackDamage = DAMAGE_DATA[index];
        this.attackRange = RANGE_DATA[index];
        this.upgradeCost = UPGRADE_COST_DATA[index];

        // Toc do danh: Nhan manh su nang ne cua giap tru (Lv1: 0.9, Lv2: 1.0, Lv3: 1.1)
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
        super.setLevel(level);  // Goi parent validation tu lop cha Soldier (dam bao level >= 1)
        if (this.level > MAX_LEVEL) this.level = MAX_LEVEL;
        applyLevelData();       // Tu dong dong bo lai chi so khi thuc the doi cap do
    }

    // ========================================================
    // CAC HAM TRUY VAN DU LIEU TINH (STATIC GETTERS) CHO UI
    // ========================================================

    /**
     * Lay luong mau toi da theo cap do ma khong can khoi tao thuc the
     */
    public static float getStaticMaxHealth(int level) {
        int idx = Math.min(Math.max(level, 1), 3) - 1;
        return HEALTH_DATA[idx];
    }

    /**
     * Lay sat thuong theo cap do ma khong can khoi tao thuc the
     */
    public static float getStaticDamage(int level) {
        int idx = Math.min(Math.max(level, 1), 3) - 1;
        return DAMAGE_DATA[idx];
    }

    /**
     * Lay tam danh theo cap do ma khong can khoi tao thuc the
     */
    public static float getStaticRange(int level) {
        int idx = Math.min(Math.max(level, 1), 3) - 1;
        return RANGE_DATA[idx];
    }

    /**
     * Lay chi phi nang cap len cap tiep theo tu cap do hien tai
     */
    public static int getStaticUpgradeCost(int level) {
        int idx = Math.min(Math.max(level, 1), 3) - 1;
        return UPGRADE_COST_DATA[idx];
    }
}
