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
 * MovementSystem chuyên xử lý di chuyển dọc theo waypoints.
 *
 * Logic:
 * - Nếu có đối thủ trong tầm tấn công -> setState(ATTACKING) và dừng di chuyển
 * - Nếu không có địch -> setState(MOVING) và di chuyển theo waypoints
 * - Khi đến waypoint cuối (gần trụ) -> setState(ATTACKING) hướng tấn công trụ
 * - Nếu đang ATTACKING mà target chết/ra ngoài tầm -> quay lại MOVING
 *
 * Issues Fixed:
 * 1. Thêm logic chuyển ATTACKING -> MOVING khi target chết/ra ngoài tầm
 * 6. Ưu tiên kiểm tra trụ trước khi set IDLE
 * 7. Đặt vận tốc về 0 khi đến waypoint
 * 8. Dùng isDead() thay vì kiểm tra getHealth() > 0
 */
public class MovementSystem {

    private final EntityManager entityManager;
    private final MapManager mapManager;

    private static final float WAYPOINT_REACH_DISTANCE = 5f;
    private static final float EPSILON = 0.001f;  // Để tránh chia cho số quá nhỏ (Issue 2)

    public MovementSystem(EntityManager entityManager, MapManager mapManager) {
        this.entityManager = entityManager;
        this.mapManager = mapManager;
    }

    public void update(float delta) {
        // Update Soldiers
        for (Soldier soldier : entityManager.getSoldiers()) {
            if (soldier.isActive() && !soldier.isDead()) {  // Issue 8: Dùng isDead()
                updateEntityMovement(soldier, delta);
            }
        }

        // Update Enemies
        for (Enemy enemy : entityManager.getEnemies()) {
            if (enemy.isActive() && !enemy.isDead()) {  // Issue 8: Dùng isDead()
                updateEntityMovement(enemy, delta);
            }
        }
    }

    /**
     * Cập nhật di chuyển cho một entity dựa trên trạng thái hiện tại
     */
    private void updateEntityMovement(CombatEntity entity, float delta) {
        CombatEntity.State currentState = entity.getCurrentState();

        // ===== Issue 1: Xử lý khi entity đang ATTACKING =====
        if (currentState == CombatEntity.State.ATTACKING) {
            // Kiểm tra xem target còn sống và trong tầm không
            if (!isTargetValid(entity)) {
                // Target chết hoặc ra khỏi tầm -> chuyển về MOVING & tiếp tục di chuyển
                entity.setState(CombatEntity.State.MOVING);
                entity.setTargetId(0);
                // Tiếp tục hàm để di chuyển
            } else {
                // Target vẫn còn & trong tầm -> giữ ATTACKING, không di chuyển
                entity.getVelocity().setZero();
                return;
            }
        }

        // Nếu đang ở trạng thái khác (MINING, DYING, ...) thì không di chuyển
        if (currentState != CombatEntity.State.IDLE && currentState != CombatEntity.State.MOVING &&
            currentState != CombatEntity.State.ATTACKING) {
            entity.getVelocity().setZero();
            return;
        }

        // ===== 1. Kiểm tra có đối thủ trong tầm tấn công =====
        CombatEntity nearestEnemy = findNearestEnemyInRange(entity);
        if (nearestEnemy != null) {
            // Có địch -> chuyển sang ATTACKING, dừng di chuyển
            entity.setState(CombatEntity.State.ATTACKING);
            entity.setTargetId(nearestEnemy.getId());
            entity.getVelocity().setZero();
            return;
        }

        // ===== 2. Không có địch -> di chuyển theo waypoints =====
        Array<Vector2> waypoints = entity.getWaypoints();
        if (waypoints == null || waypoints.size == 0) {
            // Issue 6: Kiểm tra xem có trong tầm trụ không trước khi set IDLE
            Vector2 towerPos = getTargetTowerPosition(entity);
            float towerRange = getTowerAttackRange(entity); // Issue 3
            if (entity.getPosition().dst2(towerPos) <= towerRange * towerRange) {
                entity.setState(CombatEntity.State.ATTACKING);
                entity.setTargetId(0);  // ID = 0 = trụ
                entity.getVelocity().setZero();
            } else {
                entity.setState(CombatEntity.State.IDLE);
                entity.getVelocity().setZero();
            }
            return;
        }

        entity.setState(CombatEntity.State.MOVING);

        int currentIdx = entity.getCurrentWaypointIndex();
        Vector2 currentPos = entity.getPosition();

        // Bỏ qua các waypoint đã đến
        float reachDistSq = WAYPOINT_REACH_DISTANCE * WAYPOINT_REACH_DISTANCE;
        while (currentIdx < waypoints.size &&
               currentPos.dst2(waypoints.get(currentIdx)) <= reachDistSq) {
            currentIdx++;
        }
        entity.setCurrentWaypointIndex(currentIdx);

        // Nếu đã hết waypoints -> đã tới gần trụ địch
        if (currentIdx >= waypoints.size) {
            // Kiểm tra xem đã trong tầm tấn công trụ chưa
            Vector2 towerPos = getTargetTowerPosition(entity);
            float towerRange = getTowerAttackRange(entity); // Issue 3
            if (currentPos.dst2(towerPos) <= towerRange * towerRange) {
                // Đã trong tầm trụ -> tấn công trụ
                entity.setState(CombatEntity.State.ATTACKING);
                entity.setTargetId(0);  // Mục tiêu là trụ (ID = 0 để CombatSystem nhận diện)
                entity.getVelocity().setZero();
            } else {
                // Chưa tới tầm trụ -> tiếp tục di chuyển về phía trụ
                moveTowardsTarget(entity, delta, towerPos);
            }
            return;
        }

        // ===== 3. Di chuyển đến waypoint hiện tại =====
        Vector2 targetPos = waypoints.get(currentIdx);
        moveTowardsTarget(entity, delta, targetPos);
    }

    /**
     * Di chuyển entity về phía mục tiêu (waypoint hoặc trụ)
     * Cập nhật vị trí, vận tốc, góc quay
     *
     * Issue 2: Dùng EPSILON = 0.001f thay vì 0.01f để tránh chia cho số quá nhỏ nhưng > 0
     * Issue 7: Đặt vận tốc về 0 khi đến nơi để tránh trôi
     */
    private void moveTowardsTarget(CombatEntity entity, float delta, Vector2 target) {
        Vector2 currentPos = entity.getPosition();
        float speed = entity.getSpeed();
        float step = speed * delta;

        float dx = target.x - currentPos.x;
        float dy = target.y - currentPos.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Di chuyển
        if (step >= distance) {
            currentPos.set(target);
            // Issue 7: Đặt vận tốc về 0 ngay khi đến nơi
            entity.getVelocity().setZero();
        } else if (distance > EPSILON) {  // Issue 2: EPSILON thay vì 0.01f
            float ratio = step / distance;
            currentPos.x += dx * ratio;
            currentPos.y += dy * ratio;

            // Cập nhật vận tốc hướng di chuyển
            entity.getVelocity().set(dx / distance, dy / distance).scl(speed);
            float angle = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;
            entity.setRotation(angle);
        } else {
            // Distance quá nhỏ -> dừng ngay
            entity.getVelocity().setZero();
        }
    }

    /**
     * Tìm đối thủ gần nhất trong tầm tấn công
     * - Soldier tìm Enemy
     * - Enemy tìm Soldier
     *
     * Issue 8: Dùng isDead() thay vì kiểm tra getHealth() > 0
     */
    private CombatEntity findNearestEnemyInRange(CombatEntity entity) {
        float range = entity.getAttackRange();
        float rangeSq = range * range;
        Vector2 pos = entity.getPosition();

        CombatEntity nearest = null;
        float minDistSq = rangeSq;

        if (entity instanceof Soldier) {
            // Soldier tìm Enemy
            for (Enemy enemy : entityManager.getEnemies()) {
                if (enemy.isDead() || !enemy.isActive()) continue;  // Issue 8
                float distSq = pos.dst2(enemy.getPosition());
                if (distSq < minDistSq) {
                    minDistSq = distSq;
                    nearest = enemy;
                }
            }
        } else if (entity instanceof Enemy) {
            // Enemy tìm Soldier
            for (Soldier soldier : entityManager.getSoldiers()) {
                if (soldier.isDead() || !soldier.isActive()) continue;  // Issue 8
                float distSq = pos.dst2(soldier.getPosition());
                if (distSq < minDistSq) {
                    minDistSq = distSq;
                    nearest = soldier;
                }
            }
        }

        return nearest;
    }

    /**
     * Lấy vị trí trụ mà entity hướng tới
     */
    private Vector2 getTargetTowerPosition(CombatEntity entity) {
        if (entity instanceof Enemy) {
            return mapManager.getPlayerCastlePosition();   // Enemy tấn công lâu đài của player
        } else {
            return mapManager.getEnemyBasePosition();      // Soldier tấn công base của enemy
        }
    }

    /**
     * Issue 3: Lấy tầm tấn công trụ
     * Ưu tiên lấy từ entity nếu có, ngược lại dùng attack range thường
     */
    private float getTowerAttackRange(CombatEntity entity) {
        // Hiện tại dùng attack range thường; có thể extend để entity có getTowerAttackRange() riêng
        return entity.getAttackRange();
    }

    /**
     * Issue 1: Kiểm tra xem target còn hợp lệ không
     * - Target phải còn sống
     * - Target phải còn trong tầm tấn công
     * - Target phải là kẻ thù (cùng phe thì không đánh)
     */
    private boolean isTargetValid(CombatEntity entity) {
        long targetId = entity.getTargetId();

        // ID = 0 = trụ, luôn hợp lệ (tấn công trụ không cần kiểm tra)
        if (targetId == 0) {
            return true;
        }

        // Tìm target trong danh sách
        CombatEntity target = null;
        if (entity instanceof Soldier) {
            for (Enemy enemy : entityManager.getEnemies()) {
                if (enemy.getId() == targetId) {
                    target = enemy;
                    break;
                }
            }
        } else if (entity instanceof Enemy) {
            for (Soldier soldier : entityManager.getSoldiers()) {
                if (soldier.getId() == targetId) {
                    target = soldier;
                    break;
                }
            }
        }

        if (target == null) return false;  // Target không tồn tại
        if (target.isDead()) return false;  // Target đã chết
        if (!target.isActive()) return false;  // Target không active

        // Kiểm tra xem target còn trong tầm không
        float rangeSquared = entity.getAttackRange() * entity.getAttackRange();
        return entity.getPosition().dst2(target.getPosition()) <= rangeSquared;
    }

    /**
     * Thiết lập đường đi cho entity và reset trạng thái
     *
     * Issue 4: Hiện tại giữ static vì là utility method, nhưng có thể chuyển non-static nếu cần mock test
     */
    public void setPath(CombatEntity entity, Array<Vector2> waypoints) {
        if (entity != null) {
            entity.setWaypoints(waypoints);
            entity.setCurrentWaypointIndex(0);
            entity.setState(CombatEntity.State.MOVING);
            entity.setTargetId(0);
        }
    }
}
