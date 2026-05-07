package com.hust.towerdefence;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hust.towerdefence.View.screens.DemoMovementScreen;

import java.util.HashMap;
import java.util.Map;

public class MainGame extends Game {
    public static final float WORLD_HEIGHT = 9f;// đây là chiều cao của thế giới ảo, có thể điều chỉnh tùy theo nhu cầu của bạn
    public static final float WORLD_WIDTH = 16f;// đây là chiều rộng của thế giới
    public static final float UNIT_SCALE = 1f / 16f; // đây là để scale lên
    private OrthographicCamera camera; // camera 2D để hiển thị thế giới ảo, giúp quản lý góc nhìn và chuyển động của camera trong game

    private FPSLogger fpsLogger;
    private Batch batch;
    private InputMultiplexer inputMultiplexer;

    private final Map<Class<? extends Screen>,Screen> screenCache  = new HashMap<>();// render chính của game
    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);
        batch = new SpriteBatch();

        addScreen(new DemoMovementScreen(this));// đây là phương thức khởi tạo
        setScreen(DemoMovementScreen.class);

    }
    public void addScreen(Screen screen) {
        screenCache.put(screen.getClass(), screen);
    }
    public void setScreen(Class<? extends Screen> screenClass) {
        Screen screen = screenCache.get(screenClass);
        if (screen == null) {
            throw new GdxRuntimeException("Screen " + screenClass.getSimpleName() + " not found in cache");
        }
        super.setScreen(screen);
    }
    public void removeScreen(Screen screen) {
        screenCache.remove(screen.getClass());
    }

    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render();
    }
    public void resize(int width, int height) {
        super.resize(width, height);
    }
    @Override
    public void dispose() {
        for (Screen screen : screenCache.values()) {
            screen.dispose();
        }
        screenCache.clear();

        batch.dispose();
    }

    public Batch getBatch() { return batch; }
    public OrthographicCamera getCamera() {
        return camera;
    }


    public void setInputProcessors(InputProcessor... processors) {
        inputMultiplexer.clear();
        if (processors == null) return;

        for (InputProcessor processor : processors) {
            inputMultiplexer.addProcessor(processor);
        }
    }

}
