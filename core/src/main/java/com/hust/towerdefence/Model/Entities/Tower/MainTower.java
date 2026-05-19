package com.hust.towerdefence.Model.Entities.Tower;

import com.badlogic.gdx.math.Vector2;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;

/**
 * MainTower đại diện cho Nhà chính (Core).
 * Nếu thực thể này có currentHealth <= 0, game sẽ kết thúc.
 */
public class MainTower extends BaseTower {
    private float health;
    private float maxHealth;

    public MainTower(Vector2 position, CombatEntity.Team team, float maxHealth) {

        super(position);
        this.team = team;
        this.maxHealth = maxHealth;
    }
    public float setHealth(float health) {
        return this.health = Math.max(0, health);
    };
    public float getHealth() { return health; }

}
