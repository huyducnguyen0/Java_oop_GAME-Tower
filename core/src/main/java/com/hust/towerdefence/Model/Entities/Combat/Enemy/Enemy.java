package com.hust.towerdefence.Model.Entities.Combat.Enemy;

import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;

public abstract class Enemy extends CombatEntity {



    protected int goldReward;
    protected float expReward;


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
        this.goldReward = 0;// Mặc định không có phần thưởng, sẽ được set cụ thể trong từng loại enemy
        this.expReward = 0;
        this.team = Team.ENEMY;
        this.currentState = State.IDLE;
    }

    // ===== Getter / Setter =====

    public int getGoldReward() { return goldReward; }

    public void setGoldReward(int goldReward) {
        this.goldReward = Math.max(0, goldReward);
    }

    public float getExpReward() { return expReward; }

    public void setExpReward(float expReward) {
        this.expReward = Math.max(0, expReward);
    }



}
