// ===================== TargetingSystem.java =====================
package com.hust.towerdefence.Model.Systems;

import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Managers.EntityManager;

import com.hust.towerdefence.Model.GameWorld;
import java.util.List;

public class TargetingSystem {
    private final GameWorld world;
    private AIController aiController; // Sử dụng AI nếu cần chiến lược phức tạp

    public TargetingSystem(GameWorld world) {
        this.world = world;
        this.aiController = new AIController(); // hoặc inject từ world
    }

    public void update(float delta) {
        EntityManager entityManager = world.getEntityManager();

        // Lọc tất cả thực thể có khả năng tấn công (CombatEntity có attackRange > 0)
        for (BaseEntity entity : entityManager.getAllEntities()) {
            if (!entity.isActive() || !(entity instanceof CombatEntity)) continue;
            CombatEntity combatant = (CombatEntity) entity;

            // Kiểm tra nếu thực thể này có thể tấn công
            if (combatant.getAttackRange() <= 0) continue;

            // Nếu đang có mục tiêu, kiểm tra còn hợp lệ không
            BaseEntity currentTarget = combatant.getCurrentTarget();
            if (currentTarget != null && isTargetValid(combatant, currentTarget)) {
                continue; // Giữ nguyên mục tiêu
            }

            // Tìm mục tiêu mới
            BaseEntity newTarget = findTarget(combatant, entityManager);
            combatant.setCurrentTarget(newTarget);
        }
    }

    private boolean isTargetValid(CombatEntity attacker, BaseEntity target) {
        return target != null
            && target.isActive()
            && !target.isDead()
            && isEnemy(attacker, target)   // kẻ thù
            && isWithinRange(attacker, target);
    }

    private boolean isEnemy(CombatEntity attacker, BaseEntity target) {
        // Đơn giản: attacker là phe ta (Soldier, Tower) thì tìm Enemy; ngược lại attacker là Enemy thì tìm Soldier/Tower
        // Có thể dựa vào type hoặc teamID
        return attacker.isHostileTo(target);
    }

    private boolean isWithinRange(CombatEntity attacker, BaseEntity target) {
        float dx = target.getX() - attacker.getX();
        float dy = target.getY() - attacker.getY();
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        return dist <= attacker.getAttackRange();
    }

    private BaseEntity findTarget(CombatEntity attacker, EntityManager em) {
        // Sử dụng AI targeting nếu có nhiều chiến lược (gần nhất, yếu nhất, ...)
        // Ở đây mặc định lấy kẻ địch gần nhất
        List<BaseEntity> candidates = em.getHostileEntities(attacker); // giả sử EntityManager có phương thức này
        if (candidates.isEmpty()) return null;

        BaseEntity closest = null;
        float minDist = Float.MAX_VALUE;
        float ax = attacker.getX(), ay = attacker.getY();
        for (BaseEntity e : candidates) {
            if (!e.isActive() || e.isDead()) continue;
            float dx = e.getX() - ax;
            float dy = e.getY() - ay;
            float dist = dx * dx + dy * dy;
            if (dist < minDist) {
                minDist = dist;
                closest = e;
            }
        }
        return closest;
    }
}
