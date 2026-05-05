package com.hust.towerdefence.View.renderer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.EntityManager;
import com.hust.towerdefence.View.entity.UnitView;
import com.hust.towerdefence.View.entity.EnemyView;
import com.hust.towerdefence.View.entity.EffectView;
import com.hust.towerdefence.View.world.GameWorldView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameRenderer implements Disposable {

    // --- LibGDX core ---
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer; // dùng cho healthbar, debug shapes
    private final OrthographicCamera camera;
    private final AnimationController animationController;

    // --- Sub-views ---
    private final GameWorldView worldView;       // lớp 1: background + tilemap
    private final EffectView effectView;         // lớp 3: explosion, VFX

    // Map entity → view để sync khi EntityManager thêm/xóa entity
    private final Map<Unit, UnitView> unitViewMap = new HashMap<>();
    private final Map<Enemy, EnemyView> enemyViewMap = new HashMap<>();

    // --- References ---
    private final GameWorld gameWorld;
    private final EntityManager entityManager;

    public GameRenderer(GameWorld gameWorld, EntityManager entityManager) {
        this.gameWorld = gameWorld;
        this.entityManager = entityManager;

        // SpriteBatch — tạo 1 lần duy nhất, dùng chung cho mọi sub-view
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        // Camera — viewport khớp với world size
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false);
        this.camera.zoom = 1f;

        // Animation controller — load texture atlas một lần
        this.animationController = new AnimationController();

        // Sub-views
        this.worldView = new GameWorldView(gameWorld, batch, shapeRenderer);
        this.effectView = new EffectView(batch);
    }

    public void render(float delta) {
        // Sync view map với entity list hiện tại
        // (xử lý entity mới được thêm hoặc đã chết bị xóa)
        syncEntityViews();

        // Cập nhật camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // === Lớp 1: World / Background ===
        worldView.render(delta);

        // === Lớp 2: Entities (y-sort) ===
        batch.begin();
        renderEntitiesSorted(delta);
        batch.end();

        // === Lớp 3: Effects / VFX ===
        batch.begin();
        effectView.render(delta);
        batch.end();

        // === Debug: vẽ healthbar bằng ShapeRenderer ===
        // TODO: chuyển sang HealthBarWidget trong HUD sau
        renderHealthBars();
    }

    /**
     * Sync UnitView/EnemyView map với danh sách entity thực tế.
     * Gọi mỗi frame để tự động tạo view cho entity mới,
     * xóa view của entity đã bị EntityManager remove.
     */
    private void syncEntityViews() {
        // Thêm view cho Unit mới
        for (Unit unit : entityManager.getUnits()) {
            if (!unitViewMap.containsKey(unit)) {
                unitViewMap.put(unit, new UnitView(unit, animationController));
            }
        }
        // Xóa view của Unit đã bị remove
        unitViewMap.keySet().retainAll(entityManager.getUnits());

        // Thêm view cho Enemy mới
        for (Enemy enemy : entityManager.getEnemies()) {
            if (!enemyViewMap.containsKey(enemy)) {
                enemyViewMap.put(enemy, new EnemyView(enemy, animationController));
            }
        }
        // Xóa view của Enemy đã bị remove
        enemyViewMap.keySet().retainAll(entityManager.getEnemies());
    }

    /**
     * Gom tất cả entity views, sort theo Y giảm dần,
     * entity ở phía dưới màn hình (Y nhỏ hơn) vẽ sau → tạo depth 2.5D.
     */
    private void renderEntitiesSorted(float delta) {
        // Gom chung để sort cùng nhau
        List<UnitView> units = new ArrayList<>(unitViewMap.values());
        List<EnemyView> enemies = new ArrayList<>(enemyViewMap.values());

        // Sort theo Y giảm dần (Y lớn hơn = xa hơn = vẽ trước)
        units.sort(Comparator.comparingDouble(v -> -v.getEntity().getY()));
        enemies.sort(Comparator.comparingDouble(v -> -v.getEntity().getY()));

        // Merge và vẽ theo thứ tự Y
        int ui = 0, ei = 0;
        while (ui < units.size() && ei < enemies.size()) {
            float uy = units.get(ui).getEntity().getY();
            float ey = enemies.get(ei).getEntity().getY();
            if (uy >= ey) {
                units.get(ui++).render(batch, delta);
            } else {
                enemies.get(ei++).render(batch, delta);
            }
        }
        while (ui < units.size()) units.get(ui++).render(batch, delta);
        while (ei < enemies.size()) enemies.get(ei++).render(batch, delta);
    }

    /**
     * Vẽ healthbar đơn giản bằng ShapeRenderer.
     * Chạy sau batch.end() vì ShapeRenderer và SpriteBatch
     * không thể begin() cùng lúc.
     */
    private void renderHealthBars() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Unit unit : entityManager.getUnits()) {
            drawHealthBar(unit.getX(), unit.getY() + 40,
                unit.getHp(), unit.getMaxHp());
        }
        for (Enemy enemy : entityManager.getEnemies()) {
            drawHealthBar(enemy.getX(), enemy.getY() + 40,
                enemy.getHp(), enemy.getMaxHp());
        }

        shapeRenderer.end();
    }

    private void drawHealthBar(float x, float y, int hp, int maxHp) {
        float barWidth = 40f;
        float barHeight = 5f;
        float ratio = (float) hp / maxHp;

        // Nền đỏ
        shapeRenderer.setColor(0.8f, 0.1f, 0.1f, 1f);
        shapeRenderer.rect(x - barWidth / 2, y, barWidth, barHeight);

        // Phần HP còn lại — xanh lá
        shapeRenderer.setColor(0.1f, 0.8f, 0.1f, 1f);
        shapeRenderer.rect(x - barWidth / 2, y, barWidth * ratio, barHeight);
    }

    // --- Camera controls ---

    /** Gọi từ input handler để pan camera theo vị trí player/event */
    public void centerCameraOn(float x, float y) {
        camera.position.set(x, y, 0);
        // Clamp camera trong world bounds
        float halfW = camera.viewportWidth / 2f;
        float halfH = camera.viewportHeight / 2f;
        camera.position.x = Math.max(halfW, Math.min(x, gameWorld.WORLD_WIDTH - halfW));
        camera.position.y = Math.max(halfH, Math.min(y, gameWorld.WORLD_HEIGHT - halfH));
    }

    public void zoom(float amount) {
        camera.zoom = Math.max(0.5f, Math.min(camera.zoom + amount, 3f));
    }

    public OrthographicCamera getCamera() { return camera; }

    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    /** Thông báo để EffectView spawn effect tại vị trí */
    public void spawnEffect(float x, float y, String effectType) {
        effectView.spawnEffect(x, y, effectType);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        worldView.dispose();
        effectView.dispose();
        animationController.dispose();
        unitViewMap.values().forEach(UnitView::dispose);
        enemyViewMap.values().forEach(EnemyView::dispose);
    }
}
