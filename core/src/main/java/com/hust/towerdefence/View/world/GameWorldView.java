package com.hust.towerdefence.View.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.hust.towerdefence.Model.GameWorld;

public class GameWorldView implements Disposable {

    private final GameWorld gameWorld;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    // TODO: thay bằng TiledMap thực tế
    // private TiledMap tiledMap;
    // private OrthogonalTiledMapRenderer mapRenderer;

    public GameWorldView(GameWorld gameWorld,
                         SpriteBatch batch,
                         ShapeRenderer shapeRenderer) {
        this.gameWorld = gameWorld;
        this.batch = batch;
        this.shapeRenderer = shapeRenderer;

        // TODO: load TiledMap
        // tiledMap = new TmxMapLoader().load("maps/map.tmx");
        // mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
    }

    /**
     * Render theo thứ tự 4 sub-layer:
     * 1. Background
     * 2. Tilemap (ground)
     * 3. Static objects (trees, rocks, buildings)
     * 4. Ambient (fog of war, lighting)
     */
    public void render(float delta) {
        renderBackground();
        renderTilemap();
        renderStaticObjects();
        renderAmbient();
    }

    // --- Sub-layer 1: Background ---
    private void renderBackground() {
        // Placeholder: vẽ nền xanh lá đơn giản bằng ShapeRenderer
        // TODO: thay bằng background texture/parallax
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.5f, 0.2f, 1f); // màu cỏ
        shapeRenderer.rect(0, 0, gameWorld.WORLD_WIDTH, gameWorld.WORLD_HEIGHT);
        shapeRenderer.end();
    }

    // --- Sub-layer 2: Tilemap ---
    private void renderTilemap() {
        // TODO: thay bằng TiledMap render thực tế
        // mapRenderer.setView(camera); // camera truyền từ GameRenderer
        // mapRenderer.render();

        // Placeholder: vẽ grid để dễ debug
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.3f, 0.6f, 0.3f, 0.3f);
        float tileSize = 64f;
        for (float x = 0; x < gameWorld.WORLD_WIDTH; x += tileSize) {
            shapeRenderer.line(x, 0, x, gameWorld.WORLD_HEIGHT);
        }
        for (float y = 0; y < gameWorld.WORLD_HEIGHT; y += tileSize) {
            shapeRenderer.line(0, y, gameWorld.WORLD_WIDTH, y);
        }
        shapeRenderer.end();
    }

    // --- Sub-layer 3: Static Objects ---
    private void renderStaticObjects() {
        // Vẽ vị trí đặc biệt từ GameWorld để dễ nhận biết
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Mỏ vàng — màu vàng
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.circle(gameWorld.MINE_X, gameWorld.MINE_Y, 30f);

        // Căn cứ chính — màu xanh dương
        shapeRenderer.setColor(Color.ROYAL);
        shapeRenderer.circle(gameWorld.BASE_X, gameWorld.BASE_Y, 50f);

        shapeRenderer.end();

        // Label cho các vị trí — dùng batch
        // TODO: thay bằng Sprite thực tế
        batch.begin();
        // font.draw(batch, "MINE", gameWorld.MINE_X, gameWorld.MINE_Y);
        // font.draw(batch, "BASE", gameWorld.BASE_X, gameWorld.BASE_Y);
        batch.end();
    }

    // --- Sub-layer 4: Ambient ---
    private void renderAmbient() {
        // TODO: fog of war, day/night lighting
        // Placeholder: không làm gì
    }

    @Override
    public void dispose() {
        // TODO: tiledMap.dispose(); mapRenderer.dispose();
    }
}
