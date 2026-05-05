package com.hust.towerdefence.Model.Entities.Tower;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GoldMine Tests
 * Verify mining, respawn, and resource management
 */
@DisplayName("GoldMine - Static Mining Point")
public class GoldMineTest {

    private GoldMine mine;
    private static final int INITIAL_CAPACITY = 1000;
    private static final float DEFAULT_SPAWN_RATE = 10.0f;  // 10 gold/sec

    @BeforeEach
    public void setUp() {
        mine = new GoldMine(100, 100, INITIAL_CAPACITY);
    }

    // ===================== Constructor Tests =====================

    @Test
    @DisplayName("Constructor - Should initialize with correct values")
    public void testConstructorInitialization() {
        assertEquals(100, mine.getX());
        assertEquals(100, mine.getY());
        assertEquals(INITIAL_CAPACITY, mine.getGoldCapacity());
        assertEquals(INITIAL_CAPACITY, mine.getGoldRemaining());
        assertFalse(mine.isExhausted());
        assertTrue(mine.isActive());
    }

    @Test
    @DisplayName("Constructor - With custom spawn rate")
    public void testConstructorWithSpawnRate() {
        GoldMine customMine = new GoldMine(50, 50, 500, 5.0f);

        assertEquals(500, customMine.getGoldCapacity());
        assertEquals(5.0f, customMine.getGoldSpawnRate());
    }

    @Test
    @DisplayName("Constructor - Negative capacity becomes 0")
    public void testConstructorNegativeCapacity() {
        GoldMine badMine = new GoldMine(0, 0, -100);

        assertEquals(0, badMine.getGoldCapacity());
    }

    // ===================== Extract Gold Tests =====================

    @Test
    @DisplayName("Extract gold - Should reduce remaining gold")
    public void testExtractGold() {
        int extracted = mine.extractGold(100);

        assertEquals(100, extracted);
        assertEquals(INITIAL_CAPACITY - 100, mine.getGoldRemaining());
        assertFalse(mine.isExhausted());
    }

    @Test
    @DisplayName("Extract gold - Cannot extract more than available")
    public void testExtractGoldMoreThanAvailable() {
        int extracted = mine.extractGold(5000);

        assertEquals(INITIAL_CAPACITY, extracted);
        assertEquals(0, mine.getGoldRemaining());
        assertTrue(mine.isExhausted());
    }

    @Test
    @DisplayName("Extract gold - Cannot extract negative amount")
    public void testExtractGoldNegative() {
        int extracted = mine.extractGold(-100);

        assertEquals(0, extracted);
        assertEquals(INITIAL_CAPACITY, mine.getGoldRemaining());
    }

    @Test
    @DisplayName("Extract gold - Sets exhausted when emptied")
    public void testExtractGoldUntilExhausted() {
        mine.extractGold(INITIAL_CAPACITY);

        assertTrue(mine.isExhausted());
        assertEquals(0, mine.getGoldRemaining());
        assertFalse(mine.hasMoney());
    }

    // ===================== Respawn Tests =====================

    @Test
    @DisplayName("Update - Should spawn gold over time")
    public void testGoldRespawn() {
        // Extract all gold
        mine.extractGold(INITIAL_CAPACITY);
        assertTrue(mine.isExhausted());

        // Update for 1 second (should spawn 10 gold at default rate)
        mine.update(1.0f);

        assertEquals(10, mine.getGoldRemaining());
        assertFalse(mine.isExhausted());
    }

    @Test
    @DisplayName("Update - Should not spawn beyond capacity")
    public void testGoldRespawnDontExceedCapacity() {
        // Start with some gold
        mine.setGoldRemaining(990);  // 10 gold left to fill

        // Update for 2 seconds (would spawn 20 gold, but capped at 10)
        mine.update(2.0f);

        assertEquals(INITIAL_CAPACITY, mine.getGoldRemaining());
    }

    @Test
    @DisplayName("Update - Custom spawn rate")
    public void testCustomSpawnRate() {
        GoldMine customMine = new GoldMine(0, 0, 1000, 5.0f);  // 5 gold/sec

        customMine.extractGold(1000);
        customMine.update(1.0f);

        assertEquals(5, customMine.getGoldRemaining());
    }

    @Test
    @DisplayName("Update - No spawn when not exhausted and full")
    public void testNoSpawnWhenFull() {
        int initialGold = mine.getGoldRemaining();

        mine.update(10.0f);

        assertEquals(initialGold, mine.getGoldRemaining());
    }

    // ===================== Add Gold Tests =====================

    @Test
    @DisplayName("Add gold - Should increase remaining gold")
    public void testAddGold() {
        mine.setGoldRemaining(500);
        mine.addGold(100);

        assertEquals(600, mine.getGoldRemaining());
    }

    @Test
    @DisplayName("Add gold - Cannot exceed capacity")
    public void testAddGoldExceedCapacity() {
        mine.setGoldRemaining(950);
        mine.addGold(100);

        assertEquals(INITIAL_CAPACITY, mine.getGoldRemaining());
    }

    @Test
    @DisplayName("Add gold - Sets exhausted to false")
    public void testAddGoldResetsExhausted() {
        mine.extractGold(INITIAL_CAPACITY);
        assertTrue(mine.isExhausted());

        mine.addGold(50);

        assertFalse(mine.isExhausted());
        assertEquals(50, mine.getGoldRemaining());
    }

    // ===================== Gold Percentage Tests =====================

    @Test
    @DisplayName("Get gold percentage - Full mine")
    public void testGoldPercentageFull() {
        assertEquals(1.0f, mine.getGoldPercentage(), 0.01f);
    }

    @Test
    @DisplayName("Get gold percentage - Half full")
    public void testGoldPercentageHalf() {
        mine.setGoldRemaining(500);

        assertEquals(0.5f, mine.getGoldPercentage(), 0.01f);
    }

    @Test
    @DisplayName("Get gold percentage - Empty")
    public void testGoldPercentageEmpty() {
        mine.extractGold(INITIAL_CAPACITY);

        assertEquals(0.0f, mine.getGoldPercentage(), 0.01f);
    }

    // ===================== State Tests =====================

    @Test
    @DisplayName("Has money - Returns true when gold available")
    public void testHasMoneyTrue() {
        mine.setGoldRemaining(100);

        assertTrue(mine.hasMoney());
    }

    @Test
    @DisplayName("Has money - Returns false when exhausted")
    public void testHasMoneyFalse() {
        mine.extractGold(INITIAL_CAPACITY);

        assertFalse(mine.hasMoney());
    }

    @Test
    @DisplayName("Is exhausted - Initial state")
    public void testIsExhaustedInitial() {
        assertFalse(mine.isExhausted());
    }

    @Test
    @DisplayName("Is exhausted - After extraction")
    public void testIsExhaustedAfterExtraction() {
        mine.extractGold(INITIAL_CAPACITY);

        assertTrue(mine.isExhausted());
    }

    // ===================== Setter Tests =====================

    @Test
    @DisplayName("Set gold capacity")
    public void testSetGoldCapacity() {
        mine.setGoldCapacity(500);

        assertEquals(500, mine.getGoldCapacity());
    }

    @Test
    @DisplayName("Set gold capacity - Reduces remaining if exceeds")
    public void testSetGoldCapacityReducesRemaining() {
        mine.setGoldRemaining(800);
        mine.setGoldCapacity(500);

        assertEquals(500, mine.getGoldRemaining());
    }

    @Test
    @DisplayName("Set gold remaining")
    public void testSetGoldRemaining() {
        mine.setGoldRemaining(300);

        assertEquals(300, mine.getGoldRemaining());
        assertFalse(mine.isExhausted());
    }

    @Test
    @DisplayName("Set gold remaining - Zero sets exhausted")
    public void testSetGoldRemainingZero() {
        mine.setGoldRemaining(0);

        assertEquals(0, mine.getGoldRemaining());
        assertTrue(mine.isExhausted());
    }

    @Test
    @DisplayName("Set spawn rate")
    public void testSetSpawnRate() {
        mine.setGoldSpawnRate(20.0f);

        assertEquals(20.0f, mine.getGoldSpawnRate());
    }

    @Test
    @DisplayName("Set spawn rate - Negative becomes 0")
    public void testSetSpawnRateNegative() {
        mine.setGoldSpawnRate(-5.0f);

        assertEquals(0, mine.getGoldSpawnRate());
    }

    // ===================== Reset Tests =====================

    @Test
    @DisplayName("Reset - Should restore to initial state")
    public void testReset() {
        mine.extractGold(500);
        mine.setActive(false);

        mine.reset();

        assertEquals(INITIAL_CAPACITY, mine.getGoldRemaining());
        assertFalse(mine.isExhausted());
    }

    // ===================== Position Tests =====================

    @Test
    @DisplayName("Position - Mine is static and doesn't move")
    public void testPositionStatic() {
        float initialX = mine.getX();
        float initialY = mine.getY();

        mine.update(10.0f);  // Long update shouldn't change position

        assertEquals(initialX, mine.getX());
        assertEquals(initialY, mine.getY());
    }

    @Test
    @DisplayName("Velocity - Mine has no velocity")
    public void testVelocityZero() {
        assertEquals(0, mine.getVelocity().x);
        assertEquals(0, mine.getVelocity().y);
    }

    // ===================== Size Tests =====================

    @Test
    @DisplayName("Size - Width and height set correctly")
    public void testSize() {
        assertEquals(1.5f, mine.getWidth(), 0.01f);
        assertEquals(1.5f, mine.getHeight(), 0.01f);
    }

    // ===================== Integration Tests =====================

    @Test
    @DisplayName("Integration - Miner mining scenario")
    public void testMinerMiningScenario() {
        // Miner extracts gold in cycles
        int miningCycles = 5;
        int goldPerCycle = 50;

        for (int i = 0; i < miningCycles; i++) {
            int extracted = mine.extractGold(goldPerCycle);
            assertEquals(goldPerCycle, extracted);
        }

        int remaining = INITIAL_CAPACITY - (miningCycles * goldPerCycle);
        assertEquals(remaining, mine.getGoldRemaining());
    }

    @Test
    @DisplayName("Integration - Full mining cycle with respawn")
    public void testFullMiningCycle() {
        GoldMine testMine = new GoldMine(0, 0, 100, 10.0f);  // 100 capacity, 10/sec

        // Extract all
        testMine.extractGold(100);
        assertTrue(testMine.isExhausted());

        // Wait for respawn (2 seconds = 20 gold)
        testMine.update(2.0f);
        assertEquals(20, testMine.getGoldRemaining());

        // Extract again
        int extracted = testMine.extractGold(20);
        assertEquals(20, extracted);
        assertEquals(0, testMine.getGoldRemaining());
    }

    @Test
    @DisplayName("Integration - Multiple miners working on same mine")
    public void testMultipleMinersOnSameMine() {
        GoldMine sharedMine = new GoldMine(0, 0, 1000, 0);  // No respawn for this test

        // Two miners extract
        int miner1Extract = sharedMine.extractGold(200);
        int miner2Extract = sharedMine.extractGold(300);

        assertEquals(200, miner1Extract);
        assertEquals(300, miner2Extract);
        assertEquals(500, sharedMine.getGoldRemaining());
    }
}

