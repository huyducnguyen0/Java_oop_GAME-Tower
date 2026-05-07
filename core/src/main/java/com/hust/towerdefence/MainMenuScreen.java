package com.hust.towerdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuScreen implements Screen {
    private final MainGame game;
    private Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;
    private Skin skin;

    private Texture backgroundTexture;
    private SpriteBatch batch;

    // Thiết lập đơn vị thế giới chuẩn 16:9
    public static final float WORLD_WIDTH = 1600;
    public static final float WORLD_HEIGHT = 900;

    public MainMenuScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        stage = new Stage(viewport);

        batch = new SpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);

        // 1. NÚT START GAME - Chuyển sang BoovGameScreen
        TextButton startButton = new TextButton("Start Game", skin);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Hiệu ứng mờ dần trước khi chuyển màn cho chuyên nghiệp
                stage.getRoot().addAction(Actions.sequence(
                    Actions.fadeOut(0.5f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            game.setScreen(new BoovGameScreen(game));
                        }
                    })
                ));
            }
        });
        table.add(startButton).width(400).height(80).padBottom(20).row();

        // Slider Music
        table.add(new Label("Music Volume", skin)).padBottom(5).row();
        Slider musicSlider = new Slider(0, 1, 0.1f, false, skin);
        musicSlider.setValue(0.5f);
        table.add(musicSlider).width(500).padBottom(20).row();

        // Slider Sound
        table.add(new Label("Sound Volume", skin)).padBottom(5).row();
        Slider soundSlider = new Slider(0, 1, 0.1f, false, skin);
        soundSlider.setValue(0.7f);
        table.add(soundSlider).width(500).padBottom(40).row();

        // 2. NÚT QUIT GAME - Đóng cửa sổ ứng dụng
        TextButton quitButton = new TextButton("Quit Game", skin);
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        table.add(quitButton).width(400).height(80).row();

        stage.addActor(table);

        // Animation Fade-in khi vừa mở Menu
        stage.getRoot().getColor().a = 0;
        stage.getRoot().addAction(Actions.fadeIn(1.5f));

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Vẽ hình nền
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.end();

        // Vẽ UI
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        backgroundTexture.dispose();
        batch.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
