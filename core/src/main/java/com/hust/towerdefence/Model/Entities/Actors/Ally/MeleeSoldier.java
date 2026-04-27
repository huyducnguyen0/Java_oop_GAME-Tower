package com.hust.towerdefence.Model.Entities.Actors.Ally;

import com.hust.towerdefence.Model.Entities.DestructibleEntity;
import com.hust.towerdefence.Model.Entities.MovingEntity;
import com.hust.towerdefence.Model.Entities.EntityStats;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Đơn vị cận chiến cơ bản.
 * Vai trò: Giữ chân địch, tạo khoảng trống cho các đơn vị tầm xa gây sát thương.
 */
public class MeleeSoldier extends MovingEntity {

    public MeleeSoldier(float x, float y) {
        super(
            x, y, 32, 32,               // Tọa độ và kích thước
            EntityStats.MELEE_HP,       // Máu
            EntityStats.MELEE_DEF,      // Giáp
            1,                          // Phe Ta (Ally)
            EntityStats.MELEE_ATK,      // Sức tấn công
            EntityStats.MELEE_RANGE,    // Tầm đánh (ngắn)
            EntityStats.MELEE_COOLDOWN, // Tốc độ đánh
            EntityStats.MELEE_SPEED,    // Tốc độ di chuyển
            EntityStats.MELEE_VISION    // Tầm nhìn
        );
    }

    @Override
    protected void performAttack(DestructibleEntity target) {
        // Tấn công trực tiếp vào mục tiêu
        if (target != null && target.isActive()) {
            target.takeDamage(this.atk);
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!active) return;
        // Thực hiện vẽ Sprite tại đây sau khi load Texture
    }

    @Override
    protected void onDeath() {
        this.removed = true; // Đánh dấu để xóa khỏi danh sách quản lý
    }

    @Override
    public void dispose() {
        // Giải phóng tài nguyên hình ảnh khi đối tượng bị hủy
    }
}
