package com.hust.towerdefence.Model.Managers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


public class MapManager {
    // Kích thước bản đồ (số ô)
    private int width, height;
    // Kích thước mỗi ô pixel (thường 32x32 hoặc 64x64)
    private int tileSize;

    // Lưới dữ liệu
    private TileType[][] tiles;

    // Danh sách các điểm waypoint (theo thứ tự từ START đến END)
    private Array<Vector2> waypoints;   // tọa độ thế giới (pixel) của từng điểm chính

    // Điểm start và end (tọa độ ô)
    private Vector2 startGridPos;
    private Vector2 endGridPos;

    public MapManager(int width, int height, int tileSize) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        tiles = new TileType[width][height];
        waypoints = new Array<>();
        startGridPos = new Vector2();
        endGridPos = new Vector2();
    }

    /**
     * Khởi tạo bản đồ từ file (bạn sẽ đọc từ TiledMap hoặc file txt)
     * Tạm thời tạo bản đồ mẫu.
     */
    public void loadMapFromFile(String mapPath) {
        // TODO: Đọc file, ví dụ đọc mảng int từ .txt hoặc dùng LibGDX TiledMap
        // Ở đây tạo bản đồ demo 10x10:
        createDemoMap();
        // Sau khi có tiles, tính toán waypoints tự động hoặc đọc từ file
        calculateWaypoints();
    }

    private void createDemoMap() {
        // Khởi tạo tất cả là ROAD
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = TileType.ROAD;
            }
        }
        // Đặt START
        startGridPos.set(0, 2);
        tiles[(int)startGridPos.x][(int)startGridPos.y] = TileType.START;
        // Đặt END
        endGridPos.set(width - 1, height - 3);
        tiles[(int)endGridPos.x][(int)endGridPos.y] = TileType.END;
        // Rải một vài platform để xây tháp
        tiles[3][4] = TileType.TOWER_PLATFORM;
        tiles[5][6] = TileType.TOWER_PLATFORM;
        // ...
    }

    /**
     * Tính toán các waypoint dọc theo đường đi (thường là các điểm uốn)
     * Bạn có thể dùng BFS từ start đến end, sau đó lấy tất cả các ô trên đường đi.
     * Tối ưu hơn: chỉ lấy các điểm mà hướng di chuyển thay đổi.
     */
    private void calculateWaypoints() {
        waypoints.clear();
        // Giả định: dùng pathfinding đơn giản (BFS) để tìm lộ trình các ô từ start đến end
        // Sau đó chuyển các ô đó thành tọa độ thế giới (pixel) và thêm vào waypoints.
        // Dưới đây là giả lập:
        waypoints.add(gridToWorld(startGridPos));
        // ... thêm các điểm trung gian
        waypoints.add(gridToWorld(endGridPos));
    }

    // Chuyển đổi tọa độ ô -> tọa độ pixel (trung tâm ô)
    public Vector2 gridToWorld(Vector2 gridPos) {
        return new Vector2(gridPos.x * tileSize + tileSize/2f,
            gridPos.y * tileSize + tileSize/2f);
    }

    // Chuyển đổi tọa độ pixel -> tọa độ ô
    public Vector2 worldToGrid(Vector2 worldPos) {
        int gridX = (int)(worldPos.x / tileSize);
        int gridY = (int)(worldPos.y / tileSize);
        // Clamp trong biên
        gridX = Math.max(0, Math.min(width - 1, gridX));
        gridY = Math.max(0, Math.min(height - 1, gridY));
        return new Vector2(gridX, gridY);
    }

    // Kiểm tra một ô có xây được tháp không
    public boolean canBuildTowerAt(int gridX, int gridY) {
        if (!isWithinBounds(gridX, gridY)) return false;
        TileType tile = tiles[gridX][gridY];
        return tile.isBuildable();
    }

    // Kiểm tra ô có thể đi qua (cho enemy)
    public boolean isWalkable(int gridX, int gridY) {
        if (!isWithinBounds(gridX, gridY)) return false;
        return tiles[gridX][gridY].isWalkable();
    }

    // Lấy loại tile
    public TileType getTile(int gridX, int gridY) {
        return tiles[gridX][gridY];
    }

    // Lấy danh sách waypoint (sao chép để tránh chỉnh sửa ngoài ý muốn)
    public Array<Vector2> getWaypoints() {
        Array<Vector2> copy = new Array<>();
        for (Vector2 wp : waypoints) {
            copy.add(wp.cpy());
        }
        return copy;
    }

    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    // Getters
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getTileSize() { return tileSize; }
    public Vector2 getStartGridPos() { return startGridPos.cpy(); }
    public Vector2 getEndGridPos() { return endGridPos.cpy(); }

    /**
     * Lấy vị trí Player Castle (Start Point của map)
     */
    public Vector2 getPlayerCastlePosition() {
        return gridToWorld(startGridPos);
    }

    /**
     * Lấy vị trí Enemy Base (End Point của map)
     */
    public Vector2 getEnemyBasePosition() {
        return gridToWorld(endGridPos);
    }
}
