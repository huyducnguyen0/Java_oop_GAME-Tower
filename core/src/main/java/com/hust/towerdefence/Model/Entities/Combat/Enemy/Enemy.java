package com.hust.towerdefence.Model.Entities.Combat.Enemy;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;

public abstract class Enemy extends CombatEntity {

    protected int goldReward;
    protected float expReward;
    protected int currentWaypointIndex; // Chỉ số waypoint hiện tại
    public Enemy() {
        super();
        this.team = Team.ENEMY;
        this.currentState = State.IDLE;
        this.level = 1; // Mặc định level 1 cho enemy base class
    }
    @Override
    public void reset() {
        super.reset();
        this.level = 1;
        this.goldReward = 0;
        this.expReward = 0;
        this.team = Team.ENEMY;
        this.currentState = State.IDLE;
    }



    public int getCurrentWaypointIndex() { return currentWaypointIndex; }
    public void setCurrentWaypointIndex(int currentWaypointIndex) { this.currentWaypointIndex = currentWaypointIndex; }

    public int getGoldReward() { return goldReward; }

    public void setGoldReward(int goldReward) {
        this.goldReward = Math.max(0, goldReward);
    }

    public float getExpReward() { return expReward; }

    public void setExpReward(float expReward) {
        this.expReward = Math.max(0, expReward);
    }



}
