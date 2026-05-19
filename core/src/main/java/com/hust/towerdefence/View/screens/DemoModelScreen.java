package com.hust.towerdefence.View.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hust.towerdefence.MainGame;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Miner;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Soldier;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.Enemy;
import com.hust.towerdefence.Model.Managers.EntityManager;
import com.hust.towerdefence.Model.Managers.MapManager;

public class DemoModelScreen extends ScreenAdapter {
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private GameWorld gameWorld;
    private OrthogonalTiledMapRenderer mapRenderer;
    private float mapWorldWidth;
    private float mapWorldHeight;

    public DemoModelScreen(MainGame game) {
    }
    @Override
    public void show() {
        // Khởi tạo GameWorld – trung tâm Model
        gameWorld = new GameWorld("mapreal.tmx", 500, 9999);

        MapManager mapManager = gameWorld.getMapManager();
        mapWorldWidth = mapManager.getMapWidth() * mapManager.getTileSize();
        mapWorldHeight = mapManager.getMapHeight() * mapManager.getTileSize();

        camera = new OrthographicCamera();
        viewport = new FitViewport(mapWorldWidth, mapWorldHeight, camera);
        viewport.apply();
        camera.position.set(mapWorldWidth / 2f, mapWorldHeight / 2f, 0);
        camera.update();

        shapeRenderer = new ShapeRenderer();

        TiledMap tiledMap = mapManager.getTiledMap();
        if (tiledMap != null) {
            mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        }
        gameWorld.spawnPawn();
        gameWorld.spawnMiner();
        gameWorld.spawnArcher();
        gameWorld.spawnWarrior();
        gameWorld.spawnTNT();

        gameWorld.spawnWarriorHacHoa();

        gameWorld.spawnPawnHacHoa();
        // Thiết lập input test
        /*Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.P:
                        gameWorld.spawnPawn();
                        break;
                    case Input.Keys.M:
                        gameWorld.spawnMiner();
                        break;
                    case Input.Keys.A:
                        gameWorld.spawnArcher();
                        break;
                    //case Input.Keys.E:
                      //  gameWorld.spawnEnemy(com.hust.towerdefence.Model.Entities.Combat.Enemy.PawnHacHoa.class);
                        //break;
                    case Input.Keys.SPACE:
                        gameWorld.setPaused(!gameWorld.isPaused());
                        break;
                }
                return true;
            }
        });*/
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Cập nhật Model (di chuyển, tấn công, máu...)
        gameWorld.update(delta);

        // Render map
        viewport.apply();
        camera.update();
        if (mapRenderer != null) {
            mapRenderer.setView(camera);
            mapRenderer.render();
        }

        // Vẽ các entity
        EntityManager em = gameWorld.getEntityManager();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // MainTower (xanh lá)
        shapeRenderer.setColor(0, 0.6f, 0, 1);
        Vector2 castlePos = gameWorld.getMapManager().getPlayerCastlePosition();
        if (castlePos != null) {
            shapeRenderer.rect(castlePos.x - 15, castlePos.y - 15, 30, 30);
        }

        // Enemy base (đỏ) – vẽ nếu có
        shapeRenderer.setColor(0.8f, 0, 0, 1);
        Vector2 enemyBasePos = gameWorld.getMapManager().getEnemyBasePosition();
        if (enemyBasePos != null) {
            shapeRenderer.rect(enemyBasePos.x - 15, enemyBasePos.y - 15, 30, 30);
        }

        // Vẽ lính (xanh dương)
        for (Soldier s : em.getSoldiers()) {
            Vector2 pos = s.getPosition();
            shapeRenderer.setColor(0, 0, 1, 1);
            shapeRenderer.circle(pos.x, pos.y, 10);

            // Nếu đang tấn công – vẽ thêm vòng đỏ
            if (s.getCurrentState() == CombatEntity.State.ATTACKING) {
                shapeRenderer.setColor(1, 0, 0, 1);
                shapeRenderer.circle(pos.x, pos.y, 14);
            }

            // Nếu là Miner và đang đào – vẽ vòng vàng
            if (s instanceof Miner) {
                Miner miner = (Miner) s;
                if (miner.getCurrentState() == CombatEntity.State.MINING) {
                    shapeRenderer.setColor(1, 1, 0, 1);
                    shapeRenderer.circle(pos.x, pos.y, 12);
                }
            }
        }

        // Vẽ địch (cam)
        for (Enemy e : em.getEnemies()) {
            Vector2 pos = e.getPosition();
            shapeRenderer.setColor(1, 0.5f, 0, 1);
            shapeRenderer.circle(pos.x, pos.y, 10);

            if (e.getCurrentState() == CombatEntity.State.ATTACKING) {
                shapeRenderer.setColor(1, 0, 0, 1);
                shapeRenderer.circle(pos.x, pos.y, 14);
            }
        }

        shapeRenderer.end();

        // Thông tin debug
        Gdx.graphics.setTitle("Gold: " + gameWorld.getEconomyManager().getGold()
            + " | FPS: " + Gdx.graphics.getFramesPerSecond());
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
        if (gameWorld != null) gameWorld.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
