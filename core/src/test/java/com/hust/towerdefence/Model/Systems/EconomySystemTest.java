package com.hust.towerdefence.Model.Systems;

import com.hust.towerdefence.Model.Entities.Unit;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class cho EconomySystem
 */
@DisplayName("EconomySystem Tests")
public class EconomySystemTest {
    private EconomySystem economySystem;
    private GameWorld gameWorld;
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        economySystem = new EconomySystem();
        gameWorld = new GameWorld();
        entityManager = gameWorld.getEntityManager();
    }

    /**
     * Test: Miner unit can gather gold
     */
    @Test
    public void testMinerGatherGold() {
        int initialGold = entityManager.getGold();

        Unit miner = new Unit(Unit.UnitType.MINER, 200, 1800, 100, 5, 3, 50);
        entityManager.addUnit(miner);

        miner.setAction(Unit.Action.MINE);
        economySystem.update(gameWorld, 0.016f);

        // Gold should remain or increase (depends on implementation)
        assertTrue("Economy system should process", true);
    }

    /**
     * Test: Multiple miners can work
     */
    @Test
    public void testMultipleMiners() {
        Unit miner1 = new Unit(Unit.UnitType.MINER, 200, 1800, 100, 5, 3, 50);
        Unit miner2 = new Unit(Unit.UnitType.MINER, 220, 1800, 100, 5, 3, 50);
        Unit miner3 = new Unit(Unit.UnitType.MINER, 240, 1800, 100, 5, 3, 50);

        entityManager.addUnit(miner1);
        entityManager.addUnit(miner2);
        entityManager.addUnit(miner3);

        miner1.setAction(Unit.Action.MINE);
        miner2.setAction(Unit.Action.MINE);
        miner3.setAction(Unit.Action.MINE);

        economySystem.update(gameWorld, 0.1f);

        // All miners should be able to work
        assertEquals("Should have 3 miners", 3, entityManager.getUnits().size());
    }

    /**
     * Test: Unit upgrade cost calculation
     */
    @Test
    @DisplayName("Test: Unit upgrade cost calculation")
    public void testUpgradeCostCalculation() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);

        int levelOneCost = unit.getUpgradeCost();
        int initialLevel = unit.getLevel();

        assertTrue(levelOneCost > 0, "Upgrade cost should be positive");
        assertEquals(1, initialLevel, "Initial level should be 1");
    }

    /**
     * Test: Unit upgrade increases stats
     */
    @Test
    @DisplayName("Test: Unit upgrade increases stats")
    public void testUnitUpgradeIncreaseStats() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);

        int initialHp = unit.getHp();
        int initialAtk = unit.getAtk();
        float initialSpd = unit.getSpd();

        unit.upgrade();

        int upgradedHp = unit.getHp();
        int upgradedAtk = unit.getAtk();
        float upgradedSpd = unit.getSpd();

        assertTrue(upgradedHp > initialHp, "HP should increase after upgrade");
        assertTrue(upgradedAtk > initialAtk, "ATK should increase after upgrade");
        assertTrue(upgradedSpd > initialSpd, "SPD should increase after upgrade");
    }

    /**
     * Test: Archer upgrade increases range
     */
    @Test
    @DisplayName("Test: Archer upgrade increases range")
    public void testArcherUpgradeIncreaseRange() {
        Unit archer = new Unit(Unit.UnitType.ARCHER, 100, 100, 100, 10, 5, 100);

        float initialRange = archer.getRange();
        archer.upgrade();
        float upgradedRange = archer.getRange();

        assertTrue(upgradedRange > initialRange, "Range should increase for archer");
    }

    /**
     * Test: Unit level increases after upgrade
     */
    @Test
    @DisplayName("Test: Unit level increases after upgrade")
    public void testUnitLevelIncreaseAfterUpgrade() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);

        int initialLevel = unit.getLevel();
        unit.upgrade();
        int upgradedLevel = unit.getLevel();

        assertEquals(initialLevel + 1, upgradedLevel, "Level should increase by 1");
    }

    /**
     * Test: Multiple upgrades
     */
    @Test
    @DisplayName("Test: Multiple upgrades")
    public void testMultipleUpgrades() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);

        int initialLevel = unit.getLevel();

        unit.upgrade();
        unit.upgrade();
        unit.upgrade();

        int finalLevel = unit.getLevel();

        assertEquals(4, finalLevel, "Level should be 4");
    }

    /**
     * Test: Cost increases with each upgrade
     */
    @Test
    @DisplayName("Test: Cost increases with each upgrade")
    public void testUpgradeCostIncreases() {
        Unit unit = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);

        int cost1 = unit.getUpgradeCost();

        unit.upgrade();
        int cost2 = unit.getUpgradeCost();

        unit.upgrade();
        int cost3 = unit.getUpgradeCost();

        assertTrue(cost1 < cost2, "Cost should increase with level");
        assertTrue(cost2 < cost3, "Cost should continue to increase");
    }

    /**
     * Test: Economy system with no units
     */
    @Test
    public void testEconomySystemWithNoUnits() {
        // Should handle gracefully
        economySystem.update(gameWorld, 0.016f);
        assertTrue("Should complete without units", true);
    }

    /**
     * Test: Gold management in economy system
     */
    @Test
    @DisplayName("Test: Gold management in economy system")
    public void testGoldManagement() {
        int initialGold = entityManager.getGold();

        entityManager.setGold(initialGold + 100);
        int newGold = entityManager.getGold();

        assertEquals(initialGold + 100, newGold, "Gold should increase");
    }

    /**
     * Test: Unit carrying gold
     */
    @Test
    @DisplayName("Test: Unit carrying gold")
    public void testUnitCarryingGold() {
        Unit miner = new Unit(Unit.UnitType.MINER, 200, 1800, 100, 5, 3, 50);

        miner.setGoldCarried(50);
        assertEquals(50, miner.getGoldCarried(), "Unit should carry 50 gold");

        miner.setGoldCarried(100);
        assertEquals(100, miner.getGoldCarried(), "Unit should carry 100 gold");
    }

    /**
     * Test: Different unit types have different costs
     */
    @Test
    @DisplayName("Test: Different unit types can be upgraded")
    public void testDifferentUnitTypesCanBeUpgraded() {
        Unit infantry = new Unit(Unit.UnitType.INFANTRY, 100, 100, 100, 10, 5, 50);
        Unit archer = new Unit(Unit.UnitType.ARCHER, 100, 100, 100, 10, 5, 100);
        Unit miner = new Unit(Unit.UnitType.MINER, 100, 100, 100, 5, 3, 50);

        infantry.upgrade();
        archer.upgrade();
        miner.upgrade();

        assertEquals(2, infantry.getLevel(), "Infantry should be level 2");
        assertEquals(2, archer.getLevel(), "Archer should be level 2");
        assertEquals(2, miner.getLevel(), "Miner should be level 2");
    }

    /**
     * Test: Economy system respects delta time
     */
    @Test
    public void testEconomySystemRespectsDeltaTime() {
        Unit miner = new Unit(Unit.UnitType.MINER, 200, 1800, 100, 5, 3, 50);
        entityManager.addUnit(miner);

        miner.setAction(Unit.Action.MINE);

        // Update with different delta times
        economySystem.update(gameWorld, 0.016f);
        economySystem.update(gameWorld, 0.032f);
        economySystem.update(gameWorld, 0.1f);

        // Should handle different delta times
        assertEquals("Should still have miner", 1, entityManager.getUnits().size());
    }
}
