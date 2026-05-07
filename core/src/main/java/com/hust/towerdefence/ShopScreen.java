package com.hust.towerdefence;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ShopScreen implements Screen {
    private Game game;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private Viewport viewport;

    public ShopScreen(Game game) {
        this.game = game;

        // 1. Nạp file map từ thư mục assets/map/shop.tmx
        map = new TmxMapLoader().load("map/shop.tmx");

        // 2. Tạo công cụ để vẽ map đó ra màn hình
        renderer = new OrthogonalTiledMapRenderer(map);

        // 3. Khởi tạo Camera (Góc nhìn) với độ phân giải 1600x900
        camera = new OrthographicCamera();
        viewport = new FitViewport(1600, 900, camera);

        // Đặt camera vào chính giữa màn hình
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
    }

    @Override
    public void render(float delta) {
        // Xóa màn hình cũ (Màu đen)
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Cập nhật camera và vẽ map lên
        camera.update();
        renderer.setView(camera);
        renderer.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        // Dọn rác khi tắt màn hình
        if (map != null) map.dispose();
        if (renderer != null) renderer.dispose();
    }

    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
}


