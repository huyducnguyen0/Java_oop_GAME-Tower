package com.hust.towerdefence.Model.Managers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

public class MapManager {
    // Kích thước bản đồ (số ô)
    private TiledMap tiledMap;
    private int mapWidth, mapHeight;

    private int tileSize;

    private TileType[][] tiles;
    // Danh sách các điểm waypoint (theo thứ tự từ START đến END)
    private Array<Vector2> waypoints;   // tọa độ thế giới (pixel) của từng điểm chính
    private Vector2 playerMainTower;    // tháp chính của người chơi
    private Vector2 enemyMainTower;     // tháp chính của địch
    private Array<Vector2> playerTowers; // các tháp phụ của người chơi (không bao gồm tháp chính)
    private Array<Vector2> enemyTowers;  // các tháp phụ của địch
    // Điểm start và end (tọa độ ô)
    private Vector2 startGridPos;
    private Vector2 endGridPos;

    public MapManager(String mapPath) {
        loadMap(mapPath);
        extractWaypoints();
        extractTowerPositions();

    }
    private void loadMap(String mapPath) {
        try {
            tiledMap = new TmxMapLoader().load(mapPath);
        } catch (Exception e) {
            throw new RuntimeException("Không thể load map: " + mapPath, e);

        }
        tileSize = tiledMap.getProperties().get("tilewidth", Integer.class);
        mapWidth = tiledMap.getProperties().get("width", Integer.class);
        mapHeight = tiledMap.getProperties().get("height", Integer.class);
    }



    private void extractWaypoints() {
        waypoints = new Array<>();
        MapLayer waypointLayer = tiledMap.getLayers().get("waypoint");
        if (waypointLayer == null) {
            System.err.println("MapManager: Không tìm thấy layer 'waypoint'.");
            return;
        }

        Map<String, Vector2> pointMap = new HashMap<>();
        for (MapObject obj : waypointLayer.getObjects()) {
            if (!(obj instanceof PointMapObject)) continue;
            PointMapObject pointObj = (PointMapObject) obj;
            String name = obj.getName();
            if (name == null || !name.matches("p\\d+")) continue;

            pointMap.put(name, pointObj.getPoint());
        }

        // Sắp xếp theo số thứ tự
        int index = 0;
        while (pointMap.containsKey("p" + index)) {
            waypoints.add(pointMap.get("p" + index));
            index++;
        }

        if (waypoints.size == 0) {
            System.err.println("MapManager: Không tìm thấy waypoint nào (p0, p1,...).");
        }
    }



    private void extractTowerPositions() {
        playerTowers = new Array<>();
        enemyTowers = new Array<>();

        // Player tower group
        MapLayer playerLayer = tiledMap.getLayers().get("towermain");
        if (playerLayer != null) {
            for (MapObject obj : playerLayer.getObjects()) {
                if (!(obj instanceof RectangleMapObject)) continue;
                RectangleMapObject rect = (RectangleMapObject) obj;
                String name = obj.getName();
                Vector2 pos = new Vector2(rect.getRectangle().x, rect.getRectangle().y);
                if ("maintower".equals(name)) {
                    playerMainTower = pos;
                } else {
                    playerTowers.add(pos);
                }
            }
        } else {
            System.err.println("MapManager: Không tìm thấy layer 'towermain'.");
        }

        // Enemy tower group
        MapLayer enemyLayer = tiledMap.getLayers().get("towerdich");
        if (enemyLayer != null) {
            for (MapObject obj : enemyLayer.getObjects()) {
                if (!(obj instanceof RectangleMapObject)) continue;
                RectangleMapObject rect = (RectangleMapObject) obj;
                String name = obj.getName();
                Vector2 pos = new Vector2(rect.getRectangle().x, rect.getRectangle().y);
                if ("enemymaintower".equals(name)) {
                    enemyMainTower = pos;
                } else {
                    enemyTowers.add(pos);
                }
            }
        } else {
            System.err.println("MapManager: Không tìm thấy layer 'towerdich'.");
        }

        // Fallback nếu không tìm thấy
        if (playerMainTower == null) playerMainTower = new Vector2(0, 0);
        if (enemyMainTower == null) enemyMainTower = new Vector2(0, 0);
    }

    public Array<Vector2> getWaypoints() {
        Array<Vector2> copy = new Array<>();
        for (Vector2 wp : waypoints) copy.add(wp.cpy());
        return copy;
    }

    /** Vị trí tháp chính của người chơi (pixel) */
    public Vector2 getPlayerCastlePosition() {
        return playerMainTower.cpy();
    }

    /** Vị trí tháp chính của địch (pixel) */
    public Vector2 getEnemyBasePosition() {
        return enemyMainTower.cpy();
    }

    /** Danh sách tháp phụ của người chơi (các tower cố định, không phải tháp chính) */
    public Array<Vector2> getPlayerTowers() {
        Array<Vector2> copy = new Array<>();
        for (Vector2 t : playerTowers) copy.add(t.cpy());
        return copy;
    }

    /** Danh sách tháp phụ của địch */
    public Array<Vector2> getEnemyTowers() {
        Array<Vector2> copy = new Array<>();
        for (Vector2 t : enemyTowers) copy.add(t.cpy());
        return copy;
    }

    /** Chuyển đổi tọa độ pixel → ô lưới (grid) */
    public Vector2 worldToGrid(Vector2 worldPos) {
        int gx = (int)(worldPos.x / tileSize);
        int gy = (int)(worldPos.y / tileSize);
        gx = Math.max(0, Math.min(mapWidth - 1, gx));
        gy = Math.max(0, Math.min(mapHeight - 1, gy));
        return new Vector2(gx, gy);
    }

    /** Chuyển đổi ô lưới → tọa độ pixel (tâm ô) */
    public Vector2 gridToWorld(Vector2 gridPos) {
        return new Vector2(gridPos.x * tileSize + tileSize / 2f,
            gridPos.y * tileSize + tileSize / 2f);
    }

    // Getters thuần túy
    public TiledMap getTiledMap() { return tiledMap; }
    public int getTileSize() { return tileSize; }
    public int getMapWidth() { return mapWidth; }
    public int getMapHeight() { return mapHeight; }

    /** Giải phóng tài nguyên TiledMap */
    public void dispose() {
        if (tiledMap != null) tiledMap.dispose();
    }
}

