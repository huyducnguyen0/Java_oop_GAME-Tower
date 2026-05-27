package com.hust.towerdefence.View.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.hust.towerdefence.MainGame;
import com.hust.towerdefence.Model.AI.AIController;
import com.hust.towerdefence.Model.Entities.Tower.MainTower;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.BuildingZone;
import com.hust.towerdefence.Model.Managers.EntityManager;
import com.hust.towerdefence.Model.Managers.MapManager;
import com.hust.towerdefence.View.ui.CombatEffectRenderer;
import com.hust.towerdefence.View.ui.DefenseTowerRenderer;
import com.hust.towerdefence.View.ui.GameHud;
import com.hust.towerdefence.View.ui.MainTowerRenderer;
import com.hust.towerdefence.View.ui.UnitRenderer;
import com.hust.towerdefence.View.ui.WorldHudRenderer;

public class DemoModelScreen extends ScreenAdapter {
    private static final String MAP_PATH = "mapreal.tmx";
    private static final int INITIAL_GOLD = 1000;
    private static final int MAX_GOLD = 2000;
    private static final int AI_LEVEL = 2;
    private static final Color SELECTED_PURCHASE_BUILDING = new Color(0.15f, 0.8f, 1f, 1f);
    private static final Color SELECTED_NON_PURCHASE_BUILDING = new Color(1f, 0.76f, 0.15f, 1f);
    private static final float SELECTION_OUTLINE_THICKNESS = 4f;

    private final MainGame game;
    private GameWorld gameWorld;
    private AIController aiController;

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private OrthogonalTiledMapRenderer mapRenderer;
    private GameHud gameHud;
    private WorldHudRenderer worldHudRenderer;
    private MainTowerRenderer mainTowerRenderer;
    private DefenseTowerRenderer defenseTowerRenderer;
    private UnitRenderer unitRenderer;
    private CombatEffectRenderer combatEffectRenderer;
    private BuildingZone selectedBuildingZone;

    private int mapPixelWidth;
    private int mapPixelHeight;

    public DemoModelScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        gameWorld = new GameWorld(MAP_PATH, INITIAL_GOLD, MAX_GOLD);
        System.out.println("GameWorld created. Player gold: " + gameWorld.getEconomyManager().getGold());

        EntityManager entityManager = gameWorld.getEntityManager();
        MapManager mapManager = gameWorld.getMapManager();
        MainTower playerTower = gameWorld.getMainTower();
        MainTower enemyTower = gameWorld.getEnemyTower();

        aiController = new AIController(AI_LEVEL, entityManager, mapManager, playerTower, enemyTower);
        System.out.println("AI initialized. AI gold: " + aiController.getGold());

        mapPixelWidth = mapManager.getMapWidth() * mapManager.getTileSize();
        mapPixelHeight = mapManager.getMapHeight() * mapManager.getTileSize();
        System.out.println("Map pixel size: " + mapPixelWidth + "x" + mapPixelHeight);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, mapPixelWidth, mapPixelHeight);
        camera.position.set(mapPixelWidth / 2f, mapPixelHeight / 2f, 0);
        camera.update();

        TiledMap tiledMap = mapManager.getTiledMap();
        if (tiledMap != null) {
            mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            System.out.println("MapRenderer created.");
        } else {
            System.err.println("Khong the load TiledMap tu " + MAP_PATH);
        }

        shapeRenderer = new ShapeRenderer();
        gameHud = new GameHud(gameWorld);
        worldHudRenderer = new WorldHudRenderer();
        mainTowerRenderer = new MainTowerRenderer();
        defenseTowerRenderer = new DefenseTowerRenderer();
        unitRenderer = new UnitRenderer();
        combatEffectRenderer = new CombatEffectRenderer();

        game.setInputProcessors(gameHud.getStage(), new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button != Input.Buttons.LEFT) return false;
                return handleWorldClick(screenX, screenY);
            }

            @Override
            public boolean keyDown(int keycode) {
                System.out.println("Adapter received key: " + keycode);
                return handleInputKey(keycode);
            }
        });
        System.out.println("InputAdapter registered via MainGame multiplexer.");
        System.out.println("DemoModelScreen ready. Phim: P=Pawn M=Miner A=Archer W=Warrior SPACE=Pause");
    }

    private boolean handleWorldClick(int screenX, int screenY) {
        Vector3 world = camera.unproject(new Vector3(screenX, screenY, 0f));
        BuildingZone hitZone = gameWorld.getMapManager().findInteractiveZone(world.x, world.y);
        if (hitZone == null) {
            selectedBuildingZone = null;
            gameHud.setSelectedBuilding((BuildingZone) null);
            return false;
        }

        selectedBuildingZone = hitZone;
        gameHud.setSelectedBuilding(hitZone);
        System.out.println("Selected building: " + hitZone.getName());
        return true;
    }

    private boolean handleInputKey(int keycode) {
        switch (keycode) {
            case Input.Keys.P:
                System.out.println("Spawn Pawn");
                gameWorld.spawnPawn();
                System.out.println("Entity count: " + gameWorld.getEntityManager().getAllEntities().size);
                return true;
            case Input.Keys.M:
                System.out.println("Spawn Miner");
                gameWorld.spawnMiner();
                return true;
            case Input.Keys.A:
                System.out.println("Spawn Archer");
                gameWorld.spawnArcher();
                return true;
            case Input.Keys.W:
                System.out.println("Spawn Warrior");
                gameWorld.spawnWarrior();
                return true;
            case Input.Keys.SPACE:
                gameWorld.setPaused(!gameWorld.isPaused());
                System.out.println("Paused: " + gameWorld.isPaused());
                return true;
            case Input.Keys.F1:
                System.out.println("===== AI STATUS =====");
                System.out.printf("Gold: %.1f  Level: %d\n", aiController.getGold(), AI_LEVEL);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void render(float delta) {
        gameWorld.update(delta);
        aiController.update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (mapRenderer != null) {
            mapRenderer.setView(camera);
            mapRenderer.render();
        }

        mainTowerRenderer.render(camera, gameWorld.getMainTower(), gameWorld.getEnemyTower());
        defenseTowerRenderer.render(camera, gameWorld.getEntityManager().getTowers());
        unitRenderer.render(camera, gameWorld.getEntityManager().getAllEntities(), delta);
        combatEffectRenderer.addEvents(gameWorld.consumeCombatVisualEvents());
        combatEffectRenderer.render(camera, delta);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        worldHudRenderer.drawUnitHealthBars(shapeRenderer, gameWorld.getEntityManager().getAllEntities());
        worldHudRenderer.drawTowerHealthBars(shapeRenderer, gameWorld.getMainTower(), gameWorld.getEnemyTower());
        worldHudRenderer.drawDefenseTowerHealthBars(shapeRenderer, gameWorld.getEntityManager().getTowers());
        drawSelectedBuildingOutline();
        shapeRenderer.end();

        gameHud.act(delta, camera);
        gameHud.draw();
    }

    private void drawSelectedBuildingOutline() {
        if (selectedBuildingZone == null) return;

        Rectangle bounds = selectedBuildingZone.getBounds();
        shapeRenderer.setColor(selectedBuildingZone.isPurchaseBuilding()
            ? SELECTED_PURCHASE_BUILDING
            : SELECTED_NON_PURCHASE_BUILDING);

        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, SELECTION_OUTLINE_THICKNESS);
        shapeRenderer.rect(
            bounds.x,
            bounds.y + bounds.height - SELECTION_OUTLINE_THICKNESS,
            bounds.width,
            SELECTION_OUTLINE_THICKNESS
        );
        shapeRenderer.rect(bounds.x, bounds.y, SELECTION_OUTLINE_THICKNESS, bounds.height);
        shapeRenderer.rect(
            bounds.x + bounds.width - SELECTION_OUTLINE_THICKNESS,
            bounds.y,
            SELECTION_OUTLINE_THICKNESS,
            bounds.height
        );
    }

    @Override
    public void resize(int width, int height) {
        if (gameHud != null) {
            gameHud.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        gameWorld.dispose();
        shapeRenderer.dispose();
        if (gameHud != null) gameHud.dispose();
        if (mainTowerRenderer != null) mainTowerRenderer.dispose();
        if (defenseTowerRenderer != null) defenseTowerRenderer.dispose();
        if (unitRenderer != null) unitRenderer.dispose();
        if (combatEffectRenderer != null) combatEffectRenderer.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
    }
}
