package com.hust.towerdefence.Model.Entities;

//import com.badlogicgames.gdx.graphics.Color;

public abstract class GameEntity {
    public enum State { IDLE, WALKING, ATTACKING, DYING, DEAD }

    protected float x, y;
    protected int hp, maxHp;
    protected State state = State.IDLE;
    protected float stateTime = 0;
    protected float alpha = 1.0f;

    public GameEntity(float x, float y, int hp) {
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.maxHp = hp;
    }

    public void update(float delta) {
        stateTime += delta;
        if (hp <= 0 && state != State.DYING && state != State.DEAD) {
            state = State.DYING;
            stateTime = 0;
        }
    }

    // Getters and Setters
    public float getX() { return x; }
    public float getY() { return y; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public State getState() { return state; }
    public float getStateTime() { return stateTime; }
    public float getAlpha() { return alpha; }
    public void setHp(int hp) { this.hp = hp; }
    public void setState(State state) { this.state = state; }
    public void setPosition(float x, float y) { this.x = x; this.y = y; }
}
