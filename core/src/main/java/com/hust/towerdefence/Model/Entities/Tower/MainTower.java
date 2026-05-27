package com.hust.towerdefence.Model.Entities.Tower;

import com.badlogic.gdx.math.Vector2;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;

public class MainTower extends BaseTower {
    public MainTower(Vector2 position, CombatEntity.Team team, float maxHealth) {
        super(position);
        this.team = team;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attackDamage = 0f;
        this.attackRange = 0f;
        this.attackSpeed = 0f;
        this.cooldownDuration = 0f;
    }

    public boolean isEnemy() {
        return getTeam() == CombatEntity.Team.ENEMY;
    }
}
