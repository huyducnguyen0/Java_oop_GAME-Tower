package com.hust.towerdefence.Model.Entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class cho GameEntity (base class)
 * Kiểm tra:
 * - Position management
 * - State management
 * - HP management
 * - Animation state tracking
 */
public class GameEntityTest {
    private GameEntity entity;

    @BeforeEach
    public void setUp() {
        // Create a concrete instance (using Enemy as example)
        entity = new Enemy(Enemy.EnemyType.BASIC, 100, 200);
    }

    @Test
    @DisplayName("Test: Entity position initialization")
    public void testPositionInitialization() {
        assertEquals(100f, entity.getX(), 0.01f, "X position should be 100");
        assertEquals(200f, entity.getY(), 0.01f, "Y position should be 200");
    }

    @Test
    @DisplayName("Test: Entity HP initialization")
    public void testHpInitialization() {
        assertTrue(entity.getHp() > 0, "HP should be positive");
        assertTrue(entity.getHp() <= entity.getMaxHp(), "HP should not exceed max HP");
    }

    @Test
    @DisplayName("Test: Set position")
    public void testSetPosition() {
        entity.setPosition(300, 400);
        assertEquals(300f, entity.getX(), 0.01f, "X position should be 300");
        assertEquals(400f, entity.getY(), 0.01f, "Y position should be 400");
    }

    @Test
    @DisplayName("Test: Set HP")
    public void testSetHp() {
        int maxHp = entity.getMaxHp();
        entity.setHp(50);
        assertEquals(50, entity.getHp(), "HP should be 50");
        assertTrue(entity.getHp() <= maxHp, "HP should not exceed max");
    }

    @Test
    @DisplayName("Test: HP cannot exceed max HP")
    public void testHpCap() {
        int maxHp = entity.getMaxHp();
        entity.setHp(maxHp + 100);
        assertTrue(entity.getHp() <= maxHp, "HP should not exceed max HP");
    }

    @Test
    @DisplayName("Test: State management")
    public void testStateManagement() {
        entity.setState(GameEntity.State.WALKING);
        assertEquals(GameEntity.State.WALKING, entity.getState(), "State should be WALKING");

        entity.setState(GameEntity.State.ATTACKING);
        assertEquals(GameEntity.State.ATTACKING, entity.getState(), "State should be ATTACKING");

        entity.setState(GameEntity.State.DEAD);
        assertEquals(GameEntity.State.DEAD, entity.getState(), "State should be DEAD");
    }

    @Test
    @DisplayName("Test: State time tracking")
    public void testStateTimeTracking() {
        entity.setState(GameEntity.State.ATTACKING);
        float initialStateTime = entity.getStateTime();

        // Simulate some time passing
        entity.update(0.5f);
        float newStateTime = entity.getStateTime();

        assertTrue(newStateTime > initialStateTime, "State time should increase");
    }

    @Test
    @DisplayName("Test: Update method")
    public void testUpdate() {
        // Should not throw exception
        entity.setState(GameEntity.State.WALKING);
        entity.update(0.016f);
        entity.update(0.032f);
        entity.update(0.5f);

        assertTrue(entity.getStateTime() > 0, "State time should be tracked");
    }

    @Test
    @DisplayName("Test: Entity death state")
    public void testDeathState() {
        entity.setState(GameEntity.State.DEAD);
        assertEquals(GameEntity.State.DEAD, entity.getState(), "Entity should be DEAD");

        entity.update(1.0f);
        assertEquals(GameEntity.State.DEAD, entity.getState(), "Entity should remain DEAD");
    }

    @Test
    @DisplayName("Test: HP set to zero")
    public void testZeroHp() {
        entity.setHp(0);
        assertEquals(0, entity.getHp(), "HP should be 0");
    }
}
