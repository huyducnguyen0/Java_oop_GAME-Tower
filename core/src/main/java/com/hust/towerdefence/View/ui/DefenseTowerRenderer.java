package com.hust.towerdefence.View.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.SnapshotArray;
import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Tower.BaseTower;
import com.hust.towerdefence.Model.Entities.Tower.DefenseTower;

public class DefenseTowerRenderer {
    private static final String PLAYER_TOWER = "Buildings/Tower.png";
    private static final String ENEMY_TOWER = "EnemyBuildings/Wood_Tower/Tower.png";
    private static final String PLAYER_ARCHER = "Units/Archer/Archer_Idle.png";
    private static final String ENEMY_ARCHER = "EnemyUnits/Archer/Archer_Idle.png";
    private static final String DESTROY_EFFECT = "Destroyed_Effect/Explosion_02.png";
    private static final float UNIT_VISUAL_HEIGHT = 64f;
    private static final float TOWER_ARCHER_PLATFORM_RATIO = 0.58f;
    private static final int EXPLOSION_FRAME_SIZE = 192;
    private static final int EXPLOSION_FRAME_COUNT = 10;

    private final Texture playerTower;
    private final Texture enemyTower;
    private final Texture playerArcher;
    private final Texture enemyArcher;
    private final Texture destroyEffect;
    private final TextureRegion playerTowerRegion;
    private final TextureRegion enemyTowerRegion;
    private final TextureRegion playerArcherRegion;
    private final TextureRegion enemyArcherRegion;
    private final TextureRegion[] destroyFrames;
    private final SpriteBatch batch;

    public DefenseTowerRenderer() {
        playerTower = new Texture(PLAYER_TOWER);
        enemyTower = new Texture(ENEMY_TOWER);
        playerArcher = new Texture(PLAYER_ARCHER);
        enemyArcher = new Texture(ENEMY_ARCHER);
        destroyEffect = new Texture(DESTROY_EFFECT);
        playerTower.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        enemyTower.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        playerArcher.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        enemyArcher.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        destroyEffect.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        playerTowerRegion = new TextureRegion(playerTower);
        enemyTowerRegion = new TextureRegion(enemyTower);
        playerArcherRegion = new TextureRegion(playerArcher, 58, 48, 70, 88);
        enemyArcherRegion = new TextureRegion(enemyArcher, 58, 48, 70, 88);
        destroyFrames = new TextureRegion[EXPLOSION_FRAME_COUNT];
        for (int i = 0; i < EXPLOSION_FRAME_COUNT; i++) {
            destroyFrames[i] = new TextureRegion(destroyEffect, i * EXPLOSION_FRAME_SIZE, 0, EXPLOSION_FRAME_SIZE, EXPLOSION_FRAME_SIZE);
        }
        batch = new SpriteBatch();
    }

    public void render(OrthographicCamera camera, SnapshotArray<BaseTower> towers) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (BaseTower tower : towers) {
            if (!(tower instanceof DefenseTower) || tower.isRemoved()) continue;
            DefenseTower defenseTower = (DefenseTower) tower;
            if (defenseTower.isDestroying()) {
                drawDestroyEffect(defenseTower);
            } else if (!defenseTower.isDead()) {
                drawTower(defenseTower);
                drawTowerArcher(defenseTower);
            }
        }
        batch.end();
    }

    private void drawTower(DefenseTower tower) {
        TextureRegion texture = tower.getTeam() == BaseEntity.Team.ENEMY ? enemyTowerRegion : playerTowerRegion;
        float width = tower.getWidth();
        float height = width * texture.getRegionHeight() / texture.getRegionWidth();
        float x = tower.getX() - width / 2f;
        float y = tower.getY() - tower.getHeight() / 2f;
        batch.draw(texture, x, y, width, height);
    }

    private void drawTowerArcher(DefenseTower tower) {
        TextureRegion archer = tower.getTeam() == BaseEntity.Team.ENEMY ? enemyArcherRegion : playerArcherRegion;
        float towerWidth = tower.getWidth();
        float towerHeight = towerWidth * 2f;
        float archerHeight = UNIT_VISUAL_HEIGHT;
        float archerWidth = archerHeight * archer.getRegionWidth() / archer.getRegionHeight();
        float x = tower.getX() - archerWidth / 2f;
        float y = tower.getY() - tower.getHeight() / 2f + towerHeight * TOWER_ARCHER_PLATFORM_RATIO;
        batch.draw(archer, x, y, archerWidth, archerHeight);
    }

    private void drawDestroyEffect(DefenseTower tower) {
        int frameIndex = Math.min(EXPLOSION_FRAME_COUNT - 1, (int) (tower.getDestroyProgress() * EXPLOSION_FRAME_COUNT));
        TextureRegion frame = destroyFrames[frameIndex];
        float size = tower.getWidth() * 1.7f;
        float x = tower.getX() - size / 2f;
        float y = tower.getY() - tower.getHeight() / 2f + tower.getWidth() * 0.2f;
        batch.draw(frame, x, y, size, size);
    }

    public void dispose() {
        playerTower.dispose();
        enemyTower.dispose();
        playerArcher.dispose();
        enemyArcher.dispose();
        destroyEffect.dispose();
        batch.dispose();
    }
}
