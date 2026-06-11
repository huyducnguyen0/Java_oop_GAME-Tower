# Phan tich OOP cho project TowerDefence

Tai lieu nay phan tich cach project game TowerDefence ap dung lap trinh huong doi tuong (Object-Oriented Programming - OOP). Muc tieu la giup thuyet trinh tren lop OOP mot cach ro rang: khong chi noi "co ke thua, co dong goi", ma chi ra tung class nao dang lam viec gi, vi sao thiet ke do co tinh OOP, va object phoi hop voi nhau trong runtime nhu the nao.

Project duoc viet bang Java + LibGDX. Ve mat kien truc, code chia thanh cac vung chinh:

```text
core/src/main/java/com/hust/towerdefence/
├── MainGame.java
├── Model/
│   ├── GameWorld.java
│   ├── AI/
│   ├── Entities/
│   ├── Managers/
│   └── Systems/
└── View/
    ├── screens/
    └── ui/

lwjgl3/src/main/java/com/hust/towerdefence/lwjgl3/
└── Lwjgl3Launcher.java
```

Noi dung can nam nhanh:

- `Model` la tang domain/gameplay logic: entity, manager, system, AI.
- `View` la tang hien thi va tuong tac: screen, HUD, renderer, audio.
- `GameWorld` la object trung tam ghep cac manager/system lai voi nhau.
- `BaseEntity -> CombatEntity -> Soldier/Enemy/BaseTower -> class cu the` la truc ke thua OOP quan trong nhat cua project.
- Project khong phai ECS thuan tuyet doi. No la hybrid: entity duoc mo hinh hoa bang OOP inheritance, con logic frame-by-frame duoc tach thanh cac `System`.

---

## 1. Ban do class quan trong

### 1.1 Entry point va game lifecycle

| File | Vai tro OOP |
|---|---|
| `lwjgl3/.../Lwjgl3Launcher.java` | Desktop launcher, tao `Lwjgl3Application` va nap `MainGame`. |
| `MainGame.java` | Ke thua `Game` cua LibGDX, quan ly screen, asset, audio, input. |
| `View/screens/MainMenuScreen.java` | Implements `Screen`, the hien tinh da hinh voi lifecycle `show`, `render`, `resize`, `dispose`. |
| `View/screens/DemoModelScreen.java` | Ke thua `ScreenAdapter`, implements `GameHud.PauseMenuListener`, la gameplay screen map 1. |
| `View/screens/DemoModelScreen2.java` | Gameplay screen map 2, cau truc tuong tu map 1 nhung khac `MAP_PATH`, `AI_LEVEL`, `GAME_SPEED`. |

### 1.2 Tang Model

| File | Vai tro OOP |
|---|---|
| `Model/GameWorld.java` | Object trung tam cua tran dau. Compose managers + systems + towers. |
| `Model/Entities/BaseEntity.java` | Abstract base class cho moi doi tuong co vi tri/kich thuoc/team/trang thai active. |
| `Model/Entities/Combat/CombatEntity.java` | Abstract base class cho cac entity co mau, tan cong, target, state, path. |
| `Model/Entities/Combat/Soldier/Soldier.java` | Abstract class cho linh phe nguoi choi. |
| `Model/Entities/Combat/Enemy/Enemy.java` | Abstract class cho linh dich, co reward va waypoint index. |
| `Model/Entities/Tower/BaseTower.java` | Abstract class cho tower co kha nang combat. |
| `Model/Entities/Tower/MainTower.java` | Nha chinh cua moi phe. Khi chet thi ket thuc tran. |
| `Model/Entities/Tower/DefenseTower.java` | Thap phong thu co level, upgrade, animation pha huy. |
| `Model/Entities/Projectile/Projectile.java` | Projectile la mot dang `CombatEntity`; `Arrow` ke thua tu no. |

### 1.3 Soldiers va enemies

| Class | Ke thua | Vai tro gameplay |
|---|---|---|
| `Pawn` | `Soldier` | Linh can chien co ban. |
| `Warrior` | `Soldier` | Linh tank, mau lon, damage vua/thap. |
| `Archer` | `Soldier` | Linh tam xa, co `arrowSpeed`. |
| `Healer` | `Soldier` | Don vi ho tro, tai su dung `attackDamage` nhu heal amount. |
| `Lancer` | `Soldier` | Linh can chien tam xa hon, danh cham, sat thuong cao. |
| `Miner` | `Soldier` | Don vi kinh te, di dao vang, mang vang ve nha. |
| `PawnHacHoa` | `Enemy` | Enemy co ban/tan cong nhanh. |
| `WarriorHacHoa` | `Enemy` | Enemy tank. |
| `TNT` | `Enemy` | Enemy nem bom, co `bombSpeed`. |

### 1.4 Managers, Systems, AI, View

| Nhom | File | Vai tro OOP |
|---|---|---|
| Manager | `EntityManager` | Quan ly collection entity theo kieu type-safe: soldiers, enemies, towers, allEntities. |
| Manager | `MapManager` | Dong goi viec load map, waypoint, building zone, convert toa do. |
| Manager | `EconomyManager` | Singleton quan ly vang, mua linh, cheat, maxGold. |
| Manager | `BuildingZone` | Value object cho vung cong trinh tren map. |
| System | `TargetingSystem` | Tim target cho combat entity. |
| System | `MovementSystem` | Dieu khien di chuyen, path, miner state machine. |
| System | `AttackSystem` | Xu ly cooldown, heal, damage, visual combat events. |
| System | `HealthSystem` | Xu ly damage/heal/death/game over/victory. |
| AI | `AIController` | Sinh wave, tinh budget, chon enemy theo score. |
| AI | `AIPersonality` | Dong goi weight ngau nhien cua AI. |
| AI | `SpawnDecision` | DTO + enum cho quyet dinh spawn. |
| View | `GameHud` | HUD, pause/result overlay, callback interface. |
| View | `PurchasePanel` | Panel mua/nang cap linh, map building -> unit option. |
| View | `TowerInfoPanel` | Panel nang cap defense tower. |
| View | `UnitRenderer` | Render animation linh dua tren runtime type va state. |
| View | `MainTowerRenderer`, `DefenseTowerRenderer`, `WorldHudRenderer`, `CombatEffectRenderer` | Tach tung nhom render rieng de giam coupling. |

---

## 2. Cay ke thua entity

Day la phan nen ve OOP cua project. Co the ve len slide nhu sau:

```text
BaseEntity (abstract)
├── CombatEntity (abstract, implements Poolable)
│   ├── Soldier (abstract)
│   │   ├── Pawn
│   │   ├── Warrior
│   │   ├── Archer
│   │   ├── Healer
│   │   ├── Lancer
│   │   └── Miner
│   ├── Enemy (abstract)
│   │   ├── PawnHacHoa
│   │   ├── WarriorHacHoa
│   │   └── TNT
│   ├── BaseTower (abstract)
│   │   ├── MainTower
│   │   └── DefenseTower
│   └── Projectile
│       └── Arrow
└── GoldMine
```

Y nghia cua tung tang:

- `BaseEntity` tra loi cau hoi: "Doi tuong nay co ton tai trong world khong?". No co `id`, `position`, `width`, `height`, `rotation`, `active`, `removed`, `team`.
- `CombatEntity` tra loi cau hoi: "Doi tuong nay co tham gia combat khong?". No them `health`, `maxHealth`, `attackDamage`, `attackRange`, `attackSpeed`, `cooldown`, `targetId`, `path`, `facing`, `State`.
- `Soldier` tra loi cau hoi: "Combat entity nay thuoc phe nguoi choi khong?". Constructor gan `team = SOLDIER`, `currentState = IDLE`, `level = 1`.
- `Enemy` tra loi cau hoi: "Combat entity nay thuoc phe dich khong?". Constructor gan `team = ENEMY`, them `goldReward`, `expReward`.
- `BaseTower` tra loi cau hoi: "Combat entity nay la cong trinh dung yen tren map khong?". Constructor nhan `Vector2 position`.
- Class cu the nhu `Archer`, `Miner`, `TNT`, `DefenseTower` tra loi cau hoi: "Doi tuong nay co thong so va hanh vi rieng gi?".

Day la vi du rat tot cho thuyet trinh: mot class con khong phai viet lai moi thu. Vi du `Archer` khong can tu tao lai `position`, `health`, `targetId`, `path`, `cooldown`. Tat ca duoc ke thua tu `BaseEntity` va `CombatEntity`; `Archer` chi them nhung gi lam no khac cac linh khac: mang stat rieng, `arrowSpeed`, override `setLevel`, override `reset`.

---

## 3. Truc cot 1: Encapsulation - Dong goi

Dong goi nghia la du lieu ben trong object khong bi truy cap lung tung. Object tu bao ve invariant cua no thong qua constructor, getter/setter, private method, va API co y nghia.

### 3.1 `BaseEntity`: dong goi nhan dang, vi tri, kich thuoc

File: `Model/Entities/BaseEntity.java`

`BaseEntity` co cac field:

```java
protected long id;
private static long nextId = 0;
protected final Vector2 position;
protected float width;
protected float height;
protected float rotation;
protected boolean active;
protected boolean removed;
protected Team team;
```

Phan hay:

- `id` duoc tao tu `nextId++` trong constructor, moi object co danh tinh rieng.
- `position` la `final Vector2`, nghia la reference khong bi thay doi sau constructor.
- `removed` khong set truc tiep tu ben ngoai ma qua `markRemoved()`.
- `overlaps(BaseEntity other)` dong goi cong thuc va cham AABB vao trong entity. Noi khac chi can goi method, khong can biet cong thuc chi tiet.
- `dst2(BaseEntity other)` dong goi cach tinh khoang cach binh phuong de toi uu, tranh can bac hai khi khong can.

Vi du de thuyet trinh:

> Thay vi moi system tu tinh collision bang nhieu dong code, project dua logic nay vao `BaseEntity.overlaps()`. Do la dong goi hanh vi gan lien voi du lieu cua entity.

Luu y co the noi neu bi hoi ve diem chua hoan hao:

- `getPosition()` tra ve chinh object `Vector2` noi bo, nen code ben ngoai co the sua `position` truc tiep. Trong game, dieu nay duoc dung co chu dich de `MovementSystem` cap nhat vi tri nhanh. Neu muon dong goi chat hon, co the tra ve ban copy nhu `MapManager` dang lam.

### 3.2 `CombatEntity`: setter bao ve chi so combat

File: `Model/Entities/Combat/CombatEntity.java`

`CombatEntity` dong goi cac chi so quan trong:

- `setHealth(float health)` clamp mau ve khoang `[0, maxHealth]`.
- `setMaxHealth(float maxHealth)` khong cho maxHealth am, neu health hien tai lon hon maxHealth thi ha ve maxHealth.
- `setAttackDamage(float attackDamage)` khong cho damage am.
- `setAttackRange(float attackRange)` khong cho range am.
- `setAttackSpeed(float attackSpeed)` vua gan attackSpeed, vua tinh lai `cooldownDuration`.
- `setFacing(float x, float y)` bo qua vector gan 0 va normalize huong nhin.
- `setLevel(int level)` dam bao level it nhat la 1.

Y nghia OOP:

- Code ben ngoai khong can nho "mau khong duoc am", "cooldownDuration = 1 / attackSpeed". Object tu giu luat cua no.
- Khi `AttackSystem` goi `attacker.setCooldownTimer(attacker.getCooldownDuration())`, no khong can biet cooldown duoc tinh tu attackSpeed nhu the nao.

Vi du de noi tren lop:

> `CombatEntity` khong chi la noi chua field. No bao ve tinh hop le cua field. Neu set mau vuot max, object tu cat xuong max. Neu set attack speed, object tu cap nhat cooldown duration. Day la dong goi du lieu + quy tac nghiep vu.

### 3.3 `DefenseTower`: state pha huy duoc an sau method

File: `Model/Entities/Tower/DefenseTower.java`

`DefenseTower` co field:

```java
private final String mapName;
private boolean destroying;
private float destroyTimer;
```

Ben ngoai khong duoc tu y set `destroying = true`. Muon pha huy tower phai goi:

- `beginDestroying()`
- `updateDestroying(delta)`
- `isDestroying()`
- `getDestroyProgress()`
- `isDestroyFinished()`

`beginDestroying()` lam mot goi thay doi nhat quan:

- set `destroying = true`
- reset `destroyTimer`
- `setActive(false)`
- `setState(State.DYING)`
- clear target bang `setTargetId(-1)`

Neu khong dong goi, moi noi trong code co the quen mot buoc, vi du quen set state `DYING`, dan den renderer/system xu ly sai. Dong goi giup hanh vi pha huy tower luon nhat quan.

### 3.4 `MapManager`: tra ve ban copy de bao ve du lieu map

File: `Model/Managers/MapManager.java`

Day la vi du dong goi rat dep:

- `getPlayerCastlePosition()` tra ve `playerMainTower.cpy()`.
- `getEnemyBasePosition()` tra ve `enemyMainTower.cpy()`.
- `getPlayerTowers()` tao `Array<Vector2>` moi va copy tung `Vector2`.
- `getPlayerBuildingZones()` tao `BuildingZone` moi tu bounds copy.
- `getPlayerMainTowerZone()`, `getEnemyMainTowerZone()`, `getPlayerTowerZones()`, `getEnemyTowerZones()` deu dung `copyZone()`/`copyZones()`.

Y nghia:

- `MapManager` giu du lieu map goc.
- Screen/System co the doc vi tri, nhung khong vo tinh sua du lieu map noi bo.
- Day la defensive copying, mot ky thuat dong goi quan trong khi field la object mutable nhu `Vector2`, `Rectangle`.

Co the noi:

> `MapManager` khong dua thang object goc ra ngoai. No dua ban copy. Nhu vay object quan ly map van lam chu du lieu cua no.

### 3.5 `BuildingZone`: value object cho vung cong trinh

File: `Model/Managers/BuildingZone.java`

`BuildingZone` co:

```java
private final String name;
private final Rectangle bounds;
```

API ben ngoai:

- `getName()`
- `getBounds()` tra ve `new Rectangle(bounds)`
- `contains(float x, float y)`
- `getCenter()`
- `isPurchaseBuilding()`
- `isDefenseTower()`
- `getDisplayName()`

OOP o day nam o viec `BuildingZone` khong chi la data rectangle. No biet y nghia cua vung do:

- Neu name la `barrack`, `archery`, `monastery`, `house1`, `house2` thi do la nha mua linh.
- Neu name la `tower1`, `tower2`, `enemytower1`, `enemytower2` thi do la tower.
- UI khong can tu viet logic string lung tung, chi can goi `zone.isPurchaseBuilding()` hoac `zone.isDefenseTower()`.

### 3.6 `EconomyManager`: dong goi tai nguyen vang bang Singleton

File: `Model/Managers/EconomyManager.java`

`EconomyManager` co private constructor:

```java
private EconomyManager(int initialGold, int maxGold)
```

Tao object qua:

```java
EconomyManager.getInstance(initialGold, maxGold)
EconomyManager.getInstance()
```

No dong goi cac rule:

- Vang ban dau khong vuot `maxGold`.
- `addGold(amount)` khong cong neu amount <= 0, va khong cho vang vuot max.
- `spendGold(amount)` khong tru tien neu amount am, khong du vang thi return false.
- Neu `cheatsEnabled`, `spendGold` return true nhung khong tru vang.
- `setGold` clamp ve `[0, maxGold]`.

Y nghia:

> Tat ca logic lien quan vang tap trung trong mot object. `GameWorld`, `PurchasePanel`, `MovementSystem` khong tu tru vang bang `gold -= cost`; chung goi API cua `EconomyManager`.

### 3.7 `CombatVisualEvent`: immutable-style event object

File: `Model/Systems/CombatVisualEvent.java`

`CombatVisualEvent` co field `final`:

```java
private final Type type;
private final BaseEntity.Team team;
private final Vector2 start;
private final Vector2 end;
```

Constructor copy vector:

```java
this.start = start.cpy();
this.end = end.cpy();
```

Getter cung tra ve copy:

```java
public Vector2 getStart() { return start.cpy(); }
public Vector2 getEnd() { return end.cpy(); }
```

Day la dong goi chat hon `BaseEntity.getPosition()`: event da tao ra thi khong bi noi khac sua toa do bat ngo. Rat hop voi kieu event truyen tu logic sang renderer.

---

## 4. Truc cot 2: Inheritance - Ke thua

Ke thua trong project khong chi de "gop code", ma de mo hinh hoa moi quan he "is-a":

- `Archer is a Soldier`
- `Soldier is a CombatEntity`
- `CombatEntity is a BaseEntity`
- `DefenseTower is a BaseTower`
- `BaseTower is a CombatEntity`
- `MainTower is a BaseTower`
- `TNT is an Enemy`

### 4.1 `BaseEntity` la lop cha goc

`BaseEntity` chua nhung thu moi entity deu can:

- `id`: dinh danh object.
- `position`: vi tri trong world.
- `width`, `height`: kich thuoc render/collision.
- `rotation`: huong quay.
- `active`, `removed`: lifecycle.
- `team`: phe.
- `overlaps`, `dst2`: hanh vi vat ly co ban.

Nhung `BaseEntity` la `abstract`, nghia la khong tao truc tiep mot "BaseEntity" vo danh. No la mo hinh chung de class con cu the hoa.

### 4.2 `CombatEntity` mo rong entity thanh doi tuong chien dau

`CombatEntity extends BaseEntity implements Poolable`.

No them:

- Mau: `health`, `maxHealth`
- Tan cong: `attackDamage`, `attackRange`, `attackSpeed`, `cooldownTimer`, `cooldownDuration`
- Muc tieu: `targetId`
- Di chuyen: `path`, `currentPathIndex`, `facing`, `speed`
- Trang thai: enum `State` gom `IDLE`, `MOVING`, `ATTACKING`, `HEALING`, `DYING`, `GOING_TO_MINE`, `MINING`, `RETURNING_HOME`

Tai sao thiet ke nay hop ly:

- Soldier, enemy, tower, projectile deu co the can health/damage/target/cooldown.
- Systems co the xu ly tat ca bang kieu `CombatEntity`, khong can viet rieng `updatePawn`, `updateTNT`, `updateTower`.

### 4.3 `Soldier` va `Enemy` tach hai nhanh phe

`Soldier`:

- set `team = Team.SOLDIER`
- set `level = 1`
- set `currentState = State.IDLE`
- co `upgradeCost`

`Enemy`:

- set `team = Team.ENEMY`
- set `level = 1`
- co `goldReward`, `expReward`
- co `currentWaypointIndex`

Day la ke thua theo y nghia domain:

- Linh nguoi choi va linh dich cung la combat entity.
- Nhung linh nguoi choi co upgrade cost, linh dich co reward.
- Neu tat ca nhat vao `CombatEntity`, class do se phinh to va chua nhieu field khong dung cho moi entity.

### 4.4 Class linh cu the ke thua va specialize

Moi class linh co pattern tuong tu:

- Static array chua data level.
- Constructor set size va goi `applyLevelData()`.
- `applyLevelData()` cap nhat `maxHealth`, `health`, `attackDamage`, `attackRange`, `attackSpeed`, `upgradeCost`.
- Override `setLevel()` de clamp level toi da 3 va cap nhat stat.
- Override `reset()` de reset ve trang thai dung.

Vi du `Archer`:

- Ke thua `Soldier`.
- Them `arrowSpeed`.
- Co `ARROW_SPEED_DATA`.
- `getArrowSpeed()` va `setArrowSpeed()`.
- Range cao hon, attack speed nhanh hon.

Vi du `Miner`:

- Ke thua `Soldier`.
- Them `miningTimer`, `carriedGold`.
- Tai su dung `attackDamage` nhu `goldPerCycle`.
- State ban dau la `GOING_TO_MINE`, khac linh chien dau.

Vi du `Healer`:

- Ke thua `Soldier`.
- Tai su dung `attackDamage` nhu heal amount.
- `getHealAmount()` tra ve `attackDamage`.
- Khi target la ally bi thuong thi `AttackSystem` heal, khi target la enemy thi ap dung poison damage.

### 4.5 Tower cung la combat entity

`BaseTower extends CombatEntity`.

Y nghia:

- Tower co health, target, attackDamage, attackRange, cooldown nhu unit.
- `TargetingSystem`, `MovementSystem`, `AttackSystem`, `HealthSystem` co the xu ly tower cung voi unit bang `CombatEntity`.

`MainTower`:

- la tower dac biet.
- attackDamage/range/speed = 0.
- neu chet thi game over/victory.

`DefenseTower`:

- co level.
- co upgrade.
- co state pha huy rieng.
- co stat theo level.

### 4.6 Ke thua tu LibGDX

OOP khong chi nam trong model tu viet, ma con trong framework:

- `MainGame extends Game`: project ke thua lifecycle cua LibGDX game.
- `MainMenuScreen implements Screen`: screen menu bat buoc implement `show`, `render`, `resize`, `dispose`, `pause`, `resume`, `hide`.
- `DemoModelScreen extends ScreenAdapter`: chi override nhung lifecycle method can dung.
- `CombatEntity implements Poolable`: tuan theo contract cua LibGDX pool, co `reset()`.

Day la cach project tan dung polymorphism cua framework. LibGDX khong can biet screen cu the la menu hay gameplay; no goi method lifecycle theo interface/base class.

---

## 5. Truc cot 3: Polymorphism - Da hinh

Da hinh nghia la cung mot kieu tham chieu cha/interface, nhung object that ben duoi co the la nhieu class con khac nhau. Khi goi method, hanh vi dung cua class con duoc chon luc runtime.

### 5.1 Systems xu ly `CombatEntity` thay vi tung class cu the

File: `Model/Systems/TargetingSystem.java`

`TargetingSystem.update()` lay:

```java
Array<CombatEntity> combatants = entityManager.getAllActiveCombatUnits();
```

Trong array nay co the co:

- `Pawn`
- `Warrior`
- `Archer`
- `Healer`
- `Lancer`
- `PawnHacHoa`
- `WarriorHacHoa`
- `TNT`
- `DefenseTower`

Nhung system chi can goi API chung:

- `entity.isDead()`
- `entity.isRemoved()`
- `entity.getTargetId()`
- `entity.setTargetId(...)`
- `entity.getTeam()`
- `entity.getAttackRange()`
- `entity.getPosition()`

Day la da hinh trong xu ly collection:

> Mot danh sach `CombatEntity` co the chua nhieu class con. System khong can biet day la `Archer` hay `TNT` de kiem tra mau, team, range, target.

### 5.2 Dynamic dispatch khi goi `setLevel`

Trong `PurchasePanel`, sau khi mua linh, code lay linh moi nhat bang kieu cha:

```java
Soldier newest = gameWorld.getEntityManager().getSoldiers().peek();
newest.setLevel(currentLv);
```

Tai compile-time, `newest` la `Soldier`.

Tai runtime, object that co the la:

- `Warrior`
- `Archer`
- `Healer`
- `Lancer`
- `Miner`

Moi class con override `setLevel()`. Vi vay:

- Neu object that la `Archer`, se goi `Archer.setLevel()`.
- Neu object that la `Miner`, se goi `Miner.setLevel()`.
- Neu object that la `Healer`, se goi `Healer.setLevel()`.

Day la da hinh chuan Java. UI khong can viet code rieng de set stat cho tung class sau khi mua; chi goi `setLevel()` tren `Soldier`.

### 5.3 Override `reset()` theo tung class

`CombatEntity` implements `Poolable`, nen co `reset()`.

`Soldier.reset()` goi `super.reset()` roi reset them `team`, `level`, `upgradeCost`.

Moi linh cu the override:

- `Pawn.reset()`
- `Warrior.reset()`
- `Archer.reset()`
- `Healer.reset()`
- `Lancer.reset()`
- `Miner.reset()`
- `TNT.reset()`
- `PawnHacHoa.reset()`
- `WarriorHacHoa.reset()`
- `Projectile.reset()`
- `Arrow.reset()`

Y nghia:

- Cung ten method `reset()`.
- Moi class reset phan du lieu rieng cua no.
- Khi dung pool hoac reset object, Java se goi dung method class con.

### 5.4 `Screen` va `ScreenAdapter`

`MainGame` quan ly screen qua map:

```java
private final Map<Class<? extends Screen>, Screen> screenCache = new HashMap<>();
```

`screenCache` chua bat ky object nao implement `Screen`.

- `MainMenuScreen` implements `Screen`.
- `DemoModelScreen` extends `ScreenAdapter`, ma `ScreenAdapter` implement `Screen`.

Khi `MainGame.setScreen(Class<? extends Screen> screenClass)` goi:

```java
super.setScreen(screen);
```

LibGDX se goi lifecycle dung theo object cu the. Day la da hinh framework:

- Menu render background/menu.
- Gameplay render map/entities/HUD.
- Cung la `Screen`, hanh vi runtime khac nhau.

### 5.5 Interface `GameHud.PauseMenuListener`

File: `View/ui/GameHud.java`

`GameHud` dinh nghia interface:

```java
public interface PauseMenuListener {
    void onResumeRequested();
    void onRestartRequested();
    void onQuitToMenuRequested();
}
```

`DemoModelScreen` va `DemoModelScreen2` implements interface nay.

`GameHud` khong can biet no dang nam trong map 1 hay map 2. Khi nguoi choi bam:

- Continue -> `pauseMenuListener.onResumeRequested()`
- Restart -> `pauseMenuListener.onRestartRequested()`
- Quit To Menu -> `pauseMenuListener.onQuitToMenuRequested()`

Day la da hinh qua interface:

> HUD chi phat tin hieu "nguoi choi muon restart". Screen cu the tu quyet dinh restart map nao.

Neu khong co interface, `GameHud` co the phai phu thuoc truc tiep vao `DemoModelScreen`, lam tang coupling va kho tai su dung cho `DemoModelScreen2`.

### 5.6 Da hinh ket hop type checking trong renderer/system

Mot so noi dung da hinh nhung van can `instanceof` do game co hanh vi dac biet:

- `TargetingSystem`: `Healer` tim ally bi thuong, `Miner` bi bo qua trong combat targeting, `BaseTower` chi target soldier/enemy.
- `MovementSystem`: `BaseTower` khong di chuyen, `Miner` co state machine rieng, unit thuong follow path/attack.
- `AttackSystem`: `Healer` heal/poison, `Archer` va `DefenseTower` tao event arrow, `TNT` tao event dynamite.
- `UnitRenderer`: chon sprite clip theo runtime type (`Miner`, `Archer`, `Warrior`, `Healer`, `Lancer`, `TNT`, `PawnHacHoa`, `Pawn`) va state.

Day la cach lam thuc dung trong game: da so hanh vi chung di qua base class, nhung nhung case co animation/logic dac biet van can phan nhanh.

Khi thuyet trinh, co the noi:

> Project dung da hinh o tang logic chung, nhung voi game rendering, moi loai linh co asset va animation rieng nen code can `instanceof` de chon sprite. Day la diem co the refactor bang Strategy/Visitor neu muon thiet ke OOP sau hon.

---

## 6. Truc cot 4: Abstraction - Truu tuong hoa

Truu tuong hoa nghia la che giau chi tiet phuc tap va chi dua ra interface/khai niem can thiet.

### 6.1 Abstract class dai dien khai niem chung

Project co nhieu abstract class:

- `BaseEntity`
- `CombatEntity`
- `Soldier`
- `Enemy`
- `BaseTower`

Chung khong nen duoc tao truc tiep, vi chung la khai niem chung.

Vi du:

- Khong co object "CombatEntity" chung chung trong game.
- Trong game chi co `Archer`, `Warrior`, `TNT`, `DefenseTower`, ...
- Nhung systems can mot khai niem chung de xu ly. Do la `CombatEntity`.

Co the noi:

> Abstract class giup minh noi voi Java rang: day la khung chung, khong phai doi tuong hoan chinh de spawn vao game.

### 6.2 `GameWorld` la abstraction cua mot tran dau

File: `Model/GameWorld.java`

Ben ngoai gameplay screen khong can biet chi tiet:

- Entity duoc luu trong bao nhieu array.
- Targeting update truoc hay movement update truoc.
- HealthSystem xu ly death ra sao.
- Spawn unit can set path nhu the nao.

Screen chi goi:

```java
gameWorld.update(delta);
gameWorld.spawnWarrior();
gameWorld.spawnMiner();
gameWorld.getEntityManager();
gameWorld.getMapManager();
gameWorld.consumeCombatVisualEvents();
```

`GameWorld` truu tuong hoa toan bo "tran dau" thanh mot object co API ro rang.

### 6.3 Systems la abstraction cua hanh vi frame-by-frame

Thay vi moi entity tu update tat ca logic, project tach thanh:

- `TargetingSystem`: ai nham vao ai.
- `MovementSystem`: ai di dau, dung lai khi trong tam danh, miner dao vang.
- `AttackSystem`: ai danh/heal/poison, cooldown, tao visual event.
- `HealthSystem`: mau, chet, pha tower, game over/victory.

Loi ich:

- Logic de doc theo chuc nang.
- Entity chi giu state va stat.
- Neu sua combat, vao `AttackSystem`.
- Neu sua movement, vao `MovementSystem`.

Day la mot kieu truu tuong hoa hanh vi: thay vi "object nao cung tu lam moi thu", game tao cac service/system phu trach tung mien logic.

### 6.4 Manager API che giau cau truc du lieu

`EntityManager` che giau viec luu entity trong nhieu `SnapshotArray`.

Ben ngoai co API:

- `addSoldier(Soldier soldier)`
- `addEnemy(Enemy enemy)`
- `addTower(BaseTower tower)`
- `getAliveSoldiers()`
- `getAliveEnemies()`
- `getAllActiveCombatUnits()`
- `getEntityById(long id, Class<T> type)`

System khong can biet:

- allEntities co du lieu gi.
- remove phai xoa khoi bao nhieu list.
- soldier/enemy/tower duoc count nhu the nao.

`MapManager` che giau TiledMap:

- Code ben ngoai khong can doc layer `towermain`, `towerdich`, `waypoint`, `waypoint_miner`.
- Chi can goi `getWaypoints(entity)`, `findInteractiveZone(x, y)`, `getPlayerTowerZones()`.

`EconomyManager` che giau economy:

- Code ben ngoai khong can tu check gold.
- Chi can goi `canBuyWarrior()`, `buyWarrior()`, `spendGold(cost)`, `addGold(amount)`.

### 6.5 Interface va enum la abstraction nho

Project dung enum de truu tuong hoa trang thai/loai:

- `BaseEntity.Team`: `SOLDIER`, `ENEMY`
- `CombatEntity.State`: `IDLE`, `MOVING`, `ATTACKING`, `HEALING`, `DYING`, `GOING_TO_MINE`, `MINING`, `RETURNING_HOME`
- `GameWorld.GameState`: `PLAYING`, `PAUSED`, `GAME_OVER`, `VICTORY`
- `CombatVisualEvent.Type`: `ARROW`, `DYNAMITE`, `HEAL`, `POISON`
- `SpawnDecision.UnitType`: `PAWN`, `WARRIOR`, `TNT`, `NONE`
- `TileType`: `START`, `END`
- `PurchasePanel.PurchaseOption`: `WARRIOR`, `ARCHER`, `HEALER`, `LANCER`, `MINER`

Dung enum tot hon string vi:

- Tranh typo.
- De switch/case.
- IDE co auto-complete.
- Code doc hon.

---

## 7. Quan he giua object

Ngoai 4 tru cot, project co nhieu quan he OOP quan trong: composition, aggregation, association, dependency.

### 7.1 Composition: object nay so huu object kia

`GameWorld` compose:

```java
private final EntityManager entityManager;
private final MapManager mapManager;
private final EconomyManager economyManager;
private final MovementSystem movementSystem;
private final TargetingSystem targetingSystem;
private final AttackSystem attackSystem;
private final HealthSystem healthSystem;
private MainTower mainTower;
private MainTower enemyTower;
```

Y nghia:

- Mot `GameWorld` dai dien mot tran dau.
- Tran dau co map, entity, economy, systems, towers.
- Khi `GameWorld.dispose()`, no dispose map, clear entity, dispose economy singleton.

`DemoModelScreen` compose:

- `GameWorld`
- `AIController`
- camera
- map renderer
- HUD
- world HUD renderer
- tower renderer
- unit renderer
- combat effect renderer

Screen la object dieu phoi cho viec show/render/dispose mot man choi.

`GameHud` compose:

- `Stage`
- `Table`
- `Label`
- `TextButton`
- `PurchasePanel`
- `TowerInfoPanel`

HUD gom nhieu component UI nho hon.

### 7.2 Aggregation: manager giu danh sach object game

`EntityManager` giu:

- `SnapshotArray<Soldier> soldiers`
- `SnapshotArray<Enemy> enemies`
- `SnapshotArray<GoldMine> goldMines`
- `SnapshotArray<Projectile> projectiles`
- `SnapshotArray<BaseTower> towers`
- `SnapshotArray<BaseEntity> allEntities`

Entity co the duoc tao boi `GameWorld`, sau do dua vao manager. Manager khong phai class cha cua entity, ma la object quan ly collection.

### 7.3 Association: object biet object khac qua tham chieu

Vi du:

- `AttackSystem` biet `EntityManager` va `HealthSystem`.
- `HealthSystem` biet `EntityManager` va `GameWorld`.
- `MovementSystem` biet `EntityManager`, `mainTower`, `enemyTower`, `MapManager`.
- `AIController` biet `EntityManager`, `MapManager`, `playerMainTower`, `enemyMainTower`.
- `PurchasePanel` biet `GameWorld` de spawn unit/nang cap.

Day la association/dependency injection don gian: cac object nhan dependency qua constructor thay vi tu tao lung tung.

### 7.4 Dependency: class dung class khac tam thoi

Vi du:

- `GameWorld.spawnArcher()` tao `new Archer()`, set position/path/state, roi add vao `EntityManager`.
- `MapManager.getWaypoints(BaseEntity entity)` nhan entity de quyet dinh duong di: enemy di nguoc, miner di duong miner, soldier thuong di duong chinh.
- `UnitRenderer.selectClip(CombatEntity entity)` dung runtime type de chon sprite.

---

## 8. Luong runtime: object phoi hop nhu the nao

### 8.1 Khoi dong game

Flow:

```text
Lwjgl3Launcher.main()
  -> new Lwjgl3Application(new MainGame(), config)
  -> MainGame.create()
  -> tao UiAssets, AudioManager, Screens
  -> setScreen(MainMenuScreen.class)
```

OOP points:

- `Lwjgl3Launcher` chi chiu trach nhiem khoi dong desktop app.
- `MainGame` la object goc cua LibGDX game.
- Screen duoc quan ly bang interface `Screen`, nen co the thay doi giua menu/gameplay.

### 8.2 Bat dau gameplay

Khi vao map 1:

```text
MainMenuScreen button
  -> game.setScreen(new DemoModelScreen(game))
  -> DemoModelScreen.show()
  -> new GameWorld("Game_Map.tmx", 1000, 3000)
  -> new AIController(...)
  -> new renderers + HUD
```

Trong `GameWorld` constructor:

```text
new MapManager(mapPath)
new EntityManager()
EconomyManager.getInstance(initialGold, maxGold)
createFixedEntities()
new HealthSystem(...)
new AttackSystem(...)
new TargetingSystem(...)
new MovementSystem(...)
```

OOP points:

- Screen khong tu tao tower thu cong. No giao cho `GameWorld`.
- `GameWorld` khong tu load tung object map bang string trong screen. No giao cho `MapManager`.
- Systems duoc tao voi dependency can thiet, moi system phu trach mot viec.

### 8.3 Spawn soldier

Vi du spawn Archer:

```text
PurchasePanel / hotkey
  -> gameWorld.spawnArcher()
  -> economyManager.canBuyArcher()
  -> economyManager.buyArcher()
  -> new Archer()
  -> archer.setPosition(homePos)
  -> archer.setPath(mapManager.getWaypoints(archer))
  -> archer.setState(MOVING)
  -> entityManager.addSoldier(archer)
```

OOP points:

- `Archer` tu biet stat cua no qua constructor + `applyLevelData()`.
- `EconomyManager` tu biet gia va cach tru vang.
- `MapManager` tu biet waypoint nao phu hop voi entity.
- `EntityManager` tu biet add soldier vao list nao.

### 8.4 Mot frame gameplay

Trong `GameWorld.update(delta)`:

```text
if state != PLAYING return
targetingSystem.update(delta)
movementSystem.update(delta)
attackSystem.update(delta)
healthSystem.update(delta)
```

Thu tu nay co y nghia:

- Targeting chon muc tieu truoc.
- Movement dua entity den gan target hoac di theo path.
- Attack neu dang trong tam va cooldown xong.
- Health xu ly death, tower destruction, game over/victory.

### 8.5 Targeting

`TargetingSystem` lay tat ca combat unit:

```text
entityManager.getAllActiveCombatUnits()
```

Sau do:

- Bo qua entity chet/removed/miner.
- Neu target cu khong hop le thi clear target.
- Neu la `Healer`, uu tien tim ally bi thuong.
- Neu la unit thuong, uu tien enemy tower gan trong range.
- Neu chua co target thi tim enemy gan nhat.

OOP points:

- System xu ly qua `CombatEntity`.
- Dac diem dac biet cua `Healer`, `Miner`, `BaseTower` duoc detect khi can.
- `targetId` dung id thay vi giu reference target truc tiep, giup lay target qua `EntityManager.getEntityById()`.

### 8.6 Movement

`MovementSystem` xu ly:

- `BaseTower`: khong di chuyen, chi doi state `ATTACKING`/`IDLE`.
- `Miner`: state machine rieng: `GOING_TO_MINE -> MINING -> RETURNING_HOME -> GOING_TO_MINE`.
- Unit thuong: follow path, tam dung path khi gap target trong range, tiep tuc path khi het target.

OOP points:

- State cua object nam trong `CombatEntity.State`.
- `Miner` ke thua `Soldier`, nhung co behavior dac biet dua tren state va field rieng (`miningTimer`, `carriedGold`).
- `MovementSystem` goi API chung `getPath`, `setPath`, `setFacing`, `setState`.

### 8.7 Attack

`AttackSystem`:

- Check state `ATTACKING` hoac `HEALING`.
- Giam cooldown.
- Lay target qua `EntityManager`.
- Check range.
- Neu healer dang heal ally: tao visual event `HEAL`, goi `healthSystem.heal`.
- Neu healer danh enemy: tao `POISON`, goi `applyHealerPoison`.
- Neu attacker khac: tao visual event arrow/dynamite neu can, goi `healthSystem.takeDamage`.
- Reset cooldown.

OOP points:

- `AttackSystem` khong truc tiep sua `target.health -= damage`; no goi `HealthSystem.takeDamage`.
- `CombatVisualEvent` la object event tach logic damage khoi logic render effect.
- Healer la vi du mot class con tai su dung field `attackDamage` theo nghia rieng.

### 8.8 Health va death

`HealthSystem.takeDamage(entity, damage)`:

- Neu entity chet/removed thi return.
- Tru mau qua setter.
- Neu mau <= 0 thi `onDeath(entity)`.

`onDeath`:

- Neu entity la player main tower -> `gameWorld.gameOver()`.
- Neu entity la enemy main tower -> `gameWorld.victory()`.
- Neu entity la `DefenseTower` -> `beginDestroying()`.
- Con lai -> `markRemoved()` va remove khoi manager.

OOP points:

- Death rule tap trung mot noi.
- `DefenseTower` co lifecycle chet rieng, khong bien mat ngay ma co animation pha huy.
- `GameWorld` dong goi state tran dau (`GAME_OVER`, `VICTORY`).

---

## 9. Phan tich tung nhom class theo OOP

### 9.1 `GameWorld`

`GameWorld` la facade/orchestrator cua Model.

No dong goi:

- State tran dau: `PLAYING`, `PAUSED`, `GAME_OVER`, `VICTORY`.
- Managers: entity, map, economy.
- Systems: targeting, movement, attack, health.
- Special entities: `mainTower`, `enemyTower`.
- API spawn unit va upgrade tower.

Diem OOP manh:

- Composition ro rang.
- API de screen dung rat gon.
- Game loop tach thanh systems.
- Khong de screen cham truc tiep vao qua nhieu logic combat.

Diem co the cai thien:

- `spawnPawn`, `spawnMiner`, `spawnArcher`, `spawnWarrior`, `spawnHealer`, `spawnLancer` co lap lai logic: check money, tao unit, set position/path/state, add soldier. Co the refactor bang Factory Method hoac UnitFactory.
- `spawnTNT`, `spawnWarriorHacHoa`, `spawnPawnHacHoa` cung co pattern tuong tu.

### 9.2 `EntityManager`

`EntityManager` la collection manager.

Diem OOP:

- Dong goi viec add/remove entity vao nhieu list.
- Cho phep query theo abstraction: alive soldiers, alive enemies, active combat units.
- Generic method `getEntityById(long id, Class<T> type)` giup lay entity type-safe.

Vi du hay:

```java
public <T extends BaseEntity> T getEntityById(long id, Class<T> type)
```

Y nghia:

- `T extends BaseEntity` gioi han generic chi cho entity.
- `type.isInstance(e)` check runtime type.
- `type.cast(e)` cast an toan hon cast tay.

Diem co the cai thien:

- `removeFromAllLists` dung `instanceof` nhieu. Neu class entity tang nhieu, co the can registry/type map.
- `markForRemoval` co pending removals nhung `HealthSystem` hien remove truc tiep o mot so case. Co the thong nhat remove cuoi frame.

### 9.3 `MapManager`

`MapManager` la abstraction giua TiledMap va game logic.

Diem OOP:

- Constructor load map va extract data.
- Cac method private `loadMap`, `extractWaypoints`, `extractTowerPositions` an chi tiet load map.
- Public API tra ve ban copy.
- `getWaypoints(BaseEntity entity)` quyet dinh duong di dua tren runtime type.

Day la vi du abstraction + encapsulation tot:

> Screen khong can biet layer trong Tiled ten gi. No chi goi `findInteractiveZone` hoac `getWaypoints`.

### 9.4 `EconomyManager`

`EconomyManager` la Singleton.

Diem OOP:

- Private constructor ngan tao nhieu economy.
- Static `getInstance` quan ly instance.
- Cac cost la constant public.
- Method `canBuyX`/`buyX` bien logic tien thanh API nghia ro.

Diem can noi can than:

- Singleton de tien cho game nho, nhung neu game co nhieu tran song song/test song song thi singleton co the gay state global. Trong project hien tai, `GameWorld.dispose()` goi `EconomyManager.dispose()` de reset instance khi thoat tran, nen chap nhan duoc.

### 9.5 Soldiers

Tat ca soldier co chung pattern:

```text
Soldier
  -> constructor set team/level/state
  -> class con set size + applyLevelData
  -> setLevel override de cap nhat stat
  -> reset override de quay ve state dung
```

`Warrior`:

- High HP, low/moderate damage.
- Mang static `HEALTH_DATA`, `DAMAGE_DATA`, `RANGE_DATA`, `UPGRADE_COST_DATA`.
- `applyLevelData()` la trung tam dong bo stat.

`Archer`:

- Them `arrowSpeed`.
- Tam danh xa.
- Attack speed tang theo level.

`Healer`:

- Heal amount nam trong `attackDamage`.
- Range la support range.
- Co `getHealAmount()`.
- Trong `AttackSystem`, khi target cung team va bi thuong thi heal; khi target enemy thi poison.

`Lancer`:

- Attack range dai hon can chien co ban.
- Attack speed cham hon.
- Renderer co animation attack theo huong (`lancerAttackClip`).

`Miner`:

- Co state kinh te rieng.
- `getGoldPerCycle()` tra ve `attackDamage`.
- `MovementSystem` dung `miningTimer`, `carriedGold`.

Day la vi du inheritance + specialization:

> Cung la `Soldier`, nhung moi class con co chi so va vai tro rieng. Systems nhin chung xu ly chung, nhung state/field rieng tao gameplay khac nhau.

### 9.6 Enemies

`Enemy` la base cho phe dich.

Class con:

- `PawnHacHoa`
- `WarriorHacHoa`
- `TNT`

Diem OOP:

- Cung co stat theo level nhu soldier.
- Cung override `setLevel`/`reset`.
- Co reward field ke thua tu `Enemy`.
- `TNT` them `bombSpeed`.

Trong `AIController`, enemy duoc tao theo enum:

```text
UnitType.PAWN -> new PawnHacHoa()
UnitType.WARRIOR -> new WarriorHacHoa()
UnitType.TNT -> new TNT()
```

Diem co the cai thien:

- Dung switch de tao enemy la factory logic nam trong `AIController`. Co the tach thanh `EnemyFactory` neu muon OOP ro hon.

### 9.7 Towers

`MainTower` va `DefenseTower` cung la `BaseTower`, nen cung co health/team/target/combat state.

`MainTower`:

- Co `isEnemy()`.
- La dieu kien thang/thua trong `HealthSystem`.

`DefenseTower`:

- Co `MAX_TOWER_LEVEL`.
- Co stat theo level.
- `upgrade()` tang level va apply stat.
- `beginDestroying()` tao death lifecycle rieng.
- `getDestroyProgress()` cho renderer biet animation pha huy dang den dau.

OOP hay:

> Logic pha huy tower nam trong object tower, renderer chi doc `isDestroying()` va `getDestroyProgress()` de ve.

### 9.8 AI

`AIController` la object quan ly wave.

Diem OOP:

- Co state noi bo: wave, timer, gold, current enemy level.
- Co dependency vao `EntityManager`, `MapManager`, `MainTower`.
- Co `AIPersonality` de dong goi weight random.
- Co inner class `GameState` lam snapshot phan tich state.
- `SpawnDecision.UnitType` la enum de truu tuong hoa loai enemy.

Flow AI:

```text
update(delta)
  -> neu het initial delay thi startNewWave()
  -> neu dang spawning thi dem timer
  -> pickUnitType()
  -> spawnEnemy()
  -> neu wave xong thi doi wave sau
```

Diem hay:

- AI co state va behavior rieng, khong nhot trong screen.
- `AIPersonality` lam cho AI co variation.

Diem co the cai thien:

- `SpawnDecision` hien co class nhung `AIController.pickUnitType()` tra ve `UnitType` thay vi tra ve `SpawnDecision`. Neu muon dung dung abstraction nay, co the cho AI return `SpawnDecision`.

### 9.9 UI va renderer

`GameHud`:

- Dong goi Stage + label/button/panel.
- Co interface `PauseMenuListener`.
- Cap nhat UI tu `GameWorld`.
- Chon panel phu hop theo `BuildingZone`: purchase building -> `PurchasePanel`, defense tower -> `TowerInfoPanel`.

`PurchasePanel`:

- Co private enum `PurchaseOption` de map building name sang unit.
- Delegate stat UI sang static getter cua class linh.
- Goi `gameWorld.spawnX()` de tao linh.
- Nang cap tat ca soldier cung loai bang `s.setLevel(newLevel)`.

`TowerInfoPanel`:

- Doc `DefenseTower` tu `GameWorld`.
- Neu enemy tower thi khong cho upgrade.
- Neu player tower va du tien thi goi `gameWorld.upgradeDefenseTower`.

`UnitRenderer`:

- Lay `SnapshotArray<BaseEntity>`.
- Loc `CombatEntity`, bo qua `BaseTower`.
- Chon sprite theo runtime type + state.
- Co inner class `Clip` dong goi texture + frame + crop logic.

`CombatEffectRenderer`:

- Nhan `CombatVisualEvent` tu model.
- Render arrow/dynamite/heal/poison.
- Day la separation giua gameplay event va visual effect.

---

## 10. Design pattern va ky thuat OOP xuat hien trong project

### 10.1 Singleton

Class: `EconomyManager`

Dac diem:

- `private static EconomyManager instance`
- private constructor
- `getInstance(...)`
- `dispose()` reset instance

Ly do dung:

- Economy la tai nguyen toan tran.
- Nhieu noi can truy cap economy: `GameWorld`, `MovementSystem`, `GameHud`, `PurchasePanel`.

Can noi can bang:

- Singleton tien cho project nho.
- Neu ve sau can test/multi-game-world, nen chuyen thanh dependency binh thuong thay vi global singleton.

### 10.2 Facade / Orchestrator

Class: `GameWorld`

`GameWorld` che giau managers/systems va dua ra API don gian cho screen:

- `update`
- `spawnX`
- `upgradeDefenseTower`
- `getEntityManager`
- `getMapManager`
- `getEconomyManager`
- `consumeCombatVisualEvents`

### 10.3 State pattern dang enum state machine

Enum: `CombatEntity.State`, `GameWorld.GameState`

Vi du `Miner` trong `MovementSystem`:

```text
GOING_TO_MINE
  -> MINING
  -> RETURNING_HOME
  -> GOING_TO_MINE
```

Vi du game state:

```text
PLAYING
  -> PAUSED
  -> GAME_OVER / VICTORY
```

Day chua phai State Pattern day du bang class rieng cho tung state, nhung la state machine ro rang bang enum.

### 10.4 Factory Method dang method spawn

`GameWorld.spawnPawn`, `spawnMiner`, `spawnArcher`, `spawnWarrior`, `spawnHealer`, `spawnLancer` la factory-like methods:

- Tao object.
- Gan position/path/state.
- Add vao manager.

`AIController.spawnEnemy(UnitType type)` cung la factory-like:

- `PAWN` -> `PawnHacHoa`
- `WARRIOR` -> `WarriorHacHoa`
- `TNT` -> `TNT`

Neu thuyet trinh, co the noi day la "factory logic", chua tach thanh class Factory rieng.

### 10.5 Observer/Callback

`GameHud.PauseMenuListener` la callback interface:

- HUD phat event.
- Screen nhan event.
- Giam phu thuoc nguoc tu HUD vao screen cu the.

LibGDX `ChangeListener`, `ClickListener` cung la event/callback:

- Button click -> anonymous class override `changed`/`clicked`.
- Day la da hinh qua class listener.

### 10.6 DTO / Value Object

Classes:

- `CombatVisualEvent`
- `SpawnDecision`
- `BuildingZone`

Chung chua du lieu co y nghia, duoc truyen giua cac phan:

- `CombatVisualEvent`: logic -> renderer.
- `SpawnDecision`: AI decision data.
- `BuildingZone`: map -> UI/gameplay.

### 10.7 Separation of Concerns

Project tach:

- Entity: du lieu/trang thai doi tuong.
- System: xu ly logic.
- Manager: luu tru/truy van.
- Screen: lifecycle/update/render flow.
- Renderer/UI: hien thi.

Day la diem rat nen nhan manh:

> Project ap dung OOP khong phai bang cach de moi object lam tat ca moi thu, ma bang cach chia trach nhiem thanh cac object co vai tro ro.

---

## 11. Vi du thuyet trinh theo 4 tru cot

### 11.1 Neu noi ve dong goi

Noi:

> Trong project, `CombatEntity` dong goi cac chi so combat nhu health, maxHealth, attackDamage, attackSpeed. Cac setter khong gan thang gia tri ma co validate, vi du health duoc clamp tu 0 den maxHealth, attackDamage khong cho am, attackSpeed tu dong cap nhat cooldownDuration. Nhu vay object tu bao ve trang thai hop le cua no.

Dan chung:

- `CombatEntity.setHealth`
- `CombatEntity.setAttackSpeed`
- `EconomyManager.spendGold`
- `MapManager.getPlayerCastlePosition` tra ve copy
- `CombatVisualEvent.getStart/getEnd` tra ve copy

### 11.2 Neu noi ve ke thua

Noi:

> Entity hierarchy la phan OOP ro nhat. Tat ca object gameplay deu bat dau tu `BaseEntity`. Nhung object co combat ke thua `CombatEntity`. Linh nguoi choi ke thua `Soldier`, linh dich ke thua `Enemy`, tower ke thua `BaseTower`. Nho vay cac class con nhu `Archer`, `Miner`, `TNT`, `DefenseTower` khong phai lap lai field health, position, target, path, cooldown.

Dan chung:

- `Archer extends Soldier`
- `TNT extends Enemy`
- `DefenseTower extends BaseTower`
- `MainTower extends BaseTower`
- `MainGame extends Game`
- `DemoModelScreen extends ScreenAdapter`

### 11.3 Neu noi ve da hinh

Noi:

> `EntityManager.getAllActiveCombatUnits()` tra ve danh sach `CombatEntity`, nhung ben trong co the la `Archer`, `Warrior`, `TNT`, `DefenseTower`. `TargetingSystem`, `MovementSystem`, `AttackSystem` xu ly chung qua API cua `CombatEntity`. Khi UI goi `Soldier newest.setLevel(currentLv)`, Java se tu goi dung `setLevel` cua class con that su, vi `Archer`, `Healer`, `Miner` deu override method nay.

Dan chung:

- `Array<CombatEntity> combatants`
- `Soldier newest = ...; newest.setLevel(currentLv)`
- `GameHud.PauseMenuListener`
- `Screen` lifecycle cua LibGDX

### 11.4 Neu noi ve truu tuong hoa

Noi:

> Project dung abstract class de tao cac khai niem chung nhu `BaseEntity`, `CombatEntity`, `Soldier`, `Enemy`, `BaseTower`. Chung khong phai doi tuong cu the de spawn, ma la khung logic chung. Ngoai ra, `GameWorld` truu tuong hoa ca tran dau thanh mot object, screen chi can goi `gameWorld.update(delta)` va cac method spawn/upgrade thay vi biet chi tiet target, movement, attack, health update the nao.

Dan chung:

- Abstract class hierarchy.
- `GameWorld.update`.
- `MapManager` an chi tiet TiledMap.
- `EntityManager` an chi tiet collection.

---

## 12. OOP trong luong mua va nang cap linh

Day la luong rat hop de thuyet trinh vi co du Model + View + Manager + Entity.

```text
Nguoi choi click building
  -> DemoModelScreen.handleWorldClick()
  -> MapManager.findInteractiveZone()
  -> GameHud.setSelectedBuilding(BuildingZone)
  -> PurchasePanel.show(zone)
  -> PurchaseOption.fromBuilding(zone)
  -> Buy button
  -> GameWorld.spawnX()
  -> EconomyManager.buyX()
  -> new Soldier subclass
  -> EntityManager.addSoldier()
```

OOP points:

- `BuildingZone` dong goi y nghia vung click.
- `GameHud` quyet dinh panel nao hien dua tren method cua zone.
- `PurchasePanel.PurchaseOption` map building -> unit.
- `GameWorld` la facade spawn.
- `EconomyManager` quan ly tien.
- `Soldier` subclass tu set stat.
- `EntityManager` quan ly collection.

Nang cap linh:

```text
Upgrade button
  -> selectedOption.getStaticUpgradeCostForLevel(level)
  -> EconomyManager.spendGold(cost)
  -> increment level cua building option
  -> loop soldiers
  -> s.setLevel(newLevel)
```

OOP point quan trong:

- `s` la `Soldier` reference.
- Runtime object co the la `Warrior`, `Archer`, `Healer`, `Lancer`, `Miner`.
- `setLevel` goi dung override cua class con.

---

## 13. OOP trong combat

Combat flow:

```text
TargetingSystem
  -> set targetId
MovementSystem
  -> set state MOVING / ATTACKING / HEALING / IDLE
AttackSystem
  -> apply damage/heal/poison
HealthSystem
  -> death/game state
Renderer
  -> render animation/effect theo state/event
```

Object roles:

- `CombatEntity`: noi luu stat va state combat.
- `TargetingSystem`: chon target.
- `MovementSystem`: chuyen state va di chuyen.
- `AttackSystem`: xu ly action khi trong range.
- `HealthSystem`: cap nhat mau va death.
- `CombatVisualEvent`: cau noi tu attack logic sang visual effect.
- `UnitRenderer`/`CombatEffectRenderer`: hien thi.

Vi sao day la OOP tot:

- Moi class co mot trach nhiem chinh.
- Logic khong bi don het vao `render()` cua screen.
- Entity co chung abstraction, system co the lam viec tren abstraction.

---

## 14. OOP trong AI

`AIController` la mot object co state:

- `wavesStarted`
- `enemiesRemainingToSpawn`
- `spawnTimer`
- `isSpawningWave`
- `currentEnemyLevel`
- `currentSpawnInterval`
- `gold`

No co behavior:

- `update(delta)`
- `startNewWave()`
- `pickUnitType(forceSpawn)`
- `spawnEnemy(type)`
- `evaluatePawn`
- `evaluateWarrior`
- `evaluateTNT`
- `evaluateSave`

`AIPersonality` la object nho dong goi random weight:

- `pawnWeight`
- `warriorWeight`
- `tntWeight`
- `saveWeight`

Y nghia OOP:

- AI khong chi la ham static. No la object co bo nho/state rieng.
- Moi tran/moi map co the tao AIController khac nhau.
- `AIPersonality` tach randomness/personality khoi logic wave.

Neu thuyet trinh:

> AIController giong mot doi thu ao. No co tai nguyen vang, co wave timer, co personality weight va dua ra quyet dinh spawn dua tren game state. Day la cach mo hinh hoa AI thanh object co trang thai va hanh vi.

---

## 15. Cac diem OOP tot nhat trong project nen nhan manh

1. Entity hierarchy ro rang:
   `BaseEntity -> CombatEntity -> Soldier/Enemy/BaseTower -> concrete classes`.

2. Systems tach behavior:
   target, movement, attack, health khong nhot het trong entity.

3. `GameWorld` lam trung tam composition:
   gom managers, systems, towers, game state.

4. Encapsulation tot o manager/value object:
   `MapManager` va `CombatVisualEvent` tra ve copy de bao ve du lieu noi bo.

5. Dynamic dispatch that su:
   `Soldier.setLevel()` goi override cua class con khi upgrade.

6. Interface callback:
   `GameHud.PauseMenuListener` tach HUD khoi screen cu the.

7. Lifecycle LibGDX:
   `MainGame`, `Screen`, `ScreenAdapter`, `dispose()` cho thay project hieu object lifecycle va resource management.

8. Enum state ro rang:
   game state, combat state, event type, unit type.

---

## 16. Cac diem co the cai thien neu thay hoi

Day la phan nen noi chu dong neu thay hoi "OOP cua em co diem nao chua toi uu?".

### 16.1 Tao `UnitFactory` / `EnemyFactory`

Hien tai `GameWorld.spawnX()` lap lai nhieu logic. Co the tao:

```java
interface UnitFactory {
    Soldier createSoldier(UnitType type, int level);
}
```

Hoac class:

```java
public class SoldierFactory {
    public Soldier create(PurchaseOption option, int level) { ... }
}
```

Loi ich:

- Giam code lap.
- De them unit moi.
- `GameWorld` gon hon.

### 16.2 Dung Strategy cho unit stat

Moi class linh hien co static array va `applyLevelData()` gan stat. Co the tach stat thanh object:

```text
UnitStats(level, health, damage, range, attackSpeed, upgradeCost)
```

Hoac:

```java
interface LevelScaling {
    UnitStats statsForLevel(int level);
}
```

Loi ich:

- UI va gameplay doc chung mot source stat.
- Giam static getter lap lai.

### 16.3 Giam `instanceof` trong renderer/system

Renderer hien dung nhieu `instanceof` de chon animation. Co the cai tien bang:

- `getAnimationKey()` trong entity.
- Strategy renderer per unit type.
- Registry map `Class<?> -> UnitRenderConfig`.

Nhung voi project game OOP mon hoc, cach hien tai van chap nhan duoc vi asset/animation moi unit that su khac nhau.

### 16.4 Tao abstract gameplay screen chung

`DemoModelScreen` va `DemoModelScreen2` gan nhu giong nhau, khac:

- `MAP_PATH`
- `AI_LEVEL`
- `GAME_SPEED`
- restart screen type

Co the tach:

```text
AbstractGameplayScreen
├── Map1Screen
└── Map2Screen
```

Loi ich:

- Reuse logic show/render/dispose.
- Map moi chi can override config.

### 16.5 Dong goi `BaseEntity.getPosition()`

`getPosition()` hien tra ve mutable `Vector2` noi bo. De dong goi chat hon:

- Them `getPositionCopy()`.
- Chi cho MovementSystem sua qua method `moveBy`, `setPosition`.

Nhung doi voi game loop can performance, viec tra reference co the la trade-off chap nhan duoc.

### 16.6 Thong nhat remove entity

`EntityManager` co `pendingRemovals`, nhung `HealthSystem` co noi remove truc tiep. Co the thong nhat:

- `markForRemoval`
- cuoi frame `processPendingRemovals`

Loi ich:

- An toan hon khi iterate collection.
- Giam bug remove trong luc loop.

---

## 17. Script thuyet trinh de dung truc tiep

### Mo dau

> Project cua nhom em la game TowerDefence viet bang Java va LibGDX. Ve mat OOP, project chia thanh Model va View. Model chua game world, entity, manager, system va AI. View chua screen, HUD, panel va renderer. Phan OOP trung tam nam o cay ke thua entity va cach cac system xu ly entity qua abstraction.

### Noi ve kien truc

> Game khong de tat ca logic trong mot class lon. `GameWorld` la object dai dien cho mot tran dau. Ben trong no compose `EntityManager`, `MapManager`, `EconomyManager` va bon system chinh: `TargetingSystem`, `MovementSystem`, `AttackSystem`, `HealthSystem`. Moi frame, `GameWorld.update()` goi lan luot cac system nay.

### Noi ve ke thua

> Cay ke thua cua project bat dau tu `BaseEntity`, sau do `CombatEntity`, roi tach thanh `Soldier`, `Enemy`, `BaseTower`. Cac class cu the nhu `Archer`, `Miner`, `TNT`, `DefenseTower` ke thua tu cac lop nay. Nhờ vậy các class con dùng lại được id, position, health, target, path, cooldown và chỉ cần định nghĩa phần riêng như stat theo level, arrowSpeed, bombSpeed hoặc mining data.

### Noi ve dong goi

> Dong goi duoc the hien o cac setter cua `CombatEntity` nhu `setHealth`, `setMaxHealth`, `setAttackDamage`, `setAttackSpeed`. Object tu bao ve gia tri hop le. `MapManager` cung dong goi du lieu map bang cach tra ve ban copy cua `Vector2` va `BuildingZone`, tranh viec code ben ngoai sua nham du lieu map goc. `EconomyManager` dong goi tat ca logic vang, mua linh va cheat.

### Noi ve da hinh

> Da hinh the hien ro trong `EntityManager.getAllActiveCombatUnits()`: danh sach tra ve kieu `CombatEntity`, nhung ben trong co nhieu object khac nhau nhu `Archer`, `Warrior`, `TNT`, `DefenseTower`. Systems xu ly chung qua API `CombatEntity`. Ngoai ra khi UI nang cap linh, code goi `setLevel` tren bien kieu `Soldier`, Java se runtime dispatch den `setLevel` cua class con that su.

### Noi ve truu tuong hoa

> Abstract class nhu `BaseEntity`, `CombatEntity`, `Soldier`, `Enemy`, `BaseTower` giup mo hinh hoa cac khai niem chung. `GameWorld` truu tuong hoa ca tran dau thanh mot object. `MapManager` truu tuong hoa TiledMap thanh cac method nhu `getWaypoints` va `findInteractiveZone`. `EntityManager` truu tuong hoa viec luu tru va truy van entity.

### Ket luan

> Diem manh OOP cua project la mo hinh entity co hierarchy ro, tach logic thanh systems, va dung object manager de dong goi tung mien trach nhiem. Neu phat trien tiep, nhom em co the refactor spawn logic thanh Factory, tach gameplay screen chung, va giam `instanceof` trong renderer bang strategy/registry.

---

## 18. Cau hoi thay co the hoi va cach tra loi

### Hoi: Vi sao `BaseEntity` va `CombatEntity` la abstract?

Tra loi:

> Vi chung la khai niem nen tang, khong phai doi tuong cu the trong game. Game khong spawn mot `CombatEntity` chung chung, ma spawn `Archer`, `Warrior`, `TNT`, `DefenseTower`. Abstract class giup chia se field/method chung nhung ngan tao object khong day du y nghia.

### Hoi: Project co da hinh that khong, hay chi co ke thua?

Tra loi:

> Co da hinh. Vi du `EntityManager` tra ve `Array<CombatEntity>`, cac system xu ly list nay ma khong can biet object that la `Archer`, `TNT` hay `DefenseTower`. Them nua, `PurchasePanel` goi `setLevel` tren bien kieu `Soldier`; runtime se goi override cua class con.

### Hoi: Tai sao khong cho moi entity tu update chinh no?

Tra loi:

> Nhom em tach logic thanh systems de moi class co mot trach nhiem ro. Entity giu state/stat, con `TargetingSystem`, `MovementSystem`, `AttackSystem`, `HealthSystem` xu ly tung loai logic. Cach nay giup debug va mo rong de hon.

### Hoi: Singleton `EconomyManager` co tot khong?

Tra loi:

> Voi project game nho, singleton giup truy cap economy don gian vi nhieu noi can doc/tru/cong vang. Tuy nhien singleton la global state, nen neu sau nay can test tot hon hoac chay nhieu game world song song thi co the chuyen sang dependency injection binh thuong.

### Hoi: Healer co phai vi pham OOP khi dung `attackDamage` lam heal amount?

Tra loi:

> Day la trade-off thiet ke. `Healer` van la `Soldier` va `CombatEntity`, nen no dung chung khung combat. Trong domain cua game, heal amount co the xem la "effect power" thay vi damage. De ro nghia hon, class co method `getHealAmount()`. Neu cai tien, co the doi ten field chung thanh `effectPower` hoac tach `SupportEntity`.

### Hoi: Co diem nao chua toi uu trong OOP?

Tra loi:

> Co. `DemoModelScreen` va `DemoModelScreen2` lap lai logic, co the tach abstract gameplay screen. Spawn logic trong `GameWorld` co the tach factory. Renderer con dung nhieu `instanceof`, co the cai tien bang strategy/registry. Nhung trong project hien tai, cac phan nay van hoat dong ro va de doc.

### Hoi: `MapManager` dong goi du lieu nhu the nao?

Tra loi:

> `MapManager` load TiledMap va extract waypoint/building zone. Ben ngoai khong doc layer map truc tiep ma goi method nhu `getWaypoints`, `findInteractiveZone`. Nhieu getter tra ve ban copy cua `Vector2`/`BuildingZone`, giup bao ve du lieu map goc khoi bi sua ngoai y muon.

### Hoi: Vi sao `GameHud` dung interface `PauseMenuListener`?

Tra loi:

> Vi HUD chi nen biet nguoi choi bam Resume/Restart/Quit, khong nen phu thuoc vao screen cu the. Interface giup `DemoModelScreen` va `DemoModelScreen2` tu dinh nghia cach xu ly. Day la da hinh va giam coupling.

---

## 19. Tom tat mot slide

Neu chi co mot slide ve OOP, nen viet:

```text
OOP trong TowerDefence

1. Encapsulation
   - CombatEntity validate health/damage/range/speed.
   - EconomyManager dong goi vang.
   - MapManager/CombatVisualEvent tra ve ban copy.

2. Inheritance
   - BaseEntity -> CombatEntity -> Soldier/Enemy/BaseTower -> class cu the.
   - MainGame extends Game, screens implement/extend Screen.

3. Polymorphism
   - Systems xu ly Array<CombatEntity>.
   - Soldier.setLevel() runtime dispatch den Archer/Warrior/Miner...
   - GameHud.PauseMenuListener callback interface.

4. Abstraction
   - Abstract classes mo hinh hoa khai niem chung.
   - GameWorld truu tuong hoa mot tran dau.
   - Managers che giau map/entity/economy details.
```

---

## 20. Ket luan danh gia

Project nay co tinh OOP kha ro, dac biet o phan model gameplay:

- Co hierarchy entity hop ly.
- Co abstract class cho khai niem chung.
- Co da hinh that trong systems va UI callback.
- Co encapsulation trong manager, setter, event object.
- Co composition giua `GameWorld`, managers, systems, screens, renderers.

Diem dang gia nhat de thuyet trinh la project khong chi "co class" ma class co vai tro domain ro:

- `Archer`, `Miner`, `TNT` la doi tuong gameplay co identity va stat.
- `GameWorld` la tran dau.
- `EntityManager` la kho quan ly entity.
- `MapManager` la abstraction cua map.
- `EconomyManager` la economy service.
- `Systems` la cac bo xu ly hanh vi.
- `View` la tang hien thi va input.

Neu can mot cau ket:

> TowerDefence ap dung OOP bang cach bien cac khai niem trong game thanh object co trang thai va hanh vi rieng, dung ke thua de chia se cau truc, dong goi de bao ve du lieu, da hinh de xu ly nhieu loai entity qua mot interface chung, va truu tuong hoa de tach game logic thanh cac manager/system de doc, sua va mo rong.
