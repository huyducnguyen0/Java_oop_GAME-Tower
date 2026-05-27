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

## 2) Trang thai phase hien tai

Da hoan thanh:
- Phase A - Tower target priority:
  - Unit uu tien DefenseTower dich trong vung can thiep.
  - Melee tinh khoang cach toi mep tower nen khong bo qua tower ngoai vi target center qua xa.
  - DefenseTower van chi target unit, khong target tower.
- Phase B - Direction and sprite correctness:
  - `CombatEntity` co facing vector.
  - `MovementSystem` cap nhat facing theo path/target.
  - `UnitRenderer` flip theo facing, khong theo team.
  - Lancer attack dung cac asset huong Right/Up/Down/UpRight/DownRight, huong trai flip.
  - Lancer scale da chinh rieng de gan bang linh khac.
- Phase C - Projectile polish:
  - Archer/TNT/DefenseTower projectile co start offset hop ly hon.
  - Projectile bay den mep tower khi target la tower.
  - Arrow/dynamite duration tinh theo distance va clamp min/max.
- Phase E - UI polish lan 1:
  - `UiAssets` tach style HUD/panel/button.
  - HUD/PurchasePanel/TowerInfoPanel dung asset `assets/UI`.
  - Luu y quan trong: KHONG dung `*_9Slides` truc tiep cho panel/button nho; da gay UI phong to xau. Hien tai dung `*_3Slides` + min size drawable = 0.

Con can lam tiep:
- Phase D - Clean official gameplay controls.
- UI polish lan 2 sau khi user review screenshot truc tiep.
- Real projectile hit timing neu can gameplay chinh xac hon.
- TNT AOE optional.
- Death animation/rubble optional.

---

## 3) Issue uu tien cao nhat tiep theo

### Phase D - Clean official gameplay controls

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
  - SPACE pause
  - F1 AI status
- User da noi Pawn khong nam trong flow mua chinh.

Muc tieu:
- Lam ro gameplay official khong co Pawn.
- Quyet dinh co giu hotkey debug khong:
  - Neu ban nop/demo final: nen remove/disable `P = Pawn` va log debug lien quan.
  - Co the giu M/A/W neu van can debug, nhung nen an khoi huong dan console final.

Files can xem/sua:
- `core/src/main/java/com/hust/towerdefence/View/screens/DemoModelScreen.java`
- `core/src/main/java/com/hust/towerdefence/Model/GameWorld.java`

Huong implement de xuat:
- Toi thieu:
  - Remove case `Input.Keys.P` trong `handleInputKey`.
  - Sua log ready khong nhac `P=Pawn`.
- Neu muon sach hon:
  - Doi debug hotkey thanh flag `DEBUG_HOTKEYS`.
  - Khi flag false, tat M/A/W/P/F1, chi giu SPACE pause.

Acceptance check:
- Official purchase flow khong co Pawn.
- Bam P trong game khong spawn Pawn nua neu da chon final mode.
- Build pass bang `./gradlew build -x test`.

---

## 4) UI polish lan 2

Trang thai hien tai:
- UI da polish lan 1 bang `assets/UI`.
- Da sua loi dung nham `*_9Slides`, hien tai panel/button nho dung `*_3Slides`.
- Van can user review screenshot truc tiep vi visual taste khong bat duoc bang build.

Muc tieu:
- HUD/panel nho gon, khong che building/path.
- Text doc duoc, button khong bi phong to.
- Panel mua/tower nhin dong bo voi pixel fantasy map.

Files can xem/sua:
- `core/src/main/java/com/hust/towerdefence/View/ui/UiAssets.java`
- `core/src/main/java/com/hust/towerdefence/View/ui/GameHud.java`
- `core/src/main/java/com/hust/towerdefence/View/ui/PurchasePanel.java`
- `core/src/main/java/com/hust/towerdefence/View/ui/TowerInfoPanel.java`

Can tranh:
- Khong dung `Button_*_9Slides.png` / `Carved_9Slides.png` cho button/panel nho neu khong xu ly nine-slice dung cach.
- Khong de drawable min size mac dinh ep layout.

Acceptance check:
- Chup screenshot click barrack/house/tower/enemy tower.
- Panel khong tran man hinh va khong che lane quan trong.
- Button height khoang 28-34px, khong bi keo thanh doc.

---

## 5) Phase da hoan thanh chi tiet

### Phase A - Fix tower target priority (DONE)

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

Acceptance check da dat:
- Enemy di qua `tower1/tower2` se dung danh tower neu vao range.
- Player unit di qua `enemytower1/enemytower2` se dung danh tower neu vao range.
- Khi tower no xong bien mat, unit tiep tuc di path hoac danh target khac.
- Main tower khong bi danh truoc khi unit thuc su den cuoi path hoac khong con tower can duong.

---

### Phase B - Direction and sprite correctness (DONE)

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

Acceptance check da dat/co ban:
- Player/enemy khong bi quay nguoc khi di chuyen.
- Archer/Warrior/Pawn attack nhin ve phia target.
- Lancer toi thieu khong bi quay sai trai/phai.

---

### Phase C - Projectile polish (DONE)

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

Acceptance check da dat/co ban:
- Arrow khong bay tu chan/bung unit.
- Tower arrow bay tu archer tren tower.
- Dynamite bay nhin ro, khong qua nhanh.

---

## 6) Phase F - Optional gameplay extensions

Chi lam sau khi cac phase tren on:
- TNT explosion AOE thay vi direct damage.
- Real projectile hit timing (damage khi projectile cham target).
- Unit death animation thay vi remove ngay.
- Tower destroyed rubble sprite thay vi chi explosion roi bien mat.
- Main castle attack/defense neu user muon.
- Wave/level UI neu gameplay sau nay co wave.

---

## 7) Checklist cho chat moi bat dau lam

Khi bat dau chat moi, dua prompt ngan:

```text
Doc assets/docs/PROJECT_CONTEXT.md va assets/docs/NEXT_DEVELOPMENT_PLAN.md.
Lam Phase D: clean official gameplay controls. Khong redesign gameplay. Sau khi sua, build bang ./gradlew build -x test va bao file da sua + cach test trong game.
```

Neu muon tiep tuc UI, thay Phase D bang "UI polish lan 2" va yeu cau chup/review screenshot trong game.
