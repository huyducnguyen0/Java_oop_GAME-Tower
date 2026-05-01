package com.hust.towerdefence.Model.Entities;

/**
 * MAIN BASE - Nhà Chính (Trái tim của căn cứ).
 * Quản lý điều kiện thắng bại và cơ chế nâng cấp hồi phục đặc biệt.
 */
public class MainBase extends BaseEntity {

    private int level = 1;

    public MainBase(float x, float y, float width, float height, int hp, int teamId) {
        super(x, y, width, height, hp, teamId);
    }

    /**
     * Cơ chế nâng cấp đặc biệt:
     * Tăng máu tối đa và hồi đầy máu bất kể máu hiện tại đang là bao nhiêu.
     */
    public void upgrade(int extraHp) {
        this.level++;
        this.maxHp += extraHp;
        this.hp = this.maxHp; // Hồi đầy máu theo mức max mới
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    protected void die() {
        super.die();
        // Nơi phát tín hiệu Game Over cho hệ thống quản lý
    }

    // Getters
    public int getLevel() { return level; }
}
