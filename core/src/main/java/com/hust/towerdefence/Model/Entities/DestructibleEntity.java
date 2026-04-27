package com.hust.towerdefence.Model.Entities;

/**
 * DestructibleEntity: Lớp dành cho tất cả thực thể có thanh máu và có thể bị tiêu diệt.
 * Ví dụ: Lính, Quái, Pháo đài, và cả Nhà chính (Main Castle).
 * Kế thừa từ GameObject để có vị trí và vùng va chạm.
 */
public abstract class DestructibleEntity extends GameObject {
    protected int hp;       // Lượng máu hiện tại
    protected int maxHp;    // Lượng máu tối đa
    protected int def;      // Chỉ số phòng thủ (giảm sát thương nhận vào)
    protected int teamID;   // Định danh phe phái (Ví dụ: 1 cho phe Ta, 2 cho phe Địch)
    protected boolean active; // Trạng thái còn sống hay đã chết để xử lý logic

    public DestructibleEntity(float x, float y, float width, float height, int hp, int def, int teamID) {
        // Gọi constructor của lớp cha GameObject để khởi tạo vị trí và hitbox
        super(x, y, width, height);
        this.hp = hp;
        this.maxHp = hp;
        this.def = def;
        this.teamID = teamID;
        this.active = true; // Mặc định khi sinh ra là còn sống
    }

    /**
     * Hàm xử lý khi thực thể nhận sát thương từ đối thủ.
     * @param damage: Lượng sát thương thô chưa qua giảm trừ của đối thủ.
     */
    public void takeDamage(int damage) {
        // Nếu thực thể đã chết thì không nhận thêm sát thương nữa
        if (!active) return;

        // Công thức tính sát thương thực tế: Sát thương trừ đi giáp.
        // Math.max(1, ...) đảm bảo dù giáp cao đến đâu thì vẫn mất ít nhất 1 máu (tránh bất tử).
        int actualDamage = Math.max(1, damage - this.def);
        this.hp -= actualDamage;

        // Kiểm tra nếu hết máu
        if (this.hp <= 0) {
            this.hp = 0;
            this.active = false; // Chuyển sang trạng thái chết
            onDeath(); // Gọi hiệu ứng chết đặc trưng của từng loại quân
        }
    }

    /**
     * Hàm hồi phục lượng máu hiện tại.
     * Thường dùng khi lính rút lui về gần Thành (Main Castle).
     * @param amount: Lượng máu được hồi.
     */
    public void heal(int amount) {
        if (!active) return;
        // Đảm bảo máu sau khi hồi không vượt quá lượng máu tối đa (maxHp)
        this.hp = Math.min(hp + amount, maxHp);
    }

    /**
     * Phương thức trừu tượng xử lý sự kiện khi thực thể bị tiêu diệt.
     * Mỗi loại thực thể sẽ ghi đè (Override) để có hiệu ứng riêng:
     * - Lính: Biến mất hoặc rơi tiền.
     * - Thành chính: Kích hoạt màn hình Game Over.
     */
    protected abstract void onDeath();

    @Override
    public void dispose() {
        // Giải phóng tài nguyên liên quan đến thực thể này nếu cần (Texture, hiệu ứng...)
    }

    // --- CÁC HÀM LẤY THÔNG TIN (Getters) ---

    // Tính toán tỷ lệ phần trăm máu còn lại (rất hữu ích để vẽ thanh máu HP bar)
    public float getHpPercent() { return (float) hp / maxHp; }

    // Kiểm tra thực thể còn hoạt động không
    public boolean isActive() { return active; }

    // Lấy ID phe phái để kiểm tra xem có phải kẻ địch hay không
    public int getTeamID() { return teamID; }
}
