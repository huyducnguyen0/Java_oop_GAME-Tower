package com.hust.towerdefence.Model.Systems;
import com.hust.towerdefence.Model.Entities.Unit;
import com.hust.towerdefence.Model.Entities.Enemy;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.EntityManager;

public class CombatSystem {
    // Thống kê chiến đấu
    private int enemiesKilled = 0;
    private int damageDealt = 0;
    private int damageToBase = 0;

    public void update(GameWorld world, float delta) {
        EntityManager em = world.getEntityManager();

        // === PHẦN 1: Units tấn công Enemies ===
        for (Unit u : em.getUnits()) {
            if (u.getType() == Unit.UnitType.MINER) continue;

            // Tìm quái gần thành nhất (ưu tiên cao hơn gần nhất)
            Enemy target = findNearestEnemyToBase(u, em, world);
            if (target != null) {
                float dist = (float) Math.hypot(target.getX() - u.getX(), target.getY() - u.getY());

                // Nếu quái trong tầm tấn công
                if (dist < u.getRange() * 100) {
                    // Tấn công từ xa - không cần đi lại gần
                    if (u.getStateTime() - u.getLastActionTime() > 1.0f) {
                        int damage = u.getAtk();
                        target.setHp(target.getHp() - damage);
                        damageDealt += damage;
                        u.setLastActionTime(u.getStateTime());
                        u.setState(Unit.State.ATTACKING);

                        // Nếu quái chết
                        if (target.getHp() <= 0) {
                            target.setState(Unit.State.DEAD);
                            enemiesKilled++;
                            // Cộng 10 điểm cho mỗi quái tiêu diệt
                            world.addScore(10);
                        }
                    }
                } else {
                    // Quái ngoài tầm - di chuyển tới
                    u.setTarget(target.getX(), target.getY());
                    u.setState(Unit.State.WALKING);
                }
            } else {
                // Không có quái - trở về IDLE
                u.setState(Unit.State.IDLE);
            }
        }

        // === PHẦN 2: Quái tấn công thành ===
        for (Enemy e : em.getEnemies()) {
            float distToBase = (float) Math.hypot(e.getX() - world.BASE_X, e.getY() - world.BASE_Y);
            if (distToBase < 60) {
                // Quái gây damage mỗi 0.5 giây, không phải mỗi frame
                if (e.getStateTime() - e.getLastActionTime() > 0.5f) {
                    int damage = e.getAtk();
                    em.setMainBuildingHp(em.getMainBuildingHp() - damage);
                    damageToBase += damage;
                    e.setLastActionTime(e.getStateTime());
                }
            }
        }
    }

    /**
     * Tìm quái gần thành nhất (cao ưu tiên)
     * Nếu không có, tìm quái gần unit nhất
     */
    private Enemy findNearestEnemyToBase(Unit u, EntityManager em, GameWorld world) {
        Enemy target = null;
        float minDistToBase = Float.MAX_VALUE;

        // Chỉ xem xét quái còn sống
        for (Enemy e : em.getEnemies()) {
            if (e.getState() == Unit.State.DEAD) continue;

            float distToBase = (float) Math.hypot(e.getX() - world.BASE_X, e.getY() - world.BASE_Y);
            if (distToBase < minDistToBase) {
                minDistToBase = distToBase;
                target = e;
            }
        }

        return target;
    }

    /**
     * Tìm quái gần nhất (backup nếu không dùng priority)
     */
    private Enemy findNearestEnemy(Unit u, EntityManager em) {
        Enemy nearest = null;
        float minDist = Float.MAX_VALUE;

        for (Enemy e : em.getEnemies()) {
            if (e.getState() == Unit.State.DEAD) continue;

            float d = (float) Math.hypot(e.getX() - u.getX(), e.getY() - u.getY());
            if (d < minDist) {
                minDist = d;
                nearest = e;
            }
        }
        return nearest;
    }

    // === Getters cho thống kê ===
    public int getEnemiesKilled() { return enemiesKilled; }
    public int getDamageDealt() { return damageDealt; }
    public int getDamageToBase() { return damageToBase; }

    public void resetStats() {
        enemiesKilled = 0;
        damageDealt = 0;
        damageToBase = 0;
    }
}
