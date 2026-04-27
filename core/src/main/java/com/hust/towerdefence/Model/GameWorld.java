package com.hust.towerdefence.Model;

import com.hust.towerdefence.Model.Managers.EntityManager;
import com.hust.towerdefence.Model.Entities.Enemy;
import java.util.Random;

public class GameWorld {
    // World Dimensions
    public final float WORLD_WIDTH = 2000;
    public final float WORLD_HEIGHT = 2000;

    // Key Locations
    public final float MINE_X = 200;
    public final float MINE_Y = 1800;
    public final float BASE_X = 1000;
    public final float BASE_Y = 1000;

    // Game States
    public enum GameState { PLAYING, PAUSED, GAME_OVER, WAVE_COMPLETE }

    private EntityManager entityManager;
    private int wave = 1;
    private float waveTimer = 0;
    private float waveDuration = 30f;  // Mỗi sóng 30 giây
    private float timeBetweenWaves = 5f;  // 5 giây giữa các sóng
    private float waveStartTimer = 0;

    private GameState gameState = GameState.PLAYING;
    private int score = 0;
    private boolean waveInProgress = false;
    private int enemiesPerWave = 5;
    private Random random = new Random();

    public GameWorld() {
        this.entityManager = new EntityManager();
    }

    public void update(float delta) {
        if (gameState != GameState.PLAYING) return;

        // === Cập nhật wave timer ===
        waveTimer += delta;
        waveStartTimer += delta;

        // === Kiểm tra kết thúc game ===
        checkGameOver();

        // === Kiểm tra kết thúc sóng ===
        if (waveInProgress) {
            // Nếu tất cả enemies chết và đã qua thời gian sóng
            if (entityManager.getEnemies().isEmpty() && waveTimer > waveDuration) {
                completeWave();
            }
        } else {
            // Đợi 5 giây rồi spawn sóng mới
            if (waveStartTimer > timeBetweenWaves) {
                spawnWave();
            }
        }
    }

    /**
     * Spawn enemies cho sóng hiện tại
     */
    private void spawnWave() {
        waveInProgress = true;
        waveTimer = 0;

        int numEnemies = enemiesPerWave + wave;  // Tăng số lượng theo sóng

        // Spawn enemies từ các hướng khác nhau
        for (int i = 0; i < numEnemies; i++) {
            float x = getRandomSpawnX();
            float y = getRandomSpawnY();

            // Chọn loại enemy ngẫu nhiên dựa trên sóng
            Enemy.EnemyType enemyType = getRandomEnemyType();
            Enemy enemy = new Enemy(enemyType, x, y);

            // Scale stats theo sóng
            scaleEnemyStats(enemy, wave);

            entityManager.addEnemy(enemy);
        }
    }

    /**
     * Chọn loại enemy ngẫu nhiên
     */
    private Enemy.EnemyType getRandomEnemyType() {
        int rand = random.nextInt(100);
        if (wave >= 10 && rand < 5) return Enemy.EnemyType.BOSS;      // 5% boss từ wave 10
        if (wave >= 5 && rand < 20) return Enemy.EnemyType.TANK;      // 20% tank từ wave 5
        if (rand < 40) return Enemy.EnemyType.FAST;                   // 40% fast
        return Enemy.EnemyType.BASIC;                                 // 35% basic
    }

    /**
     * Scale stats enemy theo sóng
     */
    private void scaleEnemyStats(Enemy enemy, int wave) {
        // Tăng HP và ATK theo sóng
        int extraHp = (wave - 1) * 20;
        int extraAtk = (wave - 1) * 3;

        enemy.setHp(enemy.getHp() + extraHp);
        enemy.setMaxHp(enemy.getMaxHp() + extraHp);
        enemy.setAtk(enemy.getAtk() + extraAtk);  // Đã thêm setAtk
    }

    /**
     * Kết thúc sóng hiện tại
     */
    private void completeWave() {
        waveInProgress = false;
        wave++;
        waveStartTimer = 0;

        // Cộng bonus points
        addScore(100 * wave);

        // Cộng vàng thưởng
        entityManager.setGold(entityManager.getGold() + 50 * wave);
    }

    /**
     * Lấy vị trí spawn ngẫu nhiên từ các cạnh bản đồ
     */
    private float getRandomSpawnX() {
        int side = random.nextInt(4);  // 0-3: top, bottom, left, right
        switch(side) {
            case 0: return random.nextFloat() * WORLD_WIDTH;  // Top
            case 1: return random.nextFloat() * WORLD_WIDTH;  // Bottom
            case 2: return 0;                                  // Left
            default: return WORLD_WIDTH;                       // Right
        }
    }

    private float getRandomSpawnY() {
        int side = random.nextInt(4);
        switch(side) {
            case 0: return 0;                                  // Top
            case 1: return WORLD_HEIGHT;                       // Bottom
            case 2: return random.nextFloat() * WORLD_HEIGHT;  // Left
            default: return random.nextFloat() * WORLD_HEIGHT; // Right
        }
    }

    /**
     * Kiểm tra điều kiện kết thúc game
     */
    private void checkGameOver() {
        // Nếu thành bị yếu
        if (entityManager.getMainBuildingHp() <= 0) {
            gameState = GameState.GAME_OVER;
        }
    }

    /**
     * Cộng điểm
     */
    public void addScore(int points) {
        this.score += points;
    }

    // === Getters & Setters ===
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public int getWave() {
        return wave;
    }

    public void setWave(int wave) {
        this.wave = wave;
    }

    public float getWaveTimer() {
        return waveTimer;
    }

    public void setWaveTimer(float waveTimer) {
        this.waveTimer = waveTimer;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState state) {
        this.gameState = state;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isWaveInProgress() {
        return waveInProgress;
    }

    public boolean isGameOver() {
        return gameState == GameState.GAME_OVER;
    }

    public boolean isPaused() {
        return gameState == GameState.PAUSED;
    }

    public void pause() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
        }
    }

    public void resume() {
        if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
        }
    }
}
