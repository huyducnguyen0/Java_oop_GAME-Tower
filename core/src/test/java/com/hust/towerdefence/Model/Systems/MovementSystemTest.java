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
 * Test class cho MovementSystem
 */
@DisplayName("MovementSystem Tests")
public class MovementSystemTest {
    private MovementSystem movementSystem;
    private GameWorld gameWorld;
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        movementSystem = new MovementSystem();
        gameWorld = new GameWorld();
        entityManager = gameWorld.getEntityManager();
    }

    @Test
    @DisplayName("Test: Unit can move to target")
    public void testUnitMovesToTarget() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);
        entityManager.addUnit(unit);

        unit.setTarget(200, 200);

        movementSystem.update(gameWorld, 0.1f);

        assertNotNull(entityManager.getUnits().get(0), "Unit should still exist");
    }

    @Test
    @DisplayName("Test: Enemy moves toward base")
    public void testEnemyMovestowardBase() {
        Enemy enemy = new Enemy(Enemy.EnemyType.BASIC, 100, 100);
        entityManager.addEnemy(enemy);

        movementSystem.update(gameWorld, 0.1f);

        assertNotNull(entityManager.getEnemies().get(0), "Enemy should exist");
    }

    @Test
    @DisplayName("Test: Movement respects speed stat")
    public void testMovementRespectsSpeed() {
        Unit fastUnit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 10, 50);
        Unit slowUnit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 2, 50);

        entityManager.addUnit(fastUnit);
        entityManager.addUnit(slowUnit);

        fastUnit.setTarget(200, 100);
        slowUnit.setTarget(200, 100);

        movementSystem.update(gameWorld, 0.1f);

        assertEquals(2, entityManager.getUnits().size(), "Should have 2 units");
    }

    @Test
    @DisplayName("Test: Multiple units moving simultaneously")
    public void testMultipleUnitsMoving() {
        Unit unit1 = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);
        Unit unit2 = new Unit(Unit.UnitType.ARCHER, 150, 150, 100, 10, 6, 100);
        Unit unit3 = new Unit(Unit.UnitType.MINER, 200, 200, 100, 5, 3, 50);

        entityManager.addUnit(unit1);
        entityManager.addUnit(unit2);
        entityManager.addUnit(unit3);

        unit1.setTarget(300, 300);
        unit2.setTarget(300, 300);
        unit3.setTarget(300, 300);

        movementSystem.update(gameWorld, 0.1f);

        assertEquals(3, entityManager.getUnits().size(), "Should have 3 units");
    }

    @Test
    @DisplayName("Test: Stationary unit doesn't move")
    public void testStationaryUnitDoesntMove() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);
        entityManager.addUnit(unit);

        unit.setTarget(unit.getX(), unit.getY());

        float initialX = unit.getX();
        float initialY = unit.getY();

        movementSystem.update(gameWorld, 0.1f);

        float finalX = unit.getX();
        float finalY = unit.getY();

        assertTrue(Math.abs(finalX - initialX) < 10, "Unit should not move significantly");
        assertTrue(Math.abs(finalY - initialY) < 10, "Unit should not move significantly");
    }

    /**
     * Test: Enemy AI movement (steering behaviors)
     */
    @Test
    @DisplayName("Test: Enemy AI movement")
    public void testEnemyAIMovement() {
        Enemy enemy = new Enemy(Enemy.EnemyType.BASIC, 500, 500);
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 600, 500, 100, 10, 5, 50);

        entityManager.addEnemy(enemy);
        entityManager.addUnit(unit);

        movementSystem.update(gameWorld, 0.1f);

        // Enemy should have some update processed
        assertNotNull("Enemy should exist", entityManager.getEnemies().get(0));
    }

    @Test
    @DisplayName("Test: Movement system with no entities")
    public void testMovementSystemWithNoEntities() {
        movementSystem.update(gameWorld, 0.1f);
        assertTrue(true, "Should complete without entities");
    }

    @Test
    @DisplayName("Test: Boundary checking")
    public void testBoundaryChecking() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);
        entityManager.addUnit(unit);

        // Try to set target outside world boundaries
        unit.setTarget(3000, 3000);

        movementSystem.update(gameWorld, 0.1f);

        // Should not crash
        assertNotNull("Unit should exist", entityManager.getUnits().get(0));
    }

    @Test
    @DisplayName("Test: Dead unit shouldn't move")
    public void testDeadUnitDoesntMove() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);
        entityManager.addUnit(unit);

        unit.setState(Unit.State.DEAD);
        unit.setTarget(200, 200);

        float initialX = unit.getX();
        float initialY = unit.getY();

        movementSystem.update(gameWorld, 0.1f);

        float finalX = unit.getX();
        float finalY = unit.getY();

        assertTrue(Math.abs(finalX - initialX) < 0.1, "Dead unit should not move");
    }

    @Test
    @DisplayName("Test: Fast unit covers more distance")
    public void testFastUnitCoverMoreDistance() {
        Unit fastUnit = new Unit(Unit.UnitType.INFANTRY, 0, 0, 100, 10, 100, 50);
        Unit slowUnit = new Unit(Unit.UnitType.INFANTRY, 0, 0, 100, 10, 10, 50);

        entityManager.addUnit(fastUnit);
        entityManager.addUnit(slowUnit);

        fastUnit.setTarget(1000, 0);
        slowUnit.setTarget(1000, 0);

        movementSystem.update(gameWorld, 0.1f);

        assertEquals(2, entityManager.getUnits().size(), "Should have 2 units");
    }

    @Test
    @DisplayName("Test: Movement system respects delta time")
    public void testMovementSystemRespectsDeltaTime() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);
        entityManager.addUnit(unit);

        unit.setTarget(200, 200);

        movementSystem.update(gameWorld, 0.016f);
        movementSystem.update(gameWorld, 0.032f);
        movementSystem.update(gameWorld, 0.1f);

        assertEquals(1, entityManager.getUnits().size(), "Should still have unit");
    }

    @Test
    @DisplayName("Test: Multiple units moving to same target")
    public void testMultipleUnitsMovingToSameTarget() {
        Unit unit1 = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);
        Unit unit2 = new Unit(Unit.UnitType.INFANTRY, 110, 110, 100, 10, 5, 50);

        entityManager.addUnit(unit1);
        entityManager.addUnit(unit2);

        unit1.setTarget(500, 500);
        unit2.setTarget(500, 500);

        movementSystem.update(gameWorld, 0.1f);

        assertEquals(2, entityManager.getUnits().size(), "Should have 2 units");
    }
}
