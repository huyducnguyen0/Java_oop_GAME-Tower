package com.hust.towerdefense.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hust.towerdefense.MainGame;
import com.hust.towerdefense.input.InputHandler;

public class InstructionsScreen implements Screen {
    private static final float UI_WIDTH = 800f;
    private static final float UI_HEIGHT = 480f;

    private final MainGame game;
    private final BitmapFont font;
    private final Viewport viewport;
    private InputHandler input;

    public InstructionsScreen(MainGame game) {
        this.game = game;
        this.font = new BitmapFont();
        this.viewport = new FitViewport(UI_WIDTH, UI_HEIGHT);
    }

    @Override
    public void show() {
        input = new InputHandler((screenX, screenY, pointer, button) -> {
            game.setScreen(new MenuScreen(game));
            dispose();
        });
        Gdx.input.setInputProcessor(input);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        game.getBatch().setProjectionMatrix(viewport.getCamera().combined);

        game.getBatch().begin();
        font.draw(game.getBatch(), "HUONG DAN", 60, UI_HEIGHT - 60);
        font.draw(game.getBatch(), "- Click START de vao game", 60, UI_HEIGHT - 110);
        font.draw(game.getBatch(), "- Chon DO KHO de test menu state", 60, UI_HEIGHT - 150);
        font.draw(game.getBatch(), "- Click bat ky de quay lai MENU", 60, UI_HEIGHT - 210);
        game.getBatch().end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (Gdx.input.getInputProcessor() == input) {
            Gdx.input.setInputProcessor(null);
        }
        font.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}

