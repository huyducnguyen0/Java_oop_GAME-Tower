/*
package com.hust.towerdefence.View.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.BuildingZone;
import com.hust.towerdefence.Model.Managers.EconomyManager;

public class PurchasePanel {
    private static final float PANEL_WIDTH = 220f;
    private static final float PANEL_HEIGHT = 96f;
    private static final float PANEL_Y_OFFSET = 22f;
    private static final float SCREEN_MARGIN = 14f;
    private static final Color INK = new Color(0.17f, 0.09f, 0.04f, 1f);
    private static final Color MUTED = new Color(0.42f, 0.29f, 0.18f, 1f);
    private static final Color WARNING = new Color(0.65f, 0.12f, 0.08f, 1f);

    private final GameWorld gameWorld;
    private final Table panel;
    private final Label titleLabel;
    private final Label buildingLabel;
    private final Label costLabel;
    private final TextButton buyButton;

    private PurchaseOption selectedOption;
    private BuildingZone selectedZone;

    public PurchasePanel(Stage stage, GameWorld gameWorld, UiAssets assets) {
        this.gameWorld = gameWorld;
        this.panel = new Table();
        this.titleLabel = new Label("", assets.getTitleLabelStyle());
        this.buildingLabel = new Label("", assets.getMutedLabelStyle());
        this.costLabel = new Label("", assets.getDefaultLabelStyle());
        this.buyButton = new TextButton("Buy", assets.getPrimaryButtonStyle());

        buildLayout(assets);
        stage.addActor(panel);
        panel.setVisible(false);

        buyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buySelectedUnit();
            }
        });
    }

    private void buildLayout(UiAssets assets) {
        panel.setBackground(assets.getPanelDrawable());
        panel.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        panel.pad(12, 16, 12, 16);
        panel.defaults().center();

        titleLabel.setAlignment(Align.center);
        buildingLabel.setAlignment(Align.center);
        costLabel.setAlignment(Align.left);
        buyButton.getLabel().setAlignment(Align.center);

        panel.add(titleLabel).center().expandX().fillX().height(22).colspan(2);
        panel.row();
        panel.add(buildingLabel).center().expandX().fillX().height(18).colspan(2).padBottom(4);
        panel.row();
        panel.add(costLabel).left().expandX().fillX().height(30).padRight(8);
        panel.add(buyButton).width(76).height(30);
    }

    public void show(BuildingZone zone) {
        selectedZone = zone;
        selectedOption = PurchaseOption.fromBuilding(zone);
        if (selectedOption == null) {
            hide();
            return;
        }
        panel.setVisible(true);
        updateTextAndState();
    }

    public void hide() {
        selectedZone = null;
        selectedOption = null;
        panel.setVisible(false);
    }

    public void update(OrthographicCamera worldCamera, float screenWidth, float screenHeight) {
        if (selectedZone == null || selectedOption == null) return;
        updateTextAndState();

        Vector2 center = selectedZone.getCenter();
        Vector3 projected = worldCamera.project(new Vector3(center.x, center.y, 0f));
        float x = MathUtils.clamp(projected.x - PANEL_WIDTH / 2f, SCREEN_MARGIN, screenWidth - PANEL_WIDTH - SCREEN_MARGIN);
        float y = MathUtils.clamp(projected.y + PANEL_Y_OFFSET, SCREEN_MARGIN, screenHeight - PANEL_HEIGHT - SCREEN_MARGIN);
        panel.setPosition(x, y);
    }

    private void updateTextAndState() {
        if (selectedOption == null) return;
        int gold = gameWorld.getEconomyManager().getGold();
        boolean canBuy = gold >= selectedOption.cost;
        titleLabel.setText("Train " + selectedOption.unitName);
        buildingLabel.setText(selectedZone == null ? "" : selectedZone.getDisplayName());
        costLabel.setText("Gold " + selectedOption.cost);
        costLabel.setColor(canBuy ? INK : WARNING);
        buildingLabel.setColor(canBuy ? MUTED : WARNING);
        buyButton.setDisabled(!canBuy);
    }

    private void buySelectedUnit() {
        if (selectedOption == null || buyButton.isDisabled()) return;

        switch (selectedOption) {
            case WARRIOR:
                gameWorld.spawnWarrior();
                break;
            case ARCHER:
                gameWorld.spawnArcher();
                break;
            case HEALER:
                gameWorld.spawnHealer();
                break;
            case LANCER:
                gameWorld.spawnLancer();
                break;
            case MINER:
                gameWorld.spawnMiner();
                break;
            default:
                break;
        }
        updateTextAndState();
    }

    private enum PurchaseOption {
        WARRIOR("Warrior", EconomyManager.COST_WARRIOR),
        ARCHER("Archer", EconomyManager.COST_ARCHER),
        HEALER("Monk", EconomyManager.COST_HEALER),
        LANCER("Lancer", EconomyManager.COST_LANCER),
        MINER("Miner", EconomyManager.COST_MINER);

        private final String unitName;
        private final int cost;

        PurchaseOption(String unitName, int cost) {
            this.unitName = unitName;
            this.cost = cost;
        }

        private static PurchaseOption fromBuilding(BuildingZone zone) {
            if (zone == null) return null;
            String building = zone.getName();
            if ("barrack".equals(building)) return WARRIOR;
            if ("archery".equals(building)) return ARCHER;
            if ("monastery".equals(building)) return HEALER;
            if ("house1".equals(building)) return LANCER;
            if ("house2".equals(building)) return MINER;
            return null;
        }
    }
}
*/

/*// Hưng
package com.hust.towerdefence.View.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.BuildingZone;
import com.hust.towerdefence.Model.Managers.EconomyManager;

public class PurchasePanel {
    private static final float PANEL_WIDTH = 220f;
    private static final float PANEL_HEIGHT = 96f;
    private static final float PANEL_Y_OFFSET = 22f;
    private static final float SCREEN_MARGIN = 14f;
    private static final Color INK = new Color(0.17f, 0.09f, 0.04f, 1f);
    private static final Color MUTED = new Color(0.42f, 0.29f, 0.18f, 1f);
    private static final Color WARNING = new Color(0.65f, 0.12f, 0.08f, 1f);

    private final GameWorld gameWorld;
    private final Table panel;
    private final Label titleLabel;
    private final Label buildingLabel;
    private final Label costLabel;
    private final TextButton buyButton;

    private PurchaseOption selectedOption;
    private BuildingZone selectedZone;

    public PurchasePanel(Stage stage, GameWorld gameWorld, UiAssets assets) {
        this.gameWorld = gameWorld;
        this.panel = new Table();
        this.titleLabel = new Label("", assets.getTitleLabelStyle());
        this.buildingLabel = new Label("", assets.getMutedLabelStyle());
        this.costLabel = new Label("", assets.getDefaultLabelStyle());
        this.buyButton = new TextButton("Buy", assets.getPrimaryButtonStyle());

        buildLayout(assets);
        stage.addActor(panel);
        panel.setVisible(false);

        buyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buySelectedUnit();
            }
        });
    }

    private void buildLayout(UiAssets assets) {
        panel.setBackground(assets.getPanelDrawable());
        panel.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        panel.pad(12, 16, 12, 16);
        panel.defaults().center();

        titleLabel.setAlignment(Align.center);
        buildingLabel.setAlignment(Align.center);
        costLabel.setAlignment(Align.left);
        buyButton.getLabel().setAlignment(Align.center);

        panel.add(titleLabel).center().expandX().fillX().height(22).colspan(2);
        panel.row();
        panel.add(buildingLabel).center().expandX().fillX().height(18).colspan(2).padBottom(4);
        panel.row();
        panel.add(costLabel).left().expandX().fillX().height(30).padRight(8);
        panel.add(buyButton).width(76).height(30);
    }

    public void show(BuildingZone zone) {
        selectedZone = zone;
        selectedOption = PurchaseOption.fromBuilding(zone);
        if (selectedOption == null) {
            hide();
            return;
        }
        panel.setVisible(true);
        updateTextAndState();
    }

    public void hide() {
        selectedZone = null;
        selectedOption = null;
        panel.setVisible(false);
    }

    public void update(OrthographicCamera worldCamera, float screenWidth, float screenHeight) {
        if (selectedZone == null || selectedOption == null) return;
        updateTextAndState();

        Vector2 center = selectedZone.getCenter();
        Vector3 projected = worldCamera.project(new Vector3(center.x, center.y, 0f));
        float x = MathUtils.clamp(projected.x - PANEL_WIDTH / 2f, SCREEN_MARGIN, screenWidth - PANEL_WIDTH - SCREEN_MARGIN);
        float y = MathUtils.clamp(projected.y + PANEL_Y_OFFSET, SCREEN_MARGIN, screenHeight - PANEL_HEIGHT - SCREEN_MARGIN);
        panel.setPosition(x, y);
    }

    private void updateTextAndState() {
        if (selectedOption == null) return;
        int gold = gameWorld.getEconomyManager().getGold();
        boolean canBuy = gold >= selectedOption.cost;
        titleLabel.setText("Train " + selectedOption.unitName);
        buildingLabel.setText(selectedZone == null ? "" : selectedZone.getDisplayName());
        costLabel.setText("Gold " + selectedOption.cost);
        costLabel.setColor(canBuy ? INK : WARNING);
        buildingLabel.setColor(canBuy ? MUTED : WARNING);

        // GIỮ NGUYÊN GỐC: Đặt disabled theo lượng tiền thực tế
        buyButton.setDisabled(!canBuy);
    }

    private void buySelectedUnit() {
        if (selectedOption == null || buyButton.isDisabled()) return;

        switch (selectedOption) {
            case WARRIOR:
                gameWorld.spawnWarrior();
                break;
            case ARCHER:
                gameWorld.spawnArcher();
                break;
            case HEALER:
                gameWorld.spawnHealer();
                break;
            case LANCER:
                gameWorld.spawnLancer();
                break;
            case MINER:
                gameWorld.spawnMiner();
                break;
            default:
                break;
        }

        // SỬA ĐÚNG DÒNG NÀY: Ép nút nhả trạng thái Checked dính chuột của LibGDX ra lập tức.
        // Điều này đảm bảo nút nháy nảy lên một cái mượt mà, và giữ nguyên màu xanh tươi nếu tiền vẫn đủ mua tiếp!
        buyButton.setChecked(false);

        updateTextAndState();
    }

    private enum PurchaseOption {
        WARRIOR("Warrior", EconomyManager.COST_WARRIOR),
        ARCHER("Archer", EconomyManager.COST_ARCHER),
        HEALER("Monk", EconomyManager.COST_HEALER),
        LANCER("Lancer", EconomyManager.COST_LANCER),
        MINER("Miner", EconomyManager.COST_MINER);

        private final String unitName;
        private final int cost;

        PurchaseOption(String unitName, int cost) {
            this.unitName = unitName;
            this.cost = cost;
        }

        private static PurchaseOption fromBuilding(BuildingZone zone) {
            if (zone == null) return null;
            String building = zone.getName();
            if ("barrack".equals(building)) return WARRIOR;
            if ("archery".equals(building)) return ARCHER;
            if ("monastery".equals(building)) return HEALER;
            if ("house1".equals(building)) return LANCER;
            if ("house2".equals(building)) return MINER;
            return null;
        }
    }
}*/


// Hưng
package com.hust.towerdefence.View.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.BuildingZone;
import com.hust.towerdefence.Model.Managers.EconomyManager;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Soldier;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Warrior;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Archer;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Lancer;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Healer;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Miner;

/**
 * Bang dieu khien mua linh va nang cap thap/nha cong trinh.
 * Da duoc chuan hoa de truy van thong so HOAN TOAN DONG tu cac mang du lieu static cua 5 class linh.
 */
public class PurchasePanel {
    // --- Cac hang so cau hinh kich thuoc va vi tri Giao dien ---
    private static final float PANEL_WIDTH = 240f;
    private static final float PANEL_HEIGHT = 155f;
    private static final float PANEL_Y_OFFSET = 22f;
    private static final float SCREEN_MARGIN = 14f;

    // --- Bang Mau He Thong Dieu Khien UI ---
    private static final Color INK = new Color(0.17f, 0.09f, 0.04f, 1f);     // Nau dam (Du dieu kien)
    private static final Color MUTED = new Color(0.42f, 0.29f, 0.18f, 1f);   // Nau mo (Max cap hoac thieu tien)
    private static final Color WARNING = new Color(0.65f, 0.12f, 0.08f, 1f); // Mau do canh bao (Thiieu tien mua linh)

    // --- Thanh phan Core Logic Ket Noi Mo Hinh Game ---
    private final GameWorld gameWorld;
    private final Table panel;
    private PurchaseOption selectedOption;
    private BuildingZone selectedZone;

    // --- Cac Nhan Hien Thi Van Ban (Labels) ---
    private final Label titleLabel;
    private final Label buildingLabel;
    private final Label hpLabel;
    private final Label statsLabel; // Nhan dung chung hien thi DMG va RNG
    private final Label costLabel;
    private final Label upgradeCostLabel;

    // --- Cac Nut Tuong Tac (Buttons) ---
    private final TextButton buyButton;
    private final TextButton upgradeButton;

    // --- Luu Tru Trang thai Cap do hien tai cua tung loai cong trinh ---
    private int warriorLevel = 1;
    private int archerLevel = 1;
    private int healerLevel = 1;
    private int lancerLevel = 1;
    private int minerLevel = 1;

    /**
     * Ham khoi tao PurchasePanel, thiet lap Listener tuong tact va nap UI vao Stage
     */
    public PurchasePanel(Stage stage, GameWorld gameWorld, UiAssets assets) {
        this.gameWorld = gameWorld;
        this.panel = new Table();

        // Khoi tao cac thanh phan giao dien tu Assets he thong
        this.titleLabel = new Label("", assets.getTitleLabelStyle());
        this.buildingLabel = new Label("", assets.getMutedLabelStyle());
        this.hpLabel = new Label("", assets.getDefaultLabelStyle());
        this.statsLabel = new Label("", assets.getDefaultLabelStyle());
        this.costLabel = new Label("", assets.getDefaultLabelStyle());
        this.upgradeCostLabel = new Label("", assets.getDefaultLabelStyle());

        this.buyButton = new TextButton("Buy", assets.getPrimaryButtonStyle());

        // Tao mot Style rieng biet hoan toan cho nut Upgrade bang cach copy tu style goc de tranh xung dot
        TextButton.TextButtonStyle upgradeStyle = new TextButton.TextButtonStyle(assets.getPrimaryButtonStyle());
        this.upgradeButton = new TextButton("Upgrade", upgradeStyle);

        // Xay dung bo cuc Layout va dua vao Stage ve
        buildLayout(assets);
        stage.addActor(panel);
        panel.setVisible(false);

        // Dang ky su kien nhan nut Buy (Dung ChangeListener truyen thong)
        buyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buySelectedUnit();
            }
        });

        // Doi sang ClickListener de chi bat chinh xac su kien click chuot, tranh lap loop tin hieu khi doi trang thai nut
        upgradeButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                upgradeSelectedBuilding();
            }
        });
    }

    /**
     * Sap xep luoi Layout o bang cho Panel bang Table UI Component
     */
    private void buildLayout(UiAssets assets) {
        panel.setBackground(assets.getPanelDrawable());
        panel.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        panel.pad(10, 12, 10, 12);
        panel.defaults().center();

        titleLabel.setAlignment(Align.center);
        buildingLabel.setAlignment(Align.center);
        hpLabel.setAlignment(Align.left);
        statsLabel.setAlignment(Align.left);
        costLabel.setAlignment(Align.left);
        upgradeCostLabel.setAlignment(Align.left);

        buyButton.getLabel().setAlignment(Align.center);
        upgradeButton.getLabel().setAlignment(Align.center);

        // Hang 1 & 2: Tieu de linh va ten cong trinh
        panel.add(titleLabel).center().expandX().fillX().height(20).colspan(2);
        panel.row();
        panel.add(buildingLabel).center().expandX().fillX().height(16).colspan(2).padBottom(4);
        panel.row();

        // Hang 3 & 4: Thong so ky thuat chi tiet cua don vi linh (HP, DMG, RNG)
        panel.add(hpLabel).left().expandX().fillX().height(18).colspan(2);
        panel.row();
        panel.add(statsLabel).left().expandX().fillX().height(18).colspan(2).padBottom(6);
        panel.row();

        // Hang 5: Gia mua va nut Mua (Buy)
        panel.add(costLabel).left().expandX().fillX().height(26).padRight(4);
        panel.add(buyButton).width(88).height(26).padBottom(4);
        panel.row();

        // Hang 6: Gia nang cap va nut Nang cap (Upgrade)
        panel.add(upgradeCostLabel).left().expandX().fillX().height(26).padRight(4);
        panel.add(upgradeButton).width(88).height(26);
    }

    /**
     * Hien thi bang dieu khien khi nguoi choi chon vao mot o cong trinh hop le
     */
    public void show(BuildingZone zone) {
        selectedZone = zone;
        selectedOption = PurchaseOption.fromBuilding(zone);
        if (selectedOption == null) {
            hide();
            return;
        }
        panel.setVisible(true);
        updateTextAndState();
    }

    /**
     * An bang dieu khien va giai phong cac muc tieu dang chon
     */
    public void hide() {
        selectedZone = null;
        selectedOption = null;
        panel.setVisible(false);
    }

    /**
     * Ham cap nhat chay lien tuc moi khung hinh (render loop) de ghim toa do theo camera va quet du lieu realtime
     */
    public void update(OrthographicCamera worldCamera, float screenWidth, float screenHeight) {
        if (selectedZone == null || selectedOption == null) return;

        // Quet va cap nhat mau sac van ban, bat/tat trang thai nen nut theo vi tien hien hanh lien tuc
        updateButtonStatesRealtime();

        // Ghim vi tri Panel UI noi theo toa do the gioi cua o cong trinh
        Vector2 center = selectedZone.getCenter();
        Vector3 projected = worldCamera.project(new Vector3(center.x, center.y, 0f));
        float x = MathUtils.clamp(projected.x - PANEL_WIDTH / 2f, SCREEN_MARGIN, screenWidth - PANEL_WIDTH - SCREEN_MARGIN);
        float y = MathUtils.clamp(projected.y + PANEL_Y_OFFSET, SCREEN_MARGIN, screenHeight - PANEL_HEIGHT - SCREEN_MARGIN);
        panel.setPosition(x, y);
    }

    /**
     * Quet luong tien cua nguoi choi realtime de dieu khien chinh xac trang thai hien thi cua cac nut bam
     */
    private void updateButtonStatesRealtime() {
        if (selectedOption == null) return;

        int gold = gameWorld.getEconomyManager().getGold();
        int currentLv = getCurrentLevelOfSelectedOption();

        // Thuc hien xu ly quet doc lap hoan toan cho hai khoi Buy va Upgrade
        handleBuyButtonRealtime(gold);
        handleUpgradeButtonRealtime(gold, currentLv);
    }

    /**
     * Xu ly quet Realtime chuyen biet cho khoi BUY (Mua linh)
     */
    private void handleBuyButtonRealtime(int gold) {
        boolean canBuy = gold >= selectedOption.cost;

        // Du tien -> chu mau nau dam (INK), Thieu tien -> chu canh bao mau do (WARNING)
        costLabel.setColor(canBuy ? INK : WARNING);

        // Cap nhat trang thai kich hoat nut de LibGDX tu dong chuyen doi giua nen xanh tuoi va xam mo
        if (buyButton.isDisabled() != !canBuy) {
            buyButton.setDisabled(!canBuy);
        }
    }

    /**
     * Xu ly quet Realtime chuyen biet cho khoi UPGRADE (Nang cap) - Doc chi so tu mang static class linh con
     */
    private void handleUpgradeButtonRealtime(int gold, int currentLv) {
        if (currentLv >= 3) {
            // TRANG THAI 1: Da kich cap toi da -> Ep mo nen (disabled = true) va doi chu sang mau mo (MUTED)
            upgradeButton.setDisabled(true);
            upgradeCostLabel.setColor(MUTED);
        } else {
            // TUY BIEN DONG: Lay truc tiep chi phi nang cap tu file mang static cua con linh thong qua delegate enum
            int upgradeCost = selectedOption.getStaticUpgradeCostForLevel(currentLv);
            boolean canUpgrade = gold >= upgradeCost;

            if (canUpgrade) {
                // TRANG THAI 2A: Chua max + DU TIEN -> Mo khoa nut nen xanh (disabled = false) va dat chu nau dam (INK)
                upgradeButton.setDisabled(false);
                upgradeCostLabel.setColor(INK);
            } else {
                // TRANG THAI 2B: Chua max + THIEU TIEN -> Khoa nut dang nen mo (disabled = true) va chuyen chu ve mo (MUTED)
                upgradeButton.setDisabled(true);
                upgradeCostLabel.setColor(MUTED);
            }
        }
    }

    /**
     * Thiet lap noi dung chu van ban tinh cho cac Label khi mo bang UI hoac ngay sau khi hoan thanh nang cap
     */
    private void updateTextAndState() {
        if (selectedOption == null) return;
        int currentLv = getCurrentLevelOfSelectedOption();

        // 1. Cap nhat cac noi dung van ban co ban
        titleLabel.setText("Train " + selectedOption.unitName + " (Lv " + currentLv + ")");
        buildingLabel.setText(selectedZone == null ? "" : selectedZone.getDisplayName());
        costLabel.setText("Gold: " + selectedOption.cost);

        // 2. Nap lai thong so chi so suc manh thuc te cua linh (HP, DMG, RNG) lay tu mang static trong class linh
        updateUnitStatsText(currentLv);

        // 3. Xu ly chuoi van ban hien thi cho gia nang cap cong trinh tu mang static
        if (currentLv >= 3) {
            upgradeCostLabel.setText("Upgrade: Max");
        } else {
            int upgradeCost = selectedOption.getStaticUpgradeCostForLevel(currentLv);
            upgradeCostLabel.setText("Upgrade: " + upgradeCost);
        }

        // 4. Dong bo hoa ngay trang thai nut bam lap tuc
        updateButtonStatesRealtime();
    }

    /**
     * Truy van du lieu mang static tap trung tu class linh tuong ung de in chi so len panel (Xoa sach cong thuc khai khong cu)
     */
    private void updateUnitStatsText(int level) {
        int hp = (int) selectedOption.getStaticMaxHealth(level);
        float dmg = selectedOption.getStaticDamage(level);
        float rng = selectedOption.getStaticRange(level);

        // Trinh bay nhan ngu nghia tieng viet khong dau theo tung kieu loai linh dac thu cua doi hinh
        if (selectedOption == PurchaseOption.MINER) {
            hpLabel.setText("");
            statsLabel.setText("EFF: " + (int)dmg + " Gold / cycle");
        } else if (selectedOption == PurchaseOption.HEALER) {
            hpLabel.setText("HP: " + hp);
            statsLabel.setText("HEAL: " + (int)dmg + "      RNG: " + rng);
        } else {
            hpLabel.setText("HP: " + hp);
            statsLabel.setText("ATK: " + (int)dmg + "      RNG: " + rng);
        }
    }

    /**
     * Lay ra cap do hien tai cua loai cong trinh/linh dang duoc chon
     */
    private int getCurrentLevelOfSelectedOption() {
        if (selectedOption == null) return 1;
        switch (selectedOption) {
            case WARRIOR: return warriorLevel;
            case ARCHER:  return archerLevel;
            case HEALER:  return healerLevel;
            case LANCER:  return lancerLevel;
            case MINER:   return minerLevel;
            default:      return 1;
        }
    }

    /**
     * Tang cap do cong trinh len 1 bac (Toi da la cap 3)
     */
    private void incrementLevelOfSelectedOption() {
        if (selectedOption == null) return;
        switch (selectedOption) {
            case WARRIOR: if (warriorLevel < 3) warriorLevel++; break;
            case ARCHER:  if (archerLevel < 3) archerLevel++; break;
            case HEALER:  if (healerLevel < 3) healerLevel++; break;
            case LANCER:  if (lancerLevel < 3) lancerLevel++; break;
            case MINER:   if (minerLevel < 3) minerLevel++; break;
        }
    }

    /**
     * Thuc thi Logic goi Spawn mua mot don vi linh moi va gan cap do tuong ung
     */
    private void buySelectedUnit() {
        if (selectedOption == null || buyButton.isDisabled()) return;

        switch (selectedOption) {
            case WARRIOR: gameWorld.spawnWarrior(); break;
            case ARCHER:  gameWorld.spawnArcher(); break;
            case HEALER:  gameWorld.spawnHealer(); break;
            case LANCER:  gameWorld.spawnLancer(); break;
            case MINER:   gameWorld.spawnMiner(); break;
            default: break;
        }

        // Dong bo cap do cho linh vua sinh ra neu cong trinh da duoc nang cap truoc do
        int currentLv = getCurrentLevelOfSelectedOption();
        if (currentLv > 1 && !gameWorld.getEntityManager().getSoldiers().isEmpty()) {
            Soldier newest = gameWorld.getEntityManager().getSoldiers().peek();
            if (newest != null) {
                newest.setLevel(currentLv);
            }
        }

        // Nha trang thai dinh chuot cua LibGDX ngay lap tuc de tao hieu ung nhap nhay phan hoi muot ma
        buyButton.setChecked(false);
        updateTextAndState();
    }

    /**
     * Thuc thi Logic tru vang nang cap cong trinh va dong bo cap do cho toan bo linh hien co tren ban do
     */
    private void upgradeSelectedBuilding() {
        // Chan tuong tac ngay tu dau neu nut dang hien thi mo dang vo hieu hoa (Khi thieu tien hoac kich cap)
        if (selectedOption == null || upgradeButton.isDisabled()) return;

        int currentLv = getCurrentLevelOfSelectedOption();
        if (currentLv >= 3) return;

        // Doc gia nang cap thoi gian thuc tu mang tinh cua lop linh con thong qua trung gian delegate
        int cost = selectedOption.getStaticUpgradeCostForLevel(currentLv);
        int gold = gameWorld.getEconomyManager().getGold();

        // Kiem tra dieu kien vi tien hop le truoc khi thuc hien giao dich tru vang nang cap
        if (gold >= cost && cost > 0) {
            gameWorld.getEconomyManager().spendGold(cost);

            incrementLevelOfSelectedOption();
            int newLevel = getCurrentLevelOfSelectedOption();

            // Quet danh sach thuc the de nang cap chi so dong loat cho toan bo linh cung loai dang chien dau
            String targetOptionName = selectedOption.name();
            for (Soldier s : gameWorld.getEntityManager().getSoldiers()) {
                if (s != null && s.getClass().getSimpleName().toUpperCase().contains(targetOptionName)) {
                    s.setLevel(newLevel);
                } else if (selectedOption == PurchaseOption.HEALER && s != null && "Healer".equals(s.getClass().getSimpleName())) {
                    s.setLevel(newLevel);
                }
            }

            // CAP NHAT NOI DUNG CHU VAN BAN DE HIEN THI CHUAN CAP DO MOI LAP TUC
            titleLabel.setText("Train " + selectedOption.unitName + " (Lv " + newLevel + ")");
            updateUnitStatsText(newLevel);
            if (newLevel >= 3) {
                upgradeCostLabel.setText("Upgrade: Max");
            } else {
                int nextUpgradeCost = selectedOption.getStaticUpgradeCostForLevel(newLevel);
                upgradeCostLabel.setText("Upgrade: " + nextUpgradeCost);
            }
        }

        // Ep nut nha trang thai Checked dinh chuot cua LibGDX ngay lap tuc de tao hieu ung nhan nhay nay len chuan chi
        upgradeButton.setChecked(false);
    }

    /**
     * Bo du lieu cau hinh thong tin dinh danh va gia mua goc.
     * Da duoc vut bo hoan toan mang so upgradeCosts cung cung cua Enum de chuyen giao tiep sang file linh.
     */
    private enum PurchaseOption {
        WARRIOR("Warrior", EconomyManager.COST_WARRIOR),
        ARCHER("Archer", EconomyManager.COST_ARCHER),
        HEALER("Monk", EconomyManager.COST_HEALER),
        LANCER("Lancer", EconomyManager.COST_LANCER),
        MINER("Miner", EconomyManager.COST_MINER);

        private final String unitName;
        private final int cost;

        PurchaseOption(String unitName, int cost) {
            this.unitName = unitName;
            this.cost = cost;
        }

        // ====================================================================
        // HE THONG DELEGATE CHUYEN TIEP LOGIC GOI SANG CHUOI MANG TINH (STATIC)
        // ====================================================================

        public float getStaticMaxHealth(int level) {
            switch (this) {
                case WARRIOR: return Warrior.getStaticMaxHealth(level);
                case ARCHER:  return Archer.getStaticMaxHealth(level);
                case HEALER:  return Healer.getStaticMaxHealth(level);
                case LANCER:  return Lancer.getStaticMaxHealth(level);
                case MINER:   return Miner.getStaticMaxHealth(level);
                default: return 0f;
            }
        }

        public float getStaticDamage(int level) {
            switch (this) {
                case WARRIOR: return Warrior.getStaticDamage(level);
                case ARCHER:  return Archer.getStaticDamage(level);
                case HEALER:  return Healer.getStaticDamage(level);
                case LANCER:  return Lancer.getStaticDamage(level);
                case MINER:   return Miner.getStaticDamage(level);
                default: return 0f;
            }
        }

        public float getStaticRange(int level) {
            switch (this) {
                case WARRIOR: return Warrior.getStaticRange(level);
                case ARCHER:  return Archer.getStaticRange(level);
                case HEALER:  return Healer.getStaticRange(level);
                case LANCER:  return Lancer.getStaticRange(level);
                case MINER:   return Miner.getStaticRange(level);
                default: return 0f;
            }
        }

        public int getStaticUpgradeCostForLevel(int level) {
            switch (this) {
                case WARRIOR: return Warrior.getStaticUpgradeCost(level);
                case ARCHER:  return Archer.getStaticUpgradeCost(level);
                case HEALER:  return Healer.getStaticUpgradeCost(level);
                case LANCER:  return Lancer.getStaticUpgradeCost(level);
                case MINER:   return Miner.getStaticUpgradeCost(level);
                default: return 0;
            }
        }

        /**
         * Anh xa dinh danh chuoi ten vung cong trinh tu TiledMap/Logic sang doi tuong enum tuong ung
         */
        private static PurchaseOption fromBuilding(BuildingZone zone) {
            if (zone == null) return null;
            String building = zone.getName();
            if ("barrack".equals(building)) return WARRIOR;
            if ("archery".equals(building)) return ARCHER;
            if ("monastery".equals(building)) return HEALER;
            if ("house1".equals(building)) return LANCER;
            if ("house2".equals(building)) return MINER;
            return null;
        }
    }
}
