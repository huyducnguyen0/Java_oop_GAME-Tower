package com.hust.towerdefence.Model.AI;

public class SpawnDecision {
    public enum UnitType {
        PAWN,      // lính cơ bản
        WARRIOR,   // lính tank
        TNT,       // bom tự sát
        NONE       // không spawn
    }

    private final UnitType type;
    private final int count;

    public SpawnDecision(UnitType type, int count) {
        this.type = type;
        this.count = count;
    }

    public UnitType getType() { return type; }
    public int getCount() { return count; }

    public boolean shouldSpawn() {
        return type != UnitType.NONE && count > 0;
    }

    public static SpawnDecision none() {
        return new SpawnDecision(UnitType.NONE, 0);
    }
}
