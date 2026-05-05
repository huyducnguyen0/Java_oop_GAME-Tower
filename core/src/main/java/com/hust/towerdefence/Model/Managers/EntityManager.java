package com.hust.towerdefence.Model.Managers;

import java.util.ArrayList;
import java.util.List;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Soldier;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.Enemy;

/**
 * EntityManager
 * Quản lý danh sách tất cả thực thể trong game (Soldiers, Enemies)
 * Chỉ xử lý: thêm/xóa entities, cập nhật, cleanup thực thể chết
 * KHÔNG xử lý: logic chiến đấu, spawn wave, AI behavior
 */
public class EntityManager {

    // ===== Collections =====
    private List<Soldier> soldiers = new ArrayList<>();   // Lính phe người chơi
    private List<Enemy> enemies = new ArrayList<>();      // Kẻ thù

    // ===== Resources =====
    private int gold = 1000;                             // Vàng hiện tại
    private int mainBuildingHp = 1000;                   // HP của thành chủ

    // ===== World Reference =====
    private GameWorld world;

    /**
     * Constructor - Khởi tạo EntityManager với world reference
     */
    public EntityManager(GameWorld world) {
        this.world = world;
    }

    /**
     * Constructor - Khởi tạo EntityManager mà không có world (cho testing)
     */
    public EntityManager() {
        this.world = null;
    }

    // ===== Add/Remove Methods =====

    /**
     * Thêm Soldier vào manager
     */
    public void addSoldier(Soldier soldier) {
        if (soldier != null && !soldiers.contains(soldier)) {
            soldiers.add(soldier);
        }
    }

    /**
     * Thêm Enemy vào manager
     */
    public void addEnemy(Enemy enemy) {
        if (enemy != null && !enemies.contains(enemy)) {
            enemies.add(enemy);
        }
    }

    /**
     * Xóa Soldier khỏi manager
     */
    public void removeSoldier(Soldier soldier) {
        soldiers.remove(soldier);
    }

    /**
     * Xóa Enemy khỏi manager
     */
    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);
    }

    /**
     * Update tất cả entities
     * - Cập nhật logic của từng entity
     * - Dọn dẹp thực thể chết (isAlive() == false)
     */
    public void update(float delta) {
        if (delta <= 0) return;

        // ===== Update Soldiers =====
        for (Soldier soldier : soldiers) {
            if (soldier.isAlive()) {
                // Update soldier logic (movement, attacking, etc)
                // updateSoldier(soldier, delta);  // Sẽ implement trong SoldierSystem
            }
        }

        // ===== Update Enemies =====
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                // Update enemy logic (AI, movement, attacking, etc)
                // updateEnemy(enemy, delta);  // Sẽ implement trong AISystem
            }
        }

        // ===== Cleanup Dead Entities =====
        cleanupDeadEntities();
    }

    /**
     * Dọn dẹp các thực thể chết
     */
    private void cleanupDeadEntities() {
        // Remove dead soldiers
        soldiers.removeIf(soldier -> !soldier.isAlive());

        // Remove dead enemies
        enemies.removeIf(enemy -> !enemy.isAlive());
    }

    /**
     * Clear tất cả entities (khi reset game)
     */
    public void clear() {
        soldiers.clear();
        enemies.clear();
    }

    // ===== Query Methods =====

    /**
     * Tìm Enemy gần nhất đến vị trí
     */
    public Enemy findNearestEnemy(float x, float y) {
        Enemy nearest = null;
        float minDist = Float.MAX_VALUE;

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;

            float dist = (float) Math.hypot(enemy.getX() - x, enemy.getY() - y);
            if (dist < minDist) {
                minDist = dist;
                nearest = enemy;
            }
        }

        return nearest;
    }

    /**
     * Tìm Soldier gần nhất đến vị trí
     */
    public Soldier findNearestSoldier(float x, float y) {
        Soldier nearest = null;
        float minDist = Float.MAX_VALUE;

        for (Soldier soldier : soldiers) {
            if (!soldier.isAlive()) continue;

            float dist = (float) Math.hypot(soldier.getX() - x, soldier.getY() - y);
            if (dist < minDist) {
                minDist = dist;
                nearest = soldier;
            }
        }

        return nearest;
    }

    /**
     * Tính tổng HP của tất cả Soldiers
     */
    public float getTotalSoldierHealth() {
        float total = 0;
        for (Soldier soldier : soldiers) {
            if (soldier.isAlive()) {
                total += soldier.getHealth();
            }
        }
        return total;
    }

    /**
     * Tính tổng HP của tất cả Enemies
     */
    public float getTotalEnemyHealth() {
        float total = 0;
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                total += enemy.getHealth();
            }
        }
        return total;
    }

    /**
     * Đếm số Soldiers còn sống
     */
    public int getAliveSoldierCount() {
        return (int) soldiers.stream().filter(CombatEntity::isAlive).count();
    }

    /**
     * Đếm số Enemies còn sống
     */
    public int getAliveEnemyCount() {
        return (int) enemies.stream().filter(CombatEntity::isAlive).count();
    }

    // ===== Resource Management =====

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = Math.max(0, gold);  // Không được âm
    }

    public void addGold(int amount) {
        this.gold += Math.max(0, amount);
    }

    public void spendGold(int amount) {
        if (amount > 0 && gold >= amount) {
            this.gold -= amount;
        }
    }

    public int getMainBuildingHp() {
        return mainBuildingHp;
    }

    public void setMainBuildingHp(int hp) {
        this.mainBuildingHp = Math.max(0, hp);
    }

    public void damageMainBuilding(int damage) {
        if (damage > 0) {
            this.mainBuildingHp = Math.max(0, mainBuildingHp - damage);
        }
    }

    public boolean isMainBuildingAlive() {
        return mainBuildingHp > 0;
    }

    // ===== Collection Getters =====

    public List<Soldier> getSoldiers() {
        return new ArrayList<>(soldiers);  // Return copy để tránh external modification
    }

    public List<Enemy> getEnemies() {
        return new ArrayList<>(enemies);   // Return copy
    }

    public int getSoldierCount() {
        return soldiers.size();
    }

    public int getEnemyCount() {
        return enemies.size();
    }

    // ===== Compatibility Methods (for legacy code) =====

    /**
     * Compatibility method - alias for addSoldier
     */
    public void addUnit(Soldier soldier) {
        addSoldier(soldier);
    }

    /**
     * Compatibility method - alias for getSoldiers
     */
    public List<Soldier> getUnits() {
        return getSoldiers();
    }

    // ===== Debug Methods =====

    /**
     * Print entity counts (for debugging)
     */
    public void printStatus() {
        System.out.println("=== EntityManager Status ===");
        System.out.println("Soldiers: " + getAliveSoldierCount() + " alive / " + soldiers.size() + " total");
        System.out.println("Enemies: " + getAliveEnemyCount() + " alive / " + enemies.size() + " total");
        System.out.println("Gold: " + gold);
        System.out.println("Main Building HP: " + mainBuildingHp);
        System.out.println("============================");
    }
}
