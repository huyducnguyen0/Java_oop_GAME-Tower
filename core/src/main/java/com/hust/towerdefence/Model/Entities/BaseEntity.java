package com.hust.towerdefence.Model.Entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * BASE ENTITY - Tầng cốt lõi và quan trọng nhất của mọi đối tượng trong Game.
 * Lớp này quản lý các thuộc tính cơ bản nhất: Vị trí (Vật lý), Sinh tồn (HP),
 * Phe phái (Danh tính) và Vòng đời (Sống/Chết).
 *
 * Thiết kế theo hướng tối giản (Minimalist) để cân được cả Viên đạn lẫn Quân lính.
 */
public abstract class BaseEntity {

    // --- Vật lý & Không gian ---
    protected Vector2 position;    // Vị trí thực thể (Vector giúp tính toán hướng/khoảng cách cực nhanh)
    protected Vector2 velocity;    // Vận tốc hiện tại (Dùng cho AI di chuyển và Projectile bay)
    protected float width, height;  // Kích thước logic để render và tính tâm thực thể
    protected Rectangle hitbox;     // Vùng va chạm vật lý (Phục vụ hệ thống Collision Detection)

    // --- Chỉ số sinh tồn & Định danh ---
    protected int hp, maxHp;       // Máu hiện tại và máu tối đa
    protected int teamId;          // ID phe phái: 1-Ta, 2-Địch, 0-Trung lập (Mỏ vàng/Chướng ngại vật)
    protected float stateTime = 0;  // Tổng thời gian đã sống (Nhóm View dùng để chạy Animation linh hoạt)

    // --- Quản lý vòng đời (Lifecycle) ---
    protected boolean active = true;   // true: Đang hoạt động; false: Ngừng tương tác (đang chạy animation chết)
    protected boolean removed = false; // true: Sẵn sàng để xóa hoàn toàn khỏi bộ nhớ (EntityManager xử lý)

    /**
     * Constructor khởi tạo thực thể với đầy đủ các thuộc tính nền tảng.
     */
    public BaseEntity(float x, float y, float width, float height, int hp, int teamId) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.width = width;
        this.height = height;
        this.hp = hp;
        this.maxHp = hp;
        this.teamId = teamId;

        // Khởi tạo hitbox dựa trên tọa độ truyền vào
        this.hitbox = new Rectangle(x, y, width, height);
    }

    /**
     * Cập nhật logic cơ bản mỗi frame.
     * @param delta Khoảng thời gian giữa 2 frame (thường là 1/60s).
     */
    public void update(float delta) {
        stateTime += delta;

        // Cơ chế tự sát khi hết máu (Dùng cho cả lính tử trận hoặc đạn va chạm tiêu biến)
        if (hp <= 0 && active) {
            die();
        }

        // Đồng bộ hóa vùng va chạm theo tọa độ mới nhất của thực thể
        syncHitbox();
    }

    /**
     * Xử lý nhận sát thương từ các nguồn bên ngoài.
     * @param amount Lượng máu bị trừ đi.
     */
    public void takeDamage(int amount) {
        // Chỉ nhận damage nếu thực thể đang hoạt động và lượng damage hợp lệ
        if (amount <= 0 || !active) return;

        this.hp -= amount;
        if (this.hp < 0) this.hp = 0;
    }

    /**
     * Đánh dấu thực thể đã chết.
     * Tách biệt active và removed để có thể chạy Animation chết trước khi biến mất hẳn.
     */
    public void die() {
        this.hp = 0;
        this.active = false;
        this.removed = true; // Lưu ý: Nếu có animation chết lâu, hãy delay việc set removed = true
    }

    public boolean isAlive() {
        return hp > 0; // Hoặc logic của bro
    }

    /**
     * Cập nhật vị trí của Rectangle va chạm khớp với Vector position.
     */
    protected void syncHitbox() {
        this.hitbox.setPosition(position.x, position.y);
    }

    // --- Getters & Setters (Cửa sổ giao tiếp chuyên nghiệp với các Hệ thống khác) ---

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public Vector2 getPosition() { return position; }
    public Vector2 getVelocity() { return velocity; }
    public Rectangle getHitbox() { return hitbox; }

    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
    public int getMaxHp() { return maxHp; }

    public float getStateTime() { return stateTime; }
    public boolean isActive() { return active; }
    public boolean isRemoved() { return removed; }

    // Các hàm tiện ích để truy cập nhanh tọa độ
    public float getX() { return position.x; }
    public float getY() { return position.y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}
