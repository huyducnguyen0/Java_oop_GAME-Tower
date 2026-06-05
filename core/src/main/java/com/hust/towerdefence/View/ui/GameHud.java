package com.hust.towerdefence.View.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.hust.towerdefence.MainGame;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.BuildingZone;
import com.hust.towerdefence.View.screens.MainMenuScreen;

/**
 * Screen-space HUD layer for gameplay.
 * It reads game state for presentation and does not own gameplay logic.
 */
public class GameHud {
    // Tăng chiều rộng HUD lên một chút để đủ chỗ nhét 2 nút cạnh nhau
    private static final float HUD_WIDTH_RATIO = 0.38f;
    private static final float HUD_MIN_WIDTH = 450f;
    private static final float HUD_MAX_WIDTH = 580f;
    private static final float HUD_HEIGHT = 50f;
    private static final float HUD_TOP_MARGIN = 12f;
    private static final float UNIT_COUNT_PANEL_WIDTH = 150f;
    private static final float UNIT_COUNT_PANEL_HEIGHT = 44f;
    private static final float UNIT_COUNT_PANEL_MARGIN = 18f;

    private final MainGame game; // Thêm biến game để có quyền chuyển màn hình
    private final GameWorld gameWorld;
    private final UiAssets assets;
    private final Stage stage;
    private final Table statusBar;
    private final Table enemyCountPanel;
    private final Table allyCountPanel;
    private final Table resultOverlay;

    private final Label goldLabel;
    private final Label selectedBuildingLabel;
    private final Label enemyCountLabel;
    private final Label allyCountLabel;
    private final Label resultLabel;
    private final TextButton pauseButton;
    private final TextButton quitButton; // Khai báo Nút Quit
    private final PurchasePanel purchasePanel;
    private final TowerInfoPanel towerInfoPanel;
    private String selectedBuildingName;

    // Cập nhật Constructor: Nhận thêm MainGame game
    public GameHud(GameWorld gameWorld, MainGame game) {
        this.gameWorld = gameWorld;
        this.game = game;
        this.assets = new UiAssets();
        this.stage = new Stage(new ScreenViewport());

        goldLabel = new Label("", assets.getDefaultLabelStyle());
        selectedBuildingLabel = new Label("", assets.getDefaultLabelStyle());
        enemyCountLabel = new Label("", assets.getDefaultLabelStyle());
        allyCountLabel = new Label("", assets.getDefaultLabelStyle());
        resultLabel = new Label("", assets.getTitleLabelStyle());

        pauseButton = new TextButton("", assets.getPauseButtonStyle());
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameWorld.setPaused(!gameWorld.isPaused());
            }
        });

        // ==========================================
        // KHỞI TẠO NÚT MENU (QUIT)
        // ==========================================
        quitButton = new TextButton("Menu", assets.getPauseButtonStyle());
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Xử lý chuyển đổi âm thanh chuẩn chỉ
                if (game.audioManager != null) {
                    game.audioManager.playScreenClick();
                    game.audioManager.stopGameplayMusic();
                    game.audioManager.playMenuMusic();
                }
                // Đá văng người chơi về lại Menu
                game.setScreen(new MainMenuScreen(game));
            }
        });

        purchasePanel = new PurchasePanel(stage, gameWorld, assets);
        towerInfoPanel = new TowerInfoPanel(stage, gameWorld, assets);

        statusBar = new Table();
        enemyCountPanel = new Table();
        allyCountPanel = new Table();
        resultOverlay = new Table();
        buildLayout();
        layoutHud();
        updateLabels();
    }

    private void buildLayout() {
        statusBar.setBackground(assets.getHudBarDrawable());
        statusBar.defaults().center();
        statusBar.padLeft(18).padRight(18).padTop(6).padBottom(8);

        goldLabel.setAlignment(Align.left);
        selectedBuildingLabel.setAlignment(Align.center);
        pauseButton.getLabel().setAlignment(Align.center);
        quitButton.getLabel().setAlignment(Align.center);

        // Sắp xếp bố cục nhét nút Menu vào cạnh nút Play
        statusBar.add(goldLabel).left().minWidth(118).padRight(12);
        statusBar.add(selectedBuildingLabel).center().expandX().fillX().minWidth(110);
        statusBar.add(pauseButton).width(75).height(34).padLeft(12);
        statusBar.add(quitButton).width(75).height(34).padLeft(8);

        stage.addActor(statusBar);

        enemyCountPanel.setBackground(assets.getPanelDrawable());
        enemyCountPanel.pad(8, 14, 8, 14);
        enemyCountLabel.setAlignment(Align.center);
        enemyCountPanel.add(enemyCountLabel).center().expand().fill();
        stage.addActor(enemyCountPanel);

        allyCountPanel.setBackground(assets.getPanelDrawable());
        allyCountPanel.pad(8, 14, 8, 14);
        allyCountLabel.setAlignment(Align.center);
        allyCountPanel.add(allyCountLabel).center().expand().fill();
        stage.addActor(allyCountPanel);

        resultLabel.setAlignment(Align.center);
        resultOverlay.setBackground(assets.getPanelDrawable());
        resultOverlay.add(resultLabel).center().pad(24);
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
        enemyCountLabel.setText("Enemy: " + gameWorld.getEntityManager().getAliveEnemyCount());
        allyCountLabel.setText("Ally: " + gameWorld.getEntityManager().getAliveSoldierCount());
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

        enemyCountPanel.setSize(UNIT_COUNT_PANEL_WIDTH, UNIT_COUNT_PANEL_HEIGHT);
        enemyCountPanel.setPosition(UNIT_COUNT_PANEL_MARGIN, screenHeight - UNIT_COUNT_PANEL_HEIGHT - UNIT_COUNT_PANEL_MARGIN);

        allyCountPanel.setSize(UNIT_COUNT_PANEL_WIDTH, UNIT_COUNT_PANEL_HEIGHT);
        allyCountPanel.setPosition(
            screenWidth - UNIT_COUNT_PANEL_WIDTH - UNIT_COUNT_PANEL_MARGIN,
            screenHeight - UNIT_COUNT_PANEL_HEIGHT - UNIT_COUNT_PANEL_MARGIN
        );

        resultOverlay.setSize(300f, 104f);
        resultOverlay.setPosition((screenWidth - 300f) / 2f, (screenHeight - 104f) / 2f);
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
