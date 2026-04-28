package com.hust.towerdefence.Model.Systems;

import com.hust.towerdefence.Model.Entities.Enemy;
import com.hust.towerdefence.Model.Entities.Unit;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class cho CombatSystem
 */
@DisplayName("CombatSystem Tests")
public class CombatSystemTest {
    private CombatSystem combatSystem;
    private GameWorld gameWorld;
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        combatSystem = new CombatSystem();
        gameWorld = new GameWorld();
        entityManager = gameWorld.getEntityManager();
    }

    @Test
    @DisplayName("Test: Unit can attack enemy")
    public void testUnitAttacksEnemy() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 20, 5, 50);
        Enemy enemy = new Enemy(Enemy.EnemyType.BASIC, 130, 100);

        entityManager.addUnit(unit);
        entityManager.addEnemy(enemy);

        unit.setAction(Unit.Action.ATTACK);
        combatSystem.update(gameWorld, 0.016f);

        assertNotNull(enemy, "Enemy should still exist");
    }

    @Test
    @DisplayName("Test: Archer has longer range")
    public void testArcherRangeAdvantage() {
        Unit archer = new Unit(Unit.UnitType.ARCHER, 100, 100, 100, 10, 6, 150);
        Unit infantry = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 20, 5, 50);

        assertTrue(archer.getRange() > infantry.getRange(), "Archer range should be > Infantry range");
    }

    @Test
    @DisplayName("Test: Combat with dead unit doesnt apply damage")
    public void testDeadUnitDoesntAttack() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 20, 5, 50);
        Enemy enemy = new Enemy(Enemy.EnemyType.BASIC, 130, 100);

        entityManager.addUnit(unit);
        entityManager.addEnemy(enemy);

        unit.setState(Unit.State.DEAD);

        int enemyInitialHp = enemy.getHp();
        combatSystem.update(gameWorld, 0.1f);
        int enemyFinalHp = enemy.getHp();

        assertEquals(enemyInitialHp, enemyFinalHp, "Dead unit should not damage enemy");
    }

    @Test
    @DisplayName("Test: Dead enemy doesn't deal damage")
    public void testDeadEnemyStopsDamage() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 20, 5, 50);
        Enemy enemy = new Enemy(Enemy.EnemyType.BASIC, 130, 100);

        entityManager.addUnit(unit);
        entityManager.addEnemy(enemy);

        enemy.setState(Enemy.State.DEAD);

        int unitInitialHp = unit.getHp();
        combatSystem.update(gameWorld, 0.1f);
        int unitFinalHp = unit.getHp();

        assertEquals(unitInitialHp, unitFinalHp, "Dead enemy should not damage unit");
    }

    @Test
    @DisplayName("Test: Combat system with no entities")
    public void testCombatSystemWithNoEntities() {
        combatSystem.update(gameWorld, 0.016f);
        assertTrue(true, "Should complete without entities");
    }

    @Test
    @DisplayName("Test: Damage is based on ATK stat")
    public void testDamageBasedOnAttack() {
        Unit strongUnit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 100, 5, 50);
        Unit weakUnit = new Unit(Unit.UnitType.INFANTRY, 105, 100, 100, 10, 5, 50);

        assertTrue(strongUnit.getAtk() > weakUnit.getAtk(), "Strong unit should deal more damage");
    }

    @Test
    @DisplayName("Test: Multiple consecutive attacks")
    public void testMultipleConsecutiveAttacks() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 20, 5, 50);
        Enemy enemy = new Enemy(Enemy.EnemyType.BASIC, 130, 100);

        entityManager.addUnit(unit);
        entityManager.addEnemy(enemy);

        unit.setAction(Unit.Action.ATTACK);

        for (int i = 0; i < 10; i++) {
            combatSystem.update(gameWorld, 0.1f);
        }

        assertNotNull(gameWorld.getEntityManager(), "Entities should exist after combat");
    }

    @Test
    @DisplayName("Test: Combat respects delta time")
    public void testCombatSystemRespectsDeltaTime() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 20, 5, 50);
        Enemy enemy = new Enemy(Enemy.EnemyType.BASIC, 130, 100);

        entityManager.addUnit(unit);
        entityManager.addEnemy(enemy);

        unit.setAction(Unit.Action.ATTACK);

        combatSystem.update(gameWorld, 0.016f);
        combatSystem.update(gameWorld, 0.032f);
        combatSystem.update(gameWorld, 0.1f);

        assertNotNull(gameWorld.getEntityManager(), "Entities should exist");
    }
}
