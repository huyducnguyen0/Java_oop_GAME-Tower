# TowerDefence (Java/libGDX) - Project Context (Snapshot)

Muc dich file nay: gom context chuan/du cho chat moi, giam token khi handoff.

Ngay cap nhat: 2026-05-27

---

## 1) Tong quan game (gameplay thuc te)

- Project: game desktop Java (libGDX) ten `TowerDefence`.
- Map/world rendering: da chay, doc map tu `assets/mapreal.tmx`.
- Game loop: `GameWorld.update(delta)` chay khi `state == PLAYING`.
- Win/Loss:
  - Player main castle (MainTower) het mau => `GAME OVER`.
  - Enemy main castle (MainTower) het mau => `VICTORY`.
- Co combat: unit co `health/maxHealth`, `attackDamage`, `attackRange`, `attackSpeed`, state machine (MOVING/ATTACKING/HEALING/MINING...).
- Tower placement: KHONG co placement runtime (tower phu lay tu object map).
- Unit spawning:
  - Player: mua qua UI panel (barrack/archery/monastery/house1/house2) va/hoac hotkey debug.
  - AI: tu dong spawn enemy theo `AIController`.

---

## 2) Entity/Systems quan trong

### 2.1 Team/State

- Team: `BaseEntity.Team` = `SOLDIER` (player) / `ENEMY`.
- Combat state: `CombatEntity.State` co: `IDLE`, `MOVING`, `ATTACKING`, `HEALING`, `GOING_TO_MINE`, `MINING`, `RETURNING_HOME`, `DYING`.

### 2.2 Entities (dang dung)

- Main castle:
  - `core/src/main/java/com/hust/towerdefence/Model/Entities/Tower/MainTower.java`
  - Ke thua `BaseTower` -> `CombatEntity` (co mau/HP, nhung khong tu ban).
  - Vi tri/bounds lay tu object layer map (center + bounds).

- Defense tower (tower phu):
  - `core/src/main/java/com/hust/towerdefence/Model/Entities/Tower/DefenseTower.java`
  - Co combat that: range/damage/attack speed, tu ban.
  - Co lifecycle destroy effect: khi HP = 0 -> `destroying` -> no effect -> remove entity.
  - Co upgrade (player tower): Lv1->Lv3 (HP/DMG/RNG/AS + cost).

- Player units:
  - `Warrior`, `Archer`, `Miner`, `Healer (Monk)`, `Lancer`.
  - `Pawn` van co trong code (hotkey P), nhung flow mua chinh hien tai KHONG dung.

- Enemy units:
  - `PawnHacHoa`, `WarriorHacHoa`, `TNT`.

### 2.3 Systems (dang dung)

- `MovementSystem`: path follow, dung lai khi co target trong range, healer dung de heal, miner mining loop.
- `TargetingSystem`:
  - Healer tim ally bi thuong trong range.
  - Unit thuong tim enemy trong range.
  - Tower phu (DefenseTower) chi target soldier/enemy (khong target tower khac).
  - Range scale: neu `attackRange <= 10` thi coi la tile unit va nhan `*64`, nguoc lai coi la px world.
- `AttackSystem`:
  - Ap dung damage/heal theo state.
  - Tao `CombatVisualEvent` de View render projectile/effect.
  - Projectile start cua DefenseTower duoc offset len cho giong ban tu vi tri archer tren thap.
- `HealthSystem`:
  - Giam mau, death handling:
    - MainTower player => GAME_OVER
    - MainTower enemy => VICTORY
    - DefenseTower => bat dau destroy animation, het animation thi remove
    - Unit khac => remove ngay

---

## 3) Map/Tiled data (mapreal.tmx)

File: `assets/mapreal.tmx`

Objectgroup quan trong:

- `towermain`:
  - `maintower` (player castle) + cac building mua unit (barrack/archery/monastery/house1/house2)
  - `tower1`, `tower2` (player defense towers)
- `towerdich`:
  - `enemymaintower` (enemy castle)
  - `enemytower1`, `enemytower2` (enemy defense towers)
- `waypoint`: duong chinh (soldier di tu player -> enemy; enemy di nguoc lai)
- `waypoint_miner`: duong rieng cho miner (di den goldmine va quay ve)
- `goldmine`: zone goldmine

Map binding:
- `MapManager` parse object rectangles thanh `BuildingZone` (name + bounds).
- Main tower position = center bounds (gameplay), spawn position = bottom-center bounds (unit spawn).
- Defense tower zones duoc lay qua:
  - `MapManager.getPlayerTowerZones()`
  - `MapManager.getEnemyTowerZones()`

---

## 4) Rendering hien tai

### 4.1 Render order (DemoModelScreen)

File: `core/src/main/java/com/hust/towerdefence/View/screens/DemoModelScreen.java`

Order:
1. Tilemap renderer
2. Main castles sprite (player/enemy)
3. Defense towers sprite + archer tren tower + destroy explosion
4. Unit sprites (player/enemy)
5. Combat effects (arrow/dynamite/heal effect)
6. World-space HP bars (unit HP khi bi thuong + tower/castle HP)
7. Selection outline (click zone)
8. HUD (screen-space)

### 4.2 Main castle rendering

Renderer: `core/src/main/java/com/hust/towerdefence/View/ui/MainTowerRenderer.java`
- Player castle: `assets/Buildings/Castle.png`
- Enemy castle: `assets/EnemyBuildings/Wood_main/Castle.png`

HP bar castle: `core/src/main/java/com/hust/towerdefence/View/ui/WorldHudRenderer.java`

### 4.3 Defense tower rendering

Renderer: `core/src/main/java/com/hust/towerdefence/View/ui/DefenseTowerRenderer.java`
- Player tower: `assets/Buildings/Tower.png`
- Enemy tower: `assets/EnemyBuildings/Wood_Tower/Tower.png`
- Archer tren tower (visual):
  - Player: `assets/Units/Archer/Archer_Idle.png` (crop 1 frame)
  - Enemy: `assets/EnemyUnits/Archer/Archer_Idle.png` (crop 1 frame)
- Destroy effect:
  - `assets/Destroyed_Effect/Explosion_02.png` (10 frames, 192x192/frame)
- Archer tren tower scale = unit visual height 64px (de dong bo voi unit renderer sau nay).

### 4.4 Unit sprites rendering

Renderer: `core/src/main/java/com/hust/towerdefence/View/ui/UnitRenderer.java`
- Muc tieu: thay circle debug bang sprite/animation.
- Scale chuan: unit visual height ~64px, co multiplier nhe theo class.
- Clip selection theo class + state:
  - Archer: Idle/Run/Shoot
  - Warrior: Idle/Run/Attack1
  - Monk(Healer): Idle/Run/Heal
  - Lancer: Idle/Run/Right_Attack
  - Miner: su dung Pawn sheet Pickaxe + Run Gold khi returning
  - Pawn/PawnHacHoa: dung Knife sheet (Idle/Run/Interact)
  - TNT: dung `EnemyUnits/TNT/Red/TNT_Red.png`
- Auto-crop padding theo alpha bbox cua frame dau (giam vu de "frame 192 co nhieu khoang trong").
- Flip left/right hien tai: `SOLDIER` flipX de nhin ve phia enemy (tam thoi).

### 4.5 Combat effects (projectile/effect)

- Event model: `core/src/main/java/com/hust/towerdefence/Model/Systems/CombatVisualEvent.java`
- Event producer: `AttackSystem` (khi damage/heal thuc su xay ra).
- Effect renderer: `core/src/main/java/com/hust/towerdefence/View/ui/CombatEffectRenderer.java`
  - Arrow: `Units/Archer/Arrow.png` + `EnemyUnits/Archer/Arrow.png`
  - Dynamite: `EnemyUnits/TNT/Dynamite/Dynamite.png`
  - Heal: `Units/Monk/Heal_Effect.png` + `EnemyUnits/Monk/Heal_Effect.png`

---

## 5) UI/HUD hien tai

### 5.1 HUD bar (top-center)

File: `core/src/main/java/com/hust/towerdefence/View/ui/GameHud.java`
- HUD bar nho, top-center, screen-space.
- Hien: Player Gold + Selected zone name + Pause/Play.
- Overlay: `GAME OVER` / `VICTORY` centered khi ket thuc.

### 5.2 Purchase panel (click building)

File: `core/src/main/java/com/hust/towerdefence/View/ui/PurchasePanel.java`
- Click vao zone mua (barrack/archery/monastery/house1/house2) => panel gan building.
- Mapping:
  - barrack -> Warrior
  - archery -> Archer
  - monastery -> Monk (Healer)
  - house1 -> Lancer
  - house2 -> Miner

### 5.3 Tower info/upgrade panel

File: `core/src/main/java/com/hust/towerdefence/View/ui/TowerInfoPanel.java`
- Click tower1/tower2 => hien HP/DMG/RNG/Level + nut Upgrade (neu du vang).
- Click enemytower1/enemytower2 => hien info (khong upgrade).
- Upgrade logic o `GameWorld.upgradeDefenseTower(mapName)` su dung `EconomyManager.spendGold`.

### 5.4 Click handling

File: `core/src/main/java/com/hust/towerdefence/View/screens/DemoModelScreen.java`
- Left click world => `MapManager.findInteractiveZone()`:
  - Player building zones (towermain)
  - Enemy tower zones (towerdich)
- `GameHud.setSelectedBuilding(zone)` tu quyet dinh show purchasePanel hay towerInfoPanel.

---

## 6) Economy/Costs

File: `core/src/main/java/com/hust/towerdefence/Model/Managers/EconomyManager.java`

- COST_WARRIOR = 120
- COST_ARCHER = 60
- COST_HEALER = 100
- COST_LANCER = 70
- COST_MINER = 80
- Pawn van co cost (50) nhung flow mua chinh khong dung.

Tower upgrade costs (DefenseTower):
- Lv1->Lv2: 180
- Lv2->Lv3: 320

---

## 7) AI

File: `core/src/main/java/com/hust/towerdefence/Model/AI/AIController.java`
- AI co gold rieng (KHONG hien trong HUD final).
- Spawn decision roulette (pawn/warrior/tnt/save).
- Enemy spawn position: `MapManager.getEnemyBaseSpawnPosition()` (bottom-center enemymaintower zone).

---

## 8) Feature status (da lam / chua lam)

Da lam:
- Main castles: position tu map object + HP + gameover/victory + render sprite.
- Defense towers: spawn tu map object, tu ban, target/attack, HP bar, destroy explosion -> remove.
- Tower upgrade UI + economy.
- Unit sprites: co animation co ban theo state.
- Combat visual: arrow/dynamite/heal effect.
- Unit HP bar (chi hien khi mat mau).
- HUD top-center + purchase panel + tower info panel.

Chua lam / con thieu (uu tien):
- Projectile logic "that" (hit timing, projectile va cham): hien tai chi la visual event.
- Chuan huong (4 huong) va animation directional cho lancer (dang dung `Right_Attack` cho moi huong).
- Chuan hoa flip direction theo vector move/target thay vi flip theo team.
- Remove/disable spawn debug Pawn (hotkey P) neu muon bo Pawn khoi gameplay chinh.
- Tower priority rule "danh tower gan nhat truoc main tower": chua fix xong; unit van co the chay thang toi main tower trong mot so case.
- UI polish (visual quality) cho purchase/tower panels (dang dung shared panel drawable).
- Projectile/attack effects cho archer/tower co animation shoot timing (sync voi frame).
- TNT explosion damage AOE (neu muon), hien tai TNT damage la direct.

---

## 9) Quick handoff notes (chat moi nen biet)

- Nguon su that: class/file paths trong `core/src/main/java/...` va assets trong `assets/`.
- Renderer dang o `View/ui/*Renderer.java` va duoc goi tu `DemoModelScreen`.
- State machine da hoat dong; UI nen doc state, khong tu tao gameplay logic moi.
- Range trong code co 2 he:
  - Range <= 10 => tile unit (nhan 64)
  - Range > 10 => px world

--- 

## 10) Command kiem tra nhanh

- Build: `./gradlew build -x test`
