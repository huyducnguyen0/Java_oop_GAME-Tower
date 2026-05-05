package com.hust.towerdefence.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.hust.towerdefence.MainGame;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        // Chạy lớp MainGame từ module core
        return new Lwjgl3Application(new MainGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("HUST Tower Defence - Test Logic");
        configuration.useVsync(true);

        // Giới hạn FPS để máy không quá nóng
        configuration.setForegroundFPS(60);

        // Kích thước cửa sổ khớp với WORLD_WIDTH và WORLD_HEIGHT trong BoovGameScreen
        configuration.setWindowedMode(800, 480);

        // Icon (nếu bro có ảnh icon thì dùng, không thì kệ nó)
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        return configuration;
    }
}
