package com.hust.towerdefence.Model.Entities.Projectile;

import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;

public class Projectile extends BaseEntity {

    public float damage; // Lượng sát thương
    public float speed; // Tốc độ di chuyển

    public CombatEntity target; // Mục tiêu mà projectile hướng đến



    public Projectile() {
        super();
    }

    /**
     * Init từ pool
     */
    public void init(float x, float y, CombatEntity target,
                     float damage, float speed) {

        this.position.set(x, y);
        this.target = target;

        this.damage = damage;
        this.speed = speed;
        this.active = true;
    }

    @Override
    public void reset() {
        super.reset();
        damage = 0;
        speed = 0;
        target = null;
        active = false;
    }
}
