package com.hust.towerdefence;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.hust.towerdefence.Model.GameWorld;

public class BoovGameScreen implements Screen {

    private final MainGame game;
    private GameWorld gameWorld;

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;

    // Tỉ lệ scale từ world (2000x2000) xuống màn hình (640x480)
    private static final float SCALE_X = 640f / 2000f;
    private static final float SCALE_Y = 480f / 2000f;

    public BoovGameScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        gameWorld = new GameWorld();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);

        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        // 1. Update model
        gameWorld.update(delta);

        // 2. Clear màn hình
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        // 3. Vẽ background grid (bản đồ)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.DARK_GRAY);
        for (int i = 0; i <= 10; i++) {
            float x = i * 64f;
            float y = i * 48f;
            shapeRenderer.line(x, 0, x, 480);
            shapeRenderer.line(0, y, 640, y);
        }
        shapeRenderer.end();

        // 4. Vẽ base (trung tâm)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLUE);
        float baseX = gameWorld.BASE_X * SCALE_X;
        float baseY = gameWorld.BASE_Y * SCALE_Y;
        shapeRenderer.circle(baseX, baseY, 15);

        // 5. Vẽ mine
        shapeRenderer.setColor(Color.YELLOW);
        float mineX = gameWorld.MINE_X * SCALE_X;
        float mineY = gameWorld.MINE_Y * SCALE_Y;
        shapeRenderer.circle(mineX, mineY, 10);

        // 6. Vẽ enemies
        for (Enemy enemy : gameWorld.getEntityManager().getEnemies()) {
            switch (enemy.getType()) {
                case BASIC: shapeRenderer.setColor(Color.RED);    break;
                case FAST:  shapeRenderer.setColor(Color.ORANGE); break;
                case TANK:  shapeRenderer.setColor(Color.PURPLE); break;
                case BOSS:  shapeRenderer.setColor(Color.SCARLET);break;
            }
            float ex = enemy.getX() * SCALE_X;
            float ey = enemy.getY() * SCALE_Y;
            shapeRenderer.circle(ex, ey, 6);
        }

        // 7. Vẽ units
        shapeRenderer.setColor(Color.GREEN);
        for (Unit unit : gameWorld.getEntityManager().getUnits()) {
            float ux = unit.getX() * SCALE_X;
            float uy = unit.getY() * SCALE_Y;
            shapeRenderer.rect(ux - 5, uy - 5, 10, 10);
        }
        shapeRenderer.end();

        // 8. Vẽ HUD (text)
        game.getBatch().begin();
        // TODO: thêm BitmapFont để hiển thị wave, gold, HP
        game.getBatch().end();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override public void pause()  { gameWorld.pause(); }
    @Override public void resume() { gameWorld.resume(); }
    @Override public void hide()   { dispose(); }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        if (gameWorld != null) gameWorld.dispose();
    }
}
