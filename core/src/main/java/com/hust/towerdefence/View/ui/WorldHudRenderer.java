package com.hust.towerdefence.View.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.SnapshotArray;
import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Tower.BaseTower;
import com.hust.towerdefence.Model.Entities.Tower.DefenseTower;
import com.hust.towerdefence.Model.Entities.Tower.MainTower;

/**
 * Draws world-space UI that should stay attached to map objects.
 */
public class WorldHudRenderer {
    private static final float TOWER_HEALTH_BAR_WIDTH = 190f;
    private static final float TOWER_HEALTH_BAR_HEIGHT = 12f;
    private static final float TOWER_HEALTH_BAR_Y_OFFSET = 10f;
    private static final float CASTLE_ASPECT_RATIO = 0.8f;
    private static final float DEFENSE_TOWER_HEALTH_BAR_WIDTH = 86f;
    private static final float PLAYER_DEFENSE_TOWER_ASPECT_RATIO = 2.0f;
    private static final float ENEMY_DEFENSE_TOWER_ASPECT_RATIO = 2.0f;
    private static final float UNIT_HEALTH_BAR_WIDTH = 42f;
    private static final float UNIT_HEALTH_BAR_HEIGHT = 6f;
    private static final float UNIT_HEALTH_BAR_Y_OFFSET = 58f;

    private static final Color BACKGROUND = new Color(0f, 0f, 0f, 0.75f);
    private static final Color PLAYER_HEALTH = new Color(0.18f, 0.85f, 0.25f, 1f);
    private static final Color ENEMY_HEALTH = new Color(0.9f, 0.12f, 0.12f, 1f);

    public void drawTowerHealthBars(ShapeRenderer shapeRenderer, MainTower playerTower, MainTower enemyTower) {
        drawTowerHealthBar(shapeRenderer, playerTower);
        drawTowerHealthBar(shapeRenderer, enemyTower);
    }

    public void drawDefenseTowerHealthBars(ShapeRenderer shapeRenderer, SnapshotArray<BaseTower> towers) {
        for (BaseTower tower : towers) {
            if (tower instanceof DefenseTower && !tower.isDead() && !tower.isRemoved()) {
                drawDefenseTowerHealthBar(shapeRenderer, (DefenseTower) tower);
            }
        }
    }

    public void drawUnitHealthBars(ShapeRenderer shapeRenderer, SnapshotArray<BaseEntity> entities) {
        for (BaseEntity entity : entities) {
            if (!(entity instanceof CombatEntity) || entity instanceof BaseTower || entity.isRemoved()) continue;
            CombatEntity combatEntity = (CombatEntity) entity;
            if (combatEntity.isDead() || combatEntity.getMaxHealth() <= 0f) continue;
            if (combatEntity.getHealth() >= combatEntity.getMaxHealth()) continue;
            drawUnitHealthBar(shapeRenderer, combatEntity);
        }
    }

    private void drawTowerHealthBar(ShapeRenderer shapeRenderer, MainTower tower) {
        if (tower == null || tower.getMaxHealth() <= 0f) return;

        float ratio = Math.max(0f, Math.min(1f, tower.getHealth() / tower.getMaxHealth()));
        float x = tower.getX() - TOWER_HEALTH_BAR_WIDTH / 2f;
        float visualTop = tower.getY() - tower.getHeight() / 2f + tower.getWidth() * CASTLE_ASPECT_RATIO;
        float y = visualTop + TOWER_HEALTH_BAR_Y_OFFSET;

        shapeRenderer.setColor(BACKGROUND);
        shapeRenderer.rect(x, y, TOWER_HEALTH_BAR_WIDTH, TOWER_HEALTH_BAR_HEIGHT);

        shapeRenderer.setColor(tower.getTeam() == BaseEntity.Team.ENEMY ? ENEMY_HEALTH : PLAYER_HEALTH);
        shapeRenderer.rect(x + 2f, y + 2f, (TOWER_HEALTH_BAR_WIDTH - 4f) * ratio, TOWER_HEALTH_BAR_HEIGHT - 4f);
    }

    private void drawDefenseTowerHealthBar(ShapeRenderer shapeRenderer, DefenseTower tower) {
        if (tower.getMaxHealth() <= 0f) return;

        float ratio = Math.max(0f, Math.min(1f, tower.getHealth() / tower.getMaxHealth()));
        float x = tower.getX() - DEFENSE_TOWER_HEALTH_BAR_WIDTH / 2f;
        float aspectRatio = tower.getTeam() == BaseEntity.Team.ENEMY
            ? ENEMY_DEFENSE_TOWER_ASPECT_RATIO
            : PLAYER_DEFENSE_TOWER_ASPECT_RATIO;
        float visualTop = tower.getY() - tower.getHeight() / 2f + tower.getWidth() * aspectRatio;
        float y = visualTop + TOWER_HEALTH_BAR_Y_OFFSET;

        shapeRenderer.setColor(BACKGROUND);
        shapeRenderer.rect(x, y, DEFENSE_TOWER_HEALTH_BAR_WIDTH, TOWER_HEALTH_BAR_HEIGHT);

        shapeRenderer.setColor(tower.getTeam() == BaseEntity.Team.ENEMY ? ENEMY_HEALTH : PLAYER_HEALTH);
        shapeRenderer.rect(x + 2f, y + 2f, (DEFENSE_TOWER_HEALTH_BAR_WIDTH - 4f) * ratio, TOWER_HEALTH_BAR_HEIGHT - 4f);
    }

    private void drawUnitHealthBar(ShapeRenderer shapeRenderer, CombatEntity entity) {
        float ratio = Math.max(0f, Math.min(1f, entity.getHealth() / entity.getMaxHealth()));
        float x = entity.getX() - UNIT_HEALTH_BAR_WIDTH / 2f;
        float y = entity.getY() + UNIT_HEALTH_BAR_Y_OFFSET;

        shapeRenderer.setColor(BACKGROUND);
        shapeRenderer.rect(x, y, UNIT_HEALTH_BAR_WIDTH, UNIT_HEALTH_BAR_HEIGHT);

        shapeRenderer.setColor(entity.getTeam() == BaseEntity.Team.ENEMY ? ENEMY_HEALTH : PLAYER_HEALTH);
        shapeRenderer.rect(x + 1f, y + 1f, (UNIT_HEALTH_BAR_WIDTH - 2f) * ratio, UNIT_HEALTH_BAR_HEIGHT - 2f);
    }
}
