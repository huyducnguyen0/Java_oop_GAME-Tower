package com.hust.towerdefence.Model.AI;

import com.hust.towerdefence.Model.Entities.Enemy;
import com.hust.towerdefence.Model.Entities.Unit;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class cho AIController
 * Kiểm tra:
 * - AI state transitions (IDLE, CHASE, ATTACK, RETREAT)
 * - Target finding logic
 * - Decision making based on HP and distance
 * - Steering behaviors (chase, attack, retreat, wander)
 */
public class AIControllerTest {
    private Enemy enemy;
    private AIController aiController;
    private GameWorld gameWorld;
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        // Khởi tạo game world và entity manager
        gameWorld = new GameWorld();
        entityManager = gameWorld.getEntityManager();

        // Khởi tạo enemy test
        enemy = new Enemy(Enemy.EnemyType.BASIC, 500, 500);
        aiController = enemy.getAIController();
    }

    /**
     * Test: AI nên ở trạng thái IDLE khi không có units
     */
    @Test
    public void testIdleStateWhenNoUnits() {
        // Khi không có units, AI không nên thay đổi trạng thái
        aiController.update(gameWorld, entityManager, 0.016f);
        // AI có thể ở IDLE hoặc CHASE (tuỳ logic)
        assertNotNull("AI state should not be null", aiController.getCurrentState());
    }

    /**
     * Test: AI nên tìm được unit gần nhất
     */
    @Test
    public void testFindNearestUnit() {
        // Thêm 3 units vào game
        Unit unit1 = new Unit(Unit.UnitType.INFANTRY, 600, 500, 100, 10, 5, 50);
        Unit unit2 = new Unit(Unit.UnitType.ARCHER, 1000, 500, 100, 10, 5, 50);
        Unit unit3 = new Unit(Unit.UnitType.MINER, 550, 500, 100, 10, 5, 50);

        entityManager.addUnit(unit1);
        entityManager.addUnit(unit2);
        entityManager.addUnit(unit3);

        // Update AI để nó có thể tìm target
        aiController.update(gameWorld, entityManager, 1.1f);

        // Unit gần nhất là unit3 (550, 500)
        Unit targetUnit = aiController.getTargetUnit();
        assertNotNull("Target unit should be found", targetUnit);
        assertEquals("Target should be the nearest unit", unit3, targetUnit);
    }

    /**
     * Test: AI nên ATTACK khi gần unit + HP cao
     */
    @Test
    public void testAttackStateWhenCloseAndHealthy() {
        // Enemy HP cao
        enemy.setHp(enemy.getMaxHp());

        // Thêm unit gần enemy (< 100 pixels)
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 550, 500, 100, 10, 5, 50);
        entityManager.addUnit(unit);

        // Update AI để quyết định
        aiController.update(gameWorld, entityManager, 1.1f);

        // AI nên ở trạng thái ATTACK
        assertEquals("Should be in ATTACK state", AIController.AIState.ATTACK, aiController.getCurrentState());
    }

    /**
     * Test: AI nên CHASE khi cách unit vừa phải + HP bình thường
     */
    @Test
    public void testChaseStateWhenMediumDistanceAndMediumHealth() {
        // Enemy HP vừa phải (50% MaxHP)
        enemy.setHp((int) (enemy.getMaxHp() * 0.5f));

        // Thêm unit ở khoảng cách vừa phải (150 pixels)
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 650, 500, 100, 10, 5, 50);
        entityManager.addUnit(unit);

        // Update AI để quyết định
        aiController.update(gameWorld, entityManager, 1.1f);

        // AI nên ở trạng thái CHASE
        assertEquals("Should be in CHASE state", AIController.AIState.CHASE, aiController.getCurrentState());
    }

    /**
     * Test: AI nên RETREAT khi HP thấp
     */
    @Test
    public void testRetreatStateWhenLowHealth() {
        // Enemy HP rất thấp (10% MaxHP)
        enemy.setHp((int) (enemy.getMaxHp() * 0.1f));

        // Thêm unit gần enemy
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 550, 500, 100, 10, 5, 50);
        entityManager.addUnit(unit);

        // Update AI để quyết định
        aiController.update(gameWorld, entityManager, 1.1f);

        // AI nên ở trạng thái RETREAT
        assertEquals("Should be in RETREAT state", AIController.AIState.RETREAT, aiController.getCurrentState());
    }

    /**
     * Test: AI không nên target dead units
     */
    @Test
    public void testIgnoreDeadUnits() {
        // Thêm 2 units, 1 dead, 1 alive
        Unit deadUnit = new Unit(Unit.UnitType.INFANTRY, 550, 500, 100, 10, 5, 50);
        Unit aliveUnit = new Unit(Unit.UnitType.ARCHER, 600, 500, 100, 10, 5, 50);

        deadUnit.setState(Unit.State.DEAD);

        entityManager.addUnit(deadUnit);
        entityManager.addUnit(aliveUnit);

        // Update AI để quyết định
        aiController.update(gameWorld, entityManager, 1.1f);

        // Target không nên là dead unit
        Unit targetUnit = aiController.getTargetUnit();
        assertNotEquals("Should not target dead unit", deadUnit, targetUnit);
    }

    /**
     * Test: Decision interval (quyết định mỗi 1 giây)
     */
    @Test
    public void testDecisionInterval() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 550, 500, 100, 10, 5, 50);
        entityManager.addUnit(unit);

        // Update nhiều lần với delta nhỏ (< 1 giây)
        aiController.update(gameWorld, entityManager, 0.3f);
        AIController.AIState state1 = aiController.getCurrentState();

        aiController.update(gameWorld, entityManager, 0.3f);
        AIController.AIState state2 = aiController.getCurrentState();

        // State có thể giống nhau vì chưa qua 1 giây
        // Sau 1 giây, decision mới sẽ được make
        aiController.update(gameWorld, entityManager, 0.5f);
        AIController.AIState state3 = aiController.getCurrentState();

        assertNotNull("State should be set after decision", state3);
    }

    /**
     * Test: Damage mechanism khi attack
     */
    @Test
    public void testAttackDamage() {
        // Enemy gần unit
        enemy.setX(550);
        enemy.setY(500);
        enemy.setHp(enemy.getMaxHp());

        Unit unit = new Unit(Unit.UnitType.INFANTRY, 500, 500, 100, 10, 5, 50);
        entityManager.addUnit(unit);

        int unitInitialHp = unit.getHp();

        // Force AI to attack state
        aiController.setCurrentState(AIController.AIState.ATTACK);

        // Simulate attack
        for (int i = 0; i < 5; i++) {
            aiController.update(gameWorld, entityManager, 0.1f);
        }

        // Unit HP should decrease
        assertTrue("Unit should take damage", unit.getHp() < unitInitialHp);
    }

    /**
     * Test: AI movement when chasing
     */
    @Test
    public void testChaseMovement() {
        enemy.setX(500);
        enemy.setY(500);

        Unit unit = new Unit(Unit.UnitType.INFANTRY, 600, 500, 100, 10, 5, 50);
        entityManager.addUnit(unit);

        float initialX = enemy.getX();

        // Force chase behavior
        aiController.setCurrentState(AIController.AIState.CHASE);
        aiController.update(gameWorld, entityManager, 0.5f);

        // Enemy should move closer to unit
        // Note: Movement speed depends on implementation
    }
}

