package com.hust.towerdefence.Model.Entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class cho Enemy
 * Kiểm tra:
 * - Enemy initialization theo loại (BASIC, TANK, FAST, BOSS)
 * - Stats scaling theo loại
 * - AI Controller integration
 * - Movement and combat capabilities
 */
public class EnemyTest {
    private Enemy basicEnemy;
    private Enemy tankEnemy;
    private Enemy fastEnemy;
    private Enemy bossEnemy;

    @BeforeEach
    public void setUp() {
        basicEnemy = new Enemy(Enemy.EnemyType.BASIC, 100, 100);
        tankEnemy = new Enemy(Enemy.EnemyType.TANK, 200, 200);
        fastEnemy = new Enemy(Enemy.EnemyType.FAST, 300, 300);
        bossEnemy = new Enemy(Enemy.EnemyType.BOSS, 400, 400);
    }

    /**
     * Test: Basic enemy initialization
     */
    @Test
    public void testBasicEnemyInitialization() {
        assertEquals(Enemy.EnemyType.BASIC, basicEnemy.getEnemyType(), "Type should be BASIC");
        assertTrue(basicEnemy.getHp() > 0, "HP should be positive");
    }

    /**
     * Test: Tank enemy has more HP
     */
    @Test
    public void testTankEnemyStats() {
        assertEquals("Type should be TANK", Enemy.EnemyType.TANK, tankEnemy.getEnemyType());

        // Tank should have higher HP than basic
        assertTrue("Tank HP should be higher than basic",
                tankEnemy.getMaxHp() >= basicEnemy.getMaxHp());
    }

    /**
     * Test: Fast enemy has higher speed
     */
    @Test
    public void testFastEnemyStats() {
        assertEquals("Type should be FAST", Enemy.EnemyType.FAST, fastEnemy.getType());

        // Fast should have higher speed than basic
        assertTrue("Fast speed should be higher than basic",
                fastEnemy.getSpd() > basicEnemy.getSpd());
    }

    /**
     * Test: Boss enemy is strongest
     */
    @Test
    public void testBossEnemyStats() {
        assertEquals("Type should be BOSS", Enemy.EnemyType.BOSS, bossEnemy.getType());

        // Boss should have highest HP and ATK
        assertTrue("Boss HP should be high", bossEnemy.getMaxHp() > basicEnemy.getMaxHp());
        assertTrue("Boss ATK should be high", bossEnemy.getAtk() > basicEnemy.getAtk());
    }

    /**
     * Test: AI Controller is initialized
     */
    @Test
    public void testAIControllerInitialization() {
        assertNotNull("AI Controller should be initialized", basicEnemy.getAIController());
        assertNotNull("Tank should have AI Controller", tankEnemy.getAIController());
        assertNotNull("Fast should have AI Controller", fastEnemy.getAIController());
        assertNotNull("Boss should have AI Controller", bossEnemy.getAIController());
    }

    /**
     * Test: Enemy movement capabilities
     */
    @Test
    public void testEnemyMovement() {
        float initialX = basicEnemy.getX();
        float initialY = basicEnemy.getY();

        // Set linear velocity
        basicEnemy.getLinearVelocity().set(10, 10);
        basicEnemy.setPosition(initialX + 20, initialY + 20);

        float newX = basicEnemy.getX();
        float newY = basicEnemy.getY();

        assertNotEquals("X should change", initialX, newX, 0.01f);
        assertNotEquals("Y should change", initialY, newY, 0.01f);
    }

    /**
     * Test: Max linear speed
     */
    @Test
    public void testMaxLinearSpeed() {
        float maxSpeed = basicEnemy.getMaxLinearSpeed();
        assertTrue("Max speed should be positive", maxSpeed > 0);
    }

    /**
     * Test: Different enemy types can have different rewards
     */
    @Test
    public void testEnemyTypeVariation() {
        // All enemies should have different characteristics
        assertNotNull("Basic enemy should exist", basicEnemy);
        assertNotNull("Tank enemy should exist", tankEnemy);
        assertNotNull("Fast enemy should exist", fastEnemy);
        assertNotNull("Boss enemy should exist", bossEnemy);

        // They should be different types
        assertNotEquals("Tank should differ from Basic", basicEnemy.getEnemyType(), tankEnemy.getEnemyType());
        assertNotEquals("Fast should differ from Tank", tankEnemy.getEnemyType(), fastEnemy.getEnemyType());
    }

    /**
     * Test: Enemy inherits from GameEntity
     */
    @Test
    public void testEnemyInheritsGameEntity() {
        assertTrue("Enemy should be instance of GameEntity", basicEnemy instanceof GameEntity);

        // Should have base entity properties
        assertTrue("Should have position", basicEnemy.getX() >= 0);
        assertTrue("Should have HP", basicEnemy.getHp() > 0);
    }

    /**
     * Test: Enemy can take damage
     */
    @Test
    public void testEnemyTakeDamage() {
        int initialHp = basicEnemy.getHp();
        basicEnemy.setHp(initialHp - 10);

        assertEquals("HP should decrease by 10", initialHp - 10, basicEnemy.getHp());
    }

    /**
     * Test: Enemy state transitions
     */
    @Test
    public void testEnemyStateTransitions() {
        basicEnemy.setState(GameEntity.State.WALKING);
        assertEquals("Should be WALKING", GameEntity.State.WALKING, basicEnemy.getState());

        basicEnemy.setState(GameEntity.State.ATTACKING);
        assertEquals("Should be ATTACKING", GameEntity.State.ATTACKING, basicEnemy.getState());

        basicEnemy.setState(GameEntity.State.DEAD);
        assertEquals("Should be DEAD", GameEntity.State.DEAD, basicEnemy.getState());
    }

    /**
     * Test: Position initialization
     */
    @Test
    public void testPositionInitialization() {
        assertEquals("Basic X position", 100f, basicEnemy.getX(), 0.01f);
        assertEquals("Basic Y position", 100f, basicEnemy.getY(), 0.01f);

        assertEquals("Tank X position", 200f, tankEnemy.getX(), 0.01f);
        assertEquals("Tank Y position", 200f, tankEnemy.getY(), 0.01f);
    }

    /**
     * Test: All enemy types have positive stats
     */
    @Test
    public void testAllEnemyTypesHavePositiveStats() {
        Enemy[] enemies = {basicEnemy, tankEnemy, fastEnemy, bossEnemy};

        for (Enemy enemy : enemies) {
            assertTrue("HP should be positive", enemy.getHp() > 0);
            assertTrue("Max HP should be positive", enemy.getMaxHp() > 0);
            assertTrue("ATK should be positive", enemy.getAtk() > 0);
            assertTrue("SPD should be positive", enemy.getSpd() > 0);
        }
    }

    /**
     * Test: Enemy update
     */
    @Test
    public void testEnemyUpdate() {
        basicEnemy.setState(GameEntity.State.WALKING);
        float initialStateTime = basicEnemy.getStateTime();

        basicEnemy.update(0.1f);

        float newStateTime = basicEnemy.getStateTime();
        assertTrue("State time should increase", newStateTime > initialStateTime);
    }
}
