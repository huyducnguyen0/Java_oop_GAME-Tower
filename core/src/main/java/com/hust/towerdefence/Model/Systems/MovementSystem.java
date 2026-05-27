package com.hust.towerdefence.Model.Systems;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity.State;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Healer;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Miner;
import com.hust.towerdefence.Model.Entities.Tower.BaseTower;
import com.hust.towerdefence.Model.Entities.Tower.MainTower;
import com.hust.towerdefence.Model.Managers.EconomyManager;
import com.hust.towerdefence.Model.Managers.EntityManager;
import com.hust.towerdefence.Model.Managers.MapManager;

public class MovementSystem {
    private static final float MINING_DURATION = 3.0f;
    private static final int GOLD_PER_TRIP = 50;
    private static final float TILE_RANGE_SCALE = 64f;
    private static final float TILE_RANGE_THRESHOLD = 10f;

    private final EntityManager entityManager;
    private final MainTower mainTower;
    private final MainTower enemyTower;
    private final MapManager mapManager;
    private final ObjectMap<CombatEntity, PausedPath> pausedPaths = new ObjectMap<>();

    public MovementSystem(EntityManager entityManager, MainTower mainTower, MainTower enemyTower, MapManager mapManager) {
        this.entityManager = entityManager;
        this.mainTower = mainTower;
        this.enemyTower = enemyTower;
        this.mapManager = mapManager;
    }

    public void update(float delta) {
        Array<CombatEntity> entities = entityManager.getAllActiveCombatUnits();
        for (CombatEntity entity : entities) {
            if (entity.isDead() || entity.isRemoved()) continue;

            if (entity instanceof BaseTower) {
                updateTower((BaseTower) entity);
                continue;
            }

            if (entity instanceof Miner) {
                updateMiner((Miner) entity, delta);
                continue;
            }

            if (entity.getPath() != null && entity.getPath().size > 0) {
                long targetId = entity.getTargetId();
                if (targetId != -1) {
                    CombatEntity target = entityManager.getEntityById(targetId, CombatEntity.class);
                    if (target != null && !target.isDead()) {
                        pausedPaths.put(entity, new PausedPath(
                            new Array<>(entity.getPath()),
                            entity.getCurrentPathIndex()
                        ));
                        entity.setPath(null);
                    }
                }
            }

            updateCombatEntity(entity, delta);
        }
    }

    private void updateTower(BaseTower tower) {
        long targetId = tower.getTargetId();
        CombatEntity target = targetId != -1 ? entityManager.getEntityById(targetId, CombatEntity.class) : null;
        if (target != null && !target.isDead() && tower.getPosition().dst(target.getPosition()) <= effectiveRange(tower)) {
            tower.setState(State.ATTACKING);
        } else {
            tower.setState(State.IDLE);
        }
    }

    private void updateCombatEntity(CombatEntity entity, float delta) {
        if (entity.getCurrentState() == State.DYING) return;

        long targetId = entity.getTargetId();
        CombatEntity target = targetId != -1 ? entityManager.getEntityById(targetId, CombatEntity.class) : null;
        boolean hasValidTarget = target != null && !target.isDead();

        State newState;
        if (hasValidTarget) {
            float dist = entity.getPosition().dst(target.getPosition());
            if (dist <= effectiveRange(entity)) {
                newState = entity instanceof Healer ? State.HEALING : State.ATTACKING;
            } else {
                newState = State.MOVING;
            }
        } else if (entity.getPath() != null && entity.getPath().size > 0) {
            newState = State.MOVING;
        } else {
            newState = State.IDLE;
        }

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

        switch (newState) {
            case MOVING:
                if (entity.getPath() != null && entity.getPath().size > 0) {
                    followPath(entity, delta);
                } else if (hasValidTarget) {
                    moveTowards(entity, target.getPosition(), delta);
                }
                break;
            case ATTACKING:
            case HEALING:
            case IDLE:
            default:
                break;
        }
    }

    private void updateMiner(Miner miner, float delta) {
        switch (miner.getCurrentState()) {
            case GOING_TO_MINE:
                if (miner.getPath() == null || miner.getPath().size == 0) {
                    miner.setState(State.MINING);
                    miner.setMiningTimer(MINING_DURATION);
                } else {
                    followPath(miner, delta);
                }
                break;
            case MINING:
                miner.setMiningTimer(miner.getMiningTimer() - delta);
                if (miner.getMiningTimer() <= 0) {
                    miner.setCarriedGold(GOLD_PER_TRIP);
                    miner.setState(State.RETURNING_HOME);

                    Array<Vector2> toMinePath = mapManager.getWaypoints(miner);
                    Array<Vector2> returnPath = new Array<>();
                    for (int i = toMinePath.size - 1; i >= 0; i--) {
                        returnPath.add(toMinePath.get(i).cpy());
                    }
                    miner.setPath(returnPath);
                    miner.setCurrentPathIndex(0);
                }
                break;
            case RETURNING_HOME:
                if (miner.getPath() == null || miner.getPath().size == 0) {
                    EconomyManager.getInstance().addGold(miner.getCarriedGold());
                    miner.setCarriedGold(0);
                    miner.setState(State.GOING_TO_MINE);
                    miner.setPath(mapManager.getWaypoints(miner));
                    miner.setCurrentPathIndex(0);
                } else {
                    followPath(miner, delta);
                }
                break;
            default:
                break;
        }
    }

    private void followPath(CombatEntity entity, float delta) {
        Array<Vector2> path = entity.getPath();
        int index = entity.getCurrentPathIndex();

        if (index >= path.size) {
            entity.setPath(null);
            entity.setCurrentPathIndex(0);
            onReachDestination(entity);
            return;
        }

        Vector2 targetWaypoint = path.get(index);
        Vector2 pos = entity.getPosition();
        float dist = pos.dst(targetWaypoint);
        if (dist < 1.0f) {
            entity.setCurrentPathIndex(index + 1);
        } else {
            Vector2 direction = targetWaypoint.cpy().sub(pos).nor();
            float step = entity.getSpeed() * delta;
            if (step >= dist) {
                pos.set(targetWaypoint);
                entity.setCurrentPathIndex(index + 1);
            } else {
                pos.add(direction.scl(step));
            }
        }
    }

    private void onReachDestination(CombatEntity entity) {
        if (entity.getTeam() == BaseEntity.Team.ENEMY) {
            entity.setTargetId(mainTower.getId());
        } else if (entity.getTeam() == BaseEntity.Team.SOLDIER) {
            entity.setTargetId(enemyTower.getId());
        }
    }

    private void moveTowards(CombatEntity entity, Vector2 targetPos, float delta) {
        Vector2 pos = entity.getPosition();
        float distance = pos.dst(targetPos);
        float range = effectiveRange(entity);
        if (distance <= range) return;

        Vector2 direction = targetPos.cpy().sub(pos).nor();
        float step = entity.getSpeed() * delta;
        float stopDistance = Math.max(0, distance - range);
        if (step > stopDistance) {
            step = stopDistance;
        }
        pos.add(direction.scl(step));
    }

    private float effectiveRange(CombatEntity entity) {
        float rawRange = entity.getAttackRange();
        return rawRange <= TILE_RANGE_THRESHOLD ? rawRange * TILE_RANGE_SCALE : rawRange;
    }

    private static class PausedPath {
        private final Array<Vector2> path;
        private final int currentIndex;

        private PausedPath(Array<Vector2> path, int currentIndex) {
            this.path = path;
            this.currentIndex = currentIndex;
        }
    }
}
