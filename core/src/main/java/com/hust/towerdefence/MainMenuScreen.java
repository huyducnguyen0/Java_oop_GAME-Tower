package com.hust.towerdefence;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMenuScreen implements Screen {
    private Game game;
    private SpriteBatch batch;
    private Texture backgroundTexture;

    public MainMenuScreen(Game game) {
        this.game = game;
        // Khởi tạo cọ vẽ
        this.batch = new SpriteBatch();
        // Tải ảnh nền từ thư mục assets
        this.backgroundTexture = new Texture(Gdx.files.internal("background.png"));
    }

    @Override
    public void render(float delta) {
        // Tô đen màn hình trước khi vẽ ảnh đè lên
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Bắt đầu vẽ ảnh nền lấp đầy màn hình
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void dispose() {
        // Dọn dẹp bộ nhớ chống giật lag cho game
        if (batch != null) batch.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
