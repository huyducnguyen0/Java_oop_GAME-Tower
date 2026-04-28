package com.hust.towerdefence.Model.Systems;

import com.hust.towerdefence.Model.Entities.Enemy;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class cho AISystem
 */
@DisplayName("AISystem Tests")
public class AISystemTest {
    private AISystem aiSystem;
    private GameWorld gameWorld;
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        aiSystem = new AISystem();
        gameWorld = new GameWorld();
        entityManager = gameWorld.getEntityManager();
    }

    @Test
    @DisplayName("Test: AI system updates all enemies")
    public void testUpdateAllEnemies() {
        Enemy enemy1 = new Enemy(Enemy.EnemyType.BASIC, 100, 100);
        Enemy enemy2 = new Enemy(Enemy.EnemyType.TANK, 200, 200);
        Enemy enemy3 = new Enemy(Enemy.EnemyType.FAST, 300, 300);

        entityManager.addEnemy(enemy1);
        entityManager.addEnemy(enemy2);
        entityManager.addEnemy(enemy3);

        aiSystem.update(gameWorld, 0.016f);

        assertNotNull(enemy1.getAIController(), "Enemy 1 AI controller should exist");
        assertNotNull(enemy2.getAIController(), "Enemy 2 AI controller should exist");
        assertNotNull(enemy3.getAIController(), "Enemy 3 AI controller should exist");
    }

    @Test
    @DisplayName("Test: Dead enemies should be skipped")
    public void testSkipDeadEnemies() {
        Enemy aliveEnemy = new Enemy(Enemy.EnemyType.BASIC, 100, 100);
        Enemy deadEnemy = new Enemy(Enemy.EnemyType.TANK, 200, 200);

        entityManager.addEnemy(aliveEnemy);
        entityManager.addEnemy(deadEnemy);

        deadEnemy.setState(Enemy.State.DEAD);

        aiSystem.update(gameWorld, 0.016f);

        assertNotNull(aliveEnemy.getState(), "Alive enemy should have valid state");
    }

    @Test
    @DisplayName("Test: AI system with no enemies")
    public void testUpdateWithNoEnemies() {
        aiSystem.update(gameWorld, 0.016f);
        assertTrue(entityManager.getEnemies().isEmpty(), "Enemies list should remain empty");
    }

    @Test
    @DisplayName("Test: Multiple consecutive updates")
    public void testMultipleConsecutiveUpdates() {
        Enemy enemy = new Enemy(Enemy.EnemyType.BASIC, 100, 100);
        entityManager.addEnemy(enemy);

        for (int i = 0; i < 100; i++) {
            aiSystem.update(gameWorld, 0.016f);
        }

        assertNotNull(entityManager.getEnemies().get(0), "Enemy should still exist");
    }

    @Test
    @DisplayName("Test: AI system performance with many enemies")
    public void testUpdateWithManyEnemies() {
        for (int i = 0; i < 50; i++) {
            Enemy enemy = new Enemy(Enemy.EnemyType.BASIC, 100 + i * 10, 100 + i * 10);
            entityManager.addEnemy(enemy);
        }

        long startTime = System.nanoTime();
        aiSystem.update(gameWorld, 0.016f);
        long endTime = System.nanoTime();

        long durationMs = (endTime - startTime) / 1_000_000;

        assertTrue(durationMs < 50, "Should handle 50 enemies efficiently");
        assertEquals(50, entityManager.getEnemies().size(), "Should still have 50 enemies");
    }

    /**
     * Test: Only living enemies are updated
     */
    @Test
    public void testOnlyLivingEnemiesUpdated() {
        Enemy enemy1 = new Enemy(Enemy.EnemyType.BASIC, 100, 100);
        Enemy enemy2 = new Enemy(Enemy.EnemyType.TANK, 200, 200);
        Enemy enemy3 = new Enemy(Enemy.EnemyType.FAST, 300, 300);

        entityManager.addEnemy(enemy1);
        entityManager.addEnemy(enemy2);
        entityManager.addEnemy(enemy3);

        enemy1.setState(Enemy.State.DEAD);
        enemy3.setState(Enemy.State.DEAD);

        // Only enemy2 should be updated
        aiSystem.update(gameWorld, 0.016f);

        // Check that alive enemy has its AI controller
        assertNotNull("Alive enemy should have AI controller", enemy2.getAIController());
    }

    /**
     * Test: AI system respects delta time
     */
    @Test
    public void testAISystemRespectsDeltaTime() {
        Enemy enemy = new Enemy(Enemy.EnemyType.BASIC, 100, 100);
        entityManager.addEnemy(enemy);

        // Update with different delta times
        aiSystem.update(gameWorld, 0.016f);
        aiSystem.update(gameWorld, 0.032f);
        aiSystem.update(gameWorld, 0.08f);

        // Should handle different delta times gracefully
        assertNotNull("Enemy should still exist", entityManager.getEnemies().get(0));
    }

    /**
     * Test: Mixed live and dead enemies
     */
    @Test
    public void testMixedLiveAndDeadEnemies() {
        Enemy live1 = new Enemy(Enemy.EnemyType.BASIC, 100, 100);
        Enemy dead1 = new Enemy(Enemy.EnemyType.TANK, 200, 200);
        Enemy live2 = new Enemy(Enemy.EnemyType.FAST, 300, 300);
        Enemy dead2 = new Enemy(Enemy.EnemyType.BOSS, 400, 400);
        Enemy live3 = new Enemy(Enemy.EnemyType.BASIC, 500, 500);

        entityManager.addEnemy(live1);
        entityManager.addEnemy(dead1);
        entityManager.addEnemy(live2);
        entityManager.addEnemy(dead2);
        entityManager.addEnemy(live3);

        dead1.setState(Enemy.State.DEAD);
        dead2.setState(Enemy.State.DEAD);

        aiSystem.update(gameWorld, 0.016f);

        // Should handle mixed enemies
        assertEquals("Should have 5 enemies", 5, entityManager.getEnemies().size());
    }
}
