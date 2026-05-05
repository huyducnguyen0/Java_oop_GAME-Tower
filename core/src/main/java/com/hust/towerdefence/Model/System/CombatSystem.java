package com.hust.towerdefence.Model.System;

import com.badlogic.gdx.math.Intersector;
import com.hust.towerdefence.Model.Manager.EntityManager; // Sửa lại đúng package manager của bro
import com.hust.towerdefence.Model.Entities.*;
import com.badlogic.gdx.utils.Array;

/**
 * COMBAT SYSTEM - Hệ thống xử lý logic chiến tranh chuyên nghiệp.
 * Chịu trách nhiệm: Sinh đạn (Ranged), Va chạm đạn và Điều phối mục tiêu di chuyển.
 */
public class CombatSystem {
    private final EntityManager entityManager;

    public CombatSystem(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void update(float delta) {
        // 1. Tự động quét mục tiêu cho lính để phục vụ di chuyển (Cái bro đang thiếu)
        autoTargeting();

        // 2. Xử lý các thực thể có khả năng bắn đạn (Trụ và Lính đánh xa)
        handleRangedAttackSignals();

        // 3. Xử lý vật lý cho đạn đang bay
        handleProjectilePhysics();
    }

    /**
     * Tự động quét mục tiêu cho lính dựa trên tầm nhìn (range * 2).
     * Giúp lính đang di chuyển thấy địch thì dừng lại đánh.
     */
    private void autoTargeting() {
        // Quét cho phe ta tìm phe địch
        for (Combat unit : entityManager.getPlayerUnits()) {
            if (!unit.isTargetExisting()) {
                // Thử tìm địch gần nhất trong tầm nhìn rộng hơn tầm đánh
                Combat nearest = entityManager.getNearestEnemy(unit.getX(), unit.getY(), unit.getInteractRange() * 2);
                if (nearest != null) unit.setTarget(nearest);
            }
        }
        // Quét cho phe địch tìm phe ta
        for (Combat unit : entityManager.getEnemyUnits()) {
            if (!unit.isTargetExisting()) {
                Combat nearest = entityManager.getNearestPlayer(unit.getX(), unit.getY(), unit.getInteractRange() * 2);
                if (nearest != null) unit.setTarget(nearest);
            }
        }
    }

    /**
     * Kiểm tra tín hiệu bắn từ tất cả đơn vị đánh xa.
     */
    private void handleRangedAttackSignals() {
        for (DefenseTower tower : entityManager.getTowers()) {
            if (tower.hasFired() && tower.isTargetExisting()) {
                spawnProjectile(tower, tower.getTarget());
                tower.consumeFireSignal();
            }
        }

        processRangedUnits(entityManager.getPlayerUnits());
        processRangedUnits(entityManager.getEnemyUnits());
    }

    private void processRangedUnits(Array<Combat> units) {
        for (Combat unit : units) {
            // unit.isRanged() và hasFired() đảm bảo chỉ xử lý lính đánh xa có tín hiệu
            if (unit.isRanged() && unit.hasFired() && unit.isTargetExisting()) {
                spawnProjectile(unit, unit.getTarget());
                unit.consumeFireSignal();
            }
        }
    }

    private void spawnProjectile(BaseEntity source, BaseEntity target) {
        float centerX = source.getX() + source.getWidth() / 2;
        float centerY = source.getY() + source.getHeight() / 2;

        int damage = (source instanceof InteractableEntity)
            ? (int)((InteractableEntity)source).getInteractValue()
            : 0;

        Projectile p = new Projectile(
            centerX, centerY,
            source.getTeamId(),
            damage,
            400f,
            target
        );
        entityManager.addEntity(p);
    }

    private void handleProjectilePhysics() {
        for (Projectile p : entityManager.getProjectiles()) {
            if (!p.isAlive()) continue;

            BaseEntity target = p.getTarget();
            if (target != null && target.isAlive() &&
                Intersector.overlaps(p.getHitbox(), target.getHitbox())) {

                target.takeDamage(p.getDamage());
                p.die();
            }
        }
    }
}
