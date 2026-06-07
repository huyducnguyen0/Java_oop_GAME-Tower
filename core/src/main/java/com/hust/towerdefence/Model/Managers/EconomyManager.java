package com.hust.towerdefence.Model.Managers;

import com.badlogic.gdx.utils.Logger;

public class EconomyManager {
    private static final Logger log = new Logger("EconomyManager", Logger.DEBUG);
    private static EconomyManager instance;

    // Các hằng số chi phí lính
    public static final int COST_PAWN = 50;
    public static final int COST_WARRIOR = 80;
    public static final int COST_ARCHER = 110;
    public static final int COST_HEALER = 150;
    public static final int COST_LANCER = 190;
    public static final int COST_MINER = 40;

    private int gold;
    private int initialGold;
    private int maxGold;
    private boolean cheatsEnabled;

    // Private constructor – chỉ dùng từ bên trong
    private EconomyManager(int initialGold, int maxGold) {
        this.initialGold = initialGold;
        this.maxGold = maxGold;
        this.gold = Math.min(initialGold, maxGold);
        this.cheatsEnabled = false;
    }

    /**
     * Khởi tạo instance nếu chưa có, hoặc trả về instance hiện tại.
     * Gọi đầu game với initialGold, maxGold mong muốn.
     * Nếu đã khởi tạo rồi thì tham số bị bỏ qua, dùng reset() để thay đổi.
     */
    public static EconomyManager getInstance(int initialGold, int maxGold) {
        if (instance == null) {
            instance = new EconomyManager(initialGold, maxGold);
        }
        return instance;
    }

    /**
     * Lấy instance đã khởi tạo. Phải gọi getInstance(int, int) trước.
     */
    public static EconomyManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("EconomyManager chưa được khởi tạo! Hãy gọi getInstance(initialGold, maxGold) trước.");
        }
        return instance;
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

    public void addGold(int amount) {
        if (amount <= 0) return;
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

    public boolean spendGold(int amount) {
        if (amount < 0) {
            log.error("spendGold called with negative amount: " + amount);
            return false;
        }
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

    public boolean canAfford(int amount) {
        return gold >= amount;
    }

    // ==================== HỖ TRỢ MUA LÍNH ====================

    public boolean canBuyPawn() { return canAfford(COST_PAWN); }
    public boolean buyPawn() { return spendGold(COST_PAWN); }

    public boolean canBuyWarrior() { return canAfford(COST_WARRIOR); }
    public boolean buyWarrior() { return spendGold(COST_WARRIOR); }

    public boolean canBuyArcher() { return canAfford(COST_ARCHER); }
    public boolean buyArcher() { return spendGold(COST_ARCHER); }

    public boolean canBuyHealer() { return canAfford(COST_HEALER); }
    public boolean buyHealer() { return spendGold(COST_HEALER); }

    public boolean canBuyLancer() { return canAfford(COST_LANCER); }
    public boolean buyLancer() { return spendGold(COST_LANCER); }

    public boolean canBuyMiner() { return canAfford(COST_MINER); }
    public boolean buyMiner() { return spendGold(COST_MINER); }

    // ==================== UTILITY ====================

    /** Reset vàng về giá trị khởi điểm ban đầu */
    public void reset() {
        gold = initialGold;
        log.debug("Reset gold to " + initialGold);
    }

    /** Dùng cho cheat hoặc load game */
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

    public void setCheatsEnabled(boolean enabled) {
        this.cheatsEnabled = enabled;
        if (enabled) log.debug("Cheats enabled: gold will never decrease");
    }

    public boolean isCheatsEnabled() {
        return cheatsEnabled;
    }

    /** Hủy instance (nếu cần reset toàn bộ game) */
    public static void dispose() {
        instance = null;
    }
}
