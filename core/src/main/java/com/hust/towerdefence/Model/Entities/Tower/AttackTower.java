package com.hust.towerdefence.Model.Entities.Tower;

import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;

public class AttackTower extends BaseTower {
    private float attackDamage;
    private float attackRange;

    // Khoảng thời gian chờ giữa 2 phát bắn (ví dụ: 1.5 giây)
    private float attackDelay;

    // Biến tích lũy thời gian hiện tại
    private float currentDelay;

    public AttackTower(float x, float y, float width, float height, float health,
                       float attackDamage, float attackRange, float attackDelay,CombatEntity.Team team) {
        super(x, y, width, height, health,team);
        this.attackDamage = attackDamage;
        this.attackRange = attackRange;
        this.attackDelay = attackDelay;
        this.currentDelay = 0;
    }

    // --- Getters & Setters ---
    public float getAttackDamage() { return attackDamage; }
    public float getAttackRange() { return attackRange; }
    public float getAttackDelay() { return attackDelay; }

    public float getCurrentDelay() { return currentDelay; }
    public void setCurrentDelay(float currentDelay) { this.currentDelay = currentDelay; }
}
