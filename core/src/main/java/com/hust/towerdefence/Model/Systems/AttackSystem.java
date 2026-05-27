package com.hust.towerdefence.Model.Systems;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector2;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.TNT;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Archer;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Healer;
import com.hust.towerdefence.Model.Entities.Tower.DefenseTower;
import com.hust.towerdefence.Model.Managers.EntityManager;

public class AttackSystem {
    private static final float TILE_RANGE_SCALE = 64f;
    private static final float TILE_RANGE_THRESHOLD = 10f;

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

            float dist = attacker.getPosition().dst(target.getPosition());
            if (dist > effectiveRange(attacker)) continue;

            if (healing) {
                if (target.getTeam() == attacker.getTeam() && target.getHealth() < target.getMaxHealth()) {
                    addVisualEvent(attacker, target, CombatVisualEvent.Type.HEAL);
                    healthSystem.heal(target, attacker.getAttackDamage());
                }
            } else {
                addAttackVisualEvent(attacker, target);
                healthSystem.takeDamage(target, attacker.getAttackDamage());
            }

            attacker.setCooldownTimer(attacker.getCooldownDuration());
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
        visualEvents.add(new CombatVisualEvent(type, source.getTeam(), getVisualStart(source), target.getPosition()));
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
        return source.getPosition();
    }

    private float effectiveRange(CombatEntity entity) {
        float rawRange = entity.getAttackRange();
        return rawRange <= TILE_RANGE_THRESHOLD ? rawRange * TILE_RANGE_SCALE : rawRange;
    }
}
