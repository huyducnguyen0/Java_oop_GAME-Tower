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
 * Kiểm tra:
 * - Adding/removing units and enemies
 * - Gold management
 * - Building HP management
 * - Entity update and cleanup
 */
public class EntityManagerTest {
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        entityManager = new EntityManager();
    }

    @Test
    @DisplayName("Test: Add unit")
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);
        entityManager.addUnit(unit);

        List<Unit> units = entityManager.getUnits();
        assertEquals("Should contain 1 unit", 1, units.size());
        assertTrue("Should contain the added unit", units.contains(unit));
    }

    @Test
    @DisplayName("Test: Add enemy")
    public void testAddEnemy() {
        Enemy enemy = new Enemy(Enemy.EnemyType.BASIC, 100, 100);
        entityManager.addEnemy(enemy);

        List<Enemy> enemies = entityManager.getEnemies();
        assertEquals("Should contain 1 enemy", 1, enemies.size());
        assertTrue("Should contain the added enemy", enemies.contains(enemy));
    }

    /**
     * Test: Add multiple units
     */
    @Test
    public void testAddMultipleUnits() {
        Unit unit1 = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);
        Unit unit2 = new Unit(Unit.UnitType.ARCHER, 200, 200, 100, 15, 6, 100);
        Unit unit3 = new Unit(Unit.UnitType.MINER, 300, 300, 100, 5, 3, 50);

        entityManager.addUnit(unit1);
        entityManager.addUnit(unit2);
        entityManager.addUnit(unit3);

        assertEquals("Should contain 3 units", 3, entityManager.getUnits().size());
    }

    /**
     * Test: Add multiple enemies
     */
    @Test
    public void testAddMultipleEnemies() {
        Enemy enemy1 = new Enemy(Enemy.EnemyType.BASIC, 100, 100);
        Enemy enemy2 = new Enemy(Enemy.EnemyType.TANK, 200, 200);
        Enemy enemy3 = new Enemy(Enemy.EnemyType.FAST, 300, 300);

        entityManager.addEnemy(enemy1);
        entityManager.addEnemy(enemy2);
        entityManager.addEnemy(enemy3);

        assertEquals("Should contain 3 enemies", 3, entityManager.getEnemies().size());
    }

    /**
     * Test: Remove dead units on update
     */
    @Test
    public void testRemoveDeadUnitsOnUpdate() {
        Unit aliveUnit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);
        Unit deadUnit = new Unit(Unit.UnitType.ARCHER, 200, 200, 100, 15, 6, 100);

        entityManager.addUnit(aliveUnit);
        entityManager.addUnit(deadUnit);

        deadUnit.setState(Unit.State.DEAD);

        entityManager.update(0.016f);

        List<Unit> units = entityManager.getUnits();
        assertEquals("Should contain 1 unit (dead removed)", 1, units.size());
        assertTrue("Should contain alive unit", units.contains(aliveUnit));
        assertFalse("Should not contain dead unit", units.contains(deadUnit));
    }

    /**
     * Test: Remove dead enemies on update
     */
    @Test
    public void testRemoveDeadEnemiesOnUpdate() {
        Enemy aliveEnemy = new Enemy(Enemy.EnemyType.BASIC, 100, 100);
        Enemy deadEnemy = new Enemy(Enemy.EnemyType.TANK, 200, 200);

        entityManager.addEnemy(aliveEnemy);
        entityManager.addEnemy(deadEnemy);

        deadEnemy.setState(Enemy.State.DEAD);

        entityManager.update(0.016f);

        List<Enemy> enemies = entityManager.getEnemies();
        assertEquals("Should contain 1 enemy (dead removed)", 1, enemies.size());
        assertTrue("Should contain alive enemy", enemies.contains(aliveEnemy));
        assertFalse("Should not contain dead enemy", enemies.contains(deadEnemy));
    }

    /**
     * Test: Gold management - initial value
     */
    @Test
    public void testInitialGold() {
        int gold = entityManager.getGold();
        assertTrue("Initial gold should be positive", gold > 0);
    }

    /**
     * Test: Set gold
     */
    @Test
    public void testSetGold() {
        entityManager.setGold(500);
        assertEquals("Gold should be 500", 500, entityManager.getGold());

        entityManager.setGold(1000);
        assertEquals("Gold should be 1000", 1000, entityManager.getGold());
    }

    /**
     * Test: Gold can go to zero
     */
    @Test
    public void testGoldCanBeZero() {
        entityManager.setGold(0);
        assertEquals("Gold should be 0", 0, entityManager.getGold());
    }

    /**
     * Test: Building HP management - initial value
     */
    @Test
    public void testInitialBuildingHp() {
        int hp = entityManager.getMainBuildingHp();
        assertTrue("Initial building HP should be positive", hp > 0);
    }

    /**
     * Test: Set building HP
     */
    @Test
    public void testSetBuildingHp() {
        entityManager.setMainBuildingHp(500);
        assertEquals("Building HP should be 500", 500, entityManager.getMainBuildingHp());
    }

    /**
     * Test: Building HP can be damaged to zero
     */
    @Test
    public void testBuildingHpToZero() {
        entityManager.setMainBuildingHp(0);
        assertEquals("Building HP should be 0", 0, entityManager.getMainBuildingHp());
    }

    /**
     * Test: Get units returns correct list
     */
    @Test
    public void testGetUnitsReturnsCorrectList() {
        Unit unit1 = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);
        Unit unit2 = new Unit(Unit.UnitType.ARCHER, 200, 200, 100, 15, 6, 100);

        entityManager.addUnit(unit1);
        entityManager.addUnit(unit2);

        List<Unit> units = entityManager.getUnits();
        assertEquals("Should return correct number of units", 2, units.size());
        assertTrue("Should contain both units", units.contains(unit1) && units.contains(unit2));
    }

    /**
     * Test: Get enemies returns correct list
     */
    @Test
    public void testGetEnemiesReturnsCorrectList() {
        Enemy enemy1 = new Enemy(Enemy.EnemyType.BASIC, 100, 100);
        Enemy enemy2 = new Enemy(Enemy.EnemyType.TANK, 200, 200);

        entityManager.addEnemy(enemy1);
        entityManager.addEnemy(enemy2);

        List<Enemy> enemies = entityManager.getEnemies();
        assertEquals("Should return correct number of enemies", 2, enemies.size());
        assertTrue("Should contain both enemies", enemies.contains(enemy1) && enemies.contains(enemy2));
    }

    /**
     * Test: Update calls update on all entities
     */
    @Test
    public void testUpdateCallsEntityUpdate() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);
        Enemy enemy = new Enemy(Enemy.EnemyType.BASIC, 100, 100);

        unit.setState(Unit.State.WALKING);
        enemy.setState(Enemy.State.WALKING);

        entityManager.addUnit(unit);
        entityManager.addEnemy(enemy);

        float initialUnitTime = unit.getStateTime();
        float initialEnemyTime = enemy.getStateTime();

        entityManager.update(0.1f);

        float newUnitTime = unit.getStateTime();
        float newEnemyTime = enemy.getStateTime();

        assertTrue("Unit state time should increase", newUnitTime > initialUnitTime);
        assertTrue("Enemy state time should increase", newEnemyTime > initialEnemyTime);
    }

    /**
     * Test: Empty entity lists
     */
    @Test
    public void testEmptyEntityLists() {
        assertTrue("Units list should be empty", entityManager.getUnits().isEmpty());
        assertTrue("Enemies list should be empty", entityManager.getEnemies().isEmpty());
    }

    /**
     * Test: Multiple updates with dead entities
     */
    @Test
    public void testMultipleUpdatesWithDeadEntities() {
        Unit unit1 = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);
        Unit unit2 = new Unit(Unit.UnitType.ARCHER, 200, 200, 100, 15, 6, 100);
        Unit unit3 = new Unit(Unit.UnitType.MINER, 300, 300, 100, 5, 3, 50);

        entityManager.addUnit(unit1);
        entityManager.addUnit(unit2);
        entityManager.addUnit(unit3);

        unit1.setState(Unit.State.DEAD);
        entityManager.update(0.016f);
        assertEquals("Should have 2 units", 2, entityManager.getUnits().size());

        unit2.setState(Unit.State.DEAD);
        entityManager.update(0.016f);
        assertEquals("Should have 1 unit", 1, entityManager.getUnits().size());

        unit3.setState(Unit.State.DEAD);
        entityManager.update(0.016f);
        assertEquals("Should have 0 units", 0, entityManager.getUnits().size());
    }
}

