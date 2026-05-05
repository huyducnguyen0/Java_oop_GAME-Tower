package com.hust.towerdefence.Model.Managers;

public enum TileType {
    ROAD(0, true, false),      // Đường đi – enemy đi được, không xây tháp
    TOWER_PLATFORM(1, false, true), // Nền xây tháp – không đi qua, được xây
    OBSTACLE(2, false, false), // Vật cản – không đi, không xây
    START(3, true, false),     // Điểm xuất phát của enemy
    END(4, true, false);       // Điểm đích (mất mạng)

    private final int id;
    private final boolean walkable;
    private final boolean buildable;

    TileType(int id, boolean walkable, boolean buildable) {
        this.id = id;
        this.walkable = walkable;
        this.buildable = buildable;
    }

    public int getId() { return id; }
    public boolean isWalkable() { return walkable; }
    public boolean isBuildable() { return buildable; }
}
