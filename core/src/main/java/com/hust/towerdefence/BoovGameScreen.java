package com.hust.towerdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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
    }

    @Override
    public void render(float delta) {

        // ===== UPDATE =====
        stateTime += delta;
        position.x += velocity.x * delta;

        // Va chạm biên
        if (position.x + 64 > WORLD_WIDTH || position.x < 0) {
            velocity.x *= -1;
        }

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
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
