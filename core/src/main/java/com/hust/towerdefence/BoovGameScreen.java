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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3; // Quan trọng
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath.LinePathParam;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.hust.towerdefence.Model.Entities.*;
import com.hust.towerdefence.Model.Manager.EntityManager;
import com.hust.towerdefence.Model.Manager.PathManager;
import com.hust.towerdefence.Model.System.CombatSystem;
import com.hust.towerdefence.Model.System.MovementSystem;
import com.hust.towerdefence.Model.AI.States.CombatState;

public class BoovGameScreen implements Screen {
    private static final float WORLD_WIDTH = 1600;
    private static final float WORLD_HEIGHT = 900;

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private final EntityManager entityManager;
    private final CombatSystem combatSystem;
    private final MovementSystem movementSystem;
    private final ShapeRenderer shapeRenderer;

    private Combat leadSoldier;

    public BoovGameScreen() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        entityManager = new EntityManager();
        combatSystem = new CombatSystem(entityManager);
        movementSystem = new MovementSystem(entityManager);
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void show() {
        map = new TmxMapLoader().load("mapreal.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        // 1. Lấy lộ trình cho Phe Ta (Base -> Enemy)
        LinePath<Vector2> pathTa = PathManager.getPathBaseToEnemy();
        Vector2 startPosTa = pathTa.getStartPoint();

        // 2. Lấy lộ trình cho Phe Địch (Enemy -> Base)
        LinePath<Vector2> pathDich = PathManager.getPathEnemyToBase();
        Vector2 startPosDich = pathDich.getStartPoint();

        // --- KHỞI TẠO PHE TA (Team 1) ---
        leadSoldier = new Combat(startPosTa.x, startPosTa.y, 32, 32, 120, 1, 45, 25, 0.8f, 80, false);
        setupAI(leadSoldier, pathTa);

        // 2. Tay cung (RANGED) - Đứng lùi lại một chút (x - 80)
        // Thông số: Range=250 (bắn xa), Damage=15 (nhẹ hơn), Cooldown=1.2s (lâu hơn)
        Combat archer = new Combat(startPosTa.x - 80, startPosTa.y, 32, 32, 80, 1, 250, 15, 1.2f, 80, true);
        setupAI(archer, pathTa);

        // --- KHỞI TẠO PHE ĐỊCH (Team 2) ---
        // Thay vì đặt cố định tại x=1000, ta đặt tại startPosDich (Nhà Địch)
        // Tăng MaxSpeed lên 80 để nó chạy tới gặp lính mình nhanh hơn
        Combat blocker = new Combat(startPosDich.x, startPosDich.y, 48, 48, 300, 2, 60, 40, 1.0f, 80, false);
        setupAI(blocker, pathDich); // Cho nó dùng AI đuổi theo Path ngược về phía nhà mình

        entityManager.addEntity(leadSoldier);
        entityManager.addEntity(archer);
        entityManager.addEntity(blocker);
    }

    // Hàm này giữ nguyên để gán behavior đi theo đường cho cả 2 bên
    private void setupAI(Combat soldier, LinePath<Vector2> path) {
        //isOpen = true trong LinePath (như mình đã thảo luận) sẽ giúp lính dừng khi hết đường
        FollowPath<Vector2, LinePathParam> followPath = new FollowPath<>(soldier, path, 15f);
        soldier.setFollowPathBehavior(followPath);
        soldier.getStateMachine().setInitialState(CombatState.IDLE_SCAN);
    }

    @Override
    public void render(float delta) {
        movementSystem.update(delta);
        combatSystem.update(delta);
        entityManager.update(delta);

        if (leadSoldier != null && leadSoldier.isActive()) {
            camera.position.lerp(new Vector3(leadSoldier.getX(), leadSoldier.getY(), 0), 0.1f);
        }

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (BaseEntity e : entityManager.getAllEntities()) {
            if (!e.isActive()) continue;
            shapeRenderer.setColor(e.getTeamId() == 1 ? Color.CYAN : Color.RED);
            shapeRenderer.rect(e.getX(), e.getY(), e.getWidth(), e.getHeight());

            // Thanh máu
            shapeRenderer.setColor(Color.GREEN);
            float hpRatio = (float)e.getHp() / (e.getTeamId() == 1 ? 120f : 300f);
            shapeRenderer.rect(e.getX(), e.getY() + e.getHeight() + 5, e.getWidth() * hpRatio, 4);
        }
        shapeRenderer.end();
    }

    @Override public void resize(int width, int height) { viewport.update(width, height); }
    @Override public void dispose() { map.dispose(); mapRenderer.dispose(); shapeRenderer.dispose(); }
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
}
