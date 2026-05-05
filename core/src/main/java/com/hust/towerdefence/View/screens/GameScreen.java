package com.hust.towerdefence.View.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.EntityManager;
import com.hust.towerdefence.View.hud.GameHUD;
import com.hust.towerdefence.View.renderer.GameRenderer;

public class GameScreen implements Screen {

    // --- Dependencies từ Model ---
    private final GameWorld gameWorld;
    private final EntityManager entityManager;

    // --- View components ---
    private final GameRenderer renderer;
    private final GameHUD hud;

    // --- State ---
    private boolean paused = false;

    public GameScreen(GameWorld gameWorld, EntityManager entityManager) {
        this.gameWorld = gameWorld;
        this.entityManager = entityManager;

        // Khởi tạo renderer trước — HUD cần biết viewport size
        this.renderer = new GameRenderer(gameWorld, entityManager);
        this.hud = new GameHUD(gameWorld, entityManager);
    }

    @Override
    public void render(float delta) {
        // 1. Xóa màn hình
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!paused) {
            // 2. Render world + entities (world space, chịu ảnh hưởng camera)
            renderer.render(delta);

            // 3. Render HUD (screen space, KHÔNG chịu ảnh hưởng camera)
            hud.render(delta);
        }
    }

    @Override
    public void resize(int width, int height) {
        // Gọi khi cửa sổ thay đổi kích thước — bắt buộc phải có
        renderer.resize(width, height);
        hud.resize(width, height);
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void show() {
        // Gọi khi Screen này được activate
        // TODO: play background music ở đây
        hud.show();
    }

    @Override
    public void hide() {
        // Gọi khi chuyển sang Screen khác
        // TODO: pause music ở đây
    }

    @Override
    public void dispose() {
        // QUAN TRỌNG: giải phóng memory, LibGDX không tự làm
        renderer.dispose();
        hud.dispose();
    }
}
