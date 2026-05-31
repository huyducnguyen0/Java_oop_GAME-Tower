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
 */
public class DemoModelScreen extends ScreenAdapter {
    // ==========================================
    // CẤU HÌNH THÔNG SỐ ĐẦU VÀO MẶC ĐỊNH
    // ==========================================
    private static final String MAP_PATH = "mapreal.tmx";                         // Đường dẫn file cấu trúc bản đồ Tiled
    private static final int INITIAL_GOLD = 1000;                                 // Lượng vàng khởi đầu của Người chơi
    private static final int MAX_GOLD = 2000;                                     // Giới hạn vàng tối đa trong màn chơi
    private static final int AI_LEVEL = 2;                                        // Cấp độ thông minh của máy (AI)

    // Cấu hình màu sắc hiển thị đường viền khi chọn công trình
    private static final Color SELECTED_PURCHASE_BUILDING = new Color(0.15f, 0.8f, 1f, 1f);     // Màu xanh khi chọn ô được phép mua
    private static final Color SELECTED_NON_PURCHASE_BUILDING = new Color(1f, 0.76f, 0.15f, 1f); // Màu vàng cho ô không thể tương tác mua
    private static final float SELECTION_OUTLINE_THICKNESS = 4f;                  // Độ dày nét vẽ đường viền (Pixel)

    // ==========================================
    // THÀNH PHẦN LOGIC VÀ ĐIỀU KHIỂN HỆ THỐNG
    // ==========================================
    private final MainGame game;                                                  // Tham chiếu đến lõi quản lý Game chính
    private GameWorld gameWorld;                                                  // Thế giới chứa toàn bộ logic, vật lý, kinh tế trận đấu
    private AIController aiController;                                            // Bộ não điều khiển quái vật và chiến thuật của Máy

    // ==========================================
    // THÀNH PHẦN ĐỒ HỌA VÀ RENDER GIAO DIỆN
    // ==========================================
    private OrthographicCamera camera;                                            // Máy ảnh 2D quản lý góc nhìn bản đồ thế giới
    private ShapeRenderer shapeRenderer;                                          // Bộ vẽ mô hình hình học (dùng vẽ thanh máu, đường viền)
    private OrthogonalTiledMapRenderer mapRenderer;                               // Bộ kết xuất hình ảnh bản đồ từ file TMX
    private GameHud gameHud;                                                      // Sân khấu UI lớp trên (Panel mua lính, nút bấm)
    private WorldHudRenderer worldHudRenderer;                                    // Vẽ các thanh chỉ số hiển thị trực tiếp trong thế giới (Healthbar)
    private MainTowerRenderer mainTowerRenderer;                                  // Kết xuất hình ảnh Nhà chính ta và địch
    private DefenseTowerRenderer defenseTowerRenderer;                            // Kết xuất hình ảnh hệ thống tháp phòng thủ dọc đường
    private UnitRenderer unitRenderer;                                            // Kết xuất hình ảnh các thực thể lính di chuyển
    private CombatEffectRenderer combatEffectRenderer;                            // Quản lý và vẽ hiệu ứng chiến đấu (Tia đạn, vụ nổ)
    private BuildingZone selectedBuildingZone;                                    // Lưu trữ vùng đất hiện tại đang được người chơi click chọn

    private int mapPixelWidth;                                                    // Tổng chiều rộng bản đồ tính theo Pixel
    private int mapPixelHeight;                                                   // Tổng chiều cao bản đồ tính theo Pixel
    private boolean isInitialized = false;                                        // Cờ đánh dấu trạng thái khởi tạo dữ liệu đơn lẻ (Singleton)

    public DemoModelScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        // KIỂM TRA: Chỉ khởi tạo các đối tượng nặng đúng 1 lần đầu tiên để giữ Cache ổn định
        if (!isInitialized) {
            // Khởi tạo thế giới game thực tế
            gameWorld = new GameWorld(MAP_PATH, INITIAL_GOLD, MAX_GOLD);
            System.out.println("GameWorld created. Player gold: " + gameWorld.getEconomyManager().getGold());

            EntityManager entityManager = gameWorld.getEntityManager();
            MapManager mapManager = gameWorld.getMapManager();
            MainTower playerTower = gameWorld.getMainTower();
            MainTower enemyTower = gameWorld.getEnemyTower();

            // Khởi tạo thực thể AI đối thủ
            aiController = new AIController(AI_LEVEL, entityManager, mapManager, playerTower, enemyTower);
            System.out.println("AI initialized. AI gold: " + aiController.getGold());

            // Tính toán kích thước Pixel thực tế của toàn map để cấu hình góc nhìn Camera
            mapPixelWidth = mapManager.getMapWidth() * mapManager.getTileSize();
            mapPixelHeight = mapManager.getMapHeight() * mapManager.getTileSize();
            System.out.println("Map pixel size: " + mapPixelWidth + "x" + mapPixelHeight);

            // Thiết lập thông số Camera bao trùm toàn bộ Map
            camera = new OrthographicCamera();
            camera.setToOrtho(false, mapPixelWidth, mapPixelHeight);
            camera.position.set(mapPixelWidth / 2f, mapPixelHeight / 2f, 0);
            camera.update();

            // Nạp bản đồ đồ họa từ MapManager
            TiledMap tiledMap = mapManager.getTiledMap();
            if (tiledMap != null) {
                mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
                System.out.println("MapRenderer created.");
            } else {
                System.err.println("Khong the load TiledMap tu " + MAP_PATH);
            }

            // Khởi tạo đồng loạt các bộ kết xuất đồ họa giao diện và thực thể
            shapeRenderer = new ShapeRenderer();
            gameHud = new GameHud(gameWorld);
            worldHudRenderer = new WorldHudRenderer();
            mainTowerRenderer = new MainTowerRenderer();
            defenseTowerRenderer = new DefenseTowerRenderer();
            unitRenderer = new UnitRenderer();
            combatEffectRenderer = new CombatEffectRenderer();

            isInitialized = true;
        }

        // ĐỒNG BỘ INPUT: Thiết lập danh sách bộ nhận diện sự kiện đa tầng mỗi khi màn hình hiển thị
        // Tầng 1: Click trúng nút UI trên Stage của gameHud xử lý trước.
        // Tầng 2: Nếu click trượt UI, InputAdapter phía sau sẽ đón để tính toán va chạm click chọn ô đất dưới Map.
        if (gameHud != null && gameHud.getStage() != null) {
            game.setInputProcessors(gameHud.getStage(), new InputAdapter() {
                @Override
                public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                    if (button != Input.Buttons.LEFT) return false; // Chỉ xử lý cú click chuột trái trái
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
        // Giải mã tọa độ click từ màn hình phẳng (Screen Coords) về tọa độ không gian Game (World Coords) dựa theo Camera
        Vector3 world = camera.unproject(new Vector3(screenX, screenY, 0f));

        // Quét tìm xem vị trí click có nằm đè lên ô đất tương tác nào không
        BuildingZone hitZone = gameWorld.getMapManager().findInteractiveZone(world.x, world.y);

        if (hitZone == null) {
            // Click trượt ra bãi trống: Xóa vùng chọn cũ và ẩn Panel mua/thông tin tháp đi
            selectedBuildingZone = null;
            gameHud.setSelectedBuilding((BuildingZone) null);
            System.out.println("Click vao dat trong -> An panel");
            return true; // Trả về true để thông báo đã nuốt sự kiện click trống
        }

        // Click trúng ô xây dựng hợp lệ: Lưu vùng chọn và truyền tín hiệu sang HUD để mở Panel tương ứng
        selectedBuildingZone = hitZone;
        gameHud.setSelectedBuilding(hitZone);
        System.out.println("Selected building: " + hitZone.getName());
        return true;
    }

    /**
     * Hệ thống phím tắt Debug/Spawn nhanh các thực thể lính trong trận đấu thử nghiệm.
     */
    private boolean handleInputKey(int keycode) {
        switch (keycode) {
            case Input.Keys.P: // Nhấn P: Triệu hồi quân Dân Quân (Pawn)
                gameWorld.spawnPawn();
                return true;
            case Input.Keys.M: // Nhấn M: Triệu hồi Phu Mỏ (Miner)
                gameWorld.spawnMiner();
                return true;
            case Input.Keys.A: // Nhấn A: Triệu hồi Xạ Thủ (Archer)
                gameWorld.spawnArcher();
                return true;
            case Input.Keys.W: // Nhấn W: Triệu hồi Chiến Binh (Warrior)
                gameWorld.spawnWarrior();
                return true;
            case Input.Keys.SPACE: // Nhấn SPACE: Tạm dừng / Tiếp tục trận đấu
                gameWorld.setPaused(!gameWorld.isPaused());
                return true;
            case Input.Keys.F1: // Nhấn F1: In trạng thái kinh tế hiện tại của AI đối thủ ra Console
                System.out.printf("Gold: %.1f  Level: %d\n", aiController.getGold(), AI_LEVEL);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void render(float delta) {
        // Cập nhật dòng chảy thời gian logic của Thế giới và AI trước khi vẽ đồ họa
        gameWorld.update(delta);
        aiController.update(delta);

        // Xóa sạch khung hình cũ bằng màu nền đen mờ chuẩn
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Lớp 1: Vẽ bản đồ nền TiledMap bám theo ma trận góc nhìn Camera
        if (mapRenderer != null) {
            mapRenderer.setView(camera);
            mapRenderer.render();
        }

        // Lớp 2: Vẽ các thành phần thực thể động (Nhà chính, Tháp thủ, Quân lính, Hiệu ứng đạn lạc)
        mainTowerRenderer.render(camera, gameWorld.getMainTower(), gameWorld.getEnemyTower());
        defenseTowerRenderer.render(camera, gameWorld.getEntityManager().getTowers());
        unitRenderer.render(camera, gameWorld.getEntityManager().getAllEntities(), delta);

        // Cập nhật và kết xuất chuỗi sự kiện va chạm hình ảnh chiến đấu
        combatEffectRenderer.addEvents(gameWorld.consumeCombatVisualEvents());
        combatEffectRenderer.render(camera, delta);

        // Lớp 3: Sử dụng ShapeRenderer để đổ màu lấp đầy (Filled) vẽ các thanh máu (Healthbars) và khung chọn ô đất
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        worldHudRenderer.drawUnitHealthBars(shapeRenderer, gameWorld.getEntityManager().getAllEntities());
        worldHudRenderer.drawTowerHealthBars(shapeRenderer, gameWorld.getMainTower(), gameWorld.getEnemyTower());
        worldHudRenderer.drawDefenseTowerHealthBars(shapeRenderer, gameWorld.getEntityManager().getTowers());

        // Vẽ khung viền highlight bao quanh ô đất đang được click chọn lên trên cùng
        drawSelectedBuildingOutline();
        shapeRenderer.end();

        // Lớp 4 (Trên cùng màn hình): Cập nhật trạng thái act và vẽ giao diện phẳng UI Stage (Bảng nút bấm, Panel nâng cấp)
        gameHud.act(delta, camera);
        gameHud.draw();
    }

    /**
     * Vẽ 4 cạnh hình chữ nhật làm đường viền bao quanh Vùng đất đang chọn để người chơi dễ nhận biết.
     */
    private void drawSelectedBuildingOutline() {
        if (selectedBuildingZone == null) return;

        Rectangle bounds = selectedBuildingZone.getBounds();

        // Phân loại màu sắc viền dựa theo tính chất vùng chọn (Được mua hay vùng cố định)
        shapeRenderer.setColor(selectedBuildingZone.isPurchaseBuilding()
            ? SELECTED_PURCHASE_BUILDING
            : SELECTED_NON_PURCHASE_BUILDING);

        // Vẽ Cạnh dưới
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, SELECTION_OUTLINE_THICKNESS);
        // Vẽ Cạnh trên
        shapeRenderer.rect(bounds.x, bounds.y + bounds.height - SELECTION_OUTLINE_THICKNESS, bounds.width, SELECTION_OUTLINE_THICKNESS);
        // Vẽ Cạnh trái
        shapeRenderer.rect(bounds.x, bounds.y, SELECTION_OUTLINE_THICKNESS, bounds.height);
        // Vẽ Cạnh phải
        shapeRenderer.rect(bounds.x + bounds.width - SELECTION_OUTLINE_THICKNESS, bounds.y, SELECTION_OUTLINE_THICKNESS, bounds.height);
    }

    @Override
    public void resize(int width, int height) {
        // Cập nhật lại cấu trúc Viewport của HUD giao diện tránh vỡ khung hình khi co giãn cửa sổ game
        if (gameHud != null) {
            gameHud.resize(width, height);
        }
    }

    @Override
    public void hide() {
        // Khi rời khỏi màn chơi (Ẩn screen), giải phóng hoàn toàn mảng Input để tránh lỗi loạn chuột đè màn hình sau
        game.setInputProcessors(new com.badlogic.gdx.InputProcessor[0]);
    }

    @Override
    public void dispose() {
        // Thực thi giải phóng có điều kiện toàn bộ bộ nhớ RAM/VRAM của cấu trúc thế giới và các bộ vẽ giao diện độc lập
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
