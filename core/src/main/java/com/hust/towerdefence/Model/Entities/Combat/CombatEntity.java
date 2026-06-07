package com.hust.towerdefence.Model.Entities.Combat;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.hust.towerdefence.Model.Entities.BaseEntity;

/*
 * CombatEntity
 * Chứa các chỉ số sinh tồn, tấn công và trạng thái logic.
 */
public abstract class CombatEntity extends BaseEntity implements Poolable {

    // ===== Team =====

    public enum State {
        IDLE, MOVING, ATTACKING, HEALING, DYING,GOING_TO_MINE, MINING, RETURNING_HOME
    } // Trạng thái hành động hiện tại (có thể dùng để điều khiển animation, logic hành vi, v.v.)
    protected int level ;
    protected final int  MAX_LEVEL = 3;

    protected State currentState; // Trạng thái hành động hiện tại (có thể dùng để điều khiển animation, logic hành vi, v.v.)
    // ===== Health =====
    protected float health; // Máu hiện tại
    protected float maxHealth;

    // ===== Combat =====

    protected float attackDamage; // Sát thương mỗi đòn tấn công
    protected float attackRange; // Khoảng cách tấn công
    protected float attackSpeed; // Tốc độ tấn công (đòn/phút)

    protected float cooldownTimer; // Thời gian còn lại cho đến khi có thể tấn công tiếp
    protected float cooldownDuration; // Thời gian giữa các đòn tấn công (tính bằng giây, = 60 / attackSpeed)
    // ===== Target =====
    protected long targetId; // ID của thực thể mục tiêu hiện tại (0 nếu không có mục tiêu)

    // ===== Movement =====
    protected Array<Vector2> path;
    protected int currentPathIndex;
    protected final Vector2 facing;


    protected float speed; // Tốc độ di chuyển (world units/giây)
    public CombatEntity() {
        super();
        targetId = -1;
        speed = 75f;
        this.path = new Array<>();
        this.currentPathIndex = 0;// Default speed
        this.facing = new Vector2(1f, 0f);

    }

    public boolean isInRange(CombatEntity other) { // đây là hàm tính xem có ở trong khoảng cách không tấn công hay không, dùng dst2 để tránh tính căn bậc hai
        float range2 = attackRange * attackRange;
        return this.dst2(other) <= range2;
    } // Kiểm tra nếu thực thể khác trong tầm tấn công

    public boolean hasTarget() {
        return targetId != 0;
    } // Kiểm tra nếu có mục tiêu hiện tại
    @Override
    public void reset() {
        health = 0;
        maxHealth = 0;

        attackDamage = 0;
        attackRange = 0;
        attackSpeed = 0;
        cooldownTimer = 0;
        cooldownDuration = 0;

        targetId = -1;// để là -1 để mà khi kiểm tra có target hay không thì sẽ check != -1, vì 0 có thể là một ID hợp lệ của một thực thể khác
        team = null;


        speed = 75f;
        facing.set(1f, 0f);
    } // Đặt lại trạng thái để tái sử dụng từ pool
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
    public Array<Vector2> getPath() {
        return path;
    }

    public void setPath(Array<Vector2> path) {
        this.path = path;
        this.currentPathIndex = 0; // luôn bắt đầu từ điểm đầu
    }

    public int getCurrentPathIndex() {
        return currentPathIndex;
    }

    public void setCurrentPathIndex(int index) {
        this.currentPathIndex = index;
    }
    public float getCooldownTimer() { return cooldownTimer; } // lấy thời gian đếm ngược để có thể tấn công tiếp
    public void setCooldownTimer(float t) { this.cooldownTimer = t; }

    public float getCooldownDuration() { return cooldownDuration; } // lấy thời gian giữa các đòn tấn công (tính bằng giây, = 60 / attackSpeed)
    public void setCooldownDuration(float cooldownDuration) {
        this.cooldownDuration = cooldownDuration;
    }
    public long getTargetId() { return targetId; }
    public void setTargetId(long targetId) { this.targetId = targetId; }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }



    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }
    public Vector2 getFacing() { return facing; }
    public void setFacing(float x, float y) {
        if (Math.abs(x) < 0.001f && Math.abs(y) < 0.001f) return;
        facing.set(x, y).nor();
    }

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
