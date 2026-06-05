package com.hust.towerdefence.Model.Systems;

import com.badlogic.gdx.math.Vector2;
import com.hust.towerdefence.Model.Entities.BaseEntity;

public class CombatVisualEvent {
    public enum Type {
        ARROW,
        DYNAMITE,
        HEAL,
        POISON
    }

    private final Type type;
    private final BaseEntity.Team team;
    private final Vector2 start;
    private final Vector2 end;

    public CombatVisualEvent(Type type, BaseEntity.Team team, Vector2 start, Vector2 end) {
        this.type = type;
        this.team = team;
        this.start = start.cpy();
        this.end = end.cpy();
    }

    public Type getType() {
        return type;
    }

    public BaseEntity.Team getTeam() {
        return team;
    }

    public Vector2 getStart() {
        return start.cpy();
    }

    public Vector2 getEnd() {
        return end.cpy();
    }
}
