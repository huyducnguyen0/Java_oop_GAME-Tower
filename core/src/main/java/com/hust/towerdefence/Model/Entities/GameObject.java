package com.hust.towerdefence.Model.Entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class GameObject {
    protected Vector2 position;
    protected float width;
    protected float height;
    protected Rectangle bounds;
    protected boolean removed = false; // Thêm cái này để quản lý việc xóa object

    public GameObject(float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(x, y, width, height);
    }

    public abstract void update(float delta);
    public abstract void draw(SpriteBatch batch);

    // Thêm hàm dọn dẹp để sau này con cháu dùng
    public abstract void dispose();

    public void setPosition(float x, float y) {
        this.position.set(x, y);
        updateBounds();
    }

    public void setPosition(Vector2 pos) {
        this.position.set(pos);
        updateBounds();
    }

    protected void updateBounds() {
        if (bounds != null) {
            bounds.set(position.x, position.y, width, height);
        }
    }

    // Getters
    public Vector2 getPosition() { return position; }
    public Rectangle getBounds() { return bounds; }
    public float getCenterX() { return position.x + width / 2; }
    public float getCenterY() { return position.y + height / 2; }

    public boolean isRemoved() { return removed; }
    public void remove() { this.removed = true; }
}
