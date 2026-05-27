package com.hust.towerdefence.Model.Managers;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.Enemy;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Miner;

import java.util.HashMap;
import java.util.Map;

public class MapManager {
    private TiledMap tiledMap;
    private int mapWidth;
    private int mapHeight;
    private int tileSize;

    private TileType[][] tiles;
    private Array<Vector2> waypoints;
    private Array<Vector2> waypoints_miner;
    private Vector2 playerMainTower;
    private Vector2 enemyMainTower;
    private Array<Vector2> playerTowers;
    private Array<Vector2> enemyTowers;
    private Array<BuildingZone> playerBuildingZones;
    private Array<BuildingZone> playerTowerZones;
    private Array<BuildingZone> enemyTowerZones;
    private BuildingZone playerMainTowerZone;
    private BuildingZone enemyMainTowerZone;
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
            throw new RuntimeException("Khong the load map: " + mapPath, e);
        }
        tileSize = tiledMap.getProperties().get("tilewidth", Integer.class);
        mapWidth = tiledMap.getProperties().get("width", Integer.class);
        mapHeight = tiledMap.getProperties().get("height", Integer.class);
    }

    private void extractWaypoints() {
        waypoints = new Array<>();
        MapLayer waypointLayer = tiledMap.getLayers().get("waypoint");
        if (waypointLayer == null) {
            System.err.println("MapManager: Khong tim thay layer 'waypoint'.");
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

        int index = 0;
        while (pointMap.containsKey("p" + index)) {
            waypoints.add(pointMap.get("p" + index));
            index++;
        }
        if (waypoints.size == 0) {
            System.err.println("MapManager: Khong tim thay waypoint nao (p0, p1, ...).");
        }

        waypoints_miner = new Array<>();
        MapLayer waypointMinerLayer = tiledMap.getLayers().get("waypoint_miner");
        if (waypointMinerLayer == null) {
            System.err.println("MapManager: Khong tim thay layer 'waypoint_miner'.");
            return;
        }

        Map<String, Vector2> minerPointMap = new HashMap<>();
        for (MapObject obj : waypointMinerLayer.getObjects()) {
            if (!(obj instanceof PointMapObject)) continue;
            PointMapObject pointObj = (PointMapObject) obj;
            String name = obj.getName();
            if (name == null || !name.matches("path\\d+")) continue;
            minerPointMap.put(name, pointObj.getPoint());
        }

        int minerIndex = 1;
        while (minerPointMap.containsKey("path" + minerIndex)) {
            waypoints_miner.add(minerPointMap.get("path" + minerIndex));
            minerIndex++;
        }

        if (waypoints_miner.size == 0) {
            System.err.println("MapManager: Khong tim thay waypoint_miner nao.");
        }
    }

    private void extractTowerPositions() {
        playerTowers = new Array<>();
        enemyTowers = new Array<>();
        playerBuildingZones = new Array<>();
        playerTowerZones = new Array<>();
        enemyTowerZones = new Array<>();

        MapLayer playerLayer = tiledMap.getLayers().get("towermain");
        if (playerLayer != null) {
            for (MapObject obj : playerLayer.getObjects()) {
                if (!(obj instanceof RectangleMapObject)) continue;
                RectangleMapObject rectObject = (RectangleMapObject) obj;
                Rectangle rect = rectObject.getRectangle();
                String name = obj.getName();
                if (name == null) continue;

                BuildingZone zone = new BuildingZone(name, rect.x, rect.y, rect.width, rect.height);
                playerBuildingZones.add(zone);
                Vector2 pos = zone.getCenter();
                if ("maintower".equals(name)) {
                    playerMainTowerZone = zone;
                    playerMainTower = pos;
                } else if (isPlayerDefenseTower(name)) {
                    playerTowerZones.add(zone);
                    playerTowers.add(pos);
                }
            }
        } else {
            System.err.println("MapManager: Khong tim thay layer 'towermain'.");
        }

        MapLayer enemyLayer = tiledMap.getLayers().get("towerdich");
        if (enemyLayer != null) {
            for (MapObject obj : enemyLayer.getObjects()) {
                if (!(obj instanceof RectangleMapObject)) continue;
                RectangleMapObject rectObject = (RectangleMapObject) obj;
                Rectangle rect = rectObject.getRectangle();
                String name = obj.getName();
                BuildingZone zone = new BuildingZone(name, rect.x, rect.y, rect.width, rect.height);
                Vector2 pos = zone.getCenter();
                if ("enemymaintower".equals(name)) {
                    enemyMainTowerZone = zone;
                    enemyMainTower = pos;
                } else if (isEnemyDefenseTower(name)) {
                    enemyTowerZones.add(zone);
                    enemyTowers.add(pos);
                }
            }
        } else {
            System.err.println("MapManager: Khong tim thay layer 'towerdich'.");
        }

        if (playerMainTower == null) playerMainTower = new Vector2(0, 0);
        if (enemyMainTower == null) enemyMainTower = new Vector2(0, 0);
    }

    public Array<Vector2> getWaypoints(BaseEntity entity) {
        Array<Vector2> copy = new Array<>();
        if (entity instanceof Enemy) {
            for (int i = waypoints.size - 1; i >= 0; i--) {
                copy.add(waypoints.get(i));
            }
        } else if (entity instanceof Miner) {
            for (Vector2 wp : waypoints_miner) {
                copy.add(wp.cpy());
            }
        } else {
            for (Vector2 wp : waypoints) {
                copy.add(wp.cpy());
            }
        }
        return copy;
    }

    public Vector2 getPlayerCastlePosition() {
        return playerMainTower.cpy();
    }

    public Vector2 getEnemyBasePosition() {
        return enemyMainTower.cpy();
    }

    public Vector2 getPlayerCastleSpawnPosition() {
        return getBottomCenter(playerMainTowerZone, playerMainTower);
    }

    public Vector2 getEnemyBaseSpawnPosition() {
        return getBottomCenter(enemyMainTowerZone, enemyMainTower);
    }

    public Array<Vector2> getPlayerTowers() {
        Array<Vector2> copy = new Array<>();
        for (Vector2 t : playerTowers) copy.add(t.cpy());
        return copy;
    }

    public Array<Vector2> getEnemyTowers() {
        Array<Vector2> copy = new Array<>();
        for (Vector2 t : enemyTowers) copy.add(t.cpy());
        return copy;
    }

    public Array<BuildingZone> getPlayerBuildingZones() {
        Array<BuildingZone> copy = new Array<>();
        for (BuildingZone zone : playerBuildingZones) {
            Rectangle bounds = zone.getBounds();
            copy.add(new BuildingZone(zone.getName(), bounds.x, bounds.y, bounds.width, bounds.height));
        }
        return copy;
    }

    public BuildingZone findPlayerBuildingZone(float worldX, float worldY) {
        for (BuildingZone zone : playerBuildingZones) {
            if (zone.contains(worldX, worldY)) {
                return zone;
            }
        }
        return null;
    }

    public BuildingZone findInteractiveZone(float worldX, float worldY) {
        BuildingZone playerZone = findPlayerBuildingZone(worldX, worldY);
        if (playerZone != null) return playerZone;

        for (BuildingZone zone : enemyTowerZones) {
            if (zone.contains(worldX, worldY)) {
                return copyZone(zone);
            }
        }
        return null;
    }

    public BuildingZone getPlayerMainTowerZone() {
        return copyZone(playerMainTowerZone);
    }

    public BuildingZone getEnemyMainTowerZone() {
        return copyZone(enemyMainTowerZone);
    }

    public Array<BuildingZone> getPlayerTowerZones() {
        return copyZones(playerTowerZones);
    }

    public Array<BuildingZone> getEnemyTowerZones() {
        return copyZones(enemyTowerZones);
    }

    private BuildingZone copyZone(BuildingZone zone) {
        if (zone == null) return null;
        Rectangle bounds = zone.getBounds();
        return new BuildingZone(zone.getName(), bounds.x, bounds.y, bounds.width, bounds.height);
    }

    private Array<BuildingZone> copyZones(Array<BuildingZone> zones) {
        Array<BuildingZone> copy = new Array<>();
        for (BuildingZone zone : zones) {
            copy.add(copyZone(zone));
        }
        return copy;
    }

    private Vector2 getBottomCenter(BuildingZone zone, Vector2 fallback) {
        if (zone == null) return fallback.cpy();
        Rectangle bounds = zone.getBounds();
        return new Vector2(bounds.x + bounds.width / 2f, bounds.y);
    }

    private boolean isPlayerDefenseTower(String name) {
        return "tower1".equals(name) || "tower2".equals(name);
    }

    private boolean isEnemyDefenseTower(String name) {
        return "enemytower1".equals(name) || "enemytower2".equals(name);
    }

    public Vector2 worldToGrid(Vector2 worldPos) {
        int gx = (int) (worldPos.x / tileSize);
        int gy = (int) (worldPos.y / tileSize);
        gx = Math.max(0, Math.min(mapWidth - 1, gx));
        gy = Math.max(0, Math.min(mapHeight - 1, gy));
        return new Vector2(gx, gy);
    }

    public Vector2 gridToWorld(Vector2 gridPos) {
        return new Vector2(
            gridPos.x * tileSize + tileSize / 2f,
            gridPos.y * tileSize + tileSize / 2f
        );
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void dispose() {
        if (tiledMap != null) tiledMap.dispose();
    }
}
