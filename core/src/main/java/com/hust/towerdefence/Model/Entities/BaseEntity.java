package com.hust.towerdefence.Model.Entities;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public abstract class BaseEntity implements Poolable {
    protected long id; //dùng để định danh đối tượng
    protected final Vector2 position; // vị trí của đối tượng
    protected final Vector2 velocity; // vận tốc (đơn vị: pixel/s) — dùng để tính toán di chuyển mỗi frame
    protected float width; // dùng để render và tính va chạm
    protected float height; // dùng để render và tính va chạm
    protected float rotation; // Hướng nhìn (độ)

    protected boolean active; // dùng để xác định xem entity có đang hoạt động hay không (ví dụ: đã chết, đã hoàn thành nhiệm vụ, v.v.)
    protected boolean removed; // dùng để đánh dấu entity đã bị xóa khỏi game world (ví dụ: sau khi chết, hoặc sau khi hoàn thành nhiệm vụ)

    public BaseEntity() {
        this.position = new Vector2();
        this.velocity = new Vector2();
        this.active = true;
        this.removed = false;
    }

    @Override
    public void reset() {
        id = 0;
        position.set(0, 0);
        velocity.set(0, 0);
        width = 0;
        height = 0;
        rotation = 0;
        active = false;
        removed = false;
    } // thực hiện reset tất cả thuộc tính về giá trị mặc định khi được trả về pool

    // ==================== Getter / Setter ====================

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void markRemoved() {
        this.removed = true;
    }
    public boolean overlaps(BaseEntity other) {
        return this.position.x < other.position.x + other.width &&
            this.position.x + this.width > other.position.x &&
            this.position.y < other.position.y + other.height &&
            this.position.y + this.height > other.position.y;
    }

    /**
     * Khoảng cách bình phương (tối ưu hơn dst)
     */
    public float dst2(BaseEntity other) {
        return this.position.dst2(other.position);
    }

    /**
     * Khoảng cách thật (ít dùng trong loop)
     *
    // ==================== Helper Methods ====================
    /**
     * Kiểm tra va chạm AABB đơn giản
     */
    public Vector2 getVelocity() {
        return velocity;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
}
