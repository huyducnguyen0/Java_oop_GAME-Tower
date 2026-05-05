package com.hust.towerdefence.Model.Managers;

import com.hust.towerdefence.Model.Entities.Combat.Soldier.Pawn;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Warrior;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Archer;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.PawnHacHoa;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.WarriorHacHoa;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.TNT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EntityManager Tests
 * Verify quản lý danh sách entities và resources
 */
@DisplayName("EntityManager - Entity & Resource Management")
public class EntityManagerTest {

    private EntityManager manager;
    private Pawn pawn1, pawn2;
    private Warrior warrior;
    private Archer archer;
    private PawnHacHoa enemy1;
    private WarriorHacHoa enemy2;
    private TNT tnt;

    @BeforeEach
    public void setUp() {
        manager = new EntityManager();

        // Create soldiers
        pawn1 = new Pawn();
        pawn2 = new Pawn();
        warrior = new Warrior();
        archer = new Archer();

        // Create enemies
        enemy1 = new PawnHacHoa();
        enemy2 = new WarriorHacHoa();
        tnt = new TNT();
    }

    // ===================== Add/Remove Tests =====================

    @Test
    @DisplayName("Add Soldier - Should add to list")
    public void testAddSoldier() {
        manager.addSoldier(pawn1);

        assertEquals(1, manager.getSoldierCount());
        assertTrue(manager.getSoldiers().contains(pawn1));
    }

    @Test
    @DisplayName("Add multiple Soldiers")
    public void testAddMultipleSoldiers() {
        manager.addSoldier(pawn1);
        manager.addSoldier(pawn2);
        manager.addSoldier(warrior);

        assertEquals(3, manager.getSoldierCount());
    }

    @Test
    @DisplayName("Add duplicate Soldier - Should not add twice")
    public void testAddDuplicateSoldier() {
        manager.addSoldier(pawn1);
        manager.addSoldier(pawn1);

        assertEquals(1, manager.getSoldierCount());
    }

    @Test
    @DisplayName("Add null Soldier - Should not add")
    public void testAddNullSoldier() {
        manager.addSoldier(null);

        assertEquals(0, manager.getSoldierCount());
    }

    @Test
    @DisplayName("Add Enemy - Should add to list")
    public void testAddEnemy() {
        manager.addEnemy(enemy1);

        assertEquals(1, manager.getEnemyCount());
        assertTrue(manager.getEnemies().contains(enemy1));
    }

    @Test
    @DisplayName("Remove Soldier")
    public void testRemoveSoldier() {
        manager.addSoldier(pawn1);
        assertEquals(1, manager.getSoldierCount());

        manager.removeSoldier(pawn1);

        assertEquals(0, manager.getSoldierCount());
    }

    @Test
    @DisplayName("Remove Enemy")
    public void testRemoveEnemy() {
        manager.addEnemy(enemy1);
        assertEquals(1, manager.getEnemyCount());

        manager.removeEnemy(enemy1);

        assertEquals(0, manager.getEnemyCount());
    }

    @Test
    @DisplayName("Clear all entities")
    public void testClear() {
        manager.addSoldier(pawn1);
        manager.addSoldier(warrior);
        manager.addEnemy(enemy1);
        manager.addEnemy(enemy2);

        manager.clear();

        assertEquals(0, manager.getSoldierCount());
        assertEquals(0, manager.getEnemyCount());
    }

    // ===================== Dead Entity Cleanup Tests =====================

    @Test
    @DisplayName("Cleanup - Remove dead soldiers on update")
    public void testCleanupDeadSoldiers() {
        manager.addSoldier(pawn1);
        manager.addSoldier(pawn2);

        assertEquals(2, manager.getSoldierCount());

        // Kill pawn1
        pawn1.takeDamage(pawn1.getMaxHealth() + 1);

        manager.update(0.016f);

        assertEquals(1, manager.getSoldierCount());
        assertTrue(manager.getSoldiers().contains(pawn2));
        assertFalse(manager.getSoldiers().contains(pawn1));
    }

    @Test
    @DisplayName("Cleanup - Remove dead enemies on update")
    public void testCleanupDeadEnemies() {
        manager.addEnemy(enemy1);
        manager.addEnemy(enemy2);

        assertEquals(2, manager.getEnemyCount());

        // Kill enemy1
        enemy1.takeDamage(enemy1.getMaxHealth() + 1);

        manager.update(0.016f);

        assertEquals(1, manager.getEnemyCount());
        assertTrue(manager.getEnemies().contains(enemy2));
        assertFalse(manager.getEnemies().contains(enemy1));
    }

    // ===================== Query Tests =====================

    @Test
    @DisplayName("Find nearest Enemy - Should find closest")
    public void testFindNearestEnemy() {
        enemy1.setPosition(0, 0);
        enemy2.setPosition(10, 0);

        manager.addEnemy(enemy1);
        manager.addEnemy(enemy2);

        // Search from (5, 0) - closer to enemy2
        enemy1.setPosition(0, 0);
        enemy2.setPosition(10, 0);

        manager.addEnemy(enemy1);
        manager.addEnemy(enemy2);

        // From (5, 0), enemy2 at (10, 0) is closer
        enemy1.setPosition(0, 0);
        enemy2.setPosition(10, 0);

        // Create new enemies with known positions
        PawnHacHoa e1 = new PawnHacHoa();
        WarriorHacHoa e2 = new WarriorHacHoa();

        e1.setPosition(0, 0);
        e2.setPosition(10, 0);

        EntityManager mgr = new EntityManager();
        mgr.addEnemy(e1);
        mgr.addEnemy(e2);

        // Search from (5, 0) - e2 is closer
        assertEquals(e2, mgr.findNearestEnemy(5, 0));
    }

    @Test
    @DisplayName("Find nearest Soldier - Should find closest")
    public void testFindNearestSoldier() {
        pawn1.setPosition(0, 0);
        pawn2.setPosition(10, 0);

        manager.addSoldier(pawn1);
        manager.addSoldier(pawn2);

        // Search from (5, 0) - pawn2 at (10, 0) is closer
        assertEquals(pawn2, manager.findNearestSoldier(5, 0));
    }

    @Test
    @DisplayName("Find nearest - Returns null when no entities")
    public void testFindNearestReturnsNull() {
        assertNull(manager.findNearestEnemy(0, 0));
        assertNull(manager.findNearestSoldier(0, 0));
    }

    @Test
    @DisplayName("Find nearest - Ignores dead entities")
    public void testFindNearestIgnoresDead() {
        pawn1.setPosition(0, 0);
        pawn2.setPosition(10, 0);

        manager.addSoldier(pawn1);
        manager.addSoldier(pawn2);

        // Kill pawn2
        pawn2.takeDamage(pawn2.getMaxHealth() + 1);

        // Should find pawn1 instead
        assertEquals(pawn1, manager.findNearestSoldier(5, 0));
    }

    // ===================== Health Query Tests =====================

    @Test
    @DisplayName("Get total soldier health")
    public void testGetTotalSoldierHealth() {
        manager.addSoldier(pawn1);
        manager.addSoldier(pawn2);
        manager.addSoldier(warrior);

        float expected = pawn1.getHealth() + pawn2.getHealth() + warrior.getHealth();

        assertEquals(expected, manager.getTotalSoldierHealth(), 0.01f);
    }

    @Test
    @DisplayName("Get total enemy health")
    public void testGetTotalEnemyHealth() {
        manager.addEnemy(enemy1);
        manager.addEnemy(enemy2);

        float expected = enemy1.getHealth() + enemy2.getHealth();

        assertEquals(expected, manager.getTotalEnemyHealth(), 0.01f);
    }

    @Test
    @DisplayName("Total health ignores dead entities")
    public void testTotalHealthIgnoresDead() {
        manager.addSoldier(pawn1);
        manager.addSoldier(pawn2);

        float healthBefore = manager.getTotalSoldierHealth();

        pawn1.takeDamage(pawn1.getMaxHealth() + 1);

        float healthAfter = manager.getTotalSoldierHealth();

        assertEquals(pawn2.getHealth(), healthAfter, 0.01f);
    }

    // ===================== Count Tests =====================

    @Test
    @DisplayName("Get alive soldier count")
    public void testGetAliveSoldierCount() {
        manager.addSoldier(pawn1);
        manager.addSoldier(pawn2);

        assertEquals(2, manager.getAliveSoldierCount());

        pawn1.takeDamage(pawn1.getMaxHealth() + 1);

        assertEquals(1, manager.getAliveSoldierCount());
    }

    @Test
    @DisplayName("Get alive enemy count")
    public void testGetAliveEnemyCount() {
        manager.addEnemy(enemy1);
        manager.addEnemy(enemy2);

        assertEquals(2, manager.getAliveEnemyCount());

        enemy1.takeDamage(enemy1.getMaxHealth() + 1);

        assertEquals(1, manager.getAliveEnemyCount());
    }

    // ===================== Gold Management Tests =====================

    @Test
    @DisplayName("Initial gold is 1000")
    public void testInitialGold() {
        assertEquals(1000, manager.getGold());
    }

    @Test
    @DisplayName("Set gold")
    public void testSetGold() {
        manager.setGold(500);

        assertEquals(500, manager.getGold());
    }

    @Test
    @DisplayName("Set gold cannot be negative")
    public void testGoldCannotBeNegative() {
        manager.setGold(-100);

        assertEquals(0, manager.getGold());
    }

    @Test
    @DisplayName("Add gold")
    public void testAddGold() {
        manager.addGold(500);

        assertEquals(1500, manager.getGold());
    }

    @Test
    @DisplayName("Spend gold - Success")
    public void testSpendGoldSuccess() {
        manager.setGold(1000);
        manager.spendGold(300);

        assertEquals(700, manager.getGold());
    }

    @Test
    @DisplayName("Spend gold - Insufficient gold")
    public void testSpendGoldInsufficent() {
        manager.setGold(100);
        manager.spendGold(500);

        // Should not change
        assertEquals(100, manager.getGold());
    }

    // ===================== Building Health Tests =====================

    @Test
    @DisplayName("Initial building HP is 1000")
    public void testInitialBuildingHp() {
        assertEquals(1000, manager.getMainBuildingHp());
    }

    @Test
    @DisplayName("Set building HP")
    public void testSetBuildingHp() {
        manager.setMainBuildingHp(500);

        assertEquals(500, manager.getMainBuildingHp());
    }

    @Test
    @DisplayName("Damage main building")
    public void testDamageMainBuilding() {
        manager.setMainBuildingHp(1000);
        manager.damageMainBuilding(200);

        assertEquals(800, manager.getMainBuildingHp());
    }

    @Test
    @DisplayName("Building health cannot be negative")
    public void testBuildingHealthCannotBeNegative() {
        manager.setMainBuildingHp(100);
        manager.damageMainBuilding(200);

        assertEquals(0, manager.getMainBuildingHp());
    }

    @Test
    @DisplayName("Is main building alive")
    public void testIsMainBuildingAlive() {
        manager.setMainBuildingHp(1000);
        assertTrue(manager.isMainBuildingAlive());

        manager.setMainBuildingHp(0);
        assertFalse(manager.isMainBuildingAlive());
    }

    // ===================== Compatibility Tests =====================

    @Test
    @DisplayName("Compatibility - addUnit() calls addSoldier()")
    public void testAddUnitCompatibility() {
        manager.addUnit(pawn1);

        assertEquals(1, manager.getSoldierCount());
    }

    @Test
    @DisplayName("Compatibility - getUnits() returns soldiers")
    public void testGetUnitsCompatibility() {
        manager.addSoldier(pawn1);
        manager.addSoldier(pawn2);

        assertTrue(manager.getUnits().contains(pawn1));
        assertTrue(manager.getUnits().contains(pawn2));
    }

    // ===================== Copy Collection Tests =====================

    @Test
    @DisplayName("getSoldiers() returns copy - modifications don't affect internal list")
    public void testGetSoldiersCopyProtection() {
        manager.addSoldier(pawn1);

        var list = manager.getSoldiers();
        list.add(pawn2);

        // Internal list should not change
        assertEquals(1, manager.getSoldierCount());
    }

    @Test
    @DisplayName("getEnemies() returns copy")
    public void testGetEnemiesCopyProtection() {
        manager.addEnemy(enemy1);

        var list = manager.getEnemies();
        list.add(enemy2);

        // Internal list should not change
        assertEquals(1, manager.getEnemyCount());
    }
}

