package com.hust.towerdefence.Model.Entities;

import com.badlogic.gdx.math.Vector2;
import java.util.List;

public abstract class MovingEntity extends CombatEntity {
    protected float speed;
    protected Vector2 velocity;
    protected Vector2 destination;       // Điểm ng chơi chỉ định (Cổng thành, vị trí thủ)
    protected DestructibleEntity target;  // Mục tiêu địch để lao vào đánh
    protected float visionRange;        // Tầm mắt để tự động phát hiện địch

    public MovingEntity(float x, float y, float width, float height, int hp, int def, int teamID, int atk, float range, float cooldown, float speed, float visionRange) {
        super(x, y, width, height, hp, def, teamID, atk, range, cooldown);
        this.speed = speed;
        this.visionRange = visionRange;
        this.velocity = new Vector2();
        this.destination = new Vector2(x, y);
        this.target = null;
    }

    /**
     * HÀM QUAN TRỌNG NHẤT: Quản lý 2 trạng thái di chuyển
     */
    public void updateMovement(float delta, List<DestructibleEntity> potentialTargets) {
        if (!active) return;

        // 1. Tự động tìm địch nếu đang rảnh hoặc đang đứng ở điểm chỉ định
        autoSeekTarget(potentialTargets);

        Vector2 finalGoal;

        // 2. Ưu tiên: Nếu thấy địch -> Chuyển sang chế độ "Truy đuổi"
        if (target != null && target.isActive()) {
            finalGoal = target.getPosition();
            float distToEnemy = position.dst(finalGoal);

            if (distToEnemy <= attackRange) {
                velocity.set(0, 0); // Đã vào tầm, đứng lại vả!
                return;
            }
        }
        // 3. Nếu ko có địch -> Đi tới điểm chỉ định (Cổng thành / Về thành hồi máu)
        else {
            finalGoal = destination;
        }

        // Logic di chuyển vật lý
        float distToGoal = position.dst(finalGoal);
        if (distToGoal > 2f) {
            velocity.set(finalGoal).sub(position).nor().scl(speed);
            position.add(velocity.x * delta, velocity.y * delta);
            updateBounds();
        } else {
            velocity.set(0, 0);
        }
    }

    /**
     * Mắt nhìn: Tự gán target nếu địch lọt vào visionRange
     */
    private void autoSeekTarget(List<DestructibleEntity> potentialTargets) {
        if (target != null && target.isActive()) return; // Đang có mục tiêu thì thôi

        for (DestructibleEntity entity : potentialTargets) {
            if (entity.isActive() && entity.getTeamID() != this.teamID) {
                if (position.dst(entity.getPosition()) <= visionRange) {
                    this.target = entity;
                    break;
                }
            }
        }
    }

    // --- Lệnh từ Người chơi ---
    public void commandMoveTo(float x, float y) {
        this.destination.set(x, y);
        this.target = null; // Khi chủ bảo đi chỗ khác thì bỏ mục tiêu cũ
    }
}
