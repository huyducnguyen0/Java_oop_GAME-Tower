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
import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Tower.DefenseTower;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.BuildingZone;

public class TowerInfoPanel {
    private static final float PANEL_WIDTH = 230f;
    private static final float PANEL_HEIGHT = 130f;
    private static final float PANEL_Y_OFFSET = 18f;
    private static final float SCREEN_MARGIN = 10f;

    private final GameWorld gameWorld;
    private final Table panel;
    private final Label titleLabel;
    private final Label hpLabel;
    private final Label statsLabel;
    private final Label costLabel;
    private final TextButton upgradeButton;

    private BuildingZone selectedZone;

    public TowerInfoPanel(Stage stage, GameWorld gameWorld, UiAssets assets) {
        this.gameWorld = gameWorld;
        this.panel = new Table();
        this.titleLabel = new Label("", assets.getDefaultLabelStyle());
        this.hpLabel = new Label("", assets.getDefaultLabelStyle());
        this.statsLabel = new Label("", assets.getDefaultLabelStyle());
        this.costLabel = new Label("", assets.getDefaultLabelStyle());
        this.upgradeButton = new TextButton("Upgrade", assets.getPauseButtonStyle());

        buildLayout(assets);
        stage.addActor(panel);
        panel.setVisible(false);

        upgradeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedZone != null && !upgradeButton.isDisabled()) {
                    gameWorld.upgradeDefenseTower(selectedZone.getName());
                    updateTextAndState();
                }
            }
        });
    }

    private void buildLayout(UiAssets assets) {
        panel.setBackground(assets.getPanelDrawable());
        panel.defaults().padLeft(8).padRight(8).center();
        panel.setSize(PANEL_WIDTH, PANEL_HEIGHT);

        panel.add(titleLabel).left().expandX().fillX().height(26).colspan(2);
        panel.row();
        panel.add(hpLabel).left().expandX().fillX().height(22).colspan(2);
        panel.row();
        panel.add(statsLabel).left().expandX().fillX().height(24).colspan(2);
        panel.row();
        panel.add(costLabel).left().expandX().fillX().height(28);
        panel.add(upgradeButton).width(88).height(30).padRight(10);
    }

    public void show(BuildingZone zone) {
        selectedZone = zone;
        updateTextAndState();
        panel.setVisible(selectedZone != null && gameWorld.getDefenseTower(selectedZone.getName()) != null);
    }

    public void hide() {
        selectedZone = null;
        panel.setVisible(false);
    }

    public void update(OrthographicCamera worldCamera, float screenWidth, float screenHeight) {
        if (selectedZone == null || !panel.isVisible()) return;
        updateTextAndState();

        Vector2 center = selectedZone.getCenter();
        Vector3 projected = worldCamera.project(new Vector3(center.x, center.y, 0f));
        float x = MathUtils.clamp(projected.x - PANEL_WIDTH / 2f, SCREEN_MARGIN, screenWidth - PANEL_WIDTH - SCREEN_MARGIN);
        float y = MathUtils.clamp(projected.y + PANEL_Y_OFFSET, SCREEN_MARGIN, screenHeight - PANEL_HEIGHT - SCREEN_MARGIN);
        panel.setPosition(x, y);
    }

    private void updateTextAndState() {
        if (selectedZone == null) return;
        DefenseTower tower = gameWorld.getDefenseTower(selectedZone.getName());
        if (tower == null) {
            hide();
            return;
        }

        boolean playerTower = tower.getTeam() == BaseEntity.Team.SOLDIER;
        titleLabel.setText(selectedZone.getDisplayName() + "  Lv." + tower.getLevel());
        hpLabel.setText("HP: " + (int) tower.getHealth() + " / " + (int) tower.getMaxHealth());
        statsLabel.setText("DMG: " + (int) tower.getAttackDamage() + "   RNG: " + (int) tower.getAttackRange());

        if (!playerTower) {
            costLabel.setText("Enemy tower");
            upgradeButton.setVisible(false);
            return;
        }

        upgradeButton.setVisible(true);
        if (!tower.canUpgrade()) {
            costLabel.setText("Max level");
            upgradeButton.setDisabled(true);
            return;
        }

        int cost = tower.getUpgradeCost();
        boolean canAfford = gameWorld.getEconomyManager().canAfford(cost);
        costLabel.setText("Upgrade: " + cost);
        upgradeButton.setDisabled(!canAfford);
    }
}
