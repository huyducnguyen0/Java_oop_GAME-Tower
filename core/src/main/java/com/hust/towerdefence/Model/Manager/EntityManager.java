package com.hust.towerdefence.Model.Manager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;

import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat;
import com.hust.towerdefence.Model.Entities.DefenseTower;
import com.hust.towerdefence.Model.Entities.Projectile;
import com.hust.towerdefence.Model.Entities.Miner;
import com.hust.towerdefence.Model.Entities.GoldMine;
import com.hust.towerdefence.Model.Entities.MainBase;
import com.hust.towerdefence.Model.Entities.ProductionStructure;

/**
 * ENTITY MANAGER - Trái tim quản lý dữ liệu tập trung.
 * Quản lý vòng đời và cung cấp các bộ lọc tìm kiếm tối ưu cho GameSystem và AI States.
 */
public class EntityManager {
    // Danh sách tổng để thực hiện update và render
    private final DelayedRemovalArray<BaseEntity> allEntities;

    // Danh sách phân loại để tối ưu truy vấn (Targeting & Economy)
    private final Array<Combat> playerUnits;      // Lính ta
    private final Array<Combat> enemyUnits;       // Lính địch
    private final Array<DefenseTower> towers;     // Trụ ta
    private final Array<MainBase> bases;          // Nhà chính 2 bên
    private final Array<Miner> miners;            // Thợ mỏ
    private final Array<GoldMine> goldMines;      // Mỏ vàng
    private final Array<ProductionStructure> productionBuildings; // Nhà lính
    private final Array<Projectile> projectiles;  // Đạn bay

    public EntityManager() {
        this.allEntities = new DelayedRemovalArray<>();
        this.playerUnits = new Array<>();
        this.enemyUnits = new Array<>();
        this.towers = new Array<>();
        this.bases = new Array<>();
        this.miners = new Array<>();
        this.goldMines = new Array<>();
        this.productionBuildings = new Array<>();
        this.projectiles = new Array<>();
    }

    /**
     * Đăng ký thực thể vào hệ thống và tự động phân loại.
     */
    public void addEntity(BaseEntity entity) {
        if (entity == null) return;

        allEntities.add(entity);

        // Phân loại sâu dựa trên Class để phục vụ tìm kiếm mục tiêu
        if (entity instanceof Combat) {
            Combat c = (Combat) entity;
            if (c.getTeamId() == 1) playerUnits.add(c);
            else if (c.getTeamId() == 2) enemyUnits.add(c);
        }
        else if (entity instanceof DefenseTower) towers.add((DefenseTower) entity);
        else if (entity instanceof Projectile) projectiles.add((Projectile) entity);
        else if (entity instanceof Miner) miners.add((Miner) entity);
        else if (entity instanceof GoldMine) goldMines.add((GoldMine) entity);
        else if (entity instanceof MainBase) bases.add((MainBase) entity);
        else if (entity instanceof ProductionStructure) productionBuildings.add((ProductionStructure) entity);
    }

    /**
     * Cập nhật logic và tự động dọn dẹp thực thể bị đánh dấu xóa (isRemoved).
     */
    public void update(float delta) {
        allEntities.begin();
        for (int i = 0; i < allEntities.size; i++) {
            BaseEntity e = allEntities.get(i);

            e.update(delta);

            if (e.isRemoved()) {
                allEntities.removeIndex(i);
                removeFromSubArrays(e);
            }
        }
        allEntities.end();
    }

    private void removeFromSubArrays(BaseEntity e) {
        if (e instanceof Combat) {
            playerUnits.removeValue((Combat) e, true);
            enemyUnits.removeValue((Combat) e, true);
        }
        else if (e instanceof DefenseTower) towers.removeValue((DefenseTower) e, true);
        else if (e instanceof Projectile) projectiles.removeValue((Projectile) e, true);
        else if (e instanceof Miner) miners.removeValue((Miner) e, true);
        else if (e instanceof GoldMine) goldMines.removeValue((GoldMine) e, true);
        else if (e instanceof MainBase) bases.removeValue((MainBase) e, true);
        else if (e instanceof ProductionStructure) productionBuildings.removeValue((ProductionStructure) e, true);
    }

    // =========================================================
    // TRUY VẤN TỐI ƯU (Dùng cho AI và Logic Game)
    // =========================================================

    /** Tìm lính địch gần nhất cho Trụ hoặc Lính ta */
    public Combat getNearestEnemy(float x, float y, float range) {
        return findNearest(x, y, range, enemyUnits);
    }

    /** Tìm lính ta gần nhất (Dùng cho AI quân địch) */
    public Combat getNearestPlayer(float x, float y, float range) {
        return findNearest(x, y, range, playerUnits);
    }

    /** Tìm mỏ vàng gần nhất cho Miner */
    public GoldMine getNearestGoldMine(float x, float y, float range) {
        return findNearest(x, y, range, goldMines);
    }

    /** Hàm lõi tìm kiếm khoảng cách (Sử dụng bình phương để tối ưu CPU) */
    private <T extends BaseEntity> T findNearest(float x, float y, float range, Array<T> targetArray) {
        T closest = null;
        float minDistanceSq = range * range;

        for (T target : targetArray) {
            if (!target.isAlive()) continue;
            float dx = x - target.getX();
            float dy = y - target.getY();
            float distSq = dx * dx + dy * dy;

            if (distSq < minDistanceSq) {
                minDistanceSq = distSq;
                closest = target;
            }
        }
        return closest;
    }

    // =========================================================
    // GETTERS & CLEANUP
    // =========================================================
    public Array<BaseEntity> getAllEntities() { return allEntities; }
    public Array<Combat> getPlayerUnits() {
        return playerUnits;
    }
    public Array<Combat> getEnemyUnits() { return enemyUnits; }
    public Array<GoldMine> getGoldMines() { return goldMines; }
    public Array<DefenseTower> getTowers() { return towers; }
    public Array<Projectile> getProjectiles() {
        return projectiles;
    }

    public void clear() {
        allEntities.clear();
        playerUnits.clear();
        enemyUnits.clear();
        towers.clear();
        bases.clear();
        miners.clear();
        goldMines.clear();
        productionBuildings.clear();
        projectiles.clear();
    }
}
