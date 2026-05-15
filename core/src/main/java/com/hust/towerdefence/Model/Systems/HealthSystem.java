// ===================== HealthSystem.java =====================
package com.hust.towerdefence.Model.Systems;

import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Tower.MainTower;
import com.hust.towerdefence.Model.Managers.EntityManager;
import com.hust.towerdefence.Model.GameWorld;

public class HealthSystem {
    private final GameWorld world;
    private boolean gameOver = false;

    public HealthSystem(GameWorld world) {
        this.world = world;
    }

    public void update(float delta) {
        EntityManager entityManager = world.getEntityManager();
        // Không có gì cần cập nhật liên tục, chủ yếu xử lý khi nhận damage.
        // Có thể thêm hồi máu tự động nếu cần.
    }

    /**
     * Áp dụng sát thương lên một entity.
     */
    public void applyDamage(BaseEntity target, float damage) {
        if (target instanceof CombatEntity) {
            CombatEntity combatTarget = (CombatEntity) target;
            if (!target.isActive() || target.isDead()) return;
            combatTarget.setHealth(combatTarget.getHealth() - damage);

            if (combatTarget.getHealth() <= 0) {
                killEntity(combatTarget);
            }
        } else if (target instanceof MainTower) {
            MainTower mainTower = (MainTower) target;
            mainTower.takeDamage(damage);
            if (mainTower.isDestroyed()) {
                gameOver = true;
                // Thông báo cho GameWorld
                world.onMainTowerDestroyed();
            }
        }
    }

    /**
     * Enemy đến đích gây damage trực tiếp lên nhà chính.
     */
    public void dealDamageToMainTower(CombatEntity enemy) {
        MainTower mainTower = world.getEntityManager().getMainTower();
        if (mainTower != null) {
            applyDamage(mainTower, enemy.getDamageToMainTower());
        }
        // Sau khi gây damage, loại bỏ enemy (hoặc giữ lại tùy game)
        world.getEntityManager().markForRemoval(enemy);
    }

    private void killEntity(CombatEntity entity) {
        entity.setState(CombatEntity.State.DYING);
        entity.setActive(false);
        // Có thể rơi vàng, kinh nghiệm
        if (killer instanceof CombatEntity) {
            world.getEconomyManager().addGold(entity.getBounty());
        }
        world.getEntityManager().markForRemoval(entity);
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
