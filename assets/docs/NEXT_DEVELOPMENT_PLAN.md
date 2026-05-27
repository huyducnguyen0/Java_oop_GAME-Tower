# TowerDefence - Next Development Plan

Muc dich file nay: dung cung `assets/docs/PROJECT_CONTEXT.md` de chat moi vao viec nhanh, khong can doc lai toan bo hoi thoai cu.

Thu tu doc cho chat moi:
1. Doc `assets/docs/PROJECT_CONTEXT.md` de nam trang thai hien tai.
2. Doc file nay de biet nen lam tiep cai gi va lam theo thu tu nao.

---

## 1) Nguyen tac lam tiep

- Khong redesign gameplay tong the; tiep tuc polish va hoan thien tren code hien co.
- UI chi doc state va goi API cua `GameWorld`, khong tu giu gameplay logic rieng.
- Gameplay logic nam trong `Model/Systems`, `GameWorld`, `EntityManager`, `MapManager`.
- Renderer nam trong `View/ui/*Renderer.java`, duoc goi tu `DemoModelScreen`.
- Moi phase nen build bang `./gradlew build -x test`.
- Neu sua visual, can test truc tiep trong game vi build khong bat duoc sai scale/offset.

---

## 2) Issue uu tien cao nhat

### Phase A - Fix tower target priority

Van de hien tai:
- Tower phu da co combat va bi danh duoc.
- Nhung unit van co the chay thang toi main tower, khong chac chan uu tien danh tower gan nhat.

Muc tieu:
- Unit tren duong phai uu tien tower dich gan nhat khi tower do con song va nam trong vung phat hien/tam can thiep.
- Main tower chi la target cuoi khi khong con tower phu can duong, hoac unit da toi cuoi path.
- Sau khi tower bi pha, unit quay lai path hoac tim target tiep.

Files can xem/sua:
- `core/src/main/java/com/hust/towerdefence/Model/Systems/TargetingSystem.java`
- `core/src/main/java/com/hust/towerdefence/Model/Systems/MovementSystem.java`
- `core/src/main/java/com/hust/towerdefence/Model/Managers/EntityManager.java`
- `core/src/main/java/com/hust/towerdefence/Model/Entities/Tower/DefenseTower.java`

Huong implement de xuat:
- Them helper trong `TargetingSystem`:
  - `findNearestEnemyTowerInRange(CombatEntity source)`
  - hoac generic filter target tower truoc unit.
- Rule target cho non-healer:
  - Neu source la `DefenseTower`: chi target unit dich, khong target tower.
  - Neu source la unit: tim `DefenseTower` dich gan nhat trong effective range truoc.
  - Neu khong co tower trong range: tim unit dich gan nhat.
- Neu target tower chet/destroying/removed thi clear target.
- Can tranh cho Miner target tower/combat.

Acceptance check:
- Enemy di qua `tower1/tower2` se dung danh tower neu vao range.
- Player unit di qua `enemytower1/enemytower2` se dung danh tower neu vao range.
- Khi tower no xong bien mat, unit tiep tuc di path hoac danh target khac.
- Main tower khong bi danh truoc khi unit thuc su den cuoi path hoac khong con tower can duong.

---

## 3) Phase B - Direction and sprite correctness

Van de hien tai:
- UnitRenderer dang flip theo team, chua theo vector di chuyen/target.
- Lancer attack dang dung `Lancer_Right_Attack` cho moi huong.
- Spec mong muon: chi can 4 huong, khong can 8 huong day du.

Muc tieu:
- Unit quay trai/phai theo huong di chuyen hoac target.
- Neu co du asset 4 huong thi map dung direction cho Lancer.
- Neu chua du, toi thieu flip left/right cho tat ca unit theo movement/target.

Files can xem/sua:
- `core/src/main/java/com/hust/towerdefence/View/ui/UnitRenderer.java`
- `core/src/main/java/com/hust/towerdefence/Model/Entities/Combat/CombatEntity.java`
- `core/src/main/java/com/hust/towerdefence/Model/Systems/MovementSystem.java`

Huong implement de xuat:
- Cach nhe nhat: trong `UnitRenderer`, tinh direction:
  - Neu co target: compare target.x voi entity.x.
  - Neu dang moving va co path/current waypoint: compare waypoint.x voi entity.x.
  - Fallback theo team.
- Neu can luu facing tot hon: them field `facingX/facingY` vao `CombatEntity`, update trong `MovementSystem`.

Acceptance check:
- Player/enemy khong bi quay nguoc khi di chuyen.
- Archer/Warrior/Pawn attack nhin ve phia target.
- Lancer toi thieu khong bi quay sai trai/phai.

---

## 4) Phase C - Projectile polish

Trang thai hien tai:
- Projectile/effect la visual event, damage apply ngay trong `AttackSystem`.
- Arrow/dynamite/heal effect da render duoc.
- Tower arrow da duoc offset de bay tu vi tri archer tren tower.

Muc tieu:
- Projectile visual spawn dung diem tay/cung cua unit.
- Projectile speed/duration hop ly hon.
- Attack visual sync gan voi state attack, khong qua nhanh/qua cham.

Files can xem/sua:
- `core/src/main/java/com/hust/towerdefence/Model/Systems/AttackSystem.java`
- `core/src/main/java/com/hust/towerdefence/View/ui/CombatEffectRenderer.java`
- `core/src/main/java/com/hust/towerdefence/View/ui/UnitRenderer.java`

Huong implement de xuat:
- Them visual start offset theo class:
  - Archer: source.y + ~42
  - Warrior/Pawn melee: co the khong can projectile
  - TNT: source.y + ~34
  - DefenseTower: dung offset hien tai nhung test lai.
- Co the tinh duration theo distance thay vi constant:
  - `duration = clamp(distance / speedPxPerSecond, min, max)`

Acceptance check:
- Arrow khong bay tu chan/bung unit.
- Tower arrow bay tu archer tren tower.
- Dynamite bay nhin ro, khong qua nhanh.

---

## 5) Phase D - Clean official gameplay controls

Trang thai hien tai:
- UI mua chinh:
  - barrack -> Warrior
  - archery -> Archer
  - monastery -> Monk/Healer
  - house1 -> Lancer
  - house2 -> Miner
- Hotkey debug van co:
  - P spawn Pawn
  - M/A/W spawn Miner/Archer/Warrior
- User da noi Pawn khong nam trong flow mua chinh.

Muc tieu:
- Quyet dinh co giu hotkey debug khong.
- Neu lam final UI hon: bo hoac an Pawn debug spawn.

Files can xem/sua:
- `core/src/main/java/com/hust/towerdefence/View/screens/DemoModelScreen.java`
- `core/src/main/java/com/hust/towerdefence/Model/GameWorld.java`

Huong implement de xuat:
- De debug hotkey trong dev phase nhung ghi ro khong phai gameplay official.
- Neu user muon final: remove `Input.Keys.P` va co the remove `spawnPawn()` khoi UI path, nhung khong can xoa class.

Acceptance check:
- Official purchase flow khong co Pawn.
- Debug behavior neu giu thi khong gay nham lan trong HUD/UI.

---

## 6) Phase E - UI polish

Trang thai hien tai:
- HUD top-center da on hon debug bar cu.
- Purchase panel va tower info panel da dung duoc nhung visual con co ban.

Muc tieu:
- Panel mua linh va panel tower trong gon hon, doc de hon.
- Nut pause/buy/upgrade dong bo asset UI.
- Khong che path/chinh gameplay area.

Files can xem/sua:
- `core/src/main/java/com/hust/towerdefence/View/ui/GameHud.java`
- `core/src/main/java/com/hust/towerdefence/View/ui/PurchasePanel.java`
- `core/src/main/java/com/hust/towerdefence/View/ui/TowerInfoPanel.java`
- `core/src/main/java/com/hust/towerdefence/View/ui/UiAssets.java`

Acceptance check:
- HUD khong che castle/path quan trong.
- Purchase/tower panel gan object duoc click, khong vuot man hinh.
- Text de doc, khong giong debug.

---

## 7) Phase F - Optional gameplay extensions

Chi lam sau khi cac phase tren on:
- TNT explosion AOE thay vi direct damage.
- Real projectile hit timing (damage khi projectile cham target).
- Unit death animation thay vi remove ngay.
- Tower destroyed rubble sprite thay vi chi explosion roi bien mat.
- Main castle attack/defense neu user muon.
- Wave/level UI neu gameplay sau nay co wave.

---

## 8) Checklist cho chat moi bat dau lam

Khi bat dau chat moi, dua prompt ngan:

```text
Doc assets/docs/PROJECT_CONTEXT.md va assets/docs/NEXT_DEVELOPMENT_PLAN.md.
Lam Phase A: fix tower target priority. Khong redesign gameplay. Sau khi sua, build bang ./gradlew build -x test va bao file da sua + cach test trong game.
```

Neu muon lam phase khac, thay `Phase A` bang phase tuong ung.

