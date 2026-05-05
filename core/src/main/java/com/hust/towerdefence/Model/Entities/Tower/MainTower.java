package com.hust.towerdefence.Model.Entities.Tower;

import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;

/**
 * MainTower đại diện cho Nhà chính (Core).
 * Nếu thực thể này có currentHealth <= 0, game sẽ kết thúc.
 */
public class MainTower extends BaseTower {

    public MainTower(float x, float y, float width, float height, float health, CombatEntity.Team team) {
        // Gọi Constructor của BaseTower để thiết lập dữ liệu
        super(x, y, width, height, health,team);
    }
}
