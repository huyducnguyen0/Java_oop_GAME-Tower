package com.hust.towerdefence.Model.Entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * GameObject: Lớp tổ tiên của tất cả mọi vật thể trong game.
 * Chứa các thuộc tính cơ bản nhất như vị trí, kích thước và vùng va chạm.
 */
public abstract class GameObject {
    protected Vector2 position;   // Tọa độ (x, y) của vật thể
    protected float width;        // Chiều rộng
    protected float height;       // Chiều cao
    protected Rectangle bounds;   // Vùng va chạm (Hitbox) dùng để tính toán va chạm/click chuột
    protected boolean removed = false; // Đánh dấu vật thể cần được xóa khỏi game (ví dụ: lính chết)

    public GameObject(float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.width = width;
        this.height = height;
        // Khởi tạo vùng va chạm dựa trên vị trí và kích thước truyền vào
        this.bounds = new Rectangle(x, y, width, height);
    }

    /**
     * Hàm cập nhật trạng thái vật thể theo thời gian.
     * ĐÃ BỎ abstract: Để các vật thể tĩnh (Cây, Đá) không bắt buộc phải viết code logic.
     * Thằng con nào cần di chuyển hay thay đổi trạng thái thì mới Override lại.
     */
    public void update(float delta) {
        // Mặc định để trống để tối ưu hiệu suất cho vật thể tĩnh
    }

    /**
     * Hàm vẽ vật thể lên màn hình.
     * Bắt buộc abstract vì mỗi vật thể có hình ảnh (Texture) khác nhau.
     */
    public abstract void draw(SpriteBatch batch);

    /**
     * Hàm giải phóng bộ nhớ.
     * Dùng để xóa các Texture/Sound khi vật thể không còn sử dụng, tránh tràn RAM.
     */
    public abstract void dispose();

    /**
     * Cập nhật vị trí mới cho vật thể bằng tọa độ x, y.
     */
    public void setPosition(float x, float y) {
        this.position.set(x, y);
        updateBounds(); // Di chuyển vị trí thì phải di chuyển cả vùng va chạm theo
    }

    /**
     * Cập nhật vị trí mới bằng một Vector2.
     */
    public void setPosition(Vector2 pos) {
        this.position.set(pos);
        updateBounds();
    }

    /**
     * Đồng bộ vùng va chạm (bounds) khớp với vị trí hiện tại của vật thể.
     */
    protected void updateBounds() {
        if (bounds != null) {
            bounds.set(position.x, position.y, width, height);
        }
    }

    // --- CÁC HÀM LẤY THÔNG TIN (Getters) ---

    public Vector2 getPosition() { return position; }

    public Rectangle getBounds() { return bounds; }

    // Lấy tọa độ tâm của vật thể (rất quan trọng để tính khoảng cách chuẩn)
    public float getCenterX() { return position.x + width / 2; }
    public float getCenterY() { return position.y + height / 2; }

    // Kiểm tra xem vật thể đã đến lúc bị xóa chưa
    public boolean isRemoved() { return removed; }

    // Đánh dấu để hệ thống quản lý (EntityManager) tiến hành xóa vật thể này
    public void remove() { this.removed = true; }
}
