package com.hust.towerdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Màn hình Menu chính - Phiên bản đã xóa chữ Mystic Tutorial thừa.
 * Tích hợp: WorldUnit (1600x900), FitViewport, Animation Fade-in và Background.
 */
public class MainMenuScreen implements Screen {
    private final MainGame game;
    private Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;
    private Skin skin;

    // Xử lý hình nền
    private Texture backgroundTexture;
    private SpriteBatch batch;

    // Thiết lập đơn vị thế giới chuẩn 16:9 theo yêu cầu của Kiên
    public static final float WORLD_WIDTH = 1600;
    public static final float WORLD_HEIGHT = 900;

    public MainMenuScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        // --- THIẾT LẬP VIEWPORT ---
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        stage = new Stage(viewport);

        // --- NẠP ASSETS ---
        batch = new SpriteBatch();
        // Nạp ảnh nền (Cái ảnh có sẵn chữ Tower Defense của ông)
        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        // Nạp bộ giao diện nút bấm
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // --- TẠO BẢNG UI (ĐÃ XÓA TIÊU ĐỀ) ---
        Table table = new Table();
        table.setFillParent(true);

        // Nhảy thẳng vào nút Start Game, không tạo Label tiêu đề nữa
        TextButton startButton = new TextButton("Start Game", skin);
        table.add(startButton).width(400).height(80).padBottom(20).row();

        // Slider âm lượng Nhạc
        table.add(new Label("Music Volume", skin)).padBottom(5).row();
        Slider musicSlider = new Slider(0, 1, 0.1f, false, skin);
        musicSlider.setValue(0.5f);
        table.add(musicSlider).width(500).padBottom(20).row();

        // Slider âm lượng Hiệu ứng
        table.add(new Label("Sound Volume", skin)).padBottom(5).row();
        Slider soundSlider = new Slider(0, 1, 0.1f, false, skin);
        soundSlider.setValue(0.7f);
        table.add(soundSlider).width(500).padBottom(40).row();

        // Nút Quit Game
        TextButton quitButton = new TextButton("Quit Game", skin);
        table.add(quitButton).width(400).height(80).row();

        stage.addActor(table);

        // --- HIỆU ỨNG ANIMATION (Sửa trong phần show như Kiên dặn) ---
        stage.getRoot().getColor().a = 0;
        stage.getRoot().addAction(Actions.fadeIn(1.5f));

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // 1. Xóa màn hình cũ
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 2. Vẽ hình nền trước
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.end();

        // 3. Vẽ các nút bấm Menu đè lên
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
