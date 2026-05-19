package com.hust.towerdefence.Model.Systems;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.BaseEntity.Team;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity.State;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Miner;
import com.hust.towerdefence.Model.Entities.Tower.MainTower;
import com.hust.towerdefence.Model.Managers.EconomyManager;
import com.hust.towerdefence.Model.Managers.EntityManager;
import com.hust.towerdefence.Model.Managers.MapManager;

public class MovementSystem {
    private EntityManager entityManager;
    private MainTower mainTower;
    private MainTower enemyTower;
    private MapManager mapManager;
    private static final float MINING_DURATION = 3.0f;
    private static final int GOLD_PER_TRIP = 50;

    public MovementSystem(EntityManager entityManager, MainTower mainTower,MainTower enemyTower, MapManager mapManager) {
        this.entityManager = entityManager;
        this.mainTower = mainTower;
        this.enemyTower = enemyTower;
        this.mapManager = mapManager;
    }

    public void update(float delta) {
        Array<CombatEntity> entities = entityManager.getAllActiveCombatUnits();
        for (CombatEntity entity : entities) {
            if (entity.isDead() || entity.isRemoved()) continue;

            // Phân nhánh cho Miner (chu kỳ khai thác)
            if (entity instanceof Miner) {
                updateMiner((Miner) entity, delta);
                continue;
            }
            if (entity.getPath() != null && entity.getPath().size > 0) {
                long targetId = entity.getTargetId();
                if (targetId != -1) {
                    CombatEntity target = entityManager.getEntityById(targetId, CombatEntity.class);
                    if (target != null && !target.isDead()) {
                        // Lưu đường đi để sau đánh xong đi tiếp
                        pausedPaths.put(entity, new PausedPath(
                            new Array<>(entity.getPath()),
                            entity.getCurrentPathIndex()
                        ));
                        entity.setPath(null);
                    }
                }
            }

            // Logic cho các entity chiến đấu bình thường
            updateCombatEntity(entity, delta);
        }
    }

    private void updateCombatEntity(CombatEntity entity, float delta) {
        State currentState = entity.getCurrentState();

        // Nếu đang chết, không làm gì
        if (currentState == State.DYING) return;

        // Lấy thông tin mục tiêu
        long targetID = entity.getTargetId();
        CombatEntity target = (targetID != -1) ? entityManager.getEntityById(targetID, CombatEntity.class) : null;

        // Kiểm tra mục tiêu hợp lệ
        boolean hasValidTarget = (target != null && !target.isDead());

        // ---- Xác định state mới dựa trên điều kiện ----
        State newState = State.IDLE;

        if (hasValidTarget) {
            float dist = entity.getPosition().dst(target.getPosition());
            if (dist <= entity.getAttackRange()) {
                newState = State.ATTACKING;
            } else {
                newState = State.MOVING; // Đuổi theo mục tiêu
            }
        } else {
            // Không có mục tiêu -> nếu còn path thì đi theo waypoint, không thì IDLE
            if (entity.getPath() != null && entity.getPath().size > 0) {
                newState = State.MOVING;
            } else {
                newState = State.IDLE;
            }
        }

        // (Có thể ép kiểu HEALING nếu có logic hồi máu, tạm bỏ qua)

        // Cập nhật state
        entity.setState(newState);
        if (newState == State.IDLE && entity.getPath() == null) {
            PausedPath paused = pausedPaths.get(entity);
            if (paused != null) {
                entity.setPath(paused.path);
                entity.setCurrentPathIndex(paused.currentIndex);
                pausedPaths.remove(entity);
                newState = State.MOVING;
                entity.setState(newState);
            }
        }
        // ---- Thực hiện di chuyển theo state ----
        switch (newState) {
            case MOVING:
                // Nếu có path (waypoint) thì đi theo, ngược lại đi về phía mục tiêu
                if (entity.getPath() != null && entity.getPath().size > 0) {
                    followPath(entity, delta);
                } else if (hasValidTarget) {
                    moveTowards(entity, target.getPosition(), delta);
                }
                break;
            case ATTACKING:
                // Đứng yên, không di chuyển (nếu cần có thể tiến lại gần nếu hơi xa)
                break;
            case IDLE:
            case HEALING:
                // Không di chuyển
                break;
            default:
                break;
        }
    }

    private void updateMiner(Miner miner, float delta) {
        switch (miner.getCurrentState()) {
            case GOING_TO_MINE:
                // Di chuyển dọc path đến mỏ vàng
                if (miner.getPath() == null || miner.getPath().size == 0) {
                    // Đã đến mỏ -> bắt đầu đào
                    miner.setState(State.MINING);
                    miner.setMiningTimer(MINING_DURATION); // hằng số, ví dụ 5 giây
                } else {
                    followPath(miner, delta);
                }
                break;

            case MINING:
                // Đếm ngược thời gian đào
                miner.setMiningTimer(miner.getMiningTimer() - delta);
                if (miner.getMiningTimer() <= 0) {
                    // Đào xong, nhặt vàng và quay về
                    miner.setCarriedGold(GOLD_PER_TRIP);
                    miner.setState(State.RETURNING_HOME);
                    // Tạo đường về nhà: đảo ngược đường đi lúc đến mỏ
                    Array<Vector2> toMinePath = mapManager.getWaypoints(miner); // đường từ nhà đến mỏ
                    Array<Vector2> returnPath = new Array<>();
                    for (int i = toMinePath.size - 1; i >= 0; i--) {
                        returnPath.add(toMinePath.get(i).cpy());
                    }
                    miner.setPath(returnPath);
                    miner.setCurrentPathIndex(0);
                }
                break;

            case RETURNING_HOME:
                // Đi ngược về nhà chính
                if (miner.getPath() == null || miner.getPath().size == 0) {
                    // Về đến nơi -> nộp vàng, cộng tiền cho người chơi
                    EconomyManager.getInstance().addGold(miner.getCarriedGold());
                    miner.setCarriedGold(0);
                    miner.setState(State.GOING_TO_MINE);
                    // Gán lại đường đi ra mỏ cho chuyến sau
                    Array<Vector2> newToMinePath = mapManager.getWaypoints(miner);
                    miner.setPath(newToMinePath);
                    miner.setCurrentPathIndex(0);
                } else {
                    followPath(miner, delta);
                }
                break;

            case DYING:

                break;

            default:
                break;
        }
    }

    /** Đi dọc theo waypoint (dùng cho cả path thường) */
    private void followPath(CombatEntity entity, float delta) {
        Array<Vector2> path = entity.getPath();
        int idx = entity.getCurrentPathIndex();

        if (idx >= path.size) {
            entity.setPath(null);
            entity.setCurrentPathIndex(0);
            onReachDestination(entity);  // xử lý khi hết đường
            return;
        }

        Vector2 targetWP = path.get(idx);
        Vector2 pos = entity.getPosition();
        float dist = pos.dst(targetWP);
        if (dist < 1.0f) {
            entity.setCurrentPathIndex(idx + 1);
        } else {
            Vector2 dir = targetWP.cpy().sub(pos).nor();
            float step = entity.getSpeed() * delta;
            if (step >= dist) {
                pos.set(targetWP);
                entity.setCurrentPathIndex(idx + 1);
            } else {
                pos.add(dir.scl(step));
            }
        }
    }

    private void onReachDestination(CombatEntity entity) {
        if (entity.getTeam() == BaseEntity.Team.ENEMY) {
            // Địch tới cuối đường → nhắm nhà chính người chơi
            entity.setTargetId(mainTower.getId());
        } else if (entity.getTeam() == BaseEntity.Team.SOLDIER) {
            // Lính ta tới cuối đường → nhắm nhà chính địch
            entity.setTargetId(enemyTower.getId());
        }
        // Sau khi gán target, state sẽ được update trong frame tới
    }

    /** Di chuyển thẳng về phía một điểm (dùng khi đuổi mục tiêu) */
    private void moveTowards(CombatEntity entity, Vector2 targetPos, float delta) {
        Vector2 pos = entity.getPosition();
        float distance = pos.dst(targetPos);

        // Dừng lại nếu đã trong tầm đánh (tránh overshoot)
        if (distance <= entity.getAttackRange()) return;

        Vector2 dir = targetPos.cpy().sub(pos).nor();
        float step = entity.getSpeed() * delta;

        // Đảm bảo không tiến quá tầm đánh (dừng ở rìa tầm đánh)
        float stopDistance = Math.max(0, distance - entity.getAttackRange());
        if (step > stopDistance) {
            step = stopDistance;
        }
        pos.add(dir.scl(step));
    }
    private ObjectMap<CombatEntity, PausedPath> pausedPaths = new ObjectMap<>();

    private static class PausedPath {
        Array<Vector2> path;
        int currentIndex;
        PausedPath(Array<Vector2> path, int index) {
            this.path = path;
            this.currentIndex = index;
        }
    }
}
