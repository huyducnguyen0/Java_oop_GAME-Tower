package com.hust.towerdefence.Model.Entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.utils.Location;
import com.hust.towerdefence.Model.AI.AIController;

public class Enemy extends GameEntity implements Steerable<Vector2> {
    public enum EnemyType { BASIC, FAST, TANK, BOSS }

    private EnemyType type;
    private float spd;
    private int atk;
    private float lastActionTime = 0;
    private AIController aiController;  // AI điều khiển Enemy

    private Vector2 position = new Vector2();
    private Vector2 linearVelocity = new Vector2();
    private float orientation;
    private float angularVelocity;
    private float boundingRadius = 16f;
    private boolean tagged;
    private float maxLinearSpeed = 200f;
    private float maxLinearAcceleration = 1000f;
    private float maxAngularSpeed = 30f;
    private float maxAngularAcceleration = 5f;
    private float zeroLinearSpeedThreshold = 0.001f;

    public Enemy(EnemyType type, float x, float y) {
        super(x, y, getBaseHp(type));
        this.type = type;
        this.atk = getBaseAtk(type);
        this.spd = getBaseSpd(type);
        this.state = State.WALKING;
        this.aiController = new AIController(this);  // Khởi tạo AI
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
        // Logic tấn công sẽ implement trong AIController
    }

    public EnemyType getType() { return type; }
    public float getSpd() { return spd; }
    public int getAtk() { return atk; }
    public void setAtk(int atk) { this.atk = atk; }
    public float getLastActionTime() { return lastActionTime; }
    public void setLastActionTime(float time) { this.lastActionTime = time; }
    public AIController getAIController() { return aiController; }
    public void setMaxHp(int hp) { this.maxHp = hp; }

    @Override
    public Vector2 getPosition() { return position.set(getX(), getY()); }
    @Override
    public float getOrientation() { return orientation; }
    @Override
    public void setOrientation(float orientation) { this.orientation = orientation; }
    @Override
    public Vector2 getLinearVelocity() { return linearVelocity; }
    @Override
    public float getAngularVelocity() { return angularVelocity; }
    @Override
    public float getBoundingRadius() { return boundingRadius; }
    @Override
    public boolean isTagged() { return tagged; }
    @Override
    public void setTagged(boolean tagged) { this.tagged = tagged; }
    @Override
    public float getMaxLinearSpeed() { return maxLinearSpeed; }
    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) { this.maxLinearSpeed = maxLinearSpeed; }
    @Override
    public float getMaxLinearAcceleration() { return maxLinearAcceleration; }
    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) { this.maxLinearAcceleration = maxLinearAcceleration; }
    @Override
    public float getMaxAngularSpeed() { return maxAngularSpeed; }
    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) { this.maxAngularSpeed = maxAngularSpeed; }
    @Override
    public float getMaxAngularAcceleration() { return maxAngularAcceleration; }
    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) { this.maxAngularAcceleration = maxAngularAcceleration; }
    @Override
    public Location<Vector2> newLocation() {
        // Trả về một Location<Vector2> mới dựa trên vị trí hiện tại
        return new Location<Vector2>() {
            final Vector2 pos = new Vector2(getX(), getY());
            @Override public Vector2 getPosition() { return pos.set(getX(), getY()); }
            @Override public float getOrientation() { return orientation; }
            @Override public void setOrientation(float orientation) { Enemy.this.orientation = orientation; }
            @Override public Location<Vector2> newLocation() { return this; }
            @Override public float vectorToAngle(Vector2 vector) { return (float)Math.atan2(vector.y, vector.x); }
            @Override public Vector2 angleToVector(Vector2 outVector, float angle) {
                outVector.x = (float)Math.cos(angle);
                outVector.y = (float)Math.sin(angle);
                return outVector;
            }
        };
    }
    @Override
    public float getZeroLinearSpeedThreshold() { return zeroLinearSpeedThreshold; }
    @Override
    public void setZeroLinearSpeedThreshold(float value) { this.zeroLinearSpeedThreshold = value; }
    @Override
    public float vectorToAngle(Vector2 vector) { return (float)Math.atan2(vector.y, vector.x); }
    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.x = (float)Math.cos(angle);
        outVector.y = (float)Math.sin(angle);
        return outVector;
    }

}
