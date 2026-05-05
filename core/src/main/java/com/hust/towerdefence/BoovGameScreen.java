package com.hust.towerdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hust.towerdefence.Model.Entities.*;
import com.hust.towerdefence.Model.Manager.EntityManager; // Đảm bảo đúng package
import com.hust.towerdefence.Model.System.CombatSystem;   // Sử dụng CombatSystem mới

public class BoovGameScreen implements Screen {

    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 480;

    private OrthographicCamera camera;
    private Viewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    // ===== HỆ THỐNG QUẢN LÝ =====
    private EntityManager entityManager;
    private CombatSystem combatSystem; // Đổi từ GameSystem sang CombatSystem
    private ShapeRenderer shapeRenderer;

    public BoovGameScreen() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        entityManager = new EntityManager();
        combatSystem = new CombatSystem(entityManager);
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void show() {
        map = new TmxMapLoader().load("map1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();

        // ===== SETUP KỊCH BẢN CHIẾN ĐẤU =====

        // 1. Lính đánh gần phe ta (Team 1) - Máu vừa, sát thương ổn
        Combat myMelee = new Combat(300, 200, 32, 32, 100, 1, 40, 15, 0.8f, 50, false);

        // 2. Lính đánh xa phe ta (Team 1) - Đứng sau lính cận chiến
        Combat myRanged = new Combat(200, 200, 32, 32, 80, 1, 300, 25, 1.2f, 50, true);

        // 3. Lính địch (Team 2) - Máu rất trâu, đánh gần, đứng đối diện lính ta
        Combat enemyTank = new Combat(335, 200, 40, 40, 500, 2, 50, 20, 1.0f, 40, false);

        // Thêm vào manager
        entityManager.addEntity(myMelee);
        entityManager.addEntity(myRanged);
        entityManager.addEntity(enemyTank);

        // ===== THIẾT LẬP MỤC TIÊU (Vì chưa có AI di chuyển) =====
        myMelee.setTarget(enemyTank);
        myRanged.setTarget(enemyTank);
        enemyTank.setTarget(myMelee); // Địch tập trung vả con cận chiến trước
    }

    @Override
    public void render(float delta) {
        // 1. Cập nhật Logic
        entityManager.update(delta);    // Chạy State Machine (CombatState)[cite: 1, 4]
        combatSystem.update(delta);     // Xử lý đạn và va chạm

        // 2. Xóa màn hình
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 3. Vẽ Map
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();

        // 4. Vẽ Debug Shapes
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (BaseEntity e : entityManager.getAllEntities()) {
            if (!e.isAlive()) continue;

            // Phân biệt màu sắc
            if (e instanceof Projectile) {
                shapeRenderer.setColor(Color.YELLOW); // Đạn vàng
            } else if (e.getTeamId() == 1) {
                shapeRenderer.setColor(Color.CYAN);   // Ta xanh
            } else if (e.getTeamId() == 2) {
                shapeRenderer.setColor(Color.RED);    // Địch đỏ
            }

            // Vẽ hitbox
            shapeRenderer.rect(e.getX(), e.getY(), e.getWidth(), e.getHeight());

            // Vẽ thêm một thanh máu nhỏ phía trên để dễ quan sát kết quả test
            shapeRenderer.setColor(Color.GREEN);
            float healthBarWidth = (e.getHp() / 500f) * e.getWidth(); // Giả định max hp tank là 500
            shapeRenderer.line(e.getX(), e.getY() + e.getHeight() + 5, e.getX() + e.getWidth(), e.getY() + e.getHeight() + 5);
        }

        shapeRenderer.end();
    }

    @Override public void resize(int width, int height) { viewport.update(width, height); }
    @Override public void dispose() {
        if(map != null) map.dispose();
        if(mapRenderer != null) mapRenderer.dispose();
        shapeRenderer.dispose();
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
