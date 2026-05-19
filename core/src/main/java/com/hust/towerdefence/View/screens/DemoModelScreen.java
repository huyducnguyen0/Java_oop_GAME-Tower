package com.hust.towerdefence.View.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.SnapshotArray;
import com.hust.towerdefence.MainGame;
import com.hust.towerdefence.Model.AI.AIController;
import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.Enemy;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.PawnHacHoa;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.TNT;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.WarriorHacHoa;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.*;
import com.hust.towerdefence.Model.Entities.Tower.MainTower;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.EntityManager;
import com.hust.towerdefence.Model.Managers.MapManager;

public class DemoModelScreen extends ScreenAdapter {
    private final MainGame game;
    private GameWorld gameWorld;
    private AIController aiController;

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private OrthogonalTiledMapRenderer mapRenderer;

    private static final String MAP_PATH = "mapreal.tmx";
    private static final int INITIAL_GOLD = 1000;
    private static final int MAX_GOLD = 2000;
    private static final int AI_LEVEL = 2;

    private int mapPixelWidth, mapPixelHeight;

    public DemoModelScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        // 1. GameWorld
        gameWorld = new GameWorld(MAP_PATH, INITIAL_GOLD, MAX_GOLD);
        System.out.println("GameWorld created. Player gold: " + gameWorld.getEconomyManager().getGold());

        // 2. Tham chiếu
        EntityManager entityManager = gameWorld.getEntityManager();
        MapManager mapManager = gameWorld.getMapManager();
        MainTower playerTower = gameWorld.getMainTower();
        MainTower enemyTower = gameWorld.getEnemyTower();

        // 3. AI
        aiController = new AIController(AI_LEVEL, entityManager, mapManager, playerTower, enemyTower);
        System.out.println("AI initialized. AI gold: " + aiController.getGold());

        // 4. Camera
        mapPixelWidth = mapManager.getMapWidth() * mapManager.getTileSize();
        mapPixelHeight = mapManager.getMapHeight() * mapManager.getTileSize();
        System.out.println("Map pixel size: " + mapPixelWidth + "x" + mapPixelHeight);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, mapPixelWidth, mapPixelHeight);
        camera.position.set(mapPixelWidth / 2f, mapPixelHeight / 2f, 0);
        camera.update();

        // 5. MapRenderer
        TiledMap tiledMap = mapManager.getTiledMap();
        if (tiledMap != null) {
            mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            System.out.println("MapRenderer created.");
        } else {
            System.err.println("Không thể load TiledMap từ " + MAP_PATH);
        }

        // 6. Công cụ vẽ
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);

        // 7. Đăng ký InputAdapter vào multiplexer (nếu có)
        try {
            InputMultiplexer multiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
            multiplexer.addProcessor(new InputAdapter() {
                @Override
                public boolean keyDown(int keycode) {
                    System.out.println("Adapter received key: " + keycode);
                    return handleInputKey(keycode);
                }
            });
            System.out.println("InputAdapter registered via multiplexer.");
        } catch (Exception e) {
            System.err.println("Không thể thêm InputAdapter: " + e.getMessage());
        }

        System.out.println("DemoModelScreen ready. Phím: P=Pawn M=Miner A=Archer W=Warrior SPACE=Pause");
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
        // --- Hỗ trợ thêm: Dùng Gdx.input trực tiếp như phương án dự phòng ---
        // Nếu InputAdapter không hoạt động, cách này vẫn bắt được phím.
        handleDirectInput();

        // Cập nhật logic
        gameWorld.update(delta);
        aiController.update(delta);

        // Xóa màn hình
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Vẽ bản đồ
        if (mapRenderer != null) {
            mapRenderer.setView(camera);
            mapRenderer.render();
        }

        // Vẽ entity
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        SnapshotArray<BaseEntity> entities = gameWorld.getEntityManager().getAllEntities();
        for (BaseEntity e : entities) {
            Color color = getEntityColor(e);
            shapeRenderer.setColor(color);
            Vector2 pos = e.getPosition();
            if (pos != null) shapeRenderer.circle(pos.x, pos.y, 8);
        }
        drawTower(gameWorld.getMainTower(), Color.BLUE);
        drawTower(gameWorld.getEnemyTower(), Color.RED);
        shapeRenderer.end();

        // Vẽ text
        SpriteBatch batch = (SpriteBatch) game.getBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        String info = "Player Gold: " + gameWorld.getEconomyManager().getGold()
            + " | AI Gold: " + aiController.getGold()
            + "\n[P]awn [M]iner [A]rcher [W]arrior   [SPACE] Pause   [F1] AI status";
        font.draw(batch, info, 10, mapPixelHeight - 10);
        batch.end();
    }

    private Color getEntityColor(BaseEntity e) {
        if (e instanceof Pawn) return Color.GREEN;
        if (e instanceof Miner) return Color.YELLOW;
        if (e instanceof Archer) return Color.CYAN;
        if (e instanceof Warrior) return Color.RED;
        if (e instanceof PawnHacHoa) return Color.ORANGE;
        if (e instanceof WarriorHacHoa) return Color.PURPLE; // đỏ sẫm
        if (e instanceof TNT) return Color.BROWN;
        return Color.WHITE; // fallback
    }
    // Phương thức dự phòng: kiểm tra phím trực tiếp từ Gdx.input
    private void handleDirectInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            System.out.println("Direct input: P pressed");
            gameWorld.spawnPawn();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            gameWorld.spawnMiner();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            gameWorld.spawnArcher();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            gameWorld.spawnWarrior();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            gameWorld.setPaused(!gameWorld.isPaused());
        }
    }

    private void drawTower(MainTower tower, Color color) {
        if (tower == null) return;
        shapeRenderer.setColor(color);
        float x = tower.getX();
        float y = tower.getY();
        shapeRenderer.circle(x, y, 20);
    }

    @Override
    public void dispose() {
        gameWorld.dispose();
        shapeRenderer.dispose();
        font.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
    }
}
