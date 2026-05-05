package com.hust.towerdefence.Model.Managers;

import com.badlogic.gdx.utils.Logger;

/**
 * EconomyManager - Quản lý tài nguyên vàng trong game.
 * Nguồn vàng: từ miner đào, từ tiêu diệt enemy.
 * Chi tiêu: mua lính (soldier).
 */
public class EconomyManager {
    private static final Logger log = new Logger("EconomyManager", Logger.DEBUG);

    private int gold;
    private final int initialGold;
    private int maxGold;          // nếu có giới hạn, mặc định Integer.MAX_VALUE
    private boolean cheatsEnabled = false; // cho debug

    // Các hằng số chi phí (khớp với UPGRADE_COST_DATA[0] của từng class)
    public static final int COST_PAWN = 50;      // Pawn: {50, 150, 0}
    public static final int COST_WARRIOR = 120;  // Warrior: {120, 300, 0}
    public static final int COST_ARCHER = 60;    // Archer: {60, 180, 0}
    public static final int COST_HEALER = 100;   // Healer: {100, 250, 0}
    public static final int COST_LANCER = 70;    // Lancer: {70, 220, 0}
    public static final int COST_MINER = 80;     // Miner: {80, 200, 0}

    public EconomyManager(int initialGold) {
        this(initialGold, Integer.MAX_VALUE);
    }

    public EconomyManager(int initialGold, int maxGold) {
        this.initialGold = initialGold;
        this.maxGold = maxGold;
        this.gold = Math.min(initialGold, maxGold);
    }

    // ==================== GETTERS ====================
    public int getGold() {
        return gold;
    }

    public int getInitialGold() {
        return initialGold;
    }

    public int getMaxGold() {
        return maxGold;
    }

    // ==================== CỘNG TRỪ VÀNG ====================

    /**
     * Thêm vàng, không vượt quá maxGold
     * @param amount số vàng thêm vào (>0)
     */
    public void addGold(int amount) {
        if (amount == 0) return;
        if (amount < 0) {
            log.error("addGold called with negative amount: " + amount);
            return;
        }
        int newGold = gold + amount;
        if (newGold > maxGold) {
            newGold = maxGold;
        }
        gold = newGold;
        log.debug("+" + amount + " gold. Total: " + gold);
    }

    /**
     * Tiêu vàng nếu đủ, trả về true nếu thành công
     * @param amount số vàng cần tiêu (>=0)
     */
    public boolean spendGold(int amount) {
        if (amount < 0) {
            log.error("spendGold called with negative amount: " + amount);
            return false;
        }

        // Cheat mode: never decrease gold
        if (cheatsEnabled) {
            log.debug("Cheat: spendGold ignored -> still have " + gold);
            return true;
        }

        if (gold >= amount) {
            gold -= amount;
            log.debug("-" + amount + " gold. Remaining: " + gold);
            return true;
        } else {
            log.debug("Not enough gold! Need " + amount + ", have " + gold);
            return false;
        }
    }

    /**
     * Kiểm tra có đủ vàng không (không thay đổi số vàng)
     */
    public boolean canAfford(int amount) {
        return gold >= amount;
    }

    // ==================== HỖ TRỢ MUA LÍNH ====================

    public boolean canBuyPawn() {
        return canAfford(COST_PAWN);
    }

    public boolean buyPawn() {
        return spendGold(COST_PAWN);
    }

    public boolean canBuyWarrior() {
        return canAfford(COST_WARRIOR);
    }

    public boolean buyWarrior() {
        return spendGold(COST_WARRIOR);
    }

    public boolean canBuyArcher() {
        return canAfford(COST_ARCHER);
    }

    public boolean buyArcher() {
        return spendGold(COST_ARCHER);
    }

    public boolean canBuyHealer() {
        return canAfford(COST_HEALER);
    }

    public boolean buyHealer() {
        return spendGold(COST_HEALER);
    }

    public boolean canBuyLancer() {
        return canAfford(COST_LANCER);
    }

    public boolean buyLancer() {
        return spendGold(COST_LANCER);
    }

    public boolean canBuyMiner() {
        return canAfford(COST_MINER);
    }

    public boolean buyMiner() {
        return spendGold(COST_MINER);
    }

    // ==================== UTILITY ====================

    /**
     * Reset vàng về giá trị ban đầu
     */
    public void reset() {
        gold = initialGold;
        log.debug("Reset gold to " + initialGold);
    }

    /**
     * Thiết lập vàng trực tiếp (dùng cho cheat hoặc load game)
     */
    public void setGold(int gold) {
        if (gold < 0) gold = 0;
        if (gold > maxGold) gold = maxGold;
        this.gold = gold;
        log.debug("Gold set to " + gold);
    }

    public void setMaxGold(int maxGold) {
        this.maxGold = maxGold;
        if (gold > maxGold) gold = maxGold;
    }

    /**
     * Bật/tắt cheat (cho phép vàng vô hạn)
     */
    public void setCheatsEnabled(boolean enabled) {
        this.cheatsEnabled = enabled;
        if (enabled) {
            log.debug("Cheats enabled: gold will never decrease");
        }
    }

    public boolean isCheatsEnabled() {
        return cheatsEnabled;
    }
}
