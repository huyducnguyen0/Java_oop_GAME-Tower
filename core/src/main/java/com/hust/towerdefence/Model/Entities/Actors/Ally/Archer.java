package com.hust.towerdefence.Model.Entities.Actors.Ally;

import com.hust.towerdefence.Model.Entities.DestructibleEntity;
import com.hust.towerdefence.Model.Entities.MovingEntity;
import com.hust.towerdefence.Model.Entities.EntityStats;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Đơn vị tấn công tầm xa.
 * Vai trò: Tiêu diệt quái từ khoảng cách an toàn, đặc biệt hiệu quả với quái bay.
 */
public class Archer extends MovingEntity {

    public Archer(float x, float y) {
        super(
            x, y, 32, 32,
            EntityStats.ARCHER_HP,
            EntityStats.ARCHER_DEF,
            1,
            EntityStats.ARCHER_ATK,
            EntityStats.ARCHER_RANGE,    // Tầm đánh (xa)
            EntityStats.ARCHER_COOLDOWN, // Bắn nhanh
            EntityStats.ARCHER_SPEED,    // Chạy nhanh để giữ khoảng cách
            EntityStats.ARCHER_VISION    // Tầm nhìn rộng
        );
    }

    @Override
    protected void performAttack(DestructibleEntity target) {
        if (target != null && target.isActive()) {
            target.takeDamage(this.atk);
            // Sau này: ProjectileManager.createArrow(this, target);
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!active) return;
    }

    @Override
    protected void onDeath() {
        this.removed = true;
    }

    @Override
    public void dispose() {
    }
}
