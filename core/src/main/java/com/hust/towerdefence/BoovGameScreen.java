package com.hust.towerdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

// Import Game Systems
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Systems.CombatSystem;
import com.hust.towerdefence.Model.Systems.MovementSystem;
import com.hust.towerdefence.Model.Systems.EconomySystem;
import com.hust.towerdefence.Model.Entities.Unit;
import com.hust.towerdefence.Model.Entities.Enemy;

public class BoovGameScreen implements Screen {

    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 480;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    private Texture boovTexture;
    private Animation<TextureRegion> walkAnimation;
    private Vector2 position;
    private Vector2 velocity;
    private float stateTime;
    private BitmapFont font;

    // === Game Systems ===
    private GameWorld gameWorld;
    private CombatSystem combatSystem;
    private MovementSystem movementSystem;
    private EconomySystem economySystem;

    public BoovGameScreen() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
    }

    @Override
    public void show() {

        // ===== LOAD MAP =====
        map = new TmxMapLoader().load("map1.tmx");

        // ⚠️ QUAN TRỌNG:
        // Nếu tile = 32px thì dùng 1/32f
        // Nếu tile = 16px thì dùng 1/16f
        // Nếu mày làm pixel-perfect thì giữ 1f
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        // ===== CAMERA =====
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();

        // ===== CHARACTER =====
        boovTexture = new Texture(Gdx.files.internal("boov.png"));
        TextureRegion[][] tmp = TextureRegion.split(boovTexture, 64, 64);
        walkAnimation = new Animation<>(0.1f, tmp[1]);

        position = new Vector2(100, 100);
        velocity = new Vector2(150, 0);
        stateTime = 0f;

        // ===== INITIALIZE FONT =====
        font = new BitmapFont();
        font.setColor(1, 1, 1, 1);  // Màu trắng

        // ===== INITIALIZE GAME SYSTEMS =====
        gameWorld = new GameWorld();
        combatSystem = new CombatSystem();
        movementSystem = new MovementSystem();
        economySystem = new EconomySystem();

        // ===== THÊM TEST ENTITIES =====
        initializeTestEntities();
    }

    /**
     * Thêm một số units để test hệ thống chiến đấu
     * Enemies sẽ được spawn tự động bởi wave system
     */
    private void initializeTestEntities() {
        // Thêm units (quân binh) ở trung tâm thành
        gameWorld.getEntityManager().addUnit(
            new Unit(Unit.UnitType.INFANTRY, 1000, 1000, 100, 20, 2, 2)
        );
        gameWorld.getEntityManager().addUnit(
            new Unit(Unit.UnitType.INFANTRY, 1050, 1050, 100, 20, 2, 2)
        );
        gameWorld.getEntityManager().addUnit(
            new Unit(Unit.UnitType.ARCHER, 1100, 900, 80, 15, 1.5f, 5)
        );
        gameWorld.getEntityManager().addUnit(
            new Unit(Unit.UnitType.ARCHER, 900, 1100, 80, 15, 1.5f, 5)
        );

        // Thêm miners để kiếm vàng
        gameWorld.getEntityManager().addUnit(
            new Unit(Unit.UnitType.MINER, 1000, 1100, 80, 5, 2, 1)
        );
        gameWorld.getEntityManager().addUnit(
            new Unit(Unit.UnitType.MINER, 1100, 1100, 80, 5, 2, 1)
        );

        // Enemies sẽ được spawn bởi wave system tự động
    }

    @Override
    public void render(float delta) {

        // ===== UPDATE GAME LOGIC =====
        stateTime += delta;
        position.x += velocity.x * delta;

        // Va chạm biên
        if (position.x + 64 > WORLD_WIDTH || position.x < 0) {
            velocity.x *= -1;
        }

        // ===== UPDATE GAME WORLD =====
        gameWorld.update(delta);
        gameWorld.getEntityManager().update(delta);

        // ===== UPDATE SYSTEMS =====
        movementSystem.update(gameWorld, delta);
        combatSystem.update(gameWorld, delta);
        economySystem.update(gameWorld, delta);

        // ===== CLEAR =====
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // ===== DRAW MAP (KHÔNG đổi camera) =====
        mapRenderer.setView(camera);
        mapRenderer.render();

        // ===== DRAW PLAYER =====
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);

        if (velocity.x < 0 && !currentFrame.isFlipX()) currentFrame.flip(true, false);
        if (velocity.x > 0 && currentFrame.isFlipX()) currentFrame.flip(true, false);

        batch.draw(currentFrame, position.x, position.y);

        batch.end();

        // ===== DRAW UI (Score, Wave, Health, Gold) =====
        batch.begin();
        drawGameUI();
        batch.end();
    }

    /**
     * Vẽ giao diện game (UI)
     */
    private void drawGameUI() {
        int baseHP = gameWorld.getEntityManager().getMainBuildingHp();
        int gold = gameWorld.getEntityManager().getGold();
        int wave = gameWorld.getWave();
        int score = gameWorld.getScore();
        int activeEnemies = gameWorld.getEntityManager().getEnemies().size();

        // Vị trí UI (top-left)
        float uiX = 20;
        float uiY = WORLD_HEIGHT - 20;

        font.draw(batch, "Wave: " + wave, uiX, uiY);
        font.draw(batch, "Score: " + score, uiX, uiY - 30);
        font.draw(batch, "Gold: " + gold, uiX, uiY - 60);
        font.draw(batch, "Base HP: " + baseHP, uiX, uiY - 90);
        font.draw(batch, "Enemies: " + activeEnemies, uiX, uiY - 120);

        // Hiển thị trạng thái game
        if (gameWorld.isGameOver()) {
            font.draw(batch, "GAME OVER!", uiX + 100, uiY + 100);
        } else if (gameWorld.isPaused()) {
            font.draw(batch, "PAUSED", uiX + 100, uiY + 100);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        boovTexture.dispose();
        batch.dispose();
        font.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
