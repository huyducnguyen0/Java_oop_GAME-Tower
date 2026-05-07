package com.hust.towerdefence.Model.Manager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;

/**
 * Quản lý lộ trình di chuyển cho các thực thể trong trò chơi.
 * Hỗ trợ tạo đường đi hai chiều giữa nhà Ta và nhà Địch dựa trên tọa độ ô (Tile).
 */
public class PathManager {

    // --- CẤU HÌNH HỆ THỐNG (Khớp với thông số map 50x30, Tile 64x64) ---
    private static final int TILE_SIZE = 64;           // Kích thước mỗi ô gạch
    private static final int MAP_HEIGHT_IN_TILES = 30; // Chiều cao map tính theo số ô

    /**
     * Lấy lộ trình từ Nhà Ta (Base) sang Nhà Địch (Enemy).
     */
    public static LinePath<Vector2> getPathBaseToEnemy() {
        return createPathFromTiles(getRawTileCoords(), false);
    }

    /**
     * Lấy lộ trình ngược lại từ Nhà Địch (Enemy) về Nhà Ta (Base).
     */
    public static LinePath<Vector2> getPathEnemyToBase() {
        return createPathFromTiles(getRawTileCoords(), true);
    }

    /**
     * Chuyển đổi mảng tọa độ thô thành đối tượng LinePath của LibGDX AI.
     *
     * @param tileCoords Mảng các điểm {x, y} theo đơn vị ô.
     * @param isReversed Nếu true, sẽ đảo ngược lộ trình.
     */
    private static LinePath<Vector2> createPathFromTiles(int[][] tileCoords, boolean isReversed) {
        Array<Vector2> waypoints = new Array<>();

        if (isReversed) {
            for (int i = tileCoords.length - 1; i >= 0; i--) {
                waypoints.add(convertToWorldSpace(tileCoords[i][0], tileCoords[i][1]));
            }
        } else {
            for (int[] coord : tileCoords) {
                waypoints.add(convertToWorldSpace(coord[0], coord[1]));
            }
        }

        // Trả về LinePath dạng "hở" (isOpen = false) vì đây là đường thẳng từ A đến B
        return new LinePath<>(waypoints, true);
    }

    /**
     * Chuyển đổi tọa độ ô (Tiled) sang tọa độ thế giới (LibGDX).
     * Đảm bảo thực thể đi vào chính giữa ô và khớp với hệ trục Y-up.
     */
    private static Vector2 convertToWorldSpace(int tileX, int tileY) {
        // X = (Số ô * kích thước) + offset nửa ô để lính đi giữa đường
        float worldX = (tileX * TILE_SIZE) + (TILE_SIZE / 2f);

        // Y = (Tổng chiều cao - chỉ số ô hiện tại - 1) * kích thước + offset nửa ô
        // Công thức này xử lý việc Tiled tính từ trên xuống, LibGDX tính từ dưới lên.
        float worldY = (MAP_HEIGHT_IN_TILES - tileY - 1) * TILE_SIZE + (TILE_SIZE / 2f);

        return new Vector2(worldX, worldY);
    }

    /**
     * Dữ liệu tọa độ thô được trích xuất từ file mapreal.tmx.
     */
    private static int[][] getRawTileCoords() {
        return new int[][]{
            {45, 12}, {45, 20}, {45, 21}, {44, 21}, {39, 21}, {39, 22},
            {36, 22}, {35, 22}, {35, 23}, {35, 24}, {34, 24}, {31, 24}, {30, 24}, {30, 25},
            {29, 25}, {13, 25}, {12, 25}, {12, 24}, {11, 24}, {7, 24}, {6, 24}, {6, 23}, {6, 9}
        };
    }
}
