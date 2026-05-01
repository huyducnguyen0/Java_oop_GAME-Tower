package com.hust.towerdefence.Model.Entities;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;

/**
 * STEERABLE ENTITY - Tầng hoàn thiện cuối cùng của thực thể di động.
 * Kết hợp: Vật lý (Base) + Tương tác (Interactable) + Di chuyển AI (Steerable) + Bộ não (StateMachine).
 *
 * Đây là lớp nền tảng vững chắc nhất để triển khai lính, quái vật có khả năng
 * tự tìm mục tiêu và di chuyển thông minh theo bầy đàn.
 */
public abstract class SteerableEntity extends InteractableEntity implements Steerable<Vector2> {

    // --- Steering & Physics (Thông số di chuyển nâng cao) ---
    protected float maxLinearSpeed;        // Tốc độ di chuyển tối đa
    protected float maxLinearAcceleration; // Độ bốc khi tăng tốc (giúp lính phản ứng nhanh)
    protected float orientation;           // Hướng mặt hiện tại (Radian)
    protected boolean tagged;              // Phục vụ thuật toán tránh va chạm bầy đàn (Group behaviors)

    // --- AI Components (Bộ não điều khiển) ---
    protected SteeringBehavior<Vector2> steeringBehavior;   // Hành vi di chuyển (Seek, Arrive, Avoidance,...)
    protected SteeringAcceleration<Vector2> steeringOutput; // Kết quả tính toán lực đẩy từ AI

    // SỬA TẠI ĐÂY: Thay đổi Raw Type thành Generic chuẩn để khớp với State<SteerableEntity>
    protected StateMachine<SteerableEntity, State<SteerableEntity>> stateMachine; // Máy trạng thái (FSM) quản lý logic (IDLE, MOVE, ATTACK)

    public SteerableEntity(float x, float y, float width, float height, int hp, int teamId,
                           float range, float value, float cooldown, float maxSpeed) {
        // Khởi tạo xuyên suốt 3 tầng: Base -> Interactable -> Steerable
        super(x, y, width, height, hp, teamId, range, value, cooldown);

        this.maxLinearSpeed = maxSpeed;
        this.maxLinearAcceleration = maxSpeed * 5; // Gia tốc thường gấp 5 lần vận tốc để lính mượt mà
        this.orientation = 0;
        this.steeringOutput = new SteeringAcceleration<>(new Vector2());
    }

    /**
     * Cập nhật logic tổng thể mỗi khung hình.
     */
    @Override
    public void update(float delta) {
        // 1. Cập nhật thời gian và các chỉ số sinh tồn từ Base & Interactable
        super.update(delta);

        // 2. Kích hoạt StateMachine: Quyết định trạng thái hiện tại (Đang đi hay đang đánh)
        if (stateMachine != null) {
            stateMachine.update();
        }

        // 3. Tính toán Steering: Nếu đang sống và có hành vi di chuyển, áp dụng lực đẩy AI
        if (active && steeringBehavior != null) {
            steeringBehavior.calculateSteering(steeringOutput);
            applySteering(steeringOutput, delta);
        }
    }

    /**
     * Chuyển hóa kết quả từ AI thành chuyển động vật lý thực tế.
     */
    protected void applySteering(SteeringAcceleration<Vector2> steering, float delta) {
        // Cập nhật vị trí dựa trên vận tốc hiện tại
        position.add(velocity.x * delta, velocity.y * delta);

        // Cập nhật vận tốc: Cộng thêm gia tốc từ AI và giới hạn bởi maxSpeed
        velocity.mulAdd(steering.linear, delta).limit(maxLinearSpeed);

        // "Look where you're going": Xoay hướng mặt theo hướng di chuyển
        if (!velocity.isZero(0.01f)) {
            this.orientation = vectorToAngle(velocity);
        }
    }

    // --- TRIỂN KHAI INTERFACE STEERABLE (Chuẩn libGDX AI) ---

    @Override public Vector2 getLinearVelocity() { return velocity; }
    @Override public float getAngularVelocity() { return 0; } // Game top-down thường bỏ qua xoay thân
    @Override public float getBoundingRadius() { return (width + height) / 4; }
    @Override public boolean isTagged() { return tagged; }
    @Override public void setTagged(boolean tagged) { this.tagged = tagged; }

    @Override public float getMaxLinearSpeed() { return maxLinearSpeed; }
    @Override public void setMaxLinearSpeed(float maxLinearSpeed) { this.maxLinearSpeed = maxLinearSpeed; }
    @Override public float getMaxLinearAcceleration() { return maxLinearAcceleration; }
    @Override public void setMaxLinearAcceleration(float maxLinearAcc) { this.maxLinearAcceleration = maxLinearAcc; }

    @Override public float getOrientation() { return orientation; }
    @Override public void setOrientation(float orientation) { this.orientation = orientation; }

    @Override public float getZeroLinearSpeedThreshold() { return 0.01f; }
    @Override public void setZeroLinearSpeedThreshold(float value) {}

    // Vector <-> Angle conversion (Đảm bảo lính quay mặt đúng hướng)
    @Override public float vectorToAngle(Vector2 vector) { return (float) Math.atan2(-vector.x, vector.y); }
    @Override public Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.x = -(float) Math.sin(angle);
        outVector.y = (float) Math.cos(angle);
        return outVector;
    }

    // --- Getters/Setters cho AI Components ---

    public void setSteeringBehavior(SteeringBehavior<Vector2> sb) { this.steeringBehavior = sb; }

    // SỬA TẠI ĐÂY: Trả về StateMachine với Generic chuẩn để các State gọi được lệnh changeState()
    public StateMachine<SteerableEntity, State<SteerableEntity>> getStateMachine() {
        return stateMachine;
    }

    // THÊM: Hàm setter để Manager có thể cài đặt bộ não ban đầu cho lính
    public void setStateMachine(StateMachine<SteerableEntity, State<SteerableEntity>> stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override public float getMaxAngularSpeed() { return 5f; }
    @Override public void setMaxAngularSpeed(float maxAngularSpeed) {}
    @Override public float getMaxAngularAcceleration() { return 10f; }
    @Override public void setMaxAngularAcceleration(float maxAngularAcceleration) {}
    @Override public Location<Vector2> newLocation() { return null; }
}
