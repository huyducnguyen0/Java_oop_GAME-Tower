package com.hust.towerdefence.Model.AI;

 import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.Flee;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.ai.utils.Location;
import com.hust.towerdefence.Model.Entities.Enemy;
import com.hust.towerdefence.Model.Entities.Unit;
import com.hust.towerdefence.Model.Entities.GameEntity;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.EntityManager;
import java.util.List;

/**
 * AI Controller điều khiển logic của Enemy
 * Hỗ trợ:
 * - Tìm mục tiêu (nearest unit)
 * - Quyết định hành động (attack, chase, retreat)
 * - Steering Behaviors: Seek, Arrive, Flee, Wander, Separation
 * - Behavior tree & State Machine
 */
public class AIController {
    public enum AIState { IDLE, CHASE, ATTACK, RETREAT }

    private final Enemy enemy;
    private AIState currentState = AIState.IDLE;
    private Unit targetUnit = null;
    private float decisionTimer = 0;
    private static final float DECISION_INTERVAL = 1.0f;  // Quyết định mỗi 1 giây

    // Steering behaviors
    private final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<>(new Vector2());
    private Arrive<Vector2> arriveBehavior;
    private Flee<Vector2> fleeBehavior;
    private Wander<Vector2> wanderBehavior;

    public AIController(Enemy enemy) {
        this.enemy = enemy;
        initializeBehaviors();
    }

    /**
     * Khởi tạo tất cả steering behaviors
     */
    private void initializeBehaviors() {
        // Arrive behavior - giảm tốc khi gần target
        arriveBehavior = new Arrive<>(enemy)
            .setTimeToTarget(0.1f)
            .setArrivalTolerance(15f);

        // Flee behavior - chạy trốn xa khỏi target
        fleeBehavior = new Flee<>(enemy);

        // Wander behavior - roaming ngẫu nhiên
        wanderBehavior = new Wander<>(enemy)
            .setWanderOffset(50f)
            .setWanderOrientation(0)
            .setWanderRadius(30f)
            .setWanderRate(5f);
    }

    /**
     * Update AI logic mỗi frame
     */
    public void update(GameWorld world, EntityManager em, float delta) {
        decisionTimer += delta;

        // === Quá trình quyết định ===
        if (decisionTimer >= DECISION_INTERVAL) {
            decisionTimer = 0;
            makeDecision(world, em);
        }

        // === Thực hiện hành động dựa trên state ===
        executeAction(delta);
    }

    /**
     * Ra quyết định hành động (1 lần mỗi giây)
     */
    private void makeDecision(GameWorld world, EntityManager em) {
        List<Unit> units = em.getUnits();

        if (units.isEmpty()) {
            // Nếu không có unit → đi thẳng về thành
            currentState = AIState.CHASE;
            return;
        }

        // 1. Tìm Unit gần nhất
        targetUnit = findNearestUnit(units);
        if (targetUnit == null) return;

        float distToUnit = getDistance(enemy.getX(), enemy.getY(),
                                       targetUnit.getX(), targetUnit.getY());
        float distToBase = getDistance(enemy.getX(), enemy.getY(),
                                       world.BASE_X, world.BASE_Y);

        // 2. Quyết định hành động dựa trên điều kiện
        if (distToUnit < 100 && enemy.getHp() > enemy.getMaxHp() * 0.5f) {
            // Đủ gần unit + HP cao → TẤN CÔNG
            currentState = AIState.ATTACK;
        } else if (distToUnit < 200 && enemy.getHp() > enemy.getMaxHp() * 0.3f) {
            // Gần unit + HP trung bình → ĐUỔI THEO
            currentState = AIState.CHASE;
        } else if (enemy.getHp() < enemy.getMaxHp() * 0.2f && distToBase > distToUnit) {
            // HP thấp + thành xa hơn unit → RÚT LUI
            currentState = AIState.RETREAT;
        } else {
            // Mặc định → ĐUỔI THEO
            currentState = AIState.CHASE;
        }
    }

    /**
     * Thực hiện hành động dựa trên state
     */
    private void executeAction(float delta) {
        switch (currentState) {
            case IDLE:
                 // Roaming - wandering around
                 wanderBehavior(delta);
                 break;

             case CHASE:
                 // Đuổi theo unit
                 if (targetUnit != null) {
                     chaseBehavior(targetUnit);
                 }
                 break;

             case ATTACK:
                 // Tấn công unit
                 if (targetUnit != null) {
                     attackBehavior(targetUnit);
                 }
                 break;

             case RETREAT:
                 // Rút lui - chạy trốn về phía xa nhất từ unit
                 if (targetUnit != null) {
                     retreatBehavior(targetUnit);
                 }
                 break;
        }
    }

    /**
     * Behavior: Đuổi theo Unit (dùng Arrive để giảm tốc thông minh)
     */
    private void chaseBehavior(Unit target) {
        // Tạo target location cho Arrive
        Location<Vector2> targetLoc = createLocationFromEntity(target);

        // Cập nhật target cho Arrive behavior
        arriveBehavior.setTarget(targetLoc);

        // Tính toán steering output
        steeringOutput.setZero();
        arriveBehavior.calculateSteering(steeringOutput);

        // Áp dụng vận tốc
        applySteering(steeringOutput, 0.016f);
        enemy.setState(Unit.State.WALKING);
    }

    /**
     * Behavior: Tấn công Unit
     */
    private void attackBehavior(Unit target) {
        float dist = getDistance(enemy.getX(), enemy.getY(),
                                 target.getX(), target.getY());

        if (dist < 40) {
            // Gần đủ để tấn công - đứng yên tấn công
            enemy.setState(Unit.State.ATTACKING);

            // Gây damage mỗi 0.5 giây
            if (enemy.getStateTime() - enemy.getLastActionTime() > 0.5f) {
                int damage = enemy.getAtk();
                target.setHp(target.getHp() - damage);
                enemy.setLastActionTime(enemy.getStateTime());

                // Nếu target chết
                if (target.getHp() <= 0) {
                    target.setState(GameEntity.State.DEAD);
                }
            }
        } else {
            // Chưa gần - tiếp tục đuổi theo
            chaseBehavior(target);
        }
    }

     /**
     * Behavior: Rút lui - chạy trốn (dùng Flee behavior)
     */
    private void retreatBehavior(Unit threat) {
        // Tạo location từ threat
        Location<Vector2> threatLoc = createLocationFromEntity(threat);

        // Cập nhật target cho Flee behavior
        fleeBehavior.setTarget(threatLoc);

        // Tính toán steering output
        steeringOutput.setZero();
        fleeBehavior.calculateSteering(steeringOutput);

        // Áp dụng vận tốc
        applySteering(steeringOutput, 0.016f);
        enemy.setState(Unit.State.WALKING);
    }

     /**
     * Behavior: Wandering - roaming ngẫu nhiên
     */
    private void wanderBehavior(float delta) {
        steeringOutput.setZero();
        wanderBehavior.calculateSteering(steeringOutput);
        applySteering(steeringOutput, delta);
        enemy.setState(Unit.State.WALKING);
    }

    /**
     * Tạo Location object từ một entity
     */
    private Location<Vector2> createLocationFromEntity(GameEntity entity) {
        return new Location<Vector2>() {
            final Vector2 pos = new Vector2(entity.getX(), entity.getY());
            @Override public Vector2 getPosition() {
                return pos.set(entity.getX(), entity.getY());
            }
            @Override public float getOrientation() { return 0; }
            @Override public void setOrientation(float orientation) {}
            @Override public Location<Vector2> newLocation() { return this; }
            @Override public float vectorToAngle(Vector2 vector) {
                return (float)Math.atan2(vector.y, vector.x);
            }
            @Override public Vector2 angleToVector(Vector2 outVector, float angle) {
                outVector.x = (float)Math.cos(angle);
                outVector.y = (float)Math.sin(angle);
                return outVector;
            }
        };
    }


    /**
     * Áp dụng steering output để di chuyển enemy
     */
    private void applySteering(SteeringAcceleration<Vector2> steering, float delta) {
        // Cập nhật vận tốc
        Vector2 vel = enemy.getLinearVelocity();
        vel.add(steering.linear.scl(delta));

        // Giới hạn tốc độ
        if (vel.len() > enemy.getMaxLinearSpeed()) {
            vel.nor().scl(enemy.getMaxLinearSpeed());
        }

        // Áp dụng vận tốc để di chuyển
        enemy.setPosition(enemy.getX() + vel.x * delta, enemy.getY() + vel.y * delta);
    }

    /**
     * Tìm Unit gần nhất (mục tiêu)
     */
    private Unit findNearestUnit(List<Unit> units) {
        Unit nearest = null;
        float minDist = Float.MAX_VALUE;

        for (Unit u : units) {
            if (u.getState() == Unit.State.DEAD) continue;

            float dist = getDistance(enemy.getX(), enemy.getY(), u.getX(), u.getY());
            if (dist < minDist) {
                minDist = dist;
                nearest = u;
            }
        }

        return nearest;
    }

    /**
     * Tính khoảng cách giữa 2 điểm
     */
    private float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.hypot(x2 - x1, y2 - y1);
    }

    // === Getters ===
    public AIState getCurrentState() { return currentState; }
    public Unit getTargetUnit() { return targetUnit; }
    public void setCurrentState(AIState state) { this.currentState = state; }
}
