package com.hust.towerdefence.Model.Systems;

import com.hust.towerdefence.Model.Entities.Unit;
import com.hust.towerdefence.Model.Entities.Enemy;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.EntityManager;

public class MovementSystem {
    public void update(GameWorld world, float delta) {
        EntityManager em = world.getEntityManager();
        // Hệ thống di chuyển cho Units
        for (Unit u : em.getUnits()) {
            if (u.getState() == Unit.State.WALKING) {
                // Logic hướng về mục tiêu đơn giản
                float dx = u.getTargetX() - u.getX();
                float dy = u.getTargetY() - u.getY();
                float dist = (float) Math.hypot(dx, dy);

                if (dist > 5) {
                    float angle = (float) Math.atan2(dy, dx);
                    u.setPosition(
                        u.getX() + (float)Math.cos(angle) * u.getSpd() * 50 * delta,
                        u.getY() + (float)Math.sin(angle) * u.getSpd() * 50 * delta
                    );
                } else {
                    u.setState(Unit.State.IDLE);
                }
            }
        }

        // Hệ thống di chuyển cho Enemies
        for (Enemy e : em.getEnemies()) {
            if (e.getState() == Enemy.State.WALKING) {
                float dx = world.BASE_X - e.getX();
                float dy = world.BASE_Y - e.getY();
                float dist = (float) Math.hypot(dx, dy);

                if (dist > 50) {
                    float angle = (float) Math.atan2(dy, dx);
                    e.setPosition(
                        e.getX() + (float)Math.cos(angle) * e.getSpd() * 40 * delta,
                        e.getY() + (float)Math.sin(angle) * e.getSpd() * 40 * delta
                    );
                }
            }
        }
    }
}
