package com.hust.towerdefence.Model.Entities.Tower;

import com.badlogic.gdx.math.Vector2;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;

public abstract class BaseTower extends CombatEntity {
    public BaseTower(Vector2 position) {
        super();
        this.position.set(position);
        this.currentState = State.IDLE;
    }
}
