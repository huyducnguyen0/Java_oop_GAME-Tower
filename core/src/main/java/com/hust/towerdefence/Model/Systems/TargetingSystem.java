package com.hust.towerdefence.Model.Systems;

import com.badlogic.gdx.utils.Array;
import com.hust.towerdefence.Model.Entities.BaseEntity.Team;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.Enemy;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Healer;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Miner;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Soldier;
import com.hust.towerdefence.Model.Entities.Tower.BaseTower;
import com.hust.towerdefence.Model.Managers.EntityManager;

public class TargetingSystem {
    private static final float TILE_RANGE_SCALE = 64f;
    private static final float TILE_RANGE_THRESHOLD = 10f;

    private final EntityManager entityManager;

    public TargetingSystem(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void update(float delta) {
        Array<CombatEntity> combatants = entityManager.getAllActiveCombatUnits();
        for (CombatEntity entity : combatants) {
            if (entity.isDead() || entity.isRemoved() || entity instanceof Miner) continue;

            boolean healer = entity instanceof Healer;
            CombatEntity currentTarget = null;
            long targetId = entity.getTargetId();
            if (targetId != -1) {
                currentTarget = entityManager.getEntityById(targetId, CombatEntity.class);
            }

            if (currentTarget != null) {
                if (isInvalidTarget(entity, currentTarget, healer)) {
                    entity.setTargetId(-1);
                }
            } else if (targetId != -1) {
                entity.setTargetId(-1);
            }

            if (entity.getTargetId() == -1) {
                CombatEntity newTarget = healer ? findNearestInjuredAlly(entity) : findNearestEnemy(entity);
                if (newTarget != null) {
                    entity.setTargetId(newTarget.getId());
                }
            }
        }
    }

    private boolean isInvalidTarget(CombatEntity source, CombatEntity target, boolean healer) {
        if (target.isDead() || target.isRemoved()) return true;
        if (healer) {
            return target == source
                || target.getTeam() != source.getTeam()
                || target.getHealth() >= target.getMaxHealth();
        }
        if (source instanceof BaseTower && !(target instanceof Soldier || target instanceof Enemy)) return true;
        return target.getTeam() == source.getTeam() || target instanceof Miner;
    }

    public CombatEntity findNearestInjuredAlly(CombatEntity source) {
        Team myTeam = source.getTeam();
        CombatEntity nearest = null;
        float minDist = Float.MAX_VALUE;

        for (CombatEntity other : entityManager.getAllActiveCombatUnits()) {
            if (other == source || other.isDead() || other.isRemoved()) continue;
            if (other.getTeam() != myTeam) continue;
            if (other.getHealth() >= other.getMaxHealth()) continue;

            float dist = source.getPosition().dst2(other.getPosition());
            float rangeSq = effectiveRange(source) * effectiveRange(source);
            if (dist <= rangeSq && dist < minDist) {
                minDist = dist;
                nearest = other;
            }
        }
        return nearest;
    }

    public CombatEntity findNearestEnemy(CombatEntity source) {
        Team myTeam = source.getTeam();
        CombatEntity nearest = null;
        float minDist = Float.MAX_VALUE;

        for (CombatEntity other : entityManager.getAllActiveCombatUnits()) {
            if (other == source || other.isDead() || other.isRemoved()) continue;
            if (other.getTeam() == myTeam) continue;
            if (other instanceof Miner) continue;
            if (source instanceof BaseTower && !(other instanceof Soldier || other instanceof Enemy)) continue;

            float dist = source.getPosition().dst2(other.getPosition());
            float rangeSq = effectiveRange(source) * effectiveRange(source);
            if (dist <= rangeSq && dist < minDist) {
                minDist = dist;
                nearest = other;
            }
        }
        return nearest;
    }

    private float effectiveRange(CombatEntity entity) {
        float rawRange = entity.getAttackRange();
        return rawRange <= TILE_RANGE_THRESHOLD ? rawRange * TILE_RANGE_SCALE : rawRange;
    }
}
