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
