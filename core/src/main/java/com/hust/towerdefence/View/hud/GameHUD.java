package com.hust.towerdefence.View.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.EntityManager;

public class GameHUD implements Disposable {

    // Scene2D Stage — tự dùng ScreenViewport (screen space)
    // KHÔNG bị ảnh hưởng bởi game camera
    private final Stage stage;
    private final SpriteBatch hudBatch;

    // --- References ---
    private final GameWorld gameWorld;
    private final EntityManager entityManager;

    // --- UI Widgets ---
    private Label goldLabel;
    private Label waveLabel;
    private Label hpLabel;
    private Label scoreLabel;
    private Label timerLabel;

    // Font mặc định — TODO: thay bằng custom font sau
    private final BitmapFont font;

    public GameHUD(GameWorld gameWorld, EntityManager entityManager) {
        this.gameWorld = gameWorld;
        this.entityManager = entityManager;

        // HUD dùng SpriteBatch riêng để tránh conflict với game renderer
        this.hudBatch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport(), hudBatch);
        this.font = new BitmapFont(); // LibGDX default font

        buildUI();
    }

    /**
     * Xây dựng layout HUD bằng Scene2D Table.
     * Table tự động căn chỉnh theo screen size.
     */
    private void buildUI() {
        // Root table — fill toàn màn hình
        Table root = new Table();
        root.setFillParent(true);

        // --- Top bar: Wave + Timer + Score ---
        Table topBar = new Table();

        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

        waveLabel  = new Label("Wave: 1", style);
        timerLabel = new Label("Time: 30s", style);
        scoreLabel = new Label("Score: 0", style);

        topBar.add(waveLabel).expandX().left().pad(10);
        topBar.add(timerLabel).expandX().center().pad(10);
        topBar.add(scoreLabel).expandX().right().pad(10);

        // --- Bottom bar: Gold + Main Building HP ---
        Table bottomBar = new Table();

        goldLabel = new Label("Gold: 1000", style);
        hpLabel   = new Label("Base HP: 1000", style);

        bottomBar.add(goldLabel).expandX().left().pad(10);
        bottomBar.add(hpLabel).expandX().right().pad(10);

        // Gắn vào root — top bar ở trên, bottom bar ở dưới
        root.add(topBar).fillX().top().row();
        root.add().expand().row(); // spacer đẩy bottom bar xuống
        root.add(bottomBar).fillX().bottom();

        stage.addActor(root);
    }

    /**
     * Gọi mỗi frame — cập nhật text từ Model rồi vẽ.
     * Chỉ update label khi giá trị thực sự thay đổi
     * để tránh tạo String mới mỗi frame (gây GC).
     */
    public void render(float delta) {
        // Cập nhật data từ Model
        updateLabels();

        // Act + Draw Stage (Scene2D tự xử lý screen space)
        stage.act(delta);
        stage.draw();
    }

    private void updateLabels() {
        goldLabel.setText("Gold: " + entityManager.getGold());
        waveLabel.setText("Wave: " + gameWorld.getWave());
        hpLabel.setText("Base HP: " + entityManager.getMainBuildingHp());
        scoreLabel.setText("Score: " + gameWorld.getScore());

        // Timer: đếm ngược thời gian còn lại của wave
        if (gameWorld.isWaveInProgress()) {
            int timeLeft = Math.max(0,
                (int)(30f - gameWorld.getWaveTimer()));
            timerLabel.setText("Time: " + timeLeft + "s");
        } else {
            timerLabel.setText("Next wave soon...");
        }
    }

    public void resize(int width, int height) {
        // ScreenViewport cần update khi resize
        stage.getViewport().update(width, height, true);
    }

    public void show() {
        // TODO: animate HUD slide in nếu muốn
    }

    /** Trả về Stage để InputMultiplexer có thể nhận input từ HUD */
    public Stage getStage() { return stage; }

    @Override
    public void dispose() {
        stage.dispose();
        hudBatch.dispose();
        font.dispose();
    }
}
