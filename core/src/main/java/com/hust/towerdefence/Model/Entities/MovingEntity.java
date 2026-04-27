package com.hust.towerdefence.Model.Entities;

import com.badlogic.gdx.math.Vector2;
import java.util.List;

/**
 * MovingEntity: Lớp dành cho các thực thể có khả năng di chuyển (Lính, Quái).
 * Kế thừa từ CombatEntity vì lính di động thường đi kèm với khả năng chiến đấu.
 */
public abstract class MovingEntity extends CombatEntity {
    protected float speed;               // Tốc độ di chuyển (pixel/giây)
    protected Vector2 velocity;          // Vectơ vận tốc hiện tại
    protected Vector2 destination;       // Điểm đến do người chơi chỉ định hoặc vị trí cố thủ
    protected DestructibleEntity target; // Mục tiêu kẻ địch đang bị khóa để truy đuổi
    protected float visionRange;         // Tầm nhìn (Phát hiện địch trong khoảng cách này)

    public MovingEntity(float x, float y, float width, float height, int hp, int def, int teamID, int atk, float range, float cooldown, float speed, float visionRange) {
        // Gọi Constructor của CombatEntity để khởi tạo chỉ số chiến đấu
        super(x, y, width, height, hp, def, teamID, atk, range, cooldown);
        this.speed = speed;
        this.visionRange = visionRange;
        this.velocity = new Vector2();
        this.destination = new Vector2(x, y); // Mặc định đứng yên tại chỗ khi vừa sinh ra
        this.target = null;
    }

    /**
     * HÀM XỬ LÝ DI CHUYỂN: Quyết định lính sẽ đi đâu dựa trên tình huống chiến trường.
     * Ưu tiên 1: Đứng lại đánh nếu địch đã trong tầm.
     * Ưu tiên 2: Truy đuổi nếu phát hiện địch trong tầm nhìn.
     * Ưu tiên 3: Đi tới điểm chỉ định của người chơi.
     */
    public void updateMovement(float delta, List<DestructibleEntity> potentialTargets) {
        if (!active) return;

        // 1. Tự động tìm kiếm kẻ địch lân cận nếu hiện tại chưa có mục tiêu
        autoSeekTarget(potentialTargets);

        Vector2 finalGoal; // Mục tiêu cuối cùng mà lính hướng tới trong khung hình này

        // 2. Kiểm tra trạng thái mục tiêu
        if (target != null && target.isActive()) {
            finalGoal = target.getPosition();
            float distToEnemy = position.dst(finalGoal);

            // Nếu đã áp sát mục tiêu trong tầm đánh -> Dừng lại để chuẩn bị tấn công
            if (distToEnemy <= attackRange) {
                velocity.set(0, 0);
                return;
            }
        } else {
            // 3. Nếu không có kẻ địch hoặc kẻ địch đã chết -> Đi tới tọa độ người chơi ra lệnh
            finalGoal = destination;
        }

        // LOGIC DI CHUYỂN VẬT LÝ
        float distToGoal = position.dst(finalGoal);
        if (distToGoal > 2f) { // Nếu còn cách mục tiêu trên 2 pixel thì tiếp tục đi
            // Tính toán Vectơ hướng: (Điểm đích - Điểm hiện tại), chuẩn hóa rồi nhân với tốc độ
            velocity.set(finalGoal).sub(position).nor().scl(speed);
            // Cập nhật tọa độ dựa trên vận tốc và thời gian delta
            position.add(velocity.x * delta, velocity.y * delta);
            updateBounds(); // Cập nhật vùng va chạm theo vị trí mới
        } else {
            velocity.set(0, 0); // Đã đến đích -> Dừng lại
        }
    }

    /**
     * Hệ thống tự động nhận diện kẻ địch (Scan).
     * Duyệt danh sách kẻ địch, con nào gần nhất lọt vào visionRange sẽ bị khóa làm target.
     */
    private void autoSeekTarget(List<DestructibleEntity> potentialTargets) {
        // Nếu đang có mục tiêu và mục tiêu đó vẫn còn sống thì tiếp tục focus, không tìm con khác
        if (target != null && target.isActive()) return;

        for (DestructibleEntity entity : potentialTargets) {
            // Chỉ tìm những đối tượng còn sống và thuộc phe đối phương
            if (entity.isActive() && entity.getTeamID() != this.teamID) {
                if (position.dst(entity.getPosition()) <= visionRange) {
                    this.target = entity;
                    break; // Khóa mục tiêu đầu tiên tìm thấy trong tầm nhìn
                }
            }
        }
    }

    /**
     * Nhận lệnh di chuyển trực tiếp từ người chơi (Click chuột).
     * Khi nhận lệnh này, lính sẽ ưu tiên đi đến chỗ mới và hủy bỏ mục tiêu truy đuổi cũ.
     */
    public void commandMoveTo(float x, float y) {
        this.destination.set(x, y);
        this.target = null;
    }

    /**
     * GHI ĐÈ hàm process của CombatEntity.
     * Kết hợp cả 2 bộ máy: "Đôi chân" (Movement) và "Cánh tay" (Combat).
     */
    @Override
    public void process(float delta, List<DestructibleEntity> enemies) {
        if (!active) return;

        // BƯỚC 1: Xử lý di chuyển và tìm mục tiêu (Sử dụng updateMovement của MovingEntity)
        updateMovement(delta, enemies);

        // BƯỚC 2: Xử lý tấn công (Sử dụng các chỉ số từ CombatEntity)
        attackTimer += delta; // Luôn cập nhật bộ đếm hồi chiêu

        // Nếu đã có mục tiêu (từ bước 1) và mục tiêu lọt vào tầm đánh
        if (target != null && target.isActive()) {
            float distance = position.dst(target.getPosition());

            // Kiểm tra: Khoảng cách <= tầm đánh VÀ đã hồi chiêu xong
            if (distance <= attackRange && attackTimer >= attackSpeed) {
                performAttack(target); // Thực hiện cú đánh (Archer bắn, Miner đập...)
                resetAttackTimer();    // Đặt lại bộ đếm hồi chiêu
            }
        }
    }
}
