package com.hust.towerdefence.Model.Managers;

import java.util.ArrayList;
import java.util.List;
import com.hust.towerdefence.Model.GameWorld;

public class EntityManager {// entity manager sẽ chỉ quản lý danh sách thực thể và cập nhật chúng, không xử lý logic chiến đấu hay spawn
    private List<Unit> units = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private int gold = 1000;
    private int mainBuildingHp = 1000;

    private GameWorld world;  // ← thêm reference đến GameWorld

    public EntityManager(GameWorld world) {  // ← nhận world qua constructor
        this.world = world;
    }

    public void addUnit(Unit unit) { units.add(unit); }
    public void addEnemy(Enemy enemy) { enemies.add(enemy); }

    public void update(float delta) {
        // Chỉ dọn dẹp thực thể chết tại đây
        units.removeIf(u -> u.getState() == Unit.State.DEAD);
        enemies.removeIf(e -> e.getState() == Enemy.State.DEAD);

        for(Unit u : units) u.update(delta);
        for(Enemy e : enemies) {
            e.getAIController().update(world, this, delta);
        }
    }

    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }
    public int getMainBuildingHp() { return mainBuildingHp; }
    public void setMainBuildingHp(int hp) { this.mainBuildingHp = hp; }
    public List<Unit> getUnits() { return units; }
    public List<Enemy> getEnemies() { return enemies; }
}
