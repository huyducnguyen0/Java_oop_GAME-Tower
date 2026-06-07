package com.hust.towerdefence.Model.Entities.Tower;

import com.badlogic.gdx.math.Vector2;
import com.hust.towerdefence.Model.Entities.BaseEntity;

public class DefenseTower extends BaseTower {
    private static final float DESTROY_DURATION = 0.75f;
    private static final int MAX_TOWER_LEVEL = 3;
    private static final int[] UPGRADE_COSTS = {200, 400, 0};
    private static final float[] HEALTH_BY_LEVEL = {1000f, 2000f, 3000f};
    private static final float[] DAMAGE_BY_LEVEL = {25f, 60f, 100f};
    private static final float[] RANGE_BY_LEVEL = {4f, 4f, 4f};
    private static final float[] ATTACK_SPEED_BY_LEVEL = {1.0f, 1.15f, 1.3f};

    private final String mapName;
    private boolean destroying;
    private float destroyTimer;

    public DefenseTower(String mapName, Vector2 position, BaseEntity.Team team) {
        super(position);
        this.mapName = mapName;
        this.team = team;
        this.level = 1;
        applyLevelStats(true);
    }

    public String getMapName() {
        return mapName;
    }

    public int getUpgradeCost() {
        if (!canUpgrade()) return 0;
        return UPGRADE_COSTS[level - 1];
    }

    public boolean canUpgrade() {
        return level < MAX_TOWER_LEVEL && !destroying && !isRemoved();
    }

    public boolean upgrade() {
        if (!canUpgrade()) return false;
        level++;
        applyLevelStats(false);
        return true;
    }

    public void beginDestroying() {
        if (destroying) return;
        destroying = true;
        destroyTimer = 0f;
        setActive(false);
        setState(State.DYING);
        setTargetId(-1);
    }

    public void updateDestroying(float delta) {
        if (destroying) {
            destroyTimer += delta;
        }
    }

    public boolean isDestroying() {
        return destroying;
    }

    public float getDestroyProgress() {
        if (!destroying) return 0f;
        return Math.min(1f, destroyTimer / DESTROY_DURATION);
    }

    public boolean isDestroyFinished() {
        return destroying && destroyTimer >= DESTROY_DURATION;
    }

    private void applyLevelStats(boolean refillHealth) {
        int index = Math.max(0, Math.min(level - 1, MAX_TOWER_LEVEL - 1));
        float oldMaxHealth = maxHealth;
        maxHealth = HEALTH_BY_LEVEL[index];
        attackDamage = DAMAGE_BY_LEVEL[index];
        attackRange = RANGE_BY_LEVEL[index];
        setAttackSpeed(ATTACK_SPEED_BY_LEVEL[index]);

        if (refillHealth || health <= 0f) {
            health = maxHealth;
        } else {
            health = Math.min(maxHealth, health + (maxHealth - oldMaxHealth));
        }
    }
}
