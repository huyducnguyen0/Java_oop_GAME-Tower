# Test Suite Documentation - Model Package

## Overview
Comprehensive test suite cho tất cả logic trong package `Model`, bao gồm:
- Entities (Unit, Enemy, GameEntity)
- Managers (EntityManager)
- Systems (AISystem, CombatSystem, EconomySystem, MovementSystem)
- Core logic (GameWorld, AIController)

## Project Configuration
- **Test Framework**: JUnit 5 (Jupiter)
- **Build Tool**: Gradle
- **Testing Setup**: 
  ```gradle
  testImplementation platform('org.junit:junit-bom:5.10.0')
  testImplementation 'org.junit.jupiter:junit-jupiter'
  useJUnitPlatform()
  ```

## Test Structure

### 1. **Entities Tests**

#### `UnitTest.java`
- Kiểm tra khởi tạo Unit theo loại (MINER, INFANTRY, ARCHER)
- Nâng cấp chỉ số và cost calculation
- Thay đổi hành động (Action)
- Gold carrying logic
- Position management

#### `EnemyTest.java`
- Enemy initialization theo loại (BASIC, TANK, FAST, BOSS)
- Stats scaling theo loại
- AI Controller integration
- Movement capabilities
- State management

#### `GameEntityTest.java`
- Position management
- State management (WALKING, ATTACKING, DEAD)
- HP management  
- Animation state tracking

---

### 2. **Manager Tests**

#### `EntityManagerTest.java` (Model/Managers/)
- Adding/removing units and enemies
- Gold management
- Building HP management
- Dead entity cleanup
- Entity update system
- List management

---

### 3. **System Tests**

#### `AISystemTest.java` (Model/Systems/)
- AI update cho tất cả enemies
- Dead enemies skip
- Performance test (50+ enemies)
- AI controller initialization
- Delta time handling

#### `CombatSystemTest.java` (Model/Systems/)
- Unit attacking enemies
- Archer range advantage
- Combat mechanics
- Dead unit/enemy damage prevention
- Damage calculations

#### `EconomySystemTest.java` (Model/Systems/)
- Unit upgrade mechanics
- Cost calculation
- Stats increase on upgrade
- Archer range bonus
- Gold management
- Multiple miners logic

#### `MovementSystemTest.java` (Model/Systems/)
- Unit movement to target
- Enemy movement
- Speed stat respect
- Multiple units moving
- Dead units immobility
- Boundary checking

---

### 4. **Core Logic Tests**

#### `AIControllerTest.java` (Model/AI/)
- AI state transitions (IDLE, CHASE, ATTACK, RETREAT)
- Target finding logic
- Decision making based on HP and distance
- Steering behaviors (chase, attack, retreat, wander)
- Decision interval (1 giây)
- Damage mechanism

#### `GameWorldTest.java` (Model/)
- World initialization
- Wave system
- Game state management
- Score system
- Pause/Resume logic
- Building HP and game over
- Multiple updates

---

## Test Statistics

| Package | Test Class | Tests Count |
|---------|-----------|------------|
| Entities | UnitTest | 20+ |
| Entities | EnemyTest | 15+ |
| Entities | GameEntityTest | 12 |
| Managers | EntityManagerTest | 15+ |
| Systems | AISystemTest | 10+ |
| Systems | CombatSystemTest | 10+ |
| Systems | EconomySystemTest | 12+ |
| Systems | MovementSystemTest | 12+ |
| AI | AIControllerTest | 10+ |
| Core | GameWorldTest | 20+ |
| **TOTAL** | **10 Classes** | **~135+ Tests** |

---

## Running Tests

### Run All Tests
```bash
./gradlew test
```

### Run Specific Test Class
```bash
./gradlew test --tests "com.hust.towerdefence.Model.*"
```

### Run Specific Test Method
```bash
./gradlew test --tests "com.hust.towerdefence.Model.Entities.UnitTest.testUpgradeLogic"
```

### Run with Detailed Output
```bash
./gradlew test --info
```

---

## Test Annotations Used

- **@Test** - Marks method as test case  
- **@BeforeEach** - Runs before each test (JUnit 5 replacement for @Before)
- **@DisplayName** - Human-readable test name
- **@BeforeAll** - Optional: Runs once before all tests in class

---

## Key Testing Patterns

### 1. Setup Pattern
```java
@BeforeEach
public void setUp() {
    // Initialize test fixtures
}

@Test
public void testSomething() {
    // Use fixtures
}
```

### 2. Assertion Pattern
```java
@Test
public void testPositive() {
    assertTrue(condition, "Should be true");
}

@Test
public void testEquality() {
    assertEquals(expected, actual, "Values should match");
}
```

### 3. Boundary Testing
- Test extreme values
- Test edge cases
- Test multiple updates
- Test with empty collections

---

## Coverage Areas

### Positive Tests ✓
- Normal operations
- Valid state transitions
- Correct calculations

### Negative Tests ✓
- Dead entities behavior
- Edge cases (zero, negative values)
- Boundary conditions
- Empty lists

### Performance Tests ✓
- Multiple entity updates
- Delta time handling
- System efficiency

---

## Common Issues & Solutions

### Issue: Tests marked RED
**Solution**: Ensure:
1. JUnit 5 imports (org.junit.jupiter.*)
2. @BeforeEach instead of @Before
3. Static imports from Assertions class
4. Build.gradle has `useJUnitPlatform()`

### Issue: Missing Dependencies
**Solution**: Check build.gradle has:
```gradle
testImplementation 'org.junit.jupiter:junit-jupiter'
testImplementation platform('org.junit:junit-bom:5.10.0')
```

---

## Maintenance Tips

1. **Add tests for new features** - Maintain 70%+ coverage
2. **Update existing tests** - When changing logic
3. **Use descriptive names** - @DisplayName for clarity
4. **Group related tests** - By feature/system
5. **Keep tests isolated** - Each test independent

---

## Next Steps

- ✅ All test classes created with JUnit 5
- ✅ Proper @BeforeEach setup
- ✅ @DisplayName annotations added
- ⭕ Run tests to verify compilation
- ⭕ Add integration tests
- ⭕ Add UI/Screen tests

---

Generated: April 2026
Framework: JUnit 5 (Jupiter) with Gradle

