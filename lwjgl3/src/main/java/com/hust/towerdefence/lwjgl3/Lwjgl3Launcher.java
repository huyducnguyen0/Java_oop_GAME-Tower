package com.hust.towerdefence.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.hust.towerdefence.MainGame;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        try {
            createApplication();
        } catch (Exception e) {
            e.printStackTrace(); // Ép in lỗi ra console
        }
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new MainGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("HUST Tower Defence - Pro AI Test");
        configuration.useVsync(true);
        configuration.setForegroundFPS(60);

        // Thiết lập kích thước cửa sổ HD cho sướng mắt
        configuration.setWindowedMode(1600, 900);

        // Tự động nhận diện icons nếu có trong assets
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        return configuration;
    }
}
