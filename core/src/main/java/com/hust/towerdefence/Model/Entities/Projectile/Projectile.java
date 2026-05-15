package com.hust.towerdefence.Model.Entities.Projectile;

import com.badlogic.gdx.math.Vector2;
import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity.Team;
public class Projectile extends BaseEntity {
    public float damage; // Lượng sát thương
    public float speed; // Tốc độ di chuyển
    private Team team; // Phe của projectile (theo phe của người bắn ra)
    private CombatEntity target;


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
