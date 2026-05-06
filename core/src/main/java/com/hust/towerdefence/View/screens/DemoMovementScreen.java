package com.hust.towerdefence.View.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
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
    private ShapeRenderer renderer;
    private MainGame game;
    private EntityManager entityManager;
    private MapManager mapManager;
    private MovementSystem movementSystem;


    @Override
    public void show() {
        camera = new OrthographicCamera(800, 600);
        camera.position.set(400, 300, 0);
        renderer = new ShapeRenderer();
        entityManager = new EntityManager();
        mapManager = new MapManager(20, 15, 32);
        mapManager.loadMapFromFile("maps/map1.tmx");

        movementSystem = new MovementSystem(entityManager, mapManager);

        // Tạo Warrior tại vị trí Player Castle
        Vector2 castlePos = mapManager.getPlayerCastlePosition();
        Warrior warrior = new Warrior(castlePos);
        warrior.setPosition(castlePos.x, castlePos.y);

        // Lấy waypoints từ bản đồ và thiết lập đường đi
        Array<Vector2> waypoints = mapManager.getWaypoints();
        movementSystem.setPath(warrior, waypoints);
        entityManager.addSoldier(warrior);

        // Tạo một Enemy (PawnHacHoa) đứng giữa đường để test giao tranh
        PawnHacHoa enemy = new PawnHacHoa();
        enemy.setPosition(400, 300);
        entityManager.addEnemy(enemy);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        movementSystem.update(delta);

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        // Vẽ castle (xanh)
        renderer.setColor(0, 0.6f, 0, 1);
        Vector2 castle = mapManager.getPlayerCastlePosition();
        renderer.rect(castle.x - 15, castle.y - 15, 30, 30);

        // Vẽ enemy base (đỏ)
        renderer.setColor(0.8f, 0, 0, 1);
        Vector2 base = mapManager.getEnemyBasePosition();
        renderer.rect(base.x - 15, base.y - 15, 30, 30);

        // Vẽ soldier
        renderer.setColor(0, 0, 1, 1);
        for (Soldier s : entityManager.getSoldiers()) {
            Vector2 pos = s.getPosition();
            renderer.circle(pos.x, pos.y, 10);
            // Vẽ trạng thái
            if (s.getCurrentState() == CombatEntity.State.ATTACKING) {
                renderer.setColor(1, 0, 0, 1);
                renderer.circle(pos.x, pos.y, 14);
                renderer.setColor(0, 0, 1, 1);
            }
        }

        // Vẽ enemy
        renderer.setColor(1, 0.5f, 0, 1);
        for (Enemy e : entityManager.getEnemies()) {
            Vector2 pos = e.getPosition();
            renderer.circle(pos.x, pos.y, 10);
        }

        renderer.end();
    }

    @Override public void resize(int w, int h) { camera.viewportWidth = w; camera.viewportHeight = h; }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { renderer.dispose(); }
}
