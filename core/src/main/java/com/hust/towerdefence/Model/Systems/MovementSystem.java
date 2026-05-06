package com.hust.towerdefence.Model.Systems;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.Enemy;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Soldier;
import com.hust.towerdefence.Model.Managers.EntityManager;
import com.hust.towerdefence.Model.Managers.MapManager;

/**
 * MovementSystem
 * Xử lý di chuyển của các CombatEntity (Soldier, Enemy) dọc theo waypoints từ PlayerCastle đến EnemyBase.
 * Mỗi entity có một danh sách waypoints (Vector2) và tốc độ.
 * Mỗi frame, entity di chuyển về waypoint hiện tại; khi đến gần, chuyển sang waypoint tiếp theo.
 */
public class MovementSystem {

    private EntityManager entityManager;
    private MapManager mapManager;

    // Khoảng cách tối thiểu để coi là đã đến waypoint (tránh rung lắc)
    private static final float WAYPOINT_REACH_DISTANCE = 5f;

    public MovementSystem(EntityManager entityManager, MapManager mapManager) {
        this.entityManager = entityManager;
        this.mapManager = mapManager;
    }

    public void update(float delta) {
        // Di chuyển tất cả Soldier còn sống
        for (Soldier soldier : entityManager.getSoldiers()) {
            if (soldier.isActive() && !soldier.isDead()) {
                moveAlongPath(soldier, delta);
            }
        }

        // Di chuyển tất cả Enemy còn sống
        for (Enemy enemy : entityManager.getEnemies()) {
            if (enemy.isActive() && !enemy.isDead()) {
                moveAlongPath(enemy, delta);
            }
        }
    }

    /**
     * Di chuyển một CombatEntity dọc theo waypoints của nó.
     * @param entity Entity cần di chuyển (phải có waypoints)
     * @param delta Thời gian trôi qua (giây)
     */
    private void moveAlongPath(CombatEntity entity, float delta) {
        Array<Vector2> waypoints = entity.getWaypoints();
        if (waypoints == null || waypoints.size == 0) {
            // Không có đường đi, đứng yên
            return;
        }

        int currentIdx = entity.getCurrentWaypointIndex();
        // Nếu đã đi hết waypoint, không di chuyển nữa (có thể đánh dấu đến đích)
        if (currentIdx >= waypoints.size) {
            return;
        }

        Vector2 targetPos = waypoints.get(currentIdx);
        Vector2 currentPos = entity.getPosition();
        float speed = entity.getSpeed();

        // Tính hướng và khoảng cách
        float dx = targetPos.x - currentPos.x;
        float dy = targetPos.y - currentPos.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance <= WAYPOINT_REACH_DISTANCE) {
            // Đã đến waypoint hiện tại, chuyển sang waypoint tiếp theo
            entity.setCurrentWaypointIndex(currentIdx + 1);
            // Sau khi chuyển, gọi đệ quy để xử lý waypoint kế tiếp trong cùng frame (tránh đứng yên 1 frame)
            moveAlongPath(entity, delta);
        } else {
            // Di chuyển về phía target
            float step = speed * delta;
            if (step >= distance) {
                // Bước quá lớn, đặt thẳng đến target
                currentPos.set(targetPos);
            } else {
                // Di chuyển bình thường
                float ratio = step / distance;
                currentPos.x += dx * ratio;
                currentPos.y += dy * ratio;
            }
            // Cập nhật vận tốc (nếu cần dùng cho physics)
            entity.getVelocity().set(dx, dy).nor().scl(speed);
            // Có thể cập nhật góc quay (rotation) theo hướng di chuyển
            float angle = (float) Math.atan2(dy, dx) * MathUtils.radiansToDegrees;
            entity.setRotation(angle);
        }
    }

    /**
     * Thiết lập đường đi cho entity (gọi khi tạo entity hoặc khi spawn)
     * @param entity CombatEntity cần set waypoints
     * @param waypoints Danh sách waypoints (tọa độ thế giới)
     */
    public static void setPath(CombatEntity entity, Array<Vector2> waypoints) {
        entity.setWaypoints(waypoints);
        entity.setCurrentWaypointIndex(0);
    }
}
