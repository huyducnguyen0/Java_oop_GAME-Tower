package com.hust.towerdefence.Model.Entities.Tower;

import com.hust.towerdefence.Model.Entities.BaseEntity;

public abstract class BaseTower extends BaseEntity {
    // Chỉ chứa các dữ liệu mà Nhà chính & Trụ đều có
    protected float maxHealth;
    protected float currentHealth;

    public BaseTower(float x, float y, float width, float height, float maxHealth) {
        super();
        // Set position
        this.setPosition(x, y);
        // Set dimensions
        this.width = width;
        this.height = height;
        // Set health
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    // Getters/Setters cho máu
    public float getMaxHealth() {
        return maxHealth;
    }

   public float getCurrentHealth() {
        return currentHealth;
    }

    @Override
    public void reset() {
        super.reset();
        this.maxHealth = 0;
        this.currentHealth = 0;
    }
}
