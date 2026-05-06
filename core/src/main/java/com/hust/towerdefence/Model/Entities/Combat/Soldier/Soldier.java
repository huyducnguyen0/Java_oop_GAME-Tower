package com.hust.towerdefence.Model.Entities.Combat.Soldier;

import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;

/**
 * Soldier
 * Lớp cơ sở cho tất cả các loại quân lính phe người chơi (Archer, Miner, Healer).
 * Giúp Systems phân biệt giữa quân đồng minh và kẻ thù (Enemy).
 */
public abstract class   Soldier extends CombatEntity {

    // ==================== Thông số kinh tế & Phát triển ====================

    protected int upgradeCost; // Chi phí nâng cấp lên cấp tiếp theo (tăng theo cấp độ)


    public Soldier() {
        super();
        this.level = 1;

        this.team = Team.SOLDIER;
        this.currentState = State.IDLE;
    }

    // ==================== Pool Reset ====================
    @Override
    public void reset() {
        super.reset();
        this.level = 1;
        this.team = Team.SOLDIER;
        this.currentState = State.IDLE;
        this.upgradeCost = 0;

    }



    public int getUpgradeCost() {
        return upgradeCost;
    }

    public void setUpgradeCost(int upgradeCost) {
        this.upgradeCost = Math.max(0, upgradeCost);
    }




}
