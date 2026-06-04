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

/**
 * Màn hình mô phỏng trận đấu chính (Gameplay Screen).
 * Quản lý tương tác giữa bản đồ TiledMap, thực thể lính/tháp, AI Controller và hệ thống hiển thị HUD.
 * ĐÃ FIX LỖI: Reset trạng thái màn chơi mỗi khi bắt đầu lại!
 */
public class DemoModelScreen extends ScreenAdapter {
    // ==========================================
    // CẤU HÌNH THÔNG SỐ ĐẦU VÀO MẶC ĐỊNH
    // ==========================================
    private static final String MAP_PATH = "Game_Map.tmx";
    private static final int INITIAL_GOLD = 1000;
    private static final int MAX_GOLD = 2000;
    private static final int AI_LEVEL = 2;

    // Cấu hình màu sắc hiển thị đường viền khi chọn công trình
    private static final Color SELECTED_PURCHASE_BUILDING = new Color(0.15f, 0.8f, 1f, 1f);
    private static final Color SELECTED_NON_PURCHASE_BUILDING = new Color(1f, 0.76f, 0.15f, 1f);
    private static final float SELECTION_OUTLINE_THICKNESS = 4f;

    // ==========================================
    // THÀNH PHẦN LOGIC VÀ ĐIỀU KHIỂN HỆ THỐNG
    // ==========================================
    private final MainGame game;
    private GameWorld gameWorld;
    private AIController aiController;

    // ==========================================
    // THÀNH PHẦN ĐỒ HỌA VÀ RENDER GIAO DIỆN
    // ==========================================
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

    // ĐÃ XÓA BIẾN CỜ KIỂM TRA isInitialized ĐỂ ÉP GAME RESET

    public DemoModelScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        // [FIX BUG RESET GIAO DIỆN VÀ THÔNG SỐ]
        // Đảm bảo dọn dẹp rác (bộ nhớ cũ) nếu người chơi chơi lại ván 2, ván 3... để không tràn RAM
        if (gameWorld != null) {
            dispose();
        }

        // 1. Khởi tạo thế giới game thực tế (Sẽ gọi lại hàm khởi tạo Gold = 1000, Máu = gốc)
        gameWorld = new GameWorld(MAP_PATH, INITIAL_GOLD, MAX_GOLD);
        System.out.println("GameWorld created. Player gold: " + gameWorld.getEconomyManager().getGold());

        EntityManager entityManager = gameWorld.getEntityManager();
        MapManager mapManager = gameWorld.getMapManager();
        MainTower playerTower = gameWorld.getMainTower();
        MainTower enemyTower = gameWorld.getEnemyTower();

        // 2. Khởi tạo lại thực thể AI đối thủ
        aiController = new AIController(AI_LEVEL, entityManager, mapManager, playerTower, enemyTower);
        System.out.println("AI initialized. AI gold: " + aiController.getGold());

        // 3. Tính toán kích thước Pixel thực tế của toàn map để cấu hình góc nhìn Camera
        mapPixelWidth = mapManager.getMapWidth() * mapManager.getTileSize();
        mapPixelHeight = mapManager.getMapHeight() * mapManager.getTileSize();
        System.out.println("Map pixel size: " + mapPixelWidth + "x" + mapPixelHeight);

        // 4. Thiết lập thông số Camera bao trùm toàn bộ Map
        camera = new OrthographicCamera();
        camera.setToOrtho(false, mapPixelWidth, mapPixelHeight);
        camera.position.set(mapPixelWidth / 2f, mapPixelHeight / 2f, 0);
        camera.update();

        // 5. Nạp lại bản đồ đồ họa từ MapManager
        TiledMap tiledMap = mapManager.getTiledMap();
        if (tiledMap != null) {
            mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            System.out.println("MapRenderer created.");
        } else {
            System.err.println("Khong the load TiledMap tu " + MAP_PATH);
        }

        // 6. Khởi tạo ĐỒNG LOẠT mới tinh các bộ kết xuất đồ họa giao diện và thực thể
        shapeRenderer = new ShapeRenderer();
        gameHud = new GameHud(gameWorld, game);
        worldHudRenderer = new WorldHudRenderer();
        mainTowerRenderer = new MainTowerRenderer();
        defenseTowerRenderer = new DefenseTowerRenderer();
        unitRenderer = new UnitRenderer();
        combatEffectRenderer = new CombatEffectRenderer();

        // 7. ĐỒNG BỘ INPUT: Thiết lập lại danh sách bộ nhận diện sự kiện đa tầng
        if (gameHud != null && gameHud.getStage() != null) {
            game.setInputProcessors(gameHud.getStage(), new InputAdapter() {
                @Override
                public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                    if (button != Input.Buttons.LEFT) return false;
                    return handleWorldClick(screenX, screenY);
                }

                @Override
                public boolean keyDown(int keycode) {
                    return handleInputKey(keycode);
                }
            });
            System.out.println("Input Multiplexer initialized successfully.");
        }
    }

    /**
     * Xử lý chuyển đổi tọa độ chuột và kiểm tra va chạm điểm chọn trên bản đồ.
     */
    private boolean handleWorldClick(int screenX, int screenY) {
        Vector3 world = camera.unproject(new Vector3(screenX, screenY, 0f));
        BuildingZone hitZone = gameWorld.getMapManager().findInteractiveZone(world.x, world.y);

        if (hitZone == null) {
            selectedBuildingZone = null;
            gameHud.setSelectedBuilding((BuildingZone) null);
            System.out.println("Click vao dat trong -> An panel");
            return true;
        }

        selectedBuildingZone = hitZone;
        gameHud.setSelectedBuilding(hitZone);
        System.out.println("Selected building: " + hitZone.getName());
        return true;
    }

    /**
     * Hệ thống phím tắt Debug/Spawn nhanh các thực thể lính.
     */
    private boolean handleInputKey(int keycode) {
        switch (keycode) {
            case Input.Keys.P:
                gameWorld.spawnPawn();
                return true;
            case Input.Keys.M:
                gameWorld.spawnMiner();
                return true;
            case Input.Keys.A:
                gameWorld.spawnArcher();
                return true;
            case Input.Keys.W:
                gameWorld.spawnWarrior();
                return true;
            case Input.Keys.SPACE:
                gameWorld.setPaused(!gameWorld.isPaused());
                return true;
            case Input.Keys.F1:
                System.out.printf("Gold: %.1f  Level: %d\n", aiController.getGold(), AI_LEVEL);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void render(float delta) {
        gameWorld.update(delta);
        if (!gameWorld.isPaused()) {
            aiController.update(delta);
        }


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

    /**
     * Vẽ 4 cạnh hình chữ nhật làm đường viền bao quanh Vùng đất đang chọn.
     */
    private void drawSelectedBuildingOutline() {
        if (selectedBuildingZone == null) return;

        Rectangle bounds = selectedBuildingZone.getBounds();

        shapeRenderer.setColor(selectedBuildingZone.isPurchaseBuilding()
            ? SELECTED_PURCHASE_BUILDING
            : SELECTED_NON_PURCHASE_BUILDING);

        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, SELECTION_OUTLINE_THICKNESS);
        shapeRenderer.rect(bounds.x, bounds.y + bounds.height - SELECTION_OUTLINE_THICKNESS, bounds.width, SELECTION_OUTLINE_THICKNESS);
        shapeRenderer.rect(bounds.x, bounds.y, SELECTION_OUTLINE_THICKNESS, bounds.height);
        shapeRenderer.rect(bounds.x + bounds.width - SELECTION_OUTLINE_THICKNESS, bounds.y, SELECTION_OUTLINE_THICKNESS, bounds.height);
    }

    @Override
    public void resize(int width, int height) {
        if (gameHud != null) {
            gameHud.resize(width, height);
        }
    }

    @Override
    public void hide() {
        game.setInputProcessors(new com.badlogic.gdx.InputProcessor[0]);
    }

    @Override
    public void dispose() {
        // [BẢO VỆ CHỐNG TRÀN RAM CỰC KỲ QUAN TRỌNG KHI RESET NHIỀU LẦN]
        if (gameWorld != null) {
            gameWorld.dispose();
            gameWorld = null; // Gán bằng null để rác hệ thống tự dọn dẹp triệt để
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
            shapeRenderer = null;
        }
        if (gameHud != null) {
            gameHud.dispose();
            gameHud = null;
        }
        if (mainTowerRenderer != null) {
            mainTowerRenderer.dispose();
            mainTowerRenderer = null;
        }
        if (defenseTowerRenderer != null) {
            defenseTowerRenderer.dispose();
            defenseTowerRenderer = null;
        }
        if (unitRenderer != null) {
            unitRenderer.dispose();
            unitRenderer = null;
        }
        if (combatEffectRenderer != null) {
            combatEffectRenderer.dispose();
            combatEffectRenderer = null;
        }
        if (mapRenderer != null) {
            mapRenderer.dispose();
            mapRenderer = null;
        }
    }
}
