package com.hust.towerdefence.Model.Entities;

import com.badlogic.gdx.math.Vector2;

/**
 * PROJECTILE - Thực thể Đạn bay.
 *
 * VAI TRÒ:
 * 1. Di chuyển từ nguồn bắn (Trụ/Lính) đến mục tiêu.
 * 2. Mang theo lượng sát thương (Damage) để trừ máu mục tiêu khi va chạm.
 * 3. Tự hủy sau khi hoàn thành nhiệm vụ hoặc bay quá xa.
 */
public class Projectile extends BaseEntity {

    private final int damage;
    private final float speed;
    private final BaseEntity target; // Mục tiêu để đuổi theo (hoặc hướng tới)

    public Projectile(float x, float y, int teamId, int damage, float speed, BaseEntity target) {
        // Đạn thường có kích thước nhỏ (ví dụ 8x8) và 1 HP
        super(x, y, 8, 8, 1, teamId);

        this.damage = damage;
        this.speed = speed;
        this.target = target;

        // Tính toán hướng bay ban đầu
        calculateInitialVelocity();
    }

    /**
     * Tính toán vận tốc dựa trên vị trí mục tiêu.
     */
    private void calculateInitialVelocity() {
        if (target != null) {
            Vector2 direction = new Vector2(target.getX() - getX(), target.getY() - getY());
            this.velocity.set(direction).nor().scl(speed);
        }
    }

    @Override
    public void update(float delta) {
        // 1. Cập nhật vị trí dựa trên vận tốc
        position.add(velocity.x * delta, velocity.y * delta);

        // 2. Cập nhật logic cơ bản (hitbox, stateTime)
        super.update(delta);

        // 3. Logic tự hủy nếu bay quá lâu (ví dụ 5 giây) để tránh tràn bộ nhớ
        if (stateTime > 5f) {
            die();
        }
    }

    /**
     * Trả về lượng sát thương mà viên đạn này mang theo.
     */
    public int getDamage() {
        return damage;
    }

    public BaseEntity getTarget() {
        return target;
    }
}
