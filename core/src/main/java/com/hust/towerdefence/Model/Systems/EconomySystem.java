package com.hust.towerdefence.Model.Systems;

import com.hust.towerdefence.Model.Entities.Unit;
import com.hust.towerdefence.Model.Entities.Enemy;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.EntityManager;

public class EconomySystem {
    public void update(GameWorld world, float delta) {
        EntityManager em = world.getEntityManager();
        for (Unit u : em.getUnits()) {
            if (u.getType() == Unit.UnitType.MINER) {
                if (u.getGoldCarried() == 0) {
                    // Đi tới mỏ
                    u.setTarget(world.MINE_X, world.MINE_Y);
                    u.setState(Unit.State.WALKING);

                    float dist = (float) Math.hypot(u.getX() - world.MINE_X, u.getY() - world.MINE_Y);
                    if (dist < 15) {
                        u.setGoldCarried(15);
                    }
                } else {
                    // Về thành
                    u.setTarget(world.BASE_X, world.BASE_Y);
                    u.setState(Unit.State.WALKING);

                    float dist = (float) Math.hypot(u.getX() - world.BASE_X, u.getY() - world.BASE_Y);
                    if (dist < 15) {
                        em.setGold(em.getGold() + u.getGoldCarried());
                        u.setGoldCarried(0);
                    }
                }
            }
        }
    }
}
