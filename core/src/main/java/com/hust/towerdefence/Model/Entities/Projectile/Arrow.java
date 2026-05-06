package com.hust.towerdefence.Model.Entities.Projectile;

import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;

public class Arrow extends Projectile {

    public Arrow() {
        super();
        this.width = 0.3f;
        this.height = 0.1f;
    }
    public void init(float x, float y, CombatEntity target) {
        super.init( x, y, target, 25f, 6f);
    }


    @Override
    public void reset() {
        super.reset();
    }
}
