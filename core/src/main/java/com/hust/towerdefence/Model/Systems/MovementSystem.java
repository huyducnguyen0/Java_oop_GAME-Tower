// ===================== MovementSystem.java =====================
package com.hust.towerdefence.Model.Systems;

import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Managers.EntityManager;
import com.hust.towerdefence.Model.Managers.MapManager;
import com.hust.towerdefence.Model.GameWorld;
import java.util.List;

public class MovementSystem {
    private final GameWorld world;

    public MovementSystem(GameWorld world) {
        this.world = world;
    }

    public void update(float delta) {
        EntityManager entityManager = world.getEntityManager();
        MapManager mapManager = world.getMapManager();

        // Cập nhật tất cả entity có khả năng di chuyển (có velocity hoặc path)
        for (BaseEntity entity : entityManager.getAllEntities()) {
            if (!entity.isActive()) continue;

            // Xử lý di chuyển theo path (dành cho enemy, lính khi di chuyển theo lệnh)
            if (entity.hasPath() && entity.getCurrentWaypointIndex() < entity.getPath().size()) {
                moveAlongPath(entity, delta);
            }
            // Xử lý di chuyển theo vận tốc (đạn, entity bị đẩy lùi, ...)
            else if (entity.hasVelocity()) {
                entity.setX(entity.getX() + entity.getVelocityX() * delta);
                entity.setY(entity.getY() + entity.getVelocityY() * delta);
                // Có thể thêm ma sát hoặc giảm tốc ở đây
            }
        }
    }

    private void moveAlongPath(BaseEntity entity, float delta) {
        List<BaseEntity.Waypoint> path = entity.getPath();
        int targetIndex = entity.getCurrentWaypointIndex();
        BaseEntity.Waypoint target = path.get(targetIndex);

        float dx = target.x - entity.getX();
        float dy = target.y - entity.getY();
        float distToTarget = (float) Math.sqrt(dx * dx + dy * dy);

        float speed = entity.getCurrentSpeed(); // lấy tốc độ hiện tại (có thể bị ảnh hưởng bởi effect)
        float moveDistance = speed * delta;

        if (moveDistance >= distToTarget) {
            // Đến được waypoint
            entity.setX(target.x);
            entity.setY(target.y);
            entity.setCurrentWaypointIndex(targetIndex + 1);

            // Nếu đã đến cuối path, xử lý sự kiện (ví dụ: enemy đến đích gây damage main tower)
            if (entity.getCurrentWaypointIndex() >= path.size()) {
                entity.clearPath();
                onPathComplete(entity);
            }
        } else {
            // Di chuyển về phía waypoint
            float ratio = moveDistance / distToTarget;
            entity.setX(entity.getX() + dx * ratio);
            entity.setY(entity.getY() + dy * ratio);
        }
    }

    private void onPathComplete(BaseEntity entity) {
        // Xử lý khi enemy đến cuối đường (gây sát thương nhà chính)
        if (entity instanceof CombatEntity) {
            world.getHealthSystem().dealDamageToMainTower((CombatEntity) entity);
        }
    }
}
