package com.hust.towerdefence.Model.Entities;

/**
 * Class này chứa toàn bộ hằng số về chỉ số của các thực thể.
 * Thiết kế theo map nhỏ, tốc độ trận đấu vừa phải để người chơi kịp quan sát.
 */
public class EntityStats {

    // ======================== PHE TA (TEAM 1) ========================

    // 1. Lính cận chiến (Melee Soldier)
    // Đặc điểm: Tanker cơ bản, giữ chân địch ở tiền tuyến.
    public static final int MELEE_HP = 150;
    public static final int MELEE_DEF = 5;
    public static final int MELEE_ATK = 20;
    public static final float MELEE_RANGE = 40f;
    public static final float MELEE_COOLDOWN = 1.0f;
    public static final float MELEE_SPEED = 70f;
    public static final float MELEE_VISION = 120f;

    // 2. Cung thủ (Archer)
    // Đặc điểm: Sát thương tầm xa, cần lính cận chiến che chắn phía trước.
    public static final int ARCHER_HP = 80;
    public static final int ARCHER_DEF = 1;
    public static final int ARCHER_ATK = 12;
    public static final float ARCHER_RANGE = 250f;
    public static final float ARCHER_COOLDOWN = 0.7f;
    public static final float ARCHER_SPEED = 100f;
    public static final float ARCHER_VISION = 300f;

    // 3. Golem (Heavy Melee)
    // Đặc điểm: Đấm gần (Short range), cực trâu, dùng để chặn đứng các đợt quái đông.
    public static final int GOLEM_HP = 600;
    public static final int GOLEM_DEF = 15;
    public static final int GOLEM_ATK = 50;
    public static final float GOLEM_RANGE = 50f;     // Đấm gần, sát thương diện rộng (tùy code sau này)
    public static final float GOLEM_COOLDOWN = 2.5f;
    public static final float GOLEM_SPEED = 30f;
    public static final float GOLEM_VISION = 100f;

    // 4. Thợ mỏ (Gold Miner)
    // Đặc điểm: Không tham chiến trừ khi bị dồn vào đường cùng, ưu tiên di chuyển tới Mỏ vàng.
    public static final int MINER_HP = 80;
    public static final int MINER_DEF = 2;
    public static final int MINER_GOLD_ATK = 10;
    public static final float MINER_RANGE = 35f;
    public static final float MINER_COOLDOWN = 1.5f;
    public static final float MINER_SPEED = 85f;
    public static final float MINER_VISION = 150f;

    // 5. Người sửa chữa (Repairman)
    // Đặc điểm: Ưu tiên di chuyển tới các công trình thấp máu để hồi phục HP.
    public static final int REPAIR_HP = 90;
    public static final int REPAIR_DEF = 3;
    public static final int REPAIR_POWER = 15;
    public static final float REPAIR_RANGE = 50f;
    public static final float REPAIR_COOLDOWN = 2.0f;
    public static final float REPAIR_SPEED = 75f;
    public static final float REPAIR_VISION = 180f;

    // 6. Khẩu pháo (Cannon)
    // Đặc điểm: Công trình tĩnh, sát thương cực lớn nhưng nạp đạn lâu, tầm bắn bao quát map.
    public static final int CANNON_HP = 300;
    public static final int CANNON_DEF = 10;
    public static final int CANNON_ATK = 80;
    public static final float CANNON_RANGE = 400f;
    public static final float CANNON_COOLDOWN = 3.5f;

    // 7. Pháo đài chính (Main Castle)
    // Đặc điểm: Mục tiêu sống còn, nếu sập là thua (Game Over).
    public static final int CASTLE_HP = 2000;
    public static final int CASTLE_DEF = 20;

    // 8. Mỏ vàng (Gold Mine)
    // Đặc điểm: Nơi thợ mỏ khai thác tiền, cần được bảo vệ để duy trì kinh tế.
    public static final int MINE_HP = 1000;
    public static final int MINE_DEF = 5;


    // ======================== PHE ĐỊCH (TEAM 2) ========================

    // 1. Quái cận chiến (Enemy Grunt)
    // Đặc điểm: Lính lác phe địch, lấy số lượng đè người, ưu tiên tấn công lính ta gặp trên đường.
    public static final int GRUNT_HP = 100;
    public static final int GRUNT_DEF = 2;
    public static final int GRUNT_ATK = 15;
    public static final float GRUNT_RANGE = 40f;
    public static final float GRUNT_COOLDOWN = 1.2f;
    public static final float GRUNT_SPEED = 75f;
    public static final float GRUNT_VISION = 130f;

    // 2. Người ném độc (Venom Thrower)
    // Đặc điểm: Đứng từ xa ném độc, gây khó chịu cho hàng phòng thủ của phe ta.
    public static final int VENOM_HP = 50;
    public static final int VENOM_DEF = 0;
    public static final int VENOM_ATK = 8;
    public static final float VENOM_RANGE = 200f;
    public static final float VENOM_COOLDOWN = 0.8f;
    public static final float VENOM_SPEED = 80f;
    public static final float VENOM_VISION = 250f;

    // 3. Quái bay (Flyer)
    // Đặc điểm: Đánh xa, tốc độ cao. Ưu tiên bỏ qua lính, bay thẳng tới Pháo đài chính để phá hủy.
    public static final int FLYER_HP = 40;
    public static final int FLYER_DEF = 0;
    public static final int FLYER_ATK = 25;
    public static final float FLYER_RANGE = 180f;     // Đã tăng: Đánh xa (khạc lửa/phóng tên độc)
    public static final float FLYER_COOLDOWN = 1.0f;
    public static final float FLYER_SPEED = 150f;
    public static final float FLYER_VISION = 500f;    // Nhìn cực xa để tìm Pháo đài chính
}
