package com.hust.towerdefence.Model.Entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests")
public class UnitTest {
    private Unit miner;
    private Unit infantry;
    private Unit archer;

    @BeforeEach
    public void setUp() {
        miner = new Unit(Unit.UnitType.MINER, 100, 100, 50, 5, 2.0f, 0);
        infantry = new Unit(Unit.UnitType.INFANTRY, 0, 0, 100, 20, 1.5f, 50);
        archer = new Unit(Unit.UnitType.ARCHER, 0, 0, 100, 10, 1.0f, 200);
    }

    @Test
    @DisplayName("Kiểm tra khởi tạo Unit theo loại")
    void testInitialization() {
        // Tạo một Miner (Thợ mỏ)
        Unit miner = new Unit(Unit.UnitType.MINER, 100, 100, 50, 5, 2.0f, 0);

        assertEquals(Unit.Action.MINE, miner.getCurrentAction(), "Miner phải có hành động mặc định là MINE");
        assertEquals(1, miner.getLevel(), "Level khởi đầu phải là 1");
    }

    @Test
    @DisplayName("Kiểm tra logic nâng cấp chỉ số")
    void testUpgradeLogic() {
        Unit archer = new Unit(Unit.UnitType.ARCHER, 0, 0, 100, 10, 1.0f, 200);
        float oldRange = archer.getRange();
        int oldAtk = archer.getAtk();

        boolean upgraded = archer.upgrade();

        assertTrue(upgraded);
        assertEquals(2, archer.getLevel(), "Sau khi upgrade level phải tăng lên 2");
        assertEquals(oldAtk + 5, archer.getAtk(), "Atk phải tăng thêm 5 đơn vị");
        assertEquals(oldRange + 50, archer.getRange(), "Archer phải được tăng tầm bắn thêm 50");
    }

    @Test
    @DisplayName("Kiểm tra chi phí nâng cấp theo cấp độ")
    void testUpgradeCost() {
        Unit infantry = new Unit(Unit.UnitType.INFANTRY, 0, 0, 100, 20, 1.5f, 50);

        assertEquals(100, infantry.getUpgradeCost(), "Level 1 cost phải là 100");

        infantry.upgrade(); // Lên level 2
        assertEquals(200, infantry.getUpgradeCost(), "Level 2 cost phải là 200");
    }

    @Test
    @DisplayName("Kiểm tra thay đổi hành động")
    void testSetAction() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 0, 0, 100, 10, 1.0f, 10);

        unit.setAction(Unit.Action.SHOOT);
        assertEquals(Unit.Action.SHOOT, unit.getCurrentAction());

        unit.performAction();
        // Kiểm tra xem state có chuyển sang ATTACKING để chạy animation không
        // Giả sử GameEntity có thuộc tính state (theo code của bạn)
        assertEquals(GameEntity.State.ATTACKING, unit.getState());
    }

    /**
     * Test: Unit type initialization
     */
    @Test
    @DisplayName("Test khởi tạo Unit loại MINER")
    void testMinerInitialization() {
        assertEquals(Unit.UnitType.MINER, miner.getType());
        assertEquals(Unit.Action.MINE, miner.getCurrentAction());
    }

    /**
     * Test: Infantry type
     */
    @Test
    @DisplayName("Test khởi tạo Unit loại INFANTRY")
    void testInfantryInitialization() {
        assertEquals(Unit.UnitType.INFANTRY, infantry.getType());
        assertEquals(Unit.Action.ATTACK, infantry.getCurrentAction());
    }

    /**
     * Test: Archer type
     */
    @Test
    @DisplayName("Test khởi tạo Unit loại ARCHER")
    void testArcherInitialization() {
        assertEquals(Unit.UnitType.ARCHER, archer.getType());
        assertEquals(Unit.Action.SHOOT, archer.getCurrentAction());
        assertTrue("Archer range phải > 100", archer.getRange() > 100);
    }

    /**
     * Test: Unit stats
     */
    @Test
    @DisplayName("Test stats của Unit")
    void testUnitStats() {
        assertTrue("Infantry ATK phải > 0", infantry.getAtk() > 0);
        assertTrue("Infantry SPD phải > 0", infantry.getSpd() > 0);
        assertTrue("Infantry HP phải > 0", infantry.getHp() > 0);
        assertTrue("Infantry Range phải >= 0", infantry.getRange() >= 0);
    }

    /**
     * Test: Set target
     */
    @Test
    @DisplayName("Test set target position")
    void testSetTarget() {
        infantry.setTarget(200, 300);
        assertEquals(200f, infantry.getTargetX(), 0.01f);
        assertEquals(300f, infantry.getTargetY(), 0.01f);
    }

    /**
     * Test: Gold carrying
     */
    @Test
    @DisplayName("Test miner carrying gold")
    void testGoldCarrying() {
        miner.setGoldCarried(50);
        assertEquals(50, miner.getGoldCarried());

        miner.setGoldCarried(100);
        assertEquals(100, miner.getGoldCarried());
    }

    /**
     * Test: Multiple upgrades
     */
    @Test
    @DisplayName("Test multiple upgrades")
    void testMultipleUpgrades() {
        int initialLevel = infantry.getLevel();

        infantry.upgrade();
        infantry.upgrade();
        infantry.upgrade();

        assertEquals(initialLevel + 3, infantry.getLevel());
    }

    /**
     * Test: Upgrade increases multiple stats
     */
    @Test
    @DisplayName("Test upgrade increases multiple stats")
    void testUpgradeIncreasesMultipleStats() {
        int initialHp = infantry.getHp();
        int initialAtk = infantry.getAtk();
        float initialSpd = infantry.getSpd();

        infantry.upgrade();

        assertTrue("HP should increase", infantry.getHp() > initialHp);
        assertTrue("ATK should increase", infantry.getAtk() > initialAtk);
        assertTrue("SPD should increase", infantry.getSpd() > initialSpd);
    }

    /**
     * Test: Unit position
     */
    @Test
    @DisplayName("Test unit position")
    void testUnitPosition() {
        Unit testUnit = new Unit(Unit.UnitType.INFANTRY, 100, 200, 100, 10, 1.0f, 50);
        assertEquals(100f, testUnit.getX(), 0.01f);
        assertEquals(200f, testUnit.getY(), 0.01f);
    }

    /**
     * Test: Unit HP management
     */
    @Test
    @DisplayName("Test unit HP management")
    void testHpManagement() {
        int maxHp = infantry.getMaxHp();
        infantry.setHp(50);
        assertEquals(50, infantry.getHp());

        infantry.setHp(maxHp);
        assertEquals(maxHp, infantry.getHp());
    }

    /**
     * Test: Unit state changes
     */
    @Test
    @DisplayName("Test unit state changes")
    void testStateChanges() {
        infantry.setState(GameEntity.State.WALKING);
        assertEquals(GameEntity.State.WALKING, infantry.getState());

        infantry.setState(GameEntity.State.ATTACKING);
        assertEquals(GameEntity.State.ATTACKING, infantry.getState());

        infantry.setState(GameEntity.State.DEAD);
        assertEquals(GameEntity.State.DEAD, infantry.getState());
    }

    /**
     * Test: Archer has bonus range on upgrade
     */
    @Test
    @DisplayName("Test archer range bonus on upgrade")
    void testArcherRangeBonus() {
        float initialRange = archer.getRange();
        archer.upgrade();
        float newRange = archer.getRange();

        assertEquals(initialRange + 50, newRange, 0.01f, "Archer should gain +50 range on upgrade");
    }

    /**
     * Test: Set and get type
     */
    @Test
    @DisplayName("Test set and get type")
    void testSetAndGetType() {
        Unit unit = new Unit(Unit.UnitType.MINER, 100, 100, 50, 5, 2.0f, 0);
        assertEquals(Unit.UnitType.MINER, unit.getType());

        unit.setType(Unit.UnitType.INFANTRY);
        assertEquals(Unit.UnitType.INFANTRY, unit.getType());
    }

    /**
     * Test: Set speed
     */
    @Test
    @DisplayName("Test set speed")
    void testSetSpeed() {
        float initialSpd = infantry.getSpd();
        infantry.setSpd(10f);
        assertEquals(10f, infantry.getSpd(), 0.01f);
    }

    /**
     * Test: Set attack
     */
    @Test
    @DisplayName("Test set attack")
    void testSetAttack() {
        int initialAtk = infantry.getAtk();
        infantry.setAtk(50);
        assertEquals(50, infantry.getAtk());
    }

    /**
     * Test: Set range
     */
    @Test
    @DisplayName("Test set range")
    void testSetRange() {
        archer.setRange(300f);
        assertEquals(300f, archer.getRange(), 0.01f);
    }

    /**
     * Test: Perform action updates state
     */
    @Test
    @DisplayName("Test perform action updates state")
    void testPerformActionUpdatesState() {
        infantry.setAction(Unit.Action.ATTACK);
        infantry.performAction();
        assertEquals(GameEntity.State.ATTACKING, infantry.getState());
    }
}
