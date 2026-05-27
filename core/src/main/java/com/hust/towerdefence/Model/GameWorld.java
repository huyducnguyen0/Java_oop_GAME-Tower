package com.hust.towerdefence.Model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.PawnHacHoa;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.TNT;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.WarriorHacHoa;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.*;
import com.hust.towerdefence.Model.Entities.Tower.DefenseTower;
import com.hust.towerdefence.Model.Entities.Tower.MainTower;
import com.hust.towerdefence.Model.Managers.*;
import com.hust.towerdefence.Model.Systems.*;

public class GameWorld {

    public enum GameState {
        PLAYING,
        PAUSED,
        GAME_OVER,
        VICTORY
    }

    // ===== Managers =====
    private final EntityManager entityManager;
    private final MapManager mapManager;
    private final EconomyManager economyManager;

    // ===== Systems =====
    private final MovementSystem movementSystem;
    private final TargetingSystem targetingSystem;
    private final AttackSystem attackSystem;
    private final HealthSystem healthSystem;

    // ===== Trạng thái game =====
    private GameState state;

    // ===== Các thực thể đặc biệt =====
    private MainTower mainTower;
    private MainTower enemyTower;// nhà chính người chơi

    public GameWorld(String mapPath, int initialGold, int maxGold) {
        // ===== Tham số khởi tạo =====
        this.state = GameState.PLAYING;

        // 1. Khởi tạo Manager (MapManager cần đầu tiên)
        mapManager = new MapManager(mapPath);
        entityManager = new EntityManager();
        economyManager = EconomyManager.getInstance(initialGold, maxGold);

        // 2. Tạo thực thể cố định (dùng biến cục bộ lấy từ mapManager)
        createFixedEntities();

        // 3. Khởi tạo System (theo thứ tự phụ thuộc)
        healthSystem = new HealthSystem(entityManager, this); // tạm thời null poolManager, sẽ set sau
        attackSystem = new AttackSystem(entityManager, healthSystem);
        targetingSystem = new TargetingSystem(entityManager);
        movementSystem = new MovementSystem(entityManager, mainTower,enemyTower, mapManager);
        // movementSystem sẽ tự gọi EconomyManager.getInstance().addGold() khi Miner về
    }

    private void createFixedEntities() {
        Vector2 mainTowerPos = mapManager.getPlayerCastlePosition();
        Vector2 enemyBasePos = mapManager.getEnemyBasePosition();

        mainTower = new MainTower(mainTowerPos, BaseEntity.Team.SOLDIER, 1000);
        enemyTower = new MainTower(enemyBasePos, BaseEntity.Team.ENEMY, 5000);
        applyTowerBounds(mainTower, mapManager.getPlayerMainTowerZone());
        applyTowerBounds(enemyTower, mapManager.getEnemyMainTowerZone());
        entityManager.addTower(mainTower);
        entityManager.addTower(enemyTower);
        createDefenseTowers(mapManager.getPlayerTowerZones(), BaseEntity.Team.SOLDIER);
        createDefenseTowers(mapManager.getEnemyTowerZones(), BaseEntity.Team.ENEMY);
    }

    private void createDefenseTowers(Array<BuildingZone> zones, BaseEntity.Team team) {
        for (BuildingZone zone : zones) {
            DefenseTower tower = new DefenseTower(zone.getName(), zone.getCenter(), team);
            applyTowerBounds(tower, zone);
            entityManager.addTower(tower);
        }
    }

    private void applyTowerBounds(com.hust.towerdefence.Model.Entities.Tower.BaseTower tower, BuildingZone zone) {
        if (tower == null || zone == null) return;
        com.badlogic.gdx.math.Rectangle bounds = zone.getBounds();
        tower.setWidth(bounds.width);
        tower.setHeight(bounds.height);
    }

    // ==================== VÒNG LẶP CHÍNH ====================

    public void update(float delta) {
        if (state != GameState.PLAYING) return;
        targetingSystem.update(delta);
        movementSystem.update(delta);
        attackSystem.update(delta);
        healthSystem.update(delta);
    }

    // ==================== API CHO CONTROLLER ====================

    public void spawnPawn() {
        if (!economyManager.canBuyPawn()) return;
        economyManager.buyPawn();
        Vector2 homePos = mapManager.getPlayerCastleSpawnPosition();
        Pawn pawn = new Pawn();
        pawn.setPosition(homePos);
        pawn.setPath(mapManager.getWaypoints(pawn));
        pawn.setState(CombatEntity.State.MOVING);
        entityManager.addSoldier(pawn);
    }

    public void spawnMiner() {
        if (!economyManager.canBuyMiner()) return;
        economyManager.buyMiner();
        Vector2 homePos = mapManager.getPlayerCastleSpawnPosition();
        Miner miner = new Miner();
        // Miner luôn xuất phát từ nhà chính
        miner.setPosition(homePos);
        miner.setPath(mapManager.getWaypoints(miner));
        miner.setState(CombatEntity.State.GOING_TO_MINE);
        miner.setMiningTimer(0);
        entityManager.addSoldier(miner);
    }

    public void spawnArcher() {
        if (!economyManager.canBuyArcher()) return;
        economyManager.buyArcher();
        Vector2 homePos = mapManager.getPlayerCastleSpawnPosition();
        Archer archer = new Archer();
        archer.setPosition(homePos);
        archer.setPath(mapManager.getWaypoints(archer));
        archer.setState(CombatEntity.State.MOVING);
        entityManager.addSoldier(archer);
    }
    public void spawnWarrior() {
        if (!economyManager.canBuyWarrior()) return;
        economyManager.buyWarrior();
        Vector2 homePos = mapManager.getPlayerCastleSpawnPosition();
        Warrior warrior = new Warrior();
        warrior.setPosition(homePos);
        warrior.setPath(mapManager.getWaypoints(warrior));
        warrior.setState(CombatEntity.State.MOVING);
        entityManager.addSoldier(warrior);
    }

    public void spawnHealer() {
        if (!economyManager.canBuyHealer()) return;
        economyManager.buyHealer();
        Vector2 homePos = mapManager.getPlayerCastleSpawnPosition();
        Healer healer = new Healer();
        healer.setPosition(homePos);
        healer.setPath(mapManager.getWaypoints(healer));
        healer.setState(CombatEntity.State.MOVING);
        entityManager.addSoldier(healer);
    }

    public void spawnLancer() {
        if (!economyManager.canBuyLancer()) return;
        economyManager.buyLancer();
        Vector2 homePos = mapManager.getPlayerCastleSpawnPosition();
        Lancer lancer = new Lancer();
        lancer.setPosition(homePos);
        lancer.setPath(mapManager.getWaypoints(lancer));
        lancer.setState(CombatEntity.State.MOVING);
        entityManager.addSoldier(lancer);
    }

    public void spawnTNT(){
        Vector2 enemyPos = mapManager.getEnemyBaseSpawnPosition();
        TNT tnt = new TNT();
        tnt.setPosition(enemyPos);
        tnt.setPath(mapManager.getWaypoints(tnt));
        tnt.setState(CombatEntity.State.IDLE);
        entityManager.addEnemy(tnt);
    }
    public void spawnWarriorHacHoa(){
        Vector2 enemyPos = mapManager.getEnemyBaseSpawnPosition();
        WarriorHacHoa warriorhachoa = new WarriorHacHoa();
        warriorhachoa.setPosition(enemyPos);
        warriorhachoa.setPath(mapManager.getWaypoints(warriorhachoa));
        warriorhachoa.setState(CombatEntity.State.IDLE);
        entityManager.addEnemy(warriorhachoa);
    }
    public void spawnPawnHacHoa(){
        Vector2 enemyPos = mapManager.getEnemyBaseSpawnPosition();
        PawnHacHoa pawnHacHoa = new PawnHacHoa();
        pawnHacHoa.setPosition(enemyPos);
        pawnHacHoa.setPath(mapManager.getWaypoints(pawnHacHoa));
        pawnHacHoa.setState(CombatEntity.State.IDLE);
        entityManager.addEnemy(pawnHacHoa);
    }

    public DefenseTower getDefenseTower(String mapName) {
        return entityManager.getDefenseTowerByMapName(mapName);
    }

    public boolean upgradeDefenseTower(String mapName) {
        DefenseTower tower = getDefenseTower(mapName);
        if (tower == null || tower.getTeam() != BaseEntity.Team.SOLDIER || !tower.canUpgrade()) return false;
        if (!economyManager.spendGold(tower.getUpgradeCost())) return false;
        return tower.upgrade();
    }

    public com.badlogic.gdx.utils.Array<CombatVisualEvent> consumeCombatVisualEvents() {
        return attackSystem.consumeVisualEvents();
    }


    // ==================== ĐIỀU KHIỂN TRẠNG THÁI ====================

    public GameState getState() {
        return state;
    }

    public void setPaused(boolean paused) {
        if (state == GameState.PLAYING && paused) {
            state = GameState.PAUSED;
        } else if (state == GameState.PAUSED && !paused) {
            state = GameState.PLAYING;
        }
    }

    public boolean isPaused() {
        return state == GameState.PAUSED;
    }

    public void gameOver() {
        if (state == GameState.PLAYING || state == GameState.PAUSED) {
            state = GameState.GAME_OVER;
        }
    }

    public void victory() {
        if (state == GameState.PLAYING || state == GameState.PAUSED) {
            state = GameState.VICTORY;
        }
    }

    public boolean isGameOver() { return state == GameState.GAME_OVER; }
    public boolean isVictory() { return state == GameState.VICTORY; }
    public boolean isPlaying() { return state == GameState.PLAYING; }

    // ==================== GETTER CHO VIEW ====================
    public EntityManager getEntityManager() { return entityManager; }
    public MapManager getMapManager() { return mapManager; }
    public EconomyManager getEconomyManager() { return economyManager; }
    public MainTower getMainTower() { return mainTower; }

    /** Dọn dẹp khi chuyển màn hoặc thoát */
    public void dispose() {
        mapManager.dispose();
        entityManager.clear();
        EconomyManager.dispose(); // reset singleton nếu cần
    }
    public MainTower getEnemyTower() {
        return enemyTower;
    }
}
