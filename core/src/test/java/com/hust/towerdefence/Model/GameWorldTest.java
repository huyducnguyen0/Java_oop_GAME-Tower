package com.hust.towerdefence.Model;

import com.hust.towerdefence.Model.Entities.Enemy;
import com.hust.towerdefence.Model.Managers.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class cho GameWorld
 */
@DisplayName("GameWorld Tests")
public class GameWorldTest {
    private GameWorld gameWorld;
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        gameWorld = new GameWorld();
        entityManager = gameWorld.getEntityManager();
    }

    @Test
    @DisplayName("Test: World initialization")
    public void testWorldInitialization() {
        assertNotNull(gameWorld, "Game world should be initialized");
        assertNotNull(entityManager, "Entity manager should exist");
        assertEquals(1, gameWorld.getWave(), "Initial wave should be 1");
        assertEquals(0, gameWorld.getScore(), "Initial score should be 0");
    }

    @Test
    @DisplayName("Test: Initial game state")
    public void testInitialGameState() {
        assertEquals(GameWorld.GameState.PLAYING, gameWorld.getGameState(), "Initial state should be PLAYING");
        assertFalse(gameWorld.isGameOver(), "Game should not be over");
        assertFalse(gameWorld.isPaused(), "Game should not be paused");
    }

    @Test
    @DisplayName("Test: World dimensions")
    public void testWorldDimensions() {
        assertEquals(2000f, gameWorld.WORLD_WIDTH, 0.01f, "World width should be 2000");
        assertEquals(2000f, gameWorld.WORLD_HEIGHT, 0.01f, "World height should be 2000");
    }

    @Test
    @DisplayName("Test: Key locations exist")
    public void testKeyLocations() {
        assertTrue(gameWorld.BASE_X > 0, "Base X should be positive");
        assertTrue(gameWorld.BASE_Y > 0, "Base Y should be positive");
        assertTrue(gameWorld.MINE_X > 0, "Mine X should be positive");
        assertTrue(gameWorld.MINE_Y > 0, "Mine Y should be positive");
    }

    @Test
    @DisplayName("Test: Wave progression")
    public void testWaveProgression() {
        int initialWave = gameWorld.getWave();

        gameWorld.setWave(2);
        assertEquals(2, gameWorld.getWave(), "Wave should be 2");

        gameWorld.setWave(5);
        assertEquals(5, gameWorld.getWave(), "Wave should be 5");
    }

    @Test
    @DisplayName("Test: Score system")
    public void testScoreSystem() {
        assertEquals(0, gameWorld.getScore(), "Initial score should be 0");

        gameWorld.addScore(100);
        assertEquals(100, gameWorld.getScore(), "Score should be 100");

        gameWorld.addScore(50);
        assertEquals(150, gameWorld.getScore(), "Score should be 150");
    }

    @Test
    @DisplayName("Test: Pause and resume")
    public void testPauseAndResume() {
        assertEquals(GameWorld.GameState.PLAYING, gameWorld.getGameState(), "Initial state should be PLAYING");

        gameWorld.pause();
        assertEquals(GameWorld.GameState.PAUSED, gameWorld.getGameState(), "State should be PAUSED");
        assertTrue(gameWorld.isPaused(), "Should be paused");

        gameWorld.resume();
        assertEquals(GameWorld.GameState.PLAYING, gameWorld.getGameState(), "State should return to PLAYING");
        assertFalse(gameWorld.isPaused(), "Should not be paused");
    }

    @Test
    @DisplayName("Test: Game state management")
    public void testGameStateManagement() {
        gameWorld.setGameState(GameWorld.GameState.GAME_OVER);
        assertEquals(GameWorld.GameState.GAME_OVER, gameWorld.getGameState(), "State should be GAME_OVER");
        assertTrue(gameWorld.isGameOver(), "Should be game over");

        gameWorld.setGameState(GameWorld.GameState.PLAYING);
        assertFalse(gameWorld.isGameOver(), "Should not be game over");
    }

    @Test
    @DisplayName("Test: Building damage leads to game over")
    public void testBuildingDamageGameOver() {
        int initialBuildingHp = entityManager.getMainBuildingHp();
        assertTrue(initialBuildingHp > 0, "Building should have HP");

        entityManager.setMainBuildingHp(0);
        gameWorld.update(0.1f);

        assertTrue(gameWorld.isGameOver(), "Should trigger game over");
    }

    @Test
    @DisplayName("Test: Update with playing state")
    public void testUpdateWithPlayingState() {
        gameWorld.setGameState(GameWorld.GameState.PLAYING);

        gameWorld.update(0.016f);
        gameWorld.update(0.032f);
        gameWorld.update(0.1f);

        assertNotNull(gameWorld, "Game world should still exist");
    }

    @Test
    @DisplayName("Test: Update while paused")
    public void testUpdateWhilePaused() {
        gameWorld.pause();

        float initialWaveTimer = gameWorld.getWaveTimer();

        gameWorld.update(0.1f);
        gameWorld.update(0.1f);

        float finalWaveTimer = gameWorld.getWaveTimer();
        assertEquals(initialWaveTimer, finalWaveTimer, 0.01f, "Wave timer should not advance while paused");
    }

    @Test
    @DisplayName("Test: Update while game is over")
    public void testUpdateWhileGameOver() {
        gameWorld.setGameState(GameWorld.GameState.GAME_OVER);

        float initialWave = gameWorld.getWave();

        gameWorld.update(10f);

        assertEquals(initialWave, gameWorld.getWave(), "Wave should not change");
    }

    @Test
    @DisplayName("Test: Wave timer")
    public void testWaveTimer() {
        gameWorld.setWaveTimer(0);
        assertEquals(0, gameWorld.getWaveTimer(), 0.01f, "Wave timer should be 0");

        gameWorld.setWaveTimer(5.5f);
        assertEquals(5.5f, gameWorld.getWaveTimer(), 0.01f, "Wave timer should be 5.5");
    }

    @Test
    @DisplayName("Test: Multiple updates")
    public void testMultipleUpdates() {
        gameWorld.setGameState(GameWorld.GameState.PLAYING);

        for (int i = 0; i < 100; i++) {
            gameWorld.update(0.016f);
        }

        assertNotNull(gameWorld, "Game world should still exist");
    }

    @Test
    @DisplayName("Test: Entity manager reference")
    public void testEntityManagerReference() {
        EntityManager em = gameWorld.getEntityManager();
        assertNotNull(em, "Entity manager should not be null");
        assertEquals(entityManager, em, "Should be same entity manager");
    }

    @Test
    @DisplayName("Test: Cannot pause when not playing")
    public void testCannotPauseWhenNotPlaying() {
        gameWorld.setGameState(GameWorld.GameState.GAME_OVER);

        gameWorld.pause();

        assertEquals(GameWorld.GameState.GAME_OVER, gameWorld.getGameState(), "State should remain GAME_OVER");
    }

    @Test
    @DisplayName("Test: Cannot resume when not paused")
    public void testCannotResumeWhenNotPaused() {
        gameWorld.setGameState(GameWorld.GameState.GAME_OVER);

        gameWorld.resume();

        assertEquals(GameWorld.GameState.GAME_OVER, gameWorld.getGameState(), "State should remain GAME_OVER");
    }

    @Test
    @DisplayName("Test: Score management")
    public void testScoreManagement() {
        gameWorld.setScore(100);
        assertEquals(100, gameWorld.getScore(), "Score should be 100");

        gameWorld.setScore(0);
        assertEquals(0, gameWorld.getScore(), "Score should be 0");

        gameWorld.setScore(1000);
        assertEquals(1000, gameWorld.getScore(), "Score should be 1000");
    }

    @Test
    @DisplayName("Test: Game state transitions")
    public void testGameStateTransitions() {
        // PLAYING -> PAUSED
        gameWorld.setGameState(GameWorld.GameState.PLAYING);
        gameWorld.pause();
        assertTrue(gameWorld.isPaused(), "Should be paused");

        // PAUSED -> PLAYING
        gameWorld.resume();
        assertFalse(gameWorld.isPaused(), "Should not be paused");

        // PLAYING -> GAME_OVER
        gameWorld.setGameState(GameWorld.GameState.PLAYING);
        entityManager.setMainBuildingHp(0);
        gameWorld.update(0.1f);
        assertTrue(gameWorld.isGameOver(), "Should be game over");
    }
}
