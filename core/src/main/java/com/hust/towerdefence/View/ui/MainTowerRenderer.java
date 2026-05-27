package com.hust.towerdefence.View.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hust.towerdefence.Model.Entities.Tower.MainTower;

/**
 * Renders the visual castle assets attached to main tower gameplay entities.
 */
public class MainTowerRenderer {
    private static final String PLAYER_CASTLE = "Buildings/Castle.png";
    private static final String ENEMY_CASTLE = "EnemyBuildings/Wood_main/Castle.png";

    private final Texture playerCastle;
    private final Texture enemyCastle;
    private final SpriteBatch batch;

    public MainTowerRenderer() {
        playerCastle = new Texture(PLAYER_CASTLE);
        enemyCastle = new Texture(ENEMY_CASTLE);
        playerCastle.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        enemyCastle.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        batch = new SpriteBatch();
    }

    public void render(OrthographicCamera camera, MainTower playerTower, MainTower enemyTower) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawTower(playerTower, playerCastle);
        drawTower(enemyTower, enemyCastle);
        batch.end();
    }

    private void drawTower(MainTower tower, Texture texture) {
        if (tower == null || texture == null) return;

        float width = tower.getWidth();
        float height = width * texture.getHeight() / texture.getWidth();
        float x = tower.getX() - width / 2f;
        float y = tower.getY() - tower.getHeight() / 2f;

        batch.draw(texture, x, y, width, height);
    }

    public void dispose() {
        playerCastle.dispose();
        enemyCastle.dispose();
        batch.dispose();
    }
}
