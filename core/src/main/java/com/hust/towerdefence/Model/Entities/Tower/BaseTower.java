package com.hust.towerdefence.Model.Entities.Tower;

import com.badlogic.gdx.math.Vector2;
import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;


public abstract class BaseTower extends BaseEntity {

    public BaseTower(Vector2 position , float width, float height, CombatEntity.Team team) {
        super();
        this.position.set(position);
        this.
        this.width = width;
        this.height = height;
    }

    // Getters/Setters cho máu

    @Override
    public void reset() {
        super.reset();
    }
}
