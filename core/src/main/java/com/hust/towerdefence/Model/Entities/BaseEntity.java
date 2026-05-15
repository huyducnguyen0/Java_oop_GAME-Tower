package com.hust.towerdefence.Model.Entities;
import com.badlogic.gdx.math.Vector2;


public abstract class BaseEntity  {
    protected long id; //dùng để định danh đối tượng
    private static long nextId = 0;
    protected final Vector2 position; // vị trí của đối tượng
    protected float width; // dùng để render và tính va chạm
    protected float height; // dùng để render và tính va chạm
    protected float rotation; // (độ)

    protected boolean active; // dùng để xác định xem entity có đang hoạt động hay không (ví dụ: đã chết)
    protected boolean removed; // dùng để đánh dấu entity đã bị xóa khỏi game world (ví dụ: sau khi chết)
    protected enum Team {
        SOLDIER,  // Quân lính (người chơi)
        ENEMY     // Kẻ thù
    }
    protected Team team;
    public BaseEntity() {
        this.id = nextId++;
        this.position = new Vector2();
        this.active = true;
        this.removed = false;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }
    public Team getTeam() { return team; };
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

    public boolean isDead() {
        return !active;
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
    public boolean overlaps(BaseEntity other) { // kiểm tra va chạm AABB đơn giản
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
     *
    // ==================== Helper Methods ====================
    /**
     * Kiểm tra va chạm AABB đơn giản
     */
    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
}
