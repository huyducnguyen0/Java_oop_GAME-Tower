package com.hust.towerdefence.Model.Managers;

import com.hust.towerdefence.Model.Entities.Enemy;
import com.hust.towerdefence.Model.Entities.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class cho EntityManager
 */
@DisplayName("EntityManager Tests")
public class EntityManagerTest {
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        entityManager = new EntityManager();
    }

    @Test
    @DisplayName("Test: Add unit")
    public void testAddUnit() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);
        entityManager.addUnit(unit);

        List<Unit> units = entityManager.getUnits();
        assertEquals(1, units.size(), "Should contain 1 unit");
        assertTrue(units.contains(unit), "Should contain the added unit");
    }

    @Test
    @DisplayName("Test: Add enemy")
    public void testAddEnemy() {
        Enemy enemy = new Enemy(Enemy.EnemyType.BASIC, 100, 100);
        entityManager.addEnemy(enemy);

        List<Enemy> enemies = entityManager.getEnemies();
        assertEquals(1, enemies.size(), "Should contain 1 enemy");
        assertTrue(enemies.contains(enemy), "Should contain the added enemy");
    }

    @Test
    @DisplayName("Test: Add multiple units")
    public void testAddMultipleUnits() {
        Unit unit1 = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);
        Unit unit2 = new Unit(Unit.UnitType.ARCHER, 200, 200, 100, 15, 6, 100);
        Unit unit3 = new Unit(Unit.UnitType.MINER, 300, 300, 100, 5, 3, 50);

        entityManager.addUnit(unit1);
        entityManager.addUnit(unit2);
        entityManager.addUnit(unit3);

        assertEquals(3, entityManager.getUnits().size(), "Should contain 3 units");
    }

    @Test
    @DisplayName("Test: Remove dead units on update")
    public void testRemoveDeadUnitsOnUpdate() {
        Unit aliveUnit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);
        Unit deadUnit = new Unit(Unit.UnitType.ARCHER, 200, 200, 100, 15, 6, 100);

        entityManager.addUnit(aliveUnit);
        entityManager.addUnit(deadUnit);

        deadUnit.setState(Unit.State.DEAD);

        entityManager.update(0.016f);

        List<Unit> units = entityManager.getUnits();
        assertEquals(1, units.size(), "Should contain 1 unit (dead removed)");
        assertTrue(units.contains(aliveUnit), "Should contain alive unit");
        assertFalse(units.contains(deadUnit), "Should not contain dead unit");
    }

    @Test
    @DisplayName("Test: Remove dead enemies on update")
    public void testRemoveDeadEnemiesOnUpdate() {
        Enemy aliveEnemy = new Enemy(Enemy.EnemyType.BASIC, 100, 100);
        Enemy deadEnemy = new Enemy(Enemy.EnemyType.TANK, 200, 200);

        entityManager.addEnemy(aliveEnemy);
        entityManager.addEnemy(deadEnemy);

        deadEnemy.setState(Enemy.State.DEAD);

        entityManager.update(0.016f);

        List<Enemy> enemies = entityManager.getEnemies();
        assertEquals(1, enemies.size(), "Should contain 1 enemy (dead removed)");
        assertTrue(enemies.contains(aliveEnemy), "Should contain alive enemy");
        assertFalse(enemies.contains(deadEnemy), "Should not contain dead enemy");
    }

    @Test
    @DisplayName("Test: Initial gold")
    public void testInitialGold() {
        int gold = entityManager.getGold();
        assertTrue(gold > 0, "Initial gold should be positive");
    }

    @Test
    @DisplayName("Test: Set gold")
    public void testSetGold() {
        entityManager.setGold(500);
        assertEquals(500, entityManager.getGold(), "Gold should be 500");

        entityManager.setGold(1000);
        assertEquals(1000, entityManager.getGold(), "Gold should be 1000");
    }

    @Test
    @DisplayName("Test: Building HP management")
    public void testBuildingHpManagement() {
        entityManager.setMainBuildingHp(500);
        assertEquals(500, entityManager.getMainBuildingHp(), "Building HP should be 500");

        entityManager.setMainBuildingHp(0);
        assertEquals(0, entityManager.getMainBuildingHp(), "Building HP should be 0");
    }

    @Test
    @DisplayName("Test: Get units returns correct list")
    public void testGetUnitsReturnsCorrectList() {
        Unit unit1 = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);
        Unit unit2 = new Unit(Unit.UnitType.ARCHER, 200, 200, 100, 15, 6, 100);

        entityManager.addUnit(unit1);
        entityManager.addUnit(unit2);

        List<Unit> units = entityManager.getUnits();
        assertEquals(2, units.size(), "Should return correct number of units");
        assertTrue(units.contains(unit1) && units.contains(unit2), "Should contain both units");
    }

    @Test
    @DisplayName("Test: Update calls entity updates")
    public void testUpdateCallsEntityUpdate() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);
        unit.setState(Unit.State.WALKING);

        entityManager.addUnit(unit);

        float initialTime = unit.getStateTime();
        entityManager.update(0.1f);
        float newTime = unit.getStateTime();

        assertTrue(newTime > initialTime, "Unit state time should increase");
    }

    @Test
    @DisplayName("Test: Empty entity lists")
    public void testEmptyEntityLists() {
        assertTrue(entityManager.getUnits().isEmpty(), "Units list should be empty");
        assertTrue(entityManager.getEnemies().isEmpty(), "Enemies list should be empty");
    }
}

