package com.hust.towerdefence.Model.Managers;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Soldier;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Miner;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.Enemy;

import com.hust.towerdefence.Model.Entities.Projectile.Projectile;
import com.hust.towerdefence.Model.Entities.Tower.GoldMine;

/**
 * EntityManager
 * Quản lý danh sách tất cả entities trong game
 * - Soldiers (Pawn, Warrior, Archer, Miner)
 * - Enemies (PawnHacHoa, WarriorHacHoa, TNT)
 * - GoldMines
 */
public class EntityManager {
    // ===== Danh sách chính =====
    private final SnapshotArray<Soldier> soldiers;      // Tất cả lính (bao gồm Miner)
    private final SnapshotArray<Enemy> enemies;         // Tất cả kẻ thù
    private final SnapshotArray<GoldMine> goldMines;    // Tất cả mỏ vàng
    private final SnapshotArray<Projectile> projectiles;  // Tất cả đạn (nếu có)

    // ===== Danh sách tổng hợp =====
    private final SnapshotArray<BaseEntity> allEntities;

    // ===== Hàng đợi xóa =====
    private final Array<BaseEntity> pendingRemovals;

    /**
     * Constructor - Khởi tạo EntityManager
     */
    public EntityManager() {
        projectiles = new SnapshotArray<>();
        soldiers = new SnapshotArray<>();
        enemies = new SnapshotArray<>();
        goldMines = new SnapshotArray<>();
        allEntities = new SnapshotArray<>();
        pendingRemovals = new Array<>();
    }

    // ===================== ADD METHODS =====================

    /**
     * Thêm Soldier vào manager
     */
    public void addSoldier(Soldier soldier) {
        if (soldier != null) {
            soldiers.add(soldier);
            allEntities.add(soldier);
        }
    }

    /**
     * Thêm Enemy vào manager
     */
    public void addEnemy(Enemy enemy) {
        if (enemy != null) {
            enemies.add(enemy);
            allEntities.add(enemy);
        }
    }

    /**
     * Thêm GoldMine vào manager
     */
    public void addGoldMine(GoldMine mine) {
        if (mine != null) {
            goldMines.add(mine);
            allEntities.add(mine);
        }
    }
    public void addProjectile(Projectile p) {
        projectiles.add(p);
        allEntities.add(p);
    }

    // ===================== REMOVE METHODS =====================

    /**
     * Đánh dấu entity để xóa (xóa safe dưới frame này)
     */
    public void markForRemoval(BaseEntity entity) {
        if (entity != null && !pendingRemovals.contains(entity, true)) {
            pendingRemovals.add(entity);
        }
    }

    /**
     * Xóa thực sự các entity đã đánh dấu
     * Gọi cuối mỗi frame
     */
    public void processPendingRemovals() {
        for (BaseEntity entity : pendingRemovals) {
            removeFromAllLists(entity);
        }
        pendingRemovals.clear();
    }
    public SnapshotArray<Projectile> getProjectiles() { return projectiles; }

    /**
     * Xóa entity từ tất cả danh sách
     */
    private void removeFromAllLists(BaseEntity entity) {
        allEntities.removeValue(entity, true);

        if (entity instanceof Soldier) {
            soldiers.removeValue((Soldier) entity, true);
        } else if (entity instanceof Enemy) {
            enemies.removeValue((Enemy) entity, true);
        } else if (entity instanceof GoldMine) {
            goldMines.removeValue((GoldMine) entity, true);
        }else if (entity instanceof Projectile) projectiles.removeValue((Projectile) entity, true);
    }

    // ===================== BASIC GETTERS =====================

    public SnapshotArray<Soldier> getSoldiers() {
        return soldiers;
    }

    public SnapshotArray<Enemy> getEnemies() {
        return enemies;
    }

    public SnapshotArray<GoldMine> getGoldMines() {
        return goldMines;
    }

    public SnapshotArray<BaseEntity> getAllEntities() {
        return allEntities;
    }

    // ===================== QUERY METHODS =====================

    /**
     * Lấy tất cả Soldiers còn sống
     */
    public Array<Soldier> getAliveSoldiers() {
        Array<Soldier> alive = new Array<>();
        for (Soldier s : soldiers) {
            if (s != null && s.getHealth() > 0) {
                alive.add(s);
            }
        }
        return alive;
    }

    /**
     * Lấy tất cả Enemies còn sống
     */
    public Array<Enemy> getAliveEnemies() {
        Array<Enemy> alive = new Array<>();
        for (Enemy e : enemies) {
            if (e != null && e.getHealth() > 0) {
                alive.add(e);
            }
        }
        return alive;
    }

    /**
     * Lấy tất cả combat units (Soldiers + Enemies)
     * Dùng cho TargetingSystem
     */
    public Array<BaseEntity> getAllCombatUnits() {
        Array<BaseEntity> units = new Array<>();
        units.addAll(soldiers);
        units.addAll(enemies);
        return units;
    }

    /**
     * Tìm Enemy gần nhất với vị trí (x, y)
     * @param maxRange Tầm tìm kiếm tối đa
     * @return Enemy gần nhất, hoặc null nếu không tìm thấy
     */
    public Enemy getNearestEnemy(float x, float y, float maxRange) {
        Enemy nearest = null;
        float minDistSq = maxRange * maxRange;

        for (Enemy e : enemies) {
            if (e == null || e.getHealth() <= 0) continue;

            float dx = e.getX() - x;
            float dy = e.getY() - y;
            float distSq = dx * dx + dy * dy;

            if (distSq < minDistSq) {
                minDistSq = distSq;
                nearest = e;
            }
        }

        return nearest;
    }

    /**
     * Tìm Soldier gần nhất với vị trí (x, y)
     * @param maxRange Tầm tìm kiếm tối đa
     * @return Soldier gần nhất, hoặc null nếu không tìm thấy
     */
    public Soldier getNearestSoldier(float x, float y, float maxRange) {
        Soldier nearest = null;
        float minDistSq = maxRange * maxRange;

        for (Soldier s : soldiers) {
            if (s == null || s.getHealth() <= 0) continue;

            float dx = s.getX() - x;
            float dy = s.getY() - y;
            float distSq = dx * dx + dy * dy;

            if (distSq < minDistSq) {
                minDistSq = distSq;
                nearest = s;
            }
        }

        return nearest;
    }

    /**
     * Lấy tất cả Miners
     */
    public Array<Miner> getMiners() {
        Array<Miner> miners = new Array<>();
        for (Soldier s : soldiers) {
            if (s instanceof Miner) {
                miners.add((Miner) s);
            }
        }
        return miners;
    }

    // ===================== UTILITY METHODS =====================

    /**
     * Xóa tất cả entities
     */
    public void clearAll() {
        soldiers.clear();
        enemies.clear();
        goldMines.clear();
        allEntities.clear();
        pendingRemovals.clear();
    }

    /**
     * Đếm tổng số entities
     */
    public int getTotalEntityCount() {
        return allEntities.size;
    }

    /**
     * Đếm số Soldiers còn sống
     */
    public int getAliveSoldierCount() {
        int count = 0;
        for (Soldier s : soldiers) {
            if (s != null && s.getHealth() > 0) count++;
        }
        return count;
    }

    /**
     * Đếm số Enemies còn sống
     */
    public int getAliveEnemyCount() {
        int count = 0;
        for (Enemy e : enemies) {
            if (e != null && e.getHealth() > 0) count++;
        }
        return count;
    }
}
