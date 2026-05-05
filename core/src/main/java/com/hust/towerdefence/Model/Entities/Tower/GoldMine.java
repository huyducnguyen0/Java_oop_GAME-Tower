package com.hust.towerdefence.Model.Entities.Tower;

import com.hust.towerdefence.Model.Entities.BaseEntity;

/**
 * GoldMine
 * Tòa nhà đứng cố định để Miners đào vàng.
 * - Không di chuyển (static building)
 * - Vô hạn vàng
 * - Chỉ cần vị trí và kích thước
 */
public class GoldMine extends BaseEntity {


    public GoldMine(float x, float y) {
        super();
        this.setPosition(x, y);

        // Kích thước mine (tính theo World Unit)
        this.width = 1.5f;
        this.height = 1.5f;

        this.active = true;
        this.removed = false;
    }



    // ===== Getters / Setters =====

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public void setWidth(float width) {
        this.width = Math.max(0, width);
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public void setHeight(float height) {
        this.height = Math.max(0, height);
    }
}
