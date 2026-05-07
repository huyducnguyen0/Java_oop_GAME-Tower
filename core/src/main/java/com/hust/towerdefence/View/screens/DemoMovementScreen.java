package com.hust.towerdefence.View.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hust.towerdefence.MainGame;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.Enemy;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.PawnHacHoa;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Soldier;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Warrior;
import com.hust.towerdefence.Model.Managers.EntityManager;
import com.hust.towerdefence.Model.Managers.MapManager;
import com.hust.towerdefence.Model.Systems.MovementSystem;

public class DemoMovementScreen extends ScreenAdapter {
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private MainGame game;
    private EntityManager entityManager;
    private MapManager mapManager;
    private MovementSystem movementSystem;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    // Kích thước map (đọc từ file map, nhưng có thể hardcode tạm)
    private float mapWorldWidth;
    private float mapWorldHeight;

    public DemoMovementScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Khởi tạo entity và map manager trước để lấy kích thước map
        mapManager = new MapManager("mapreal.tmx");
        mapWorldWidth = mapManager.getMapWidth() * mapManager.getTileSize();
        mapWorldHeight = mapManager.getMapHeight() * mapManager.getTileSize();


        camera = new OrthographicCamera();
        viewport = new FitViewport(mapWorldWidth, mapWorldHeight, camera);
        viewport.apply();
        camera.position.set(mapWorldWidth / 2f, mapWorldHeight / 2f, 0);
        camera.update();

        shapeRenderer = new ShapeRenderer();

        // Lấy tiled map từ MapManager (tránh load lại)
        tiledMap = mapManager.getTiledMap();
        if (tiledMap != null) {
            mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            System.out.println("Map loaded successfully!");
            System.out.println("Map width: " + mapManager.getMapWidth());
            System.out.println("Map height: " + mapManager.getMapHeight());
            System.out.println("Tile size: " + mapManager.getTileSize());
        } else {
            System.err.println("Failed to load map!");
        }

        entityManager = new EntityManager();
        movementSystem = new MovementSystem(entityManager, mapManager);

        // Lấy waypoints và castle positions
        Array<Vector2> waypoints = mapManager.getWaypoints();
        Vector2 castlePos = mapManager.getPlayerCastlePosition();
        Vector2 castleEnemy = mapManager.getEnemyBasePosition();
        if (castlePos == null) {
            System.err.println("Player castle position not found!");
            castlePos = new Vector2(100, 100);
        }

        Warrior warrior = new Warrior(castlePos);
        warrior.setPosition(castlePos.x, castlePos.y);

        if (waypoints != null && waypoints.size > 0) {
            movementSystem.setPath(warrior, waypoints);
            System.out.println("Waypoints count: " + waypoints.size);
        } else {
            System.err.println("No waypoints found on the map!");
        }
        entityManager.addSoldier(warrior);

        // Tạo enemy test
        PawnHacHoa enemy = new PawnHacHoa();
        enemy.setPosition(castleEnemy.x, castleEnemy.y);
        Array<Vector2> enemyPath = new Array<>();
        for (int i = waypoints.size - 1; i >= 0; i--) {
            enemyPath.add(waypoints.get(i));
        }

        if (waypoints != null && waypoints.size > 0) {
            movementSystem.setPath(enemy, enemyPath);
        } else {
            System.err.println("No waypoints found on the map!");
        }
        entityManager.addEnemy(enemy);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Cập nhật camera và viewport theo kích thước màn hình hiện tại
        viewport.apply();
        camera.update();

        // Vẽ map
        if (mapRenderer != null) {
            mapRenderer.setView(camera);
            mapRenderer.render();
        }

        // Cập nhật logic di chuyển
        movementSystem.update(delta);

        // Vẽ các entity bằng ShapeRenderer
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Vẽ castle (xanh)
        shapeRenderer.setColor(0, 0.6f, 0, 1);
        Vector2 castle = mapManager.getPlayerCastlePosition();
        if (castle != null) {
            shapeRenderer.rect(castle.x - 15, castle.y - 15, 30, 30);
        }

        // Vẽ enemy base (đỏ)
        shapeRenderer.setColor(0.8f, 0, 0, 1);
        Vector2 base = mapManager.getEnemyBasePosition();
        if (base != null) {
            shapeRenderer.rect(base.x - 15, base.y - 15, 30, 30);
        }

        // Vẽ soldier
        shapeRenderer.setColor(0, 0, 1, 1);
        for (Soldier s : entityManager.getSoldiers()) {
            Vector2 pos = s.getPosition();
            shapeRenderer.circle(pos.x, pos.y, 10);
            if (s.getCurrentState() == CombatEntity.State.ATTACKING) {
                shapeRenderer.setColor(1, 0, 0, 1);
                shapeRenderer.circle(pos.x, pos.y, 14);
                shapeRenderer.setColor(0, 0, 1, 1);
            }
        }

        // Vẽ enemy
        shapeRenderer.setColor(1, 0.5f, 0, 1);
        for (Enemy e : entityManager.getEnemies()) {
            Vector2 pos = e.getPosition();
            shapeRenderer.circle(pos.x, pos.y, 10);
        }

        shapeRenderer.end();

        // Cập nhật title FPS
        Gdx.graphics.setTitle("Tower Defence - FPS: " + Gdx.graphics.getFramesPerSecond());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(mapWorldWidth / 2f, mapWorldHeight / 2f, 0);
        camera.update();
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        if (mapManager != null) mapManager.dispose();
    }

    // Các phương thức bắt buộc khác
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
