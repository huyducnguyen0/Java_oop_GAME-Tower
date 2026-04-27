package com.hust.towerdefence.Model.Entities;

// IMPORT BẮT BUỘC: Để sử dụng danh sách List
import java.util.List;

/**
 * CombatEntity: Lớp trung gian dành cho tất cả các thực thể có khả năng chiến đấu.
 * Kế thừa từ DestructibleEntity vì muốn tấn công thì trước hết phải có máu và phe phái.
 */
public abstract class CombatEntity extends DestructibleEntity {
    protected int atk;           // Sức mạnh mỗi cú đánh (Sát thương gốc)
    protected float attackRange; // Tầm xa có thể gây sát thương (Bán kính)
    protected float attackSpeed; // Tốc độ đánh (Khoảng cách giây giữa 2 lần đánh)
    protected float attackTimer; // Bộ đếm thời gian hồi chiêu (Cooldown)

    public CombatEntity(float x, float y, float width, float height, int hp, int def, int teamID, int atk, float attackRange, float attackSpeed) {
        // Gọi Constructor của lớp cha (DestructibleEntity)
        super(x, y, width, height, hp, def, teamID);
        this.atk = atk;
        this.attackRange = attackRange;
        this.attackSpeed = attackSpeed;
        this.attackTimer = 0; // Khởi tạo: Vào game là có thể đánh được ngay
    }

    /**
     * Phương thức trừu tượng: Mỗi loại quân sẽ có cách tấn công khác nhau.
     * Archer sẽ bắn tên, Miner sẽ đập cuốc, Tower sẽ bắn đạn đại bác.
     */
    protected abstract void performAttack(DestructibleEntity target);

    /**
     * Hàm kiểm tra trạng thái hồi chiêu.
     * @param delta: Thời gian trôi qua giữa 2 khung hình (LibGDX cung cấp).
     * @return true nếu đã đủ thời gian hồi chiêu để đánh tiếp.
     */
    public boolean isReadyToAttack(float delta) {
        attackTimer += delta; // Cộng dồn thời gian trôi qua vào bộ đếm
        if (attackTimer >= attackSpeed) {
            return true;
        }
        return false;
    }

    /**
     * Reset bộ đếm về 0 sau khi thực hiện đòn đánh thành công.
     */
    public void resetAttackTimer() {
        this.attackTimer = 0;
    }

    /**
     * HÀM QUAN TRỌNG: Xử lý logic chiến đấu tự động.
     * Dùng chung cho cả Pháo đài (đứng im) và làm nền tảng cho Lính (di chuyển).
     * @param enemies: Danh sách toàn bộ kẻ địch đang có trên bản đồ.
     */
    public void process(float delta, List<DestructibleEntity> enemies) {
        // Nếu thực thể đã chết hoặc biến mất thì không xử lý gì cả
        if (!active) return;

        // 1. Cập nhật thời gian hồi chiêu liên tục theo nhịp của game
        attackTimer += delta;

        // 2. Tìm kiếm mục tiêu tối ưu (Kẻ địch gần nhất và nằm trong tầm đánh)
        DestructibleEntity bestTarget = null;
        float minDistance = attackRange; // Chỉ xét những đứa trong tầm attackRange

        for (DestructibleEntity enemy : enemies) {
            // Chỉ đánh nếu kẻ địch còn sống và khác Team (Phe ta không đánh phe mình)
            if (enemy.isActive() && enemy.getTeamID() != this.teamID) {
                // Tính khoảng cách từ bản thân đến kẻ địch
                float dist = position.dst(enemy.getPosition());

                // Nếu khoảng cách này nhỏ hơn tầm đánh và là nhỏ nhất hiện tại
                if (dist <= minDistance) {
                    minDistance = dist;
                    bestTarget = enemy;
                }
            }
        }

        // 3. Thực hiện tấn công nếu tìm thấy mục tiêu hợp lệ và đã hồi chiêu xong
        if (bestTarget != null && attackTimer >= attackSpeed) {
            performAttack(bestTarget); // Gọi hành động tấn công đặc trưng của lớp con
            resetAttackTimer();        // Đánh xong phải chờ hồi chiêu
        }
    }

    // --- Các hàm lấy thông số (Getters) ---
    public int getAtk() { return atk; }
    public float getAttackRange() { return attackRange; }
}
