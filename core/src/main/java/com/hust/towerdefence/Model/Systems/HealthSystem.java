package com.hust.towerdefence.Model.Systems;

import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.EntityManager;


public class HealthSystem {
    private EntityManager entityManager;

    private GameWorld gameWorld;

    public HealthSystem(EntityManager entityManager, GameWorld gameWorld) {
        this.entityManager = entityManager;
        this.gameWorld = gameWorld;
    }

    /**
     * Gây damage cho thực thể. Tự động xử lý chết.
     */
    public void takeDamage(CombatEntity entity, float damage) {
        if (entity.isDead() || entity.isRemoved()) return; // đã chết rồi thì thôi

        float newHealth = entity.getHealth() - damage;
        entity.setHealth(Math.max(0, newHealth));

        if (newHealth <= 0) {
            entity.setActive(false); // đánh dấu dead
            onDeath(entity);
        }
    }

    private void onDeath(BaseEntity entity) {
        // Nếu là nhà chính của người chơi -> game over
        if (entity == gameWorld.getMainTower()) {
            gameWorld.gameOver();
            return;
        }
        // Các thực thể khác: đánh dấu xóa và loại khỏi danh sách
        entity.markRemoved();
        entityManager.removeFromAllLists(entity);
    }

    public void update(float delta) {
        // Có thể để trống hoặc xử lý hồi máu từ effect
    }
}
