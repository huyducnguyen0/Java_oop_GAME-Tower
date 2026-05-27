package com.hust.towerdefence.Model.Systems;

import com.badlogic.gdx.utils.Array;
import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Tower.BaseTower;
import com.hust.towerdefence.Model.Entities.Tower.DefenseTower;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.EntityManager;

public class HealthSystem {
    private final EntityManager entityManager;
    private final GameWorld gameWorld;

    public HealthSystem(EntityManager entityManager, GameWorld gameWorld) {
        this.entityManager = entityManager;
        this.gameWorld = gameWorld;
    }

    public void takeDamage(CombatEntity entity, float damage) {
        if (entity.isDead() || entity.isRemoved()) return;

        float newHealth = entity.getHealth() - damage;
        entity.setHealth(Math.max(0, newHealth));

        if (newHealth <= 0) {
            entity.setActive(false);
            onDeath(entity);
        }
    }

    public void heal(CombatEntity entity, float amount) {
        if (entity.isDead() || entity.isRemoved() || amount <= 0) return;
        entity.setHealth(entity.getHealth() + amount);
    }

    private void onDeath(BaseEntity entity) {
        if (entity == gameWorld.getMainTower()) {
            gameWorld.gameOver();
            return;
        }
        if (entity == gameWorld.getEnemyTower()) {
            gameWorld.victory();
            return;
        }
        if (entity instanceof DefenseTower) {
            ((DefenseTower) entity).beginDestroying();
            return;
        }
        entity.markRemoved();
        entityManager.removeFromAllLists(entity);
    }

    public void update(float delta) {
        Array<DefenseTower> finished = new Array<>();
        for (BaseTower tower : entityManager.getTowers()) {
            if (!(tower instanceof DefenseTower)) continue;
            DefenseTower defenseTower = (DefenseTower) tower;
            defenseTower.updateDestroying(delta);
            if (defenseTower.isDestroyFinished()) {
                finished.add(defenseTower);
            }
        }

        for (DefenseTower tower : finished) {
            tower.markRemoved();
            entityManager.removeFromAllLists(tower);
        }
    }
}
