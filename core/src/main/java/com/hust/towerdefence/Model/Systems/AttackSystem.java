// ===================== AttackSystem.java =====================
package com.hust.towerdefence.Model.Systems;

import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Projectile.Projectile;
import com.hust.towerdefence.Model.Managers.EntityManager;
import com.hust.towerdefence.Model.GameWorld;

public class AttackSystem {
    private final GameWorld world;

    public AttackSystem(GameWorld world) {
        this.world = world;
    }

    public void update(float delta) {
        EntityManager entityManager = world.getEntityManager();
        TargetingSystem targeting = world.getTargetingSystem();

        for (BaseEntity entity : entityManager.getAllEntities()) {
            if (!entity.isActive() || !(entity instanceof CombatEntity)) continue;
            CombatEntity combatant = (CombatEntity) entity;

            // Giảm cooldown
            if (combatant.getCurrentCooldown() > 0) {
                combatant.reduceCooldown(delta);
            }

            // Kiểm tra điều kiện tấn công
            BaseEntity target = combatant.getCurrentTarget();
            if (target == null || !target.isActive() || target.isDead()) continue;

            if (combatant.getCurrentCooldown() <= 0 && isWithinRange(combatant, target)) {
                // Thực hiện tấn công
                performAttack(combatant, target, entityManager);

                // Reset cooldown
                combatant.resetCooldown();
            }
        }
    }

    private boolean isWithinRange(CombatEntity attacker, BaseEntity target) {
        float dx = target.getX() - attacker.getX();
        float dy = target.getY() - attacker.getY();
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        return dist <= attacker.getAttackRange();
    }

    private void performAttack(CombatEntity attacker, BaseEntity target, EntityManager em) {
        // Nếu là tấn công cận chiến (không có projectile)
        if (attacker.isMelee()) {
            world.getHealthSystem().applyDamage(target, attacker.getAttackDamage(), attacker);
        } else {
            // Tạo đạn và thêm vào thế giới
            Projectile projectile = attacker.createProjectile(target); // Hàm factory trong CombatEntity
            if (projectile != null) {
                em.addEntity(projectile);
            }
        }
    }
}
