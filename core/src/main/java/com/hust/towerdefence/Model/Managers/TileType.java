package com.hust.towerdefence.Model.Managers;

public enum TileType {
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
