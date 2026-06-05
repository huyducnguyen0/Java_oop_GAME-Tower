package com.hust.towerdefence.Model.Systems;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector2;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.TNT;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Archer;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Healer;
import com.hust.towerdefence.Model.Entities.Tower.BaseTower;
import com.hust.towerdefence.Model.Entities.Tower.DefenseTower;
import com.hust.towerdefence.Model.Managers.EntityManager;

public class AttackSystem {
    private static final float TILE_RANGE_SCALE = 64f;
    private static final float TILE_RANGE_THRESHOLD = 10f;
    private static final float HEALER_POISON_RADIUS = 72f;
    private static final float HEALER_POISON_DAMAGE_SCALE = 0.6f;
    private static final float ARCHER_PROJECTILE_Y_OFFSET = 42f;
    private static final float ARCHER_PROJECTILE_FORWARD_OFFSET = 16f;
    private static final float TNT_PROJECTILE_Y_OFFSET = 34f;
    private static final float TNT_PROJECTILE_FORWARD_OFFSET = 12f;

    private final EntityManager entityManager;
    private final HealthSystem healthSystem;
    private final Array<CombatVisualEvent> visualEvents = new Array<>();

    public AttackSystem(EntityManager entityManager, HealthSystem healthSystem) {
        this.entityManager = entityManager;
        this.healthSystem = healthSystem;
    }

    public void update(float delta) {
        Array<CombatEntity> combatants = entityManager.getAllActiveCombatUnits();
        for (CombatEntity attacker : combatants) {
            boolean healing = attacker instanceof Healer
                && attacker.getCurrentState() == CombatEntity.State.HEALING;
            boolean attacking = attacker.getCurrentState() == CombatEntity.State.ATTACKING;
            if (attacker.isDead() || (!attacking && !healing)) {
                continue;
            }

            float cooldown = attacker.getCooldownTimer();
            if (cooldown > 0) {
                cooldown -= delta;
                attacker.setCooldownTimer(Math.max(0, cooldown));
            }

            if (attacker.getCooldownTimer() > 0) continue;

            long targetId = attacker.getTargetId();
            if (targetId == -1) continue;

            CombatEntity target = entityManager.getEntityById(targetId, CombatEntity.class);
            if (target == null || target.isDead()) {
                attacker.setTargetId(-1);
                continue;
            }

            float dist = attackDistance(attacker, target);
            if (dist > effectiveRange(attacker)) continue;

            if (healing) {
                if (target.getTeam() == attacker.getTeam() && target.getHealth() < target.getMaxHealth()) {
                    addVisualEvent(attacker, target, CombatVisualEvent.Type.HEAL);
                    healthSystem.heal(target, attacker.getAttackDamage());
                }
            } else if (attacker instanceof Healer) {
                addVisualEvent(attacker, target, CombatVisualEvent.Type.POISON);
                applyHealerPoison((Healer) attacker, target);
            } else {
                addAttackVisualEvent(attacker, target);
                healthSystem.takeDamage(target, attacker.getAttackDamage());
            }

            attacker.setCooldownTimer(attacker.getCooldownDuration());
        }
    }

    private void applyHealerPoison(Healer healer, CombatEntity target) {
        float radiusSq = HEALER_POISON_RADIUS * HEALER_POISON_RADIUS;
        float damage = healer.getAttackDamage() * HEALER_POISON_DAMAGE_SCALE;
        Vector2 center = getVisualEnd(healer, target);

        for (CombatEntity entity : entityManager.getAllActiveCombatUnits()) {
            if (entity.isDead() || entity.isRemoved()) continue;
            if (entity.getTeam() == healer.getTeam()) continue;
            if (entity.getPosition().dst2(center) <= radiusSq) {
                healthSystem.takeDamage(entity, damage);
            }
        }
    }

    public Array<CombatVisualEvent> consumeVisualEvents() {
        Array<CombatVisualEvent> events = new Array<>(visualEvents);
        visualEvents.clear();
        return events;
    }

    private void addAttackVisualEvent(CombatEntity attacker, CombatEntity target) {
        if (attacker instanceof Archer || attacker instanceof DefenseTower) {
            addVisualEvent(attacker, target, CombatVisualEvent.Type.ARROW);
        } else if (attacker instanceof TNT) {
            addVisualEvent(attacker, target, CombatVisualEvent.Type.DYNAMITE);
        }
    }

    private void addVisualEvent(CombatEntity source, CombatEntity target, CombatVisualEvent.Type type) {
        visualEvents.add(new CombatVisualEvent(type, source.getTeam(), getVisualStart(source), getVisualEnd(source, target)));
    }

    private Vector2 getVisualStart(CombatEntity source) {
        if (source instanceof DefenseTower) {
            float towerWidth = source.getWidth();
            float towerHeight = towerWidth * 2f;
            return new Vector2(
                source.getX(),
                source.getY() - source.getHeight() / 2f + towerHeight * 0.58f + 42f
            );
        }
        if (source instanceof Archer) {
            return new Vector2(
                source.getX() + source.getFacing().x * ARCHER_PROJECTILE_FORWARD_OFFSET,
                source.getY() + ARCHER_PROJECTILE_Y_OFFSET
            );
        }
        if (source instanceof TNT) {
            return new Vector2(
                source.getX() + source.getFacing().x * TNT_PROJECTILE_FORWARD_OFFSET,
                source.getY() + TNT_PROJECTILE_Y_OFFSET
            );
        }
        return source.getPosition();
    }

    private Vector2 getVisualEnd(CombatEntity source, CombatEntity target) {
        if (target instanceof BaseTower) {
            return closestAttackPoint(source, target);
        }
        return target.getPosition();
    }

    private float effectiveRange(CombatEntity entity) {
        float rawRange = entity.getAttackRange();
        return rawRange <= TILE_RANGE_THRESHOLD ? rawRange * TILE_RANGE_SCALE : rawRange;
    }

    private float attackDistance(CombatEntity source, CombatEntity target) {
        if (target instanceof BaseTower) {
            return source.getPosition().dst(closestAttackPoint(source, target));
        }
        return source.getPosition().dst(target.getPosition());
    }

    private Vector2 closestAttackPoint(CombatEntity source, CombatEntity target) {
        float halfWidth = target.getWidth() / 2f;
        float halfHeight = target.getHeight() / 2f;
        float minX = target.getX() - halfWidth;
        float maxX = target.getX() + halfWidth;
        float minY = target.getY() - halfHeight;
        float maxY = target.getY() + halfHeight;
        float closestX = Math.max(minX, Math.min(source.getX(), maxX));
        float closestY = Math.max(minY, Math.min(source.getY(), maxY));
        return new Vector2(closestX, closestY);
    }
}
