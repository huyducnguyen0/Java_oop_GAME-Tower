package com.hust.towerdefence.Model.Entities;

public class Enemy extends GameEntity {
    public enum EnemyType { BASIC, FAST, TANK, BOSS }

    private EnemyType type;
    private float spd;
    private int atk;
    private float lastActionTime = 0;

    public Enemy(EnemyType type, float x, float y) {
        super(x, y, getBaseHp(type));
        this.type = type;
        this.atk = getBaseAtk(type);
        this.spd = getBaseSpd(type);
        this.state = State.WALKING;
    }

    // Stats cơ bản theo loại quái
    private static int getBaseHp(EnemyType type) {
        switch (type) {
            case BASIC: return 50;
            case FAST: return 30;
            case TANK: return 150;
            case BOSS: return 500;
            default: return 50;
        }
    }

    private static int getBaseAtk(EnemyType type) {
        switch (type) {
            case BASIC: return 5;
            case FAST: return 3;
            case TANK: return 15;
            case BOSS: return 50;
            default: return 5;
        }
    }

    private static float getBaseSpd(EnemyType type) {
        switch (type) {
            case BASIC: return 50f;
            case FAST: return 80f;
            case TANK: return 30f;
            case BOSS: return 40f;
            default: return 50f;
        }
    }

    // Thực hiện hành động tấn công
    public void performAttack() {
        state = State.ATTACKING;
        // Logic tấn công sẽ implement trong CombatSystem
    }

    public EnemyType getType() { return type; }
    public float getSpd() { return spd; }
    public int getAtk() { return atk; }
    public void setAtk(int atk) { this.atk = atk; }  // Thêm setter cho ATK
    public float getLastActionTime() { return lastActionTime; }
    public void setLastActionTime(float time) { this.lastActionTime = time; }
}
