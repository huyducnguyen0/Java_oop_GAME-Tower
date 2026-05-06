package com.hust.towerdefence.Model.Entities.Combat;

import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

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
    public enum Role { WARRIOR, MINER }
    public enum State {
        IDLE, MOVING, ATTACKING, MINING, HEALING, DYING
    } // Trạng thái hành động hiện tại (có thể dùng để điều khiển animation, logic hành vi, v.v.)
    protected int level ;
    protected final int  MAX_LEVEL = 3;
    protected Team team; // Phe của entity (Soldier hoặc Enemy)
    protected State currentState; // Trạng thái hành động hiện tại (có thể dùng để điều khiển animation, logic hành vi, v.v.)
    // ===== Health =====
    protected float health; // Máu hiện tại
    protected float maxHealth; // Máu tối đa
    // ===== Combat =====

    protected float attackDamage; // Sát thương mỗi đòn tấn công
    protected float attackRange; // Khoảng cách tấn công
    protected float attackSpeed; // Tốc độ tấn công (đòn/phút)
    protected float cooldownTimer; // Thời gian còn lại cho đến khi có thể tấn công tiếp
    protected float cooldownDuration; // Thời gian giữa các đòn tấn công (tính bằng giây, = 60 / attackSpeed)
    // ===== Target =====
    protected long targetId; // ID của thực thể mục tiêu hiện tại (0 nếu không có mục tiêu)

    // ===== Movement =====
    protected Array<Vector2> waypoints; // Danh sách waypoints để di chuyển
    protected int currentWaypointIndex; // Chỉ số waypoint hiện tại
    protected float speed; // Tốc độ di chuyển (world units/giây)
    public CombatEntity() {
        super();

        targetId = 0;
        currentWaypointIndex = 0;
        speed = 50f; // Default speed

    }

    public boolean isInRange(CombatEntity other) { // đây là hàm tính xem có ở trong khoảng cách không tấn công hay không, dùng dst2 để tránh tính căn bậc hai
        float range2 = attackRange * attackRange;
        return this.dst2(other) <= range2;
    } // Kiểm tra nếu thực thể khác trong tầm tấn công

    public boolean hasTarget() {
        return targetId != 0;
    } // Kiểm tra nếu có mục tiêu hiện tại

    public boolean isEnemyOf(CombatEntity other) {
        if (this.team == null || other.team == null) return false;
        return !this.team.equals(other.team);
    } // Kiểm tra xem có phải kẻ thù của entity khác khôn

    // ===== Pool =====

    @Override
    public void reset() {
        super.reset();

        health = 0;
        maxHealth = 0;

        attackDamage = 0;
        attackRange = 0;

        attackSpeed = 0;
        cooldownTimer = 0;
        cooldownDuration = 0;

        targetId = 0;
        team = null;

        waypoints = null;
        currentWaypointIndex = 0;
        speed = 50f;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public Array<Vector2> getWaypoints() { return waypoints; }
    public void setWaypoints(Array<Vector2> waypoints) { this.waypoints = waypoints; }

    public int getCurrentWaypointIndex() { return currentWaypointIndex; }
    public void setCurrentWaypointIndex(int currentWaypointIndex) { this.currentWaypointIndex = currentWaypointIndex; }

    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }

    // ===== State Management =====
    public State getCurrentState() { return currentState; }
    public void setState(State state) { this.currentState = state; }

    /**
     * Kiểm tra xem entity đã chết chưa (health <= 0 hoặc state == DYING)
     */
    public boolean isDead() {
        return health <= 0 || currentState == State.DYING;
    }

    /**
     * Kiểm tra xem entity còn sống (dùng cho ngược lại isDead())
     */
    public boolean isAlive() {
        return health > 0 && currentState != State.DYING;
    }
}
