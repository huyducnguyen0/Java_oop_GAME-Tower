package com.hust.towerdefence.Model.Systems;

import com.hust.towerdefence.Model.Entities.Enemy;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.EntityManager;

/**
 * AI System - Quản lý AI logic cho tất cả enemies
 * - Di chuyển thông minh
 * - Tìm đường đi để đánh lính
 * - Decision making (attack, chase, retreat)
 */
public class AISystem {
    public void update(GameWorld world, float delta) {
        EntityManager em = world.getEntityManager();

        // Update AI logic cho mỗi enemy
        for (Enemy enemy : em.getEnemies()) {
            if (enemy.getState() == Enemy.State.DEAD) continue;

            // Gọi AI controller update
            enemy.getAIController().update(world, em, delta);
        }
    }
}

