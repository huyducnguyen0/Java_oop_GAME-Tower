package com.hust.towerdefense;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hust.towerdefense.screens.MenuScreen;

// Đây là điểm vào chính của ứng dụng
// Kế thừa Game cho phép quản lý nhiều Screen (ví dụ: Menu, Play, Options...)
public class MainGame extends Game {

    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new MenuScreen(this));
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (batch != null) batch.dispose();
    }
}

