package com.hust.towerdefence.Model.Entities;

package com.hust.towerdefence.Model.Entities;

/**
 * CombatEntity dành cho tất cả những thứ có thể tấn công.
 * Kế thừa từ DestructibleEntity (vì muốn đánh người thì mình cũng phải có máu).
 */
public abstract class CombatEntity extends DestructibleEntity {
    protected int atk;           // Sức tấn công
    protected float attackRange; // Tầm đánh (để biết khi nào thì bắt đầu vung kiếm/bắn cung)
    protected float attackSpeed; // Tốc độ đánh (giây mỗi nhát chém)
    protected float attackTimer; // Bộ đếm thời gian hồi chiêu (cooldown)

    public CombatEntity(float x, float y, float width, float height, int hp, int def, int teamID, int atk, float attackRange, float attackSpeed) {
        super(x, y, width, height, hp, def, teamID);
        this.atk = atk;
        this.attackRange = attackRange;
        this.attackSpeed = attackSpeed;
        this.attackTimer = 0;
    }

    /**
     * Hàm kiểm tra xem đã đến lúc được đánh tiếp chưa.
     */
    public boolean canAttack(float delta) {
        attackTimer += delta;
        if (attackTimer >= attackSpeed) {
            return true;
        }
        return false;
    }

    /**
     * Reset bộ đếm sau khi vung đòn.
     */
    public void resetAttackTimer() {
        this.attackTimer = 0;
    }

    // --- Getters ---
    public int getAtk() { return atk; }
    public float getAttackRange() { return attackRange; }
}
