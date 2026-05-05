# 📋 Model.Entities - Code Review & Analysis

## 📊 Tổng Quan Cấu Trúc

```
Model.Entities/
├── BaseEntity.java (123 lines)
│   └── Nền tảng cho tất cả entities
├── Combat/
│   ├── CombatEntity.java (145 lines)
│   │   ├── Enemy/
│   │   │   ├── Enemy.java (53 lines)
│   │   │   ├── PawnHacHoa.java (66 lines)
│   │   │   ├── WarriorHacHoa.java (66 lines)
│   │   │   └── TNT.java (70 lines)
│   │   └── Soldier/
│   │       ├── Soldier.java (62 lines)
│   │       ├── Pawn.java (65 lines)
│   │       ├── Warrior.java (61 lines)
│   │       ├── Archer.java (70 lines)
│   │       ├── Miner.java (65 lines)
│   │       ├── Healer.java
│   │       └── Lancer.java
```

**Tổng cộng**: ~650+ lines code, 4 tầng inheritance

---

## ✅ Điểm Mạnh

### 1. **Hierarchy Thiết Kế Tốt** 🏗️
```
BaseEntity (Position, Velocity, Collision)
    └── CombatEntity (Health, Attack, Team)
        ├── Enemy (Rewards)
        │   ├── PawnHacHoa
        │   ├── WarriorHacHoa
        │   └── TNT
        └── Soldier (Level, Experience)
            ├── Pawn
            ├── Warrior
            ├── Archer
            └── Miner
```

**Tốt vì:**
- ✅ Separation of concerns rõ ràng
- ✅ Code reuse qua inheritance
- ✅ Dễ thêm unit type mới

### 2. **Data-Driven Design** 📊
```java
// Sử dụng static arrays để quản lý stats per level
private static final float[] HEALTH_DATA = {150f, 300f, 600f};
private static final float[] DAMAGE_DATA = {15f, 35f, 80f};
private static final int[] UPGRADE_COST_DATA = {50, 150, 0};

// Tự động apply khi setLevel()
@Override
public void setLevel(int level) {
    super.setLevel(level);
    if (this.level > MAX_LEVEL) this.level = MAX_LEVEL;
    applyLevelData();  // Auto-apply stats
}
```

**Tốt vì:**
- ✅ Dễ tune balance
- ✅ Tránh magic numbers
- ✅ Support versioning/patches dễ

### 3. **Object Pooling Support** ♻️
```java
@Override
public void reset() {
    // Reset tất cả fields về default
    health = 0;
    maxHealth = 0;
    attackDamage = 0;
    // ...
}
```

**Tốt vì:**
- ✅ Performance optimization
- ✅ Garbage collection reduction
- ✅ Prepare cho pool pattern

### 4. **Combat Logic Validation** 🛡️
```java
// Health clamping
public void setHealth(float health) {
    this.health = Math.max(0, Math.min(health, maxHealth));
}

// Attack Speed normalization
public void setAttackSpeed(float attackSpeed) {
    this.attackSpeed = attackSpeed;
    this.cooldownDuration = attackSpeed > 0 ? 1f / attackSpeed : 0;
}

// Range validation
public void setAttackRange(float attackRange) {
    this.attackRange = Math.max(0, attackRange);
}
```

**Tốt vì:**
- ✅ Prevents invalid states
- ✅ No negative damage
- ✅ Safe division by zero

### 5. **Unit Roles Phân Biệt Rõ Ràng** 👥

| Unit | Role | HP | DMG | Range | Special |
|------|------|-----|-----|-------|---------|
| **Pawn** | Melee | 150 | 15 | 0.6 | Balanced |
| **Warrior** | Tank | 300 | 15 | 1.0 | High HP |
| **Archer** | Ranged | 80 | 12 | 4.0 | Arrow Speed |
| **Miner** | Economy | 80 | - | 0.3 | Gold per cycle |

**Tốt vì:**
- ✅ Distinct playstyles
- ✅ Balanced progression
- ✅ Strategic depth

### 6. **Clean Getters/Setters** 🔧
```java
// Safe setters with validation
public void setMaxHealth(float maxHealth) {
    this.maxHealth = Math.max(0, maxHealth);
    if (health > maxHealth) health = maxHealth;
}

// Semantic naming untuk Miner
public float getGoldPerCycle() {
    return attackDamage;  // Reusing attackDamage for gold
}
```

---

## ⚠️ Vấn Đề & Gợi Ý Cải Thiện

### 🔴 Issue 1: **Missing takeDamage() Implementation**
```java
// ❌ CombatEntity.java không có takeDamage()
// Mà attachment code thấy có takeDamage():
public void takeDamage(float amount) {
    if (dead) return;
    health -= amount;
    if (health <= 0) {
        health = 0;
        onDeath();
    }
}

// onDeath() cũng missing:
protected voi
```

**Fix cần:**
```java
public void takeDamage(float amount) {
    if (dead) return;
    health = Math.max(0, health - Math.max(0, amount));
    
    if (health <= 0) {
        onDeath();
    }
}

protected void onDeath() {
    dead = true;
    active = false;
    markRemoved();
}
```

### 🔴 Issue 2: **BaseEntity Comment Syntax Error**
```java
// ❌ Line 105-107 incomplete comment
/**
 * Khoảng cách thật (ít dùng trong loop)
 *
```

**Fix:**
```java
/**
 * Khoảng cách thật (ít dùng trong loop)
 */
public float dst(BaseEntity other) {
    return this.position.dst(other.position);
}
```

### 🟡 Issue 3: **Unit Range Data Inconsistency**
```java
// ❌ Miner sử dụng pixel units (30f), nhưng Pawn dùng world units (0.6f)
// core/src/main/java/.../Soldier/Miner.java:44
this.attackRange = 30f;  // Pixel units?

// core/src/main/java/.../Soldier/Pawn.java:16
private static final float[] RANGE_DATA = {0.6f, 0.7f, 0.8f};  // World units
```

**Fix:**
```java
// Standardize - choose ONE unit system
// Option A: World units (recommended)
this.attackRange = 0.5f;  // World units

// Option B: Document clearly
private static final float RANGE_UNIT = 0.5f;  // "pixels per unit"
```

### 🟡 Issue 4: **Miner.applyLevelData() Has Hard-coded Health**
```java
// ❌ Magic numbers
this.maxHealth = 80 + (index * 40);  // Hard-coded, not in static arrays

// Should be:
private static final float[] HEALTH_DATA = {80f, 120f, 160f};
// Then:
this.maxHealth = HEALTH_DATA[index];
```

### 🟡 Issue 5: **No takeDamage() in CombatEntity** 
Combat logic cần hàm để apply damage:
```java
// ✅ Add this to CombatEntity
public void takeDamage(float amount) {
    if (dead) return;
    
    // Apply damage with reduction/armor later
    health -= Math.max(0, amount);
    
    if (health <= 0) {
        onDeath();
    }
}

protected void onDeath() {
    dead = true;
    active = false;
    markRemoved();
}
```

### 🟡 Issue 6: **Missing State Management**
```java
// ⚠️ CombatEntity có currentState nhưng:
// - Không được sử dụng trong BaseEntity
// - Các state transition logic ở đâu?
// - Không có updateState() hoặc validation

// Suggest:
public void setState(State state) {
    if (dead && state != State.DYING) return;
    this.currentState = state;
}

public State getState() {
    return currentState;
}
```

### 🟡 Issue 7: **CombatEntity Missing takeDamage Parameter Naming**
BaseEntity constructor không khởi tạo velocity và position thứ tự:
```java
// Có thể gây confuse
public BaseEntity() {
    this.position = new Vector2();
    this.velocity = new Vector2();
    this.active = true;
    this.removed = false;
}
```

---

## 🚀 Recommendations

### Priority 1: **Critical** 🔴
1. ✅ Add `takeDamage()` & `onDeath()` methods
2. ✅ Fix BaseEntity comment syntax error
3. ✅ Standardize unit range (world units vs pixels)
4. ✅ Add `setState()` getter/setter

### Priority 2: **Important** 🟡
1. Extract Miner health data to static array
2. Add `currentState` getter/setter
3. Add JavaDoc cho abstract methods
4. Add `isAlive()` convenience method

### Priority 3: **Nice to Have** 🟢
1. Add serialization support (save/load)
2. Add event system (onDeath, onLevelUp)
3. Add stat modification multipliers (buffs/debuffs)
4. Add Factory pattern untuk unit creation

---

## 🧪 Test Coverage Needed

```java
// ✅ BaseEntity Tests
- testDistance calculation (dst2)
- testCollision detection (overlaps)
- testReset functionality

// ✅ CombatEntity Tests
- testTakeDamage and health clamping
- testDeath state transition
- testCooldown timer logic
- testTarget range checking

// ✅ Unit Type Tests
- testLevelProgression (1 -> 3)
- testStatScaling per level
- testUpgradeCosts

// ✅ Balance Tests
- testEnemy vs Soldier difficulty curve
- testDPS comparisons
- testWin conditions
```

---

## 📊 Architecture Diagram

```
┌─────────────────────────────────────────┐
│         BaseEntity (Abstract)           │
│  - position, velocity                   │
│  - collision detection                  │
│  - object pooling                       │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│      CombatEntity (Abstract)            │
│  - health / maxHealth                   │
│  - attackDamage, attackRange            │
│  - cooldown system                      │
│  - team (SOLDIER/ENEMY)                 │
└──────┬────────────────────────┬─────────┘
       │                        │
   ┌───▼───────┐            ┌──▼─────┐
   │   Enemy   │            │Soldier │
   └───┬───────┘            └──┬─────┘
       │                       │
   ┌───┴─┬─────┬──────┐   ┌───┴──┬──────┬────┬──────┐
   │     │     │      │   │      │      │    │      │
Pawn  War  TNT Archer Pawn Warrior Archer Miner Healer
Hac   Hac                        │
Hoa   Hoa                     Lancer
```

---

## 💡 Best Practices Applied

| Practice | Status | Location |
|----------|--------|----------|
| Object Pooling | ✅ | BaseEntity.reset() |
| Data-Driven Config | ✅ | static arrays per unit |
| Input Validation | ✅ | Math.max/min checks |
| Inheritance Hierarchy | ✅ | 4-level clean hierarchy |
| Enum for Types | ✅ | Team, State enums |
| Getter/Setter Encapsulation | ✅ | All fields protected |
| Semantic Naming | ✅ | Miner.getGoldPerCycle() |

---

## 🎯 Conclusion

**Overall Grade: A- (9/10)**

### Strengths
- ✅ Well-structured inheritance hierarchy
- ✅ Clean data-driven design
- ✅ Object pool ready
- ✅ Good validation & clamping

### Areas for Improvement
- ⚠️ Missing damage/death methods
- ⚠️ Inconsistent unit systems (pixels vs world units)
- ⚠️ Limited state management
- ⚠️ Needs more test coverage

### Next Steps
1. Implement takeDamage() and onDeath()
2. Standardize coordinate systems
3. Add proper state machine
4. Create comprehensive tests
5. Document public interfaces


