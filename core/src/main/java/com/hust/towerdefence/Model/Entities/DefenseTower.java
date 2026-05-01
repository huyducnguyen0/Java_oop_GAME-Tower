package com.hust.towerdefence.Model.Entities;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.hust.towerdefence.Model.AI.States.TowerState;

/**
 * DEFENSE TOWER - Thực thể trụ phòng thủ chiến đấu.
 * Nhiệm vụ: Tự động quét và bắn đạn vào kẻ địch trong tầm bắn để bảo vệ căn cứ.
 * Khác biệt: Đây là trụ chiến đấu thuần túy, không phải nhà sinh lính hay nhà chính.
 */
public class DefenseTower extends InteractableEntity {

    // Bộ não AI quản lý trạng thái Quét mục tiêu và Tấn công
    protected StateMachine<DefenseTower, TowerState> stateMachine;

    /**
     * Khởi tạo Trụ phòng thủ.
     *
     * @param damage Lượng sát thương mỗi phát bắn (truyền vào tham số value của cha).
     * @param cooldown Thời gian giãn cách giữa các lần bắn (giây).
     */
    public DefenseTower(float x, float y, float width, float height, int hp, int teamId,
                        float range, float damage, float cooldown) {

        // Kế thừa từ InteractableEntity: value ở đây đóng vai trò là Sát thương
        super(x, y, width, height, hp, teamId, range, damage, cooldown);

        // Khởi tạo máy trạng thái, bắt đầu với trạng thái quét tìm kẻ địch
        this.stateMachine = new DefaultStateMachine<>(this, TowerState.STAY_SCAN);
    }

    @Override
    public void update(float delta) {
        // Cập nhật stateTime, HP và Cooldown từ lớp cha
        super.update(delta);

        // Thực thi logic AI nếu trụ còn hoạt động
        if (active && stateMachine != null) {
            stateMachine.update();
        }
    }

    /**
     * Thực hiện phát bắn bảo vệ.
     * Trụ gây sát thương trực tiếp lên mục tiêu hiện tại dựa trên interactValue.
     */
    @Override
    public void performAction() {
        if (isTargetExisting()) {
            // Gây sát thương dựa trên chỉ số sức mạnh của trụ
            target.takeDamage((int) interactValue);

            // Note: Có thể thêm logic tạo Projectile (viên đạn) tại đây nếu cần hiệu ứng bay
        }
    }

    /**
     * Kiểm tra đối tượng có phải là kẻ thù để bắn hay không.
     * Trụ chiến đấu chỉ bắn thực thể phe đối phương (khác teamId) và không phải trung lập (0).
     */
    @Override
    public boolean isValidTarget() {
        return target != null && target.getTeamId() != this.teamId && target.getTeamId() != 0;
    }

    // --- Hệ thống điều khiển AI ---

    public StateMachine<DefenseTower, TowerState> getStateMachine() {
        return stateMachine;
    }
}
