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
    private float gold;
    private float goldPerSecond;
    private float thinkTimer;
    private float thinkInterval;
    private final AIPersonality personality;
    private final EntityManager entityManager;
    private final MapManager mapManager;
    private final int level;
    private final MainTower playerMainTower;
    private final MainTower enemyMainTower;

    private static final int PAWN_COST = 8;
    private static final int WARRIOR_COST = 25;
    private static final int TNT_COST = 10;

    public AIController(int level, EntityManager entityManager, MapManager mapManager,
                        MainTower playerMainTower, MainTower enemyMainTower) {
        this.level = level;
        this.entityManager = entityManager;
        this.mapManager = mapManager;
        this.playerMainTower = playerMainTower;
        this.enemyMainTower = enemyMainTower;
        this.personality = new AIPersonality(System.nanoTime() + level);
        this.thinkTimer = 0f;

        switch (level) {
            case 1:
                gold = 80; goldPerSecond = 1.5f; thinkInterval = 4.0f; break;
            case 2:
                gold = 100; goldPerSecond = 2.5f; thinkInterval = 3.0f; break;
            case 3:
                gold = 120; goldPerSecond = 4.0f; thinkInterval = 2.0f; break;
            default:
                gold = 100; goldPerSecond = 2.0f; thinkInterval = 3.0f;
        }
    }

    public void update(float delta) {
        gold += goldPerSecond * delta;
        thinkTimer += delta;
        if (thinkTimer >= thinkInterval) {
            thinkTimer = 0f;
            SpawnDecision decision = makeDecision();
            executeDecision(decision);
        }
    }

    /**
     * Quyết định spawn dựa trên roulette wheel selection.
     * Mỗi loại quân có điểm số dương sẽ có cơ hội được chọn tỉ lệ với điểm số.
     */
    private SpawnDecision makeDecision() {
        GameState state = analyzeGameState();

        float pawnScore = Math.max(0, evaluatePawn(state) * personality.getPawnWeight());
        float warriorScore = Math.max(0, evaluateWarrior(state) * personality.getWarriorWeight());
        float tntScore = Math.max(0, evaluateTNT(state) * personality.getTntWeight());
        float saveScore = Math.max(0, evaluateSave(state) * personality.getSaveWeight());

        // In điểm để debug (có thể comment khi không cần)
        System.out.printf("Scores: Pawn=%.1f Warrior=%.1f TNT=%.1f Save=%.1f\n",
            pawnScore, warriorScore, tntScore, saveScore);

        float totalScore = pawnScore + warriorScore + tntScore + saveScore;
        if (totalScore <= 0) return SpawnDecision.none();

        // Roulette wheel selection
        float random = (float) Math.random() * totalScore;
        SpawnDecision.UnitType chosenType = null;
        float chosenScore = 0;

        if (random < pawnScore) {
            chosenType = SpawnDecision.UnitType.PAWN;
            chosenScore = pawnScore;
        } else if (random < pawnScore + warriorScore) {
            chosenType = SpawnDecision.UnitType.WARRIOR;
            chosenScore = warriorScore;
        } else if (random < pawnScore + warriorScore + tntScore) {
            chosenType = SpawnDecision.UnitType.TNT;
            chosenScore = tntScore;
        } else {
            // Save (không spawn)
            return SpawnDecision.none();
        }

        System.out.println("AI chose: " + chosenType + " with score " + chosenScore);

        // Tạo quyết định với loại đã chọn
        return createDecision(chosenType, chosenScore);
    }

    private SpawnDecision createDecision(SpawnDecision.UnitType type, float score) {
        int unitCost = getCost(type);
        int desired = scoreToCount(score);
        int affordable = (int)(gold / unitCost);
        int count = Math.min(desired, affordable);
        if (count > 0) {
            gold -= count * unitCost;
            return new SpawnDecision(type, count);
        }
        return SpawnDecision.none();
    }

    private int getCost(SpawnDecision.UnitType type) {
        switch (type) {
            case PAWN: return PAWN_COST;
            case WARRIOR: return WARRIOR_COST;
            case TNT: return TNT_COST;
            default: return 0;
        }
    }

    private GameState analyzeGameState() {
        GameState state = new GameState();
        state.myMainTowerHP = enemyMainTower.getHealth();
        state.playerMainTowerHP = playerMainTower.getHealth();

        SnapshotArray<BaseEntity> entities = entityManager.getAllEntities();
        for (int i = 0; i < entities.size; i++) {
            BaseEntity e = entities.get(i);
            if (e instanceof Soldier) state.playerSoldiers++;
            else if (e instanceof Enemy) state.myUnits++; // quân của AI
        }
        state.gold = this.gold;
        return state;
    }

    // ──── Các hàm đánh giá (có thể điều chỉnh) ────

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

    private int scoreToCount(float score) {
        if (score > 90) return 5;
        if (score > 70) return 4;
        if (score > 50) return 3;
        if (score > 30) return 2;
        return 1;
    }

    private void executeDecision(SpawnDecision decision) {
        if (!decision.shouldSpawn()) return;

        Vector2 castleEnemy = mapManager.getEnemyBaseSpawnPosition();
        if (castleEnemy == null) {
            System.err.println("Enemy base position not found!");
            castleEnemy = new Vector2(100, 100);
        }

        for (int i = 0; i < decision.getCount(); i++) {
            Enemy enemy = null;
            switch (decision.getType()) {
                case PAWN:   enemy = new PawnHacHoa(); break;
                case WARRIOR: enemy = new WarriorHacHoa(); break;
                case TNT:    enemy = new TNT(); break;
                default: continue;
            }

            enemy.setPosition(castleEnemy);
            Array<Vector2> path = mapManager.getWaypoints(enemy);
            enemy.setPath(path);
            enemy.setState(CombatEntity.State.IDLE);
            entityManager.addEnemy(enemy);
        }
    }

    private static class GameState {
        int playerSoldiers;   // lính người chơi
        int myUnits;          // quân AI hiện có trên sân
        float myMainTowerHP;
        float playerMainTowerHP;
        float gold;
    }

    public float getGold() { return gold; }
}
