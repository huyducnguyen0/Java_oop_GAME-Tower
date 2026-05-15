package com.hust.towerdefence.Model;

import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Tower.MainTower;
import com.hust.towerdefence.Model.Managers.EntityManager;
import com.hust.towerdefence.Model.Managers.MapManager;
import com.hust.towerdefence.Model.Managers.EconomyManager;
import com.hust.towerdefence.Model.Systems.MovementSystem;
import com.hust.towerdefence.Model.Systems.TargetingSystem;
import com.hust.towerdefence.Model.Systems.AttackSystem;
import com.hust.towerdefence.Model.Systems.EffectSystem;
import com.hust.towerdefence.Model.Systems.PathfindingSystem;
public class GameWorld {
    // Trạng thái game
    public enum GameState { PLAYING, PAUSED, GAME_OVER, VICTORY }
    private GameState state;

    // Managers
    private final EntityManager entityManager;
    private final MapManager mapManager;
    private final EconomyManager economyManager;

    // Systems (theo thứ tự ưu tiên cập nhật)
    private final MovementSystem movementSystem;
    private final TargetingSystem targetingSystem;
    private final AttackSystem attackSystem;
    private final HealthSystem healthSystem;
    private final EffectSystem effectSystem;
    private final PathfindingSystem pathfindingSystem;

    // AI Controller (dùng cho enemy nếu cần)
    private final AIController aiController;

    /**
     * Khởi tạo GameWorld với các tham số cơ bản.
     * @param mapWidth  chiều rộng bản đồ (số tile)
     * @param mapHeight chiều cao bản đồ (số tile)
     */
    public GameWorld(int mapWidth, int mapHeight) {
        this.state = GameState.PLAYING;

        // Khởi tạo Managers
        this.mapManager = new MapManager(mapWidth, mapHeight);
        this.entityManager = new EntityManager();
        this.economyManager = new EconomyManager();

        // Khởi tạo AI (có thể cấu hình chiến lược)
        this.aiController = new AIController();

        // Khởi tạo Systems với tham chiếu đến GameWorld này
        this.movementSystem = new MovementSystem(this);
        this.targetingSystem = new TargetingSystem(this);
        this.attackSystem = new AttackSystem(this);
        this.healthSystem = new HealthSystem(this);
        this.effectSystem = new EffectSystem(this);
        this.pathfindingSystem = new PathfindingSystem(this);

        // TODO: Thiết lập bản đồ, wave, nhà chính, ... (gọi từ bên ngoài hoặc phương thức init)
    }

    /**
     * Cập nhật toàn bộ thế giới game mỗi frame.
     * @param delta thời gian trôi qua từ frame trước (giây)
     */
    public void update(float delta) {
        if (state != GameState.PLAYING) return;

        // 1. Di chuyển tất cả entity có path/velocity
        movementSystem.update(delta);

        // 2. Cập nhật mục tiêu cho các entity có khả năng tấn công
        targetingSystem.update(delta);

        // 3. Thực hiện tấn công khi đủ điều kiện
        attackSystem.update(delta);

        // 4. Xử lý hiệu ứng (làm chậm, đốt, ...) - ưu tiên cuối vì có thể ảnh hưởng đến các frame sau
        effectSystem.update(delta);

        // 5. Dọn dẹp entity đã chết (có thể gọi sau HealthSystem nếu cần)
        entityManager.removeMarkedEntities();

        // 6. Kiểm tra điều kiện thắng/thua (có thể tách ra system riêng)
        if (healthSystem.isGameOver()) {
            state = GameState.GAME_OVER;
        }
    }

    // --------------------- Các phương thức truy cập Manager ---------------------
    public EntityManager getEntityManager() { return entityManager; }
    public MapManager getMapManager() { return mapManager; }
    public EconomyManager getEconomyManager() { return economyManager; }

    // --------------------- Các phương thức truy cập System ---------------------
    public MovementSystem getMovementSystem() { return movementSystem; }
    public TargetingSystem getTargetingSystem() { return targetingSystem; }
    public AttackSystem getAttackSystem() { return attackSystem; }
    public HealthSystem getHealthSystem() { return healthSystem; }
    public EffectSystem getEffectSystem() { return effectSystem; }
    public PathfindingSystem getPathfindingSystem() { return pathfindingSystem; }

    // --------------------- Quản lý trạng thái game ---------------------
    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public boolean isPlaying() { return state == GameState.PLAYING; }

    /**
     * Được gọi khi nhà chính bị phá hủy.
     */
    public void onMainTowerDestroyed() {
        state = GameState.GAME_OVER;
        // Có thể kích hoạt hiệu ứng kết thúc game
    }

    /**
     * Khởi tạo hoặc reset thế giới (nếu cần chơi lại).
     */
    public void reset() {
        entityManager.clearAll();
        economyManager.reset();
        mapManager.reset(); // nếu có thay đổi địa hình
        state = GameState.PLAYING;
    }
}
