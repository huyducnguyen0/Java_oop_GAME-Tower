package com.hust.towerdefence;

import com.badlogic.gdx.Game;

// Đây là điểm vào chính của ứng dụng
// Kế thừa Game cho phép quản lý nhiều Screen (ví dụ: Menu, Play, Options...)
public class MainGame extends Game {

    @Override
    public void create() {
        // Chuyển ngay sang màn hình chơi
        //setScreen(new BoovGameScreen());
        setScreen(new MainMenuScreen(this));

    }
    @Override
    public void render() {
        super.render(); // Rất quan trọng để chuyển đổi giữa các Screen
    }
}
