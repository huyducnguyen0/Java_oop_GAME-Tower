package com.hust.towerdefence.Model.Systems;

import com.badlogic.gdx.utils.Array;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Managers.EntityManager;
public class AttackSystem {
    private EntityManager entityManager;
    private HealthSystem healthSystem;

    public AttackSystem(EntityManager entityManager, HealthSystem healthSystem) {
        this.entityManager = entityManager;
        this.healthSystem = healthSystem;
    }

    public void update(float delta) {
        Array<CombatEntity> combatants = entityManager.getAllActiveCombatUnits();
        for (CombatEntity attacker : combatants) {
            if (attacker.isDead() || attacker.getCurrentState() != CombatEntity.State.ATTACKING) {
                continue;
            }

            // Giảm cooldown
            float cd = attacker.getCooldownTimer();
            if (cd > 0) {
                cd -= delta;
                if (cd < 0) cd = 0;
                attacker.setCooldownTimer(cd);
            }

            // Chỉ tấn công nếu hết cooldown
            if (attacker.getCooldownTimer() <= 0) {
                long targetId = attacker.getTargetId();
                if (targetId != -1) {
                    CombatEntity target = entityManager.getEntityById(targetId,CombatEntity.class);
                    if(target != null && !target.isDead()){
                        float dist = attacker.getPosition().dst(target.getPosition());
                        if (dist <= attacker.getAttackRange()) {
                            // Gây sát thương
                            healthSystem.takeDamage(target, attacker.getAttackDamage());
                            // Reset cooldown
                            attacker.setCooldownTimer(attacker.getCooldownDuration());
                        }
                    }else  {
                        attacker.setTargetId(-1);
                    }
                }
            }
        }
    }
}
