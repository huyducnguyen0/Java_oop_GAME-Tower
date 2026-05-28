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

public class PurchasePanel {
    private static final float PANEL_WIDTH = 240f;
    private static final float PANEL_HEIGHT = 155f;
    private static final float PANEL_Y_OFFSET = 22f;
    private static final float SCREEN_MARGIN = 14f;
    private static final Color INK = new Color(0.17f, 0.09f, 0.04f, 1f);
    private static final Color MUTED = new Color(0.42f, 0.29f, 0.18f, 1f);
    private static final Color WARNING = new Color(0.65f, 0.12f, 0.08f, 1f);

    private final GameWorld gameWorld;
    private final Table panel;
    private final Label titleLabel;
    private final Label buildingLabel;

    private final Label hpLabel;
    private final Label statsLabel; // Nhãn dùng chung cho DMG và RNG

    private final Label costLabel;
    private final Label upgradeCostLabel;
    private final TextButton buyButton;
    private final TextButton upgradeButton;

    private PurchaseOption selectedOption;
    private BuildingZone selectedZone;

    private int warriorLevel = 1;
    private int archerLevel = 1;
    private int healerLevel = 1;
    private int lancerLevel = 1;
    private int minerLevel = 1;

    public PurchasePanel(Stage stage, GameWorld gameWorld, UiAssets assets) {
        this.gameWorld = gameWorld;
        this.panel = new Table();
        this.titleLabel = new Label("", assets.getTitleLabelStyle());
        this.buildingLabel = new Label("", assets.getMutedLabelStyle());

        this.hpLabel = new Label("", assets.getDefaultLabelStyle());
        this.statsLabel = new Label("", assets.getDefaultLabelStyle());

        this.costLabel = new Label("", assets.getDefaultLabelStyle());
        this.upgradeCostLabel = new Label("", assets.getDefaultLabelStyle());
        this.buyButton = new TextButton("Buy", assets.getPrimaryButtonStyle());
        this.upgradeButton = new TextButton("Upgrade", assets.getPrimaryButtonStyle());

        buildLayout(assets);
        stage.addActor(panel);
        panel.setVisible(false);

        buyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buySelectedUnit();
            }
        });

        upgradeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                upgradeSelectedBuilding();
            }
        });
    }

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

        panel.add(titleLabel).center().expandX().fillX().height(20).colspan(2);
        panel.row();
        panel.add(buildingLabel).center().expandX().fillX().height(16).colspan(2).padBottom(4);
        panel.row();

        panel.add(hpLabel).left().expandX().fillX().height(18).colspan(2);
        panel.row();
        panel.add(statsLabel).left().expandX().fillX().height(18).colspan(2).padBottom(6);
        panel.row();

        panel.add(costLabel).left().expandX().fillX().height(26).padRight(4);
        panel.add(buyButton).width(88).height(26).padBottom(4);
        panel.row();

        panel.add(upgradeCostLabel).left().expandX().fillX().height(26).padRight(4);
        panel.add(upgradeButton).width(88).height(26);
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

        updateButtonStatesRealtime();

        Vector2 center = selectedZone.getCenter();
        Vector3 projected = worldCamera.project(new Vector3(center.x, center.y, 0f));
        float x = MathUtils.clamp(projected.x - PANEL_WIDTH / 2f, SCREEN_MARGIN, screenWidth - PANEL_WIDTH - SCREEN_MARGIN);
        float y = MathUtils.clamp(projected.y + PANEL_Y_OFFSET, SCREEN_MARGIN, screenHeight - PANEL_HEIGHT - SCREEN_MARGIN);
        panel.setPosition(x, y);
    }

    private void updateButtonStatesRealtime() {
        if (selectedOption == null) return;
        int gold = gameWorld.getEconomyManager().getGold();
        int currentLv = getCurrentLevelOfSelectedOption();

        // 1. Logic màu và trạng thái của Buy (Giữ nguyên gốc)
        boolean canBuy = gold >= selectedOption.cost;
        costLabel.setColor(canBuy ? INK : WARNING);
        if (buyButton.isDisabled() != !canBuy) {
            buyButton.setDisabled(!canBuy);
        }

        // 2. CHỈ SỬA ĐÚNG MÀU CỦA UPGRADE TẠI ĐÂY (Học tập theo Buy)
        if (currentLv >= 3) {
            upgradeCostLabel.setColor(MUTED);
            if (!upgradeButton.isDisabled()) {
                upgradeButton.setDisabled(true);
            }
        } else {
            int upgradeCost = selectedOption.getUpgradeCostForLevel(currentLv);
            boolean canUpgrade = gold >= upgradeCost;

            // Nếu đủ tiền thì để màu chữ INK đậm rõ ràng, thiếu tiền đổi sang màu WARNING (đỏ) giống hệt Buy
            upgradeCostLabel.setColor(canUpgrade ? INK : WARNING);

            if (upgradeButton.isDisabled() != !canUpgrade) {
                upgradeButton.setDisabled(!canUpgrade);
            }
        }
    }

    private void updateTextAndState() {
        if (selectedOption == null) return;
        int currentLv = getCurrentLevelOfSelectedOption();

        titleLabel.setText("Train " + selectedOption.unitName + " (Lv " + currentLv + ")");
        buildingLabel.setText(selectedZone == null ? "" : selectedZone.getDisplayName());
        costLabel.setText("Gold " + selectedOption.cost);

        // Nạp text chỉ số mới có RNG
        updateUnitStatsText(currentLv);

        if (currentLv >= 3) {
            upgradeCostLabel.setText("Upgrade: Max");
            upgradeCostLabel.setColor(MUTED);
            upgradeButton.setDisabled(true);
        } else {
            int upgradeCost = selectedOption.getUpgradeCostForLevel(currentLv);
            upgradeCostLabel.setText("Upgrade: " + upgradeCost);
        }

        updateButtonStatesRealtime();
    }

    /**
     * Đã chuyển hoàn toàn từ SPD sang RNG mẫu theo từng loại lính
     */
    private void updateUnitStatsText(int level) {
        int hp = 0;
        int dmg = 0;
        int rng = 0;

        switch (selectedOption) {
            case WARRIOR:
                hp = 120 + (level - 1) * 40;
                dmg = 15 + (level - 1) * 5;
                rng = 40; // Đánh gần tầm ngắn
                break;
            case ARCHER:
                hp = 80 + (level - 1) * 25;
                dmg = 12 + (level - 1) * 4;
                rng = 180 + (level - 1) * 20; // Cung thủ tăng tầm bắn khi lên cấp
                break;
            case HEALER:
                hp = 90 + (level - 1) * 30;
                dmg = 10 + (level - 1) * 3;
                rng = 120;
                break;
            case LANCER:
                hp = 110 + (level - 1) * 35;
                dmg = 18 + (level - 1) * 6;
                rng = 60; // Giáo dài tầm xa hơn kiếm chút
                break;
            case MINER:
                hp = 100 + (level - 1) * 30;
                dmg = 8 + (level - 1) * 2;
                rng = 35;
                break;
        }

        hpLabel.setText("HP: " + hp);
        statsLabel.setText("DMG: " + dmg + "      RNG: " + rng);
    }

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

        int currentLv = getCurrentLevelOfSelectedOption();
        if (currentLv > 1 && !gameWorld.getEntityManager().getSoldiers().isEmpty()) {
            Soldier newest = gameWorld.getEntityManager().getSoldiers().peek();
            if (newest != null) {
                newest.setLevel(currentLv);
            }
        }

        buyButton.setChecked(false);
        updateTextAndState();
    }

    private void upgradeSelectedBuilding() {
        if (selectedOption == null || upgradeButton.isDisabled()) return;

        int currentLv = getCurrentLevelOfSelectedOption();
        if (currentLv >= 3) return;

        int cost = selectedOption.getUpgradeCostForLevel(currentLv);
        int gold = gameWorld.getEconomyManager().getGold();

        if (gold >= cost && cost > 0) {
            gameWorld.getEconomyManager().spendGold(cost);

            incrementLevelOfSelectedOption();
            int newLevel = getCurrentLevelOfSelectedOption();

            String targetOptionName = selectedOption.name();
            for (Soldier s : gameWorld.getEntityManager().getSoldiers()) {
                if (s != null && s.getClass().getSimpleName().toUpperCase().contains(targetOptionName)) {
                    s.setLevel(newLevel);
                } else if (selectedOption == PurchaseOption.HEALER && s != null && "Healer".equals(s.getClass().getSimpleName())) {
                    s.setLevel(newLevel);
                }
            }
        }

        updateTextAndState();
    }

    private enum PurchaseOption {
        WARRIOR("Warrior", EconomyManager.COST_WARRIOR, new int[]{120, 300, 0}),
        ARCHER("Archer", EconomyManager.COST_ARCHER, new int[]{60, 180, 0}),
        HEALER("Monk", EconomyManager.COST_HEALER, new int[]{100, 250, 0}),
        LANCER("Lancer", EconomyManager.COST_LANCER, new int[]{70, 220, 0}),
        MINER("Miner", EconomyManager.COST_MINER, new int[]{80, 200, 0});

        private final String unitName;
        private final int cost;
        private final int[] upgradeCosts;

        PurchaseOption(String unitName, int cost, int[] upgradeCosts) {
            this.unitName = unitName;
            this.cost = cost;
            this.upgradeCosts = upgradeCosts;
        }

        public int getUpgradeCostForLevel(int currentLevel) {
            if (currentLevel >= 1 && currentLevel <= upgradeCosts.length) {
                return upgradeCosts[currentLevel - 1];
            }
            return 0;
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
