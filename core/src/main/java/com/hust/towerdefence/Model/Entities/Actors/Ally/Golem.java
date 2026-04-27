package com.hust.towerdefence.Model.Entities.Actors.Ally;

import com.hust.towerdefence.Model.Entities.DestructibleEntity;
import com.hust.towerdefence.Model.Entities.MovingEntity;
import com.hust.towerdefence.Model.Entities.EntityStats;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Đơn vị cận chiến hạng nặng.
 * Vai trò: Lá chắn thép trước pháo đài, có khả năng đè bẹp những quái vật cứng đầu nhất.
 */
public class Golem extends MovingEntity {

    public Golem(float x, float y) {
        // Golem có kích thước lớn hơn (64x64) để thể hiện sự uy mãnh
        super(
            x, y, 64, 64,
            EntityStats.GOLEM_HP,       // Máu cực cao
            EntityStats.GOLEM_DEF,      // Giáp rất dày
            1,
            EntityStats.GOLEM_ATK,      // Sát thương lớn
            EntityStats.GOLEM_RANGE,    // Đấm gần
            EntityStats.GOLEM_COOLDOWN, // Đấm rất chậm
            EntityStats.GOLEM_SPEED,    // Đi rất chậm
            EntityStats.GOLEM_VISION
        );
    }

    @Override
    protected void performAttack(DestructibleEntity target) {
        if (target != null && target.isActive()) {
            target.takeDamage(this.atk);
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
