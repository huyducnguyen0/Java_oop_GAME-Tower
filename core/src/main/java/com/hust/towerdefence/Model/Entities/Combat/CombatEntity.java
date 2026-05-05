package com.hust.towerdefence.Model.Entities.Combat;

import com.hust.towerdefence.Model.Entities.BaseEntity;

/**
 * CombatEntity
 * Chứa các chỉ số sinh tồn, tấn công và trạng thái logic.
 */
public abstract class CombatEntity extends BaseEntity {

    // ===== Team =====
    public enum Team {
        SOLDIER,  // Quân lính (người chơi)
        ENEMY     // Kẻ thù
    }

    protected Team team; // Phe của entity (Soldier hoặc Enemy)

    // ===== Health =====
    protected float health; // Máu hiện tại
    protected float maxHealth; // Máu tối đa
    protected boolean dead; // Trạng thái chết sống

    // ===== Combat =====
    protected float attackDamage; // Sát thương mỗi đòn tấn công
    protected float attackRange; // Khoảng cách tấn công

    protected float attackSpeed; // Tốc độ tấn công (đòn/phút)
    protected float cooldownTimer; // Thời gian còn lại cho đến khi có thể tấn công tiếp
    protected float cooldownDuration; // Thời gian giữa các đòn tấn công (tính bằng giây, = 60 / attackSpeed)

    // ===== Target =====
    protected long targetId; // ID của thực thể mục tiêu hiện tại (0 nếu không có mục tiêu)

    public CombatEntity() {
        super();
        dead = false;
        targetId = 0;
    }

    // ===== Logic =====

    public void takeDamage(float amount) {
        if (dead) return;

        health -= amount;

        if (health <= 0) {
            health = 0;
            onDeath();
        } //
    }// Nhận sát thương và kiểm tra nếu chết

    protected void onDeath() {
        dead = true;
        active = false;
        markRemoved();
    } // Xử lý logic nội tại khi chết (có thể override để thêm hiệu ứng, rơi đồ, v.v.)

    // ===== Helpers =====

    public boolean isInRange(CombatEntity other) { // hàm dùng để check xem địch có trong tầm bắn không

        float range2 = attackRange * attackRange;
        return this.dst2(other) <= range2;
    } // Kiểm tra nếu thực thể khác trong tầm tấn công

    public boolean hasTarget() {
        return targetId != 0;
    } // Kiểm tra nếu có mục tiêu hiện tại

    public boolean isEnemyOf(CombatEntity other) {
        if (this.team == null || other.team == null) return false;
        return !this.team.equals(other.team);
    } // Kiểm tra xem có phải kẻ thù của entity khác không

    // ===== Pool =====

    @Override
    public void reset() {
        super.reset();

        health = 0;
        maxHealth = 0;
        dead = false;

        attackDamage = 0;
        attackRange = 0;

        attackSpeed = 0;
        cooldownTimer = 0;
        cooldownDuration = 0;

        targetId = 0;
        team = null;
    } // Đặt lại trạng thái để tái sử dụng từ pool

    // ===== Getter / Setter =====

    public float getHealth() { return health; }

    public void setHealth(float health) {
        this.health = Math.max(0, Math.min(health, maxHealth));
    }

    public float getMaxHealth() { return maxHealth; }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = Math.max(0, maxHealth);
        if (health > maxHealth) health = maxHealth;
    }

    public float getAttackDamage() { return attackDamage; }

    public void setAttackDamage(float attackDamage) {
        this.attackDamage = Math.max(0, attackDamage);
    }

    public float getAttackRange() { return attackRange; }

    public void setAttackRange(float attackRange) {
        this.attackRange = Math.max(0, attackRange);
    }

    public float getAttackSpeed() { return attackSpeed; }

    public void setAttackSpeed(float attackSpeed) {
        this.attackSpeed = attackSpeed;
        this.cooldownDuration = attackSpeed > 0 ? 1f / attackSpeed : 0;
    }

    public float getCooldownTimer() { return cooldownTimer; }
    public void setCooldownTimer(float t) { this.cooldownTimer = t; }

    public float getCooldownDuration() { return cooldownDuration; }

    public long getTargetId() { return targetId; }
    public void setTargetId(long targetId) { this.targetId = targetId; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }

    public boolean isDead() { return dead; }
}
