package com.hust.towerdefence;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainGame extends Game {

    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new BoovGameScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (getScreen() != null) getScreen().dispose();
    }

    public SpriteBatch getBatch() { return batch; }
}
