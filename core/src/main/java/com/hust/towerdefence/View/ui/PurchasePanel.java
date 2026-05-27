package com.hust.towerdefence.View.ui;

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
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.BuildingZone;
import com.hust.towerdefence.Model.Managers.EconomyManager;

public class PurchasePanel {
    private static final float PANEL_WIDTH = 210f;
    private static final float PANEL_HEIGHT = 86f;
    private static final float PANEL_Y_OFFSET = 18f;
    private static final float SCREEN_MARGIN = 10f;

    private final GameWorld gameWorld;
    private final Table panel;
    private final Label titleLabel;
    private final Label costLabel;
    private final TextButton buyButton;

    private PurchaseOption selectedOption;
    private BuildingZone selectedZone;

    public PurchasePanel(Stage stage, GameWorld gameWorld, UiAssets assets) {
        this.gameWorld = gameWorld;
        this.panel = new Table();
        this.titleLabel = new Label("", assets.getDefaultLabelStyle());
        this.costLabel = new Label("", assets.getDefaultLabelStyle());
        this.buyButton = new TextButton("Buy", assets.getPauseButtonStyle());

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
        panel.defaults().padLeft(8).padRight(8).center();
        panel.setSize(PANEL_WIDTH, PANEL_HEIGHT);

        panel.add(titleLabel).left().expandX().fillX().height(28).colspan(2);
        panel.row();
        panel.add(costLabel).left().expandX().fillX().height(24);
        panel.add(buyButton).width(72).height(30).padRight(10);
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
        titleLabel.setText(selectedOption.unitName);
        costLabel.setText("Cost: " + selectedOption.cost);
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
