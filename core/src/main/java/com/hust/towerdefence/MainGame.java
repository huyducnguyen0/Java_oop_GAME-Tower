package com.hust.towerdefence;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hust.towerdefence.View.screens.DemoMovementScreen;

public class MainGame extends Game {
    public static final float WORLD_HEIGHT = 9f;// đây là chiều cao của thế giới ảo, có thể điều chỉnh tùy theo nhu cầu của bạn
    public static final float WORLD_WIDTH = 16f;// đây là chiều rộng của thế giới
    public static final float UNIT_SCALE = 1f / 16f; // đây là để scale lên
    private OrthographicCamera camera; // camera 2D để hiển thị thế giới ảo, giúp quản lý góc nhìn và chuyển động của camera trong game
    private Viewport viewport;
    private GLProfiler glProfiler;
    private FPSLogger fpsLogger;
    private Batch batch;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        fpsLogger = new FPSLogger();

        setScreen(new DemoMovementScreen());

    }

    @Override
    public void dispose() {
        batch.dispose();
        if (getScreen() != null) getScreen().dispose();
    }

    public Batch getBatch() { return batch; }
}
