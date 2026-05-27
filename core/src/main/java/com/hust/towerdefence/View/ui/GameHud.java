package com.hust.towerdefence.View.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.BuildingZone;

/**
 * Screen-space HUD layer for gameplay.
 * It reads game state for presentation and does not own gameplay logic.
 */
public class GameHud {
    private static final float HUD_WIDTH_RATIO = 0.32f;
    private static final float HUD_MIN_WIDTH = 360f;
    private static final float HUD_MAX_WIDTH = 500f;
    private static final float HUD_HEIGHT = 52f;
    private static final float HUD_TOP_MARGIN = 10f;

    private final GameWorld gameWorld;
    private final UiAssets assets;
    private final Stage stage;
    private final Table statusBar;
    private final Table resultOverlay;

    private final Label goldLabel;
    private final Label selectedBuildingLabel;
    private final Label resultLabel;
    private final TextButton pauseButton;
    private final PurchasePanel purchasePanel;
    private final TowerInfoPanel towerInfoPanel;
    private String selectedBuildingName;

    public GameHud(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.assets = new UiAssets();
        this.stage = new Stage(new ScreenViewport());

        goldLabel = new Label("", assets.getDefaultLabelStyle());
        selectedBuildingLabel = new Label("", assets.getDefaultLabelStyle());
        resultLabel = new Label("", assets.getDefaultLabelStyle());
        pauseButton = new TextButton("", assets.getPauseButtonStyle());
        purchasePanel = new PurchasePanel(stage, gameWorld, assets);
        towerInfoPanel = new TowerInfoPanel(stage, gameWorld, assets);
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameWorld.setPaused(!gameWorld.isPaused());
            }
        });

        statusBar = new Table();
        resultOverlay = new Table();
        buildLayout();
        layoutHud();
        updateLabels();
    }

    private void buildLayout() {
        statusBar.setBackground(assets.getHudBarDrawable());
        statusBar.defaults().padLeft(8).padRight(8).center();

        statusBar.add(goldLabel).left().minWidth(112);
        statusBar.add(selectedBuildingLabel).left().expandX().fillX().minWidth(92);
        statusBar.add(pauseButton).width(72).height(30).padRight(10);

        stage.addActor(statusBar);

        resultOverlay.setBackground(assets.getPanelDrawable());
        resultOverlay.add(resultLabel).center().pad(18);
        resultOverlay.setVisible(false);
        stage.addActor(resultOverlay);
    }

    public void act(float delta) {
        updateLabels();
        stage.act(delta);
    }

    public void act(float delta, OrthographicCamera worldCamera) {
        purchasePanel.update(worldCamera, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        towerInfoPanel.update(worldCamera, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        act(delta);
    }

    public void draw() {
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        layoutHud();
    }

    public Stage getStage() {
        return stage;
    }

    public void setSelectedBuilding(String selectedBuildingName) {
        this.selectedBuildingName = selectedBuildingName;
    }

    public void setSelectedBuilding(BuildingZone buildingZone) {
        if (buildingZone == null) {
            selectedBuildingName = null;
            purchasePanel.hide();
            towerInfoPanel.hide();
            return;
        }

        selectedBuildingName = buildingZone.getDisplayName();
        if (buildingZone.isPurchaseBuilding()) {
            towerInfoPanel.hide();
            purchasePanel.show(buildingZone);
        } else if (buildingZone.isDefenseTower()) {
            purchasePanel.hide();
            towerInfoPanel.show(buildingZone);
        } else {
            purchasePanel.hide();
            towerInfoPanel.hide();
        }
    }

    public void dispose() {
        stage.dispose();
        assets.dispose();
    }

    private void updateLabels() {
        goldLabel.setText("Gold: " + gameWorld.getEconomyManager().getGold());
        selectedBuildingLabel.setText(selectedBuildingName == null ? "" : selectedBuildingName);
        pauseButton.setText(gameWorld.isPaused() ? "Play" : "Pause");
        resultOverlay.setVisible(gameWorld.isGameOver() || gameWorld.isVictory());
        if (gameWorld.isGameOver()) {
            resultLabel.setText("GAME OVER");
        } else if (gameWorld.isVictory()) {
            resultLabel.setText("VICTORY");
        }
    }

    private void layoutHud() {
        float screenWidth = stage.getViewport().getWorldWidth();
        float screenHeight = stage.getViewport().getWorldHeight();
        float availableWidth = Math.max(0f, screenWidth - 24f);
        float hudWidth = Math.min(clamp(screenWidth * HUD_WIDTH_RATIO, HUD_MIN_WIDTH, HUD_MAX_WIDTH), availableWidth);
        float x = (screenWidth - hudWidth) / 2f;
        float y = screenHeight - HUD_HEIGHT - HUD_TOP_MARGIN;

        statusBar.setSize(hudWidth, HUD_HEIGHT);
        statusBar.setPosition(x, y);

        resultOverlay.setSize(300f, 104f);
        resultOverlay.setPosition((screenWidth - 300f) / 2f, (screenHeight - 104f) / 2f);
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
