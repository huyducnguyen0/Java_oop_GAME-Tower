package com.hust.towerdefence.Model.AI;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.Enemy;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.PawnHacHoa;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.TNT;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.WarriorHacHoa;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Soldier;
import com.hust.towerdefence.Model.Entities.Tower.MainTower;
import com.hust.towerdefence.Model.Managers.EntityManager;
import com.hust.towerdefence.Model.Managers.MapManager;

public class AIController {
    // ---- Cấu hình wave ----
    private static final int MAX_WAVES = 100;
    private static final float SPAWN_INTERVAL_BASE = 2.0f;   // giây spawn quân đầu tiên của mỗi wave (khi chưa tăng độ khó)
    private static final float MIN_SPAWN_INTERVAL = 0.5f;    // giới hạn thấp nhất
    private static final float INITIAL_DELAY = 2.0f;         // trễ trước wave đầu tiên

    // ---- Trạng thái wave ----
    private int wavesStarted;               // tổng số wave đã bắt đầu
    private int enemiesRemainingToSpawn;    // số quân còn phải spawn trong wave hiện tại
    private float spawnTimer;               // đếm ngược đến lần spawn tiếp theo
    private boolean isSpawningWave;         // đang trong quá trình spawn quân của 1 wave
    private float initialDelayTimer;        // đếm ngược cho lần đầu

    // ---- Độ khó động ----
    private int currentEnemyLevel;          // level của quân được spawn (1,2,3), tăng mỗi 6 wave
    private int wavesInCurrentLevel;        // wave thứ mấy trong block 6 wave hiện tại (1..6)
    private float currentSpawnInterval;     // thời gian thực tế giữa 2 lần spawn (thay đổi theo độ khó)

    // ---- Kinh tế và AI ----
    private float gold;
    private final AIPersonality personality;
    private final EntityManager entityManager;
    private final MapManager mapManager;
    private final int level;                // độ khó chung của màn chơi (1,2,3) – nếu cần sau này
    private final MainTower playerMainTower;
    private final MainTower enemyMainTower;

    private static final int PAWN_COST = 8;
    private static final int WARRIOR_COST = 25;
    private static final int TNT_COST = 10;

    /**
     * Tính số quân sẽ spawn trong 1 wave theo số thứ tự wave.
     */
    private int getEnemyCountForWave(int waveNumber) {
        if (waveNumber <= 2) return 4;
        if (waveNumber <= 6) return 6;
        // Từ wave 7 trở đi, cứ 2 wave tăng 1 quân, tối đa 9
        int extra = (waveNumber - 7) / 2 + 1;  // wave 7->1, 8->1, 9->2, 10->2, 11->3...
        return Math.min(9, 6 + extra);
    }

    /**
     * Ngân sách vàng cho wave dựa trên số quân và độ khó hiện tại.
     */
    private float getWaveGoldBudget(int enemyCount, int difficultyIndex) {
        float avgCost = (PAWN_COST + WARRIOR_COST + TNT_COST) / 3f;
        float baseGold = enemyCount * avgCost * 1.2f;
        // Càng khó (difficultyIndex cao) càng thêm vàng để mua quân mạnh
        return baseGold * (1f + difficultyIndex * 0.1f);
    }

    public AIController(int level, EntityManager entityManager, MapManager mapManager,
                        MainTower playerMainTower, MainTower enemyMainTower) {
        this.level = level;
        this.entityManager = entityManager;
        this.mapManager = mapManager;
        this.playerMainTower = playerMainTower;
        this.enemyMainTower = enemyMainTower;
        this.personality = new AIPersonality(System.nanoTime() + level);

        this.wavesStarted = 0;
        this.isSpawningWave = false;
        this.enemiesRemainingToSpawn = 0;
        this.spawnTimer = 0;
        this.initialDelayTimer = INITIAL_DELAY;
        this.gold = 0;
        this.currentEnemyLevel = 1;
        this.wavesInCurrentLevel = 0;
        this.currentSpawnInterval = SPAWN_INTERVAL_BASE;
    }

    public void update(float delta) {
        if (initialDelayTimer > 0) {
            initialDelayTimer -= delta;
            if (initialDelayTimer <= 0 && wavesStarted == 0) {
                startNewWave();
            }
        }

        if (isSpawningWave) {
            spawnTimer -= delta;
            if (spawnTimer <= 0) {
                spawnTimer = currentSpawnInterval;

                SpawnDecision.UnitType chosenType = pickUnitType(true);
                if (chosenType != null) {
                    int cost = getCost(chosenType);
                    if (gold < cost) {
                        if (gold >= PAWN_COST) chosenType = SpawnDecision.UnitType.PAWN;
                        else if (gold >= TNT_COST) chosenType = SpawnDecision.UnitType.TNT;
                        else {
                            isSpawningWave = false;
                            return;
                        }
                    }

                    gold -= getCost(chosenType);
                    spawnEnemy(chosenType);
                    enemiesRemainingToSpawn--;
                }

                if (enemiesRemainingToSpawn <= 0) {
                    isSpawningWave = false;
                }
            }
        } else {
            // Wave mới khi còn <=2 quân địch trên sân
            if (wavesStarted < MAX_WAVES && getAliveEnemyCount() <= 2) {
                startNewWave();
            }
        }
    }

    private void startNewWave() {
        wavesStarted++;

        // --- Xác định level quân và vị trí trong block 6 wave ---
        // Level tăng mỗi 6 wave: wave 1-6 lv1, 7-12 lv2, 13+ lv3
        int newLevel = Math.min(3, 1 + (wavesStarted - 1) / 6);
        if (newLevel != currentEnemyLevel) {
            // Lên level mới → reset độ khó về đầu block
            currentEnemyLevel = newLevel;
            wavesInCurrentLevel = 1;
        } else {
            wavesInCurrentLevel++;
        }
        int difficultyIndex = wavesInCurrentLevel - 1; // 0..5

        // --- Số quân trong wave này ---
        int enemyCount = getEnemyCountForWave(wavesStarted);
        enemyCount += (level - 1) * 2;

        // --- Tính spawn interval (giảm dần theo difficultyIndex) ---
        currentSpawnInterval = Math.max(MIN_SPAWN_INTERVAL, SPAWN_INTERVAL_BASE - difficultyIndex * 0.25f);

        // --- Ngân sách vàng ---
        gold = getWaveGoldBudget(enemyCount, difficultyIndex);

        enemiesRemainingToSpawn = enemyCount;
        spawnTimer = currentSpawnInterval; // quân đầu tiên ra sau 1 interval

        System.out.println("===== WAVE " + wavesStarted + " (lvl " + currentEnemyLevel +
            ", block pos " + wavesInCurrentLevel + "/6" +
            ", enemies: " + enemyCount +
            ", interval: " + currentSpawnInterval +
            ", gold: " + gold + ") =====");
        isSpawningWave = true;
    }

    /**
     * Chọn loại quân, ưu tiên quân mạnh hơn khi độ khó trong block tăng.
     */
    private SpawnDecision.UnitType pickUnitType(boolean forceSpawn) {
        GameState state = analyzeGameState();
        state.gold = this.gold;

        float pawnWeight = personality.getPawnWeight();
        float warriorWeight = personality.getWarriorWeight();
        float tntWeight = personality.getTntWeight();

        // Độ khó tăng theo wavesInCurrentLevel (càng sâu trong block càng khó)
        float difficultyFactor = (wavesInCurrentLevel - 1) * 0.2f; // 0.0 ~ 1.0
        warriorWeight *= (1f + difficultyFactor);
        tntWeight *= (1f + difficultyFactor);
        pawnWeight *= Math.max(0.5f, 1f - difficultyFactor * 0.5f);

        float pawnScore = Math.max(0, evaluatePawn(state) * pawnWeight);
        float warriorScore = Math.max(0, evaluateWarrior(state) * warriorWeight);
        float tntScore = Math.max(0, evaluateTNT(state) * tntWeight);
        float saveScore = forceSpawn ? 0 : Math.max(0, evaluateSave(state) * personality.getSaveWeight());

        float totalScore = pawnScore + warriorScore + tntScore + saveScore;
        if (totalScore <= 0) {
            return (gold >= PAWN_COST) ? SpawnDecision.UnitType.PAWN : null;
        }

        float random = (float) Math.random() * totalScore;

        if (random < pawnScore) return SpawnDecision.UnitType.PAWN;
        else if (random < pawnScore + warriorScore) return SpawnDecision.UnitType.WARRIOR;
        else if (random < pawnScore + warriorScore + tntScore) return SpawnDecision.UnitType.TNT;
        else return null; // SAVE (chỉ xảy ra nếu forceSpawn = false)
    }

    private int getCost(SpawnDecision.UnitType type) {
        switch (type) {
            case PAWN: return PAWN_COST;
            case WARRIOR: return WARRIOR_COST;
            case TNT: return TNT_COST;
            default: return Integer.MAX_VALUE;
        }
    }

    /**
     * Tạo quân địch với level hiện tại.
     */
    private void spawnEnemy(SpawnDecision.UnitType type) {
        Vector2 castleEnemy = mapManager.getEnemyBaseSpawnPosition();
        if (castleEnemy == null) {
            System.err.println("Enemy base position not found!");
            castleEnemy = new Vector2(100, 100);
        }

        Enemy enemy = null;
        switch (type) {
            case PAWN:   enemy = new PawnHacHoa(); break;
            case WARRIOR: enemy = new WarriorHacHoa(); break;
            case TNT:    enemy = new TNT(); break;
            default: return;
        }

        // Gán level quân (1,2,3) – ảnh hưởng máu, sát thương
        enemy.setLevel(currentEnemyLevel);

        enemy.setPosition(castleEnemy);
        Array<Vector2> path = mapManager.getWaypoints(enemy);
        enemy.setPath(path);
        enemy.setState(CombatEntity.State.IDLE);
        entityManager.addEnemy(enemy);
    }

    private int getAliveEnemyCount() {
        int count = 0;
        SnapshotArray<BaseEntity> entities = entityManager.getAllEntities();
        for (int i = 0; i < entities.size; i++) {
            if (entities.get(i) instanceof Enemy) count++;
        }
        return count;
    }

    private GameState analyzeGameState() {
        GameState state = new GameState();
        state.myMainTowerHP = enemyMainTower.getHealth();
        state.playerMainTowerHP = playerMainTower.getHealth();

        SnapshotArray<BaseEntity> entities = entityManager.getAllEntities();
        for (int i = 0; i < entities.size; i++) {
            BaseEntity e = entities.get(i);
            if (e instanceof Soldier) state.playerSoldiers++;
            else if (e instanceof Enemy) state.myUnits++;
        }
        state.gold = this.gold;
        return state;
    }

    // ──── Các hàm đánh giá (giữ nguyên logic) ────
    private float evaluatePawn(GameState s) {
        float score = 15;
        if (s.playerSoldiers <= 3) score += 15;
        else if (s.playerSoldiers <= 6) score += 10;
        else score -= 5;

        if (s.playerMainTowerHP < 150) score += 20;
        if (s.gold < 20) score += 15;
        if (s.gold > 60) score += 5;
        return score;
    }

    private float evaluateWarrior(GameState s) {
        float score = 20;
        if (s.playerSoldiers >= 4) score += 20;
        if (s.myMainTowerHP < 500) score += 30;
        if (s.gold < 50) score -= 10;
        if (s.playerMainTowerHP < 100) score -= 10;
        return score;
    }

    private float evaluateTNT(GameState s) {
        float score = 10;
        if (s.playerSoldiers >= 3) score += 25;
        if (s.myMainTowerHP < 600) score += 30;
        if (s.playerMainTowerHP < 300) score += 20;
        if (s.gold < 25) score += 10;
        return score;
    }

    private float evaluateSave(GameState s) {
        float score = 5;
        if (s.playerSoldiers > 5) score += 20;
        if (s.myMainTowerHP > 300 && s.playerSoldiers < 3) score += 15;
        if (s.myMainTowerHP < 120) score -= 50;
        if (s.gold < 15) score += 15;
        else if (s.gold > 80) score -= 10;
        return score;
    }

    // ──── Getter hỗ trợ UI/kiểm tra ────
    public float getGold() { return gold; }
    public int getCurrentWave() { return wavesStarted; }
    public int getMaxWaves() { return MAX_WAVES; }
    public boolean isAllWavesFinished() { return wavesStarted >= MAX_WAVES && !isSpawningWave; }
    public int getCurrentEnemyLevel() { return currentEnemyLevel; }

    private static class GameState {
        int playerSoldiers;
        int myUnits;
        float myMainTowerHP;
        float playerMainTowerHP;
        float gold;
    }
}
