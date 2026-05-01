package com.hust.towerdefence.Model.Entities;

/**
 * PRODUCTION STRUCTURE - Nhà sản xuất quân đội.
 *
 * Vai trò:
 * 1. Là thực thể tĩnh trên bản đồ (Kế thừa BaseEntity để có tọa độ và HP).
 * 2. Lưu trữ cấp độ công nghệ (level) để xác định sức mạnh lính khi sinh ra.
 * 3. Phân loại nhà (Type) để hệ thống Spawn biết cần tạo loại lính nào.
 */
public class ProductionStructure extends BaseEntity {

    /**
     * Danh sách các loại nhà sản xuất.
     * Bro có thể thêm các loại lính mới vào đây (ví dụ: TANK, ASSASSIN...).
     */
    public enum StructureType {
        BARRACKS,       // Nhà lính cận chiến
        ARCHERY_RANGE,  // Nhà lính đánh xa
        MINER_HOUSE,    // Nhà thợ mỏ
        DEFAULT         // Loại mặc định để tránh lỗi null
    }

    private final StructureType type; // Loại nhà (không đổi sau khi khởi tạo)
    private int level;                // Cấp độ nâng cấp của nhà

    /**
     * Khởi tạo Nhà sản xuất.
     *
     * @param x, y: Vị trí đặt nhà.
     * @param hp: Lượng máu (nếu bro muốn nhà có thể bị phá hủy).
     * @param teamId: Phe sở hữu (1: Ta, 2: Địch).
     * @param type: Loại nhà cụ thể từ Enum StructureType.
     */
    public ProductionStructure(float x, float y, float width, float height, int hp, int teamId, StructureType type) {
        super(x, y, width, height, hp, teamId);
        this.type = (type != null) ? type : StructureType.DEFAULT;
        this.level = 1; // Mặc định khởi đầu ở cấp 1
    }

    /**
     * Nâng cấp nhà sản xuất.
     * Hàm này chỉ tăng level công nghệ. Việc tăng chỉ số lính sẽ do
     * bảng tra cứu dữ liệu (EntityStats) quyết định dựa trên level này.
     */
    public void upgrade() {
        this.level++;
    }

    @Override
    public void update(float delta) {
        // Gọi update của cha để đồng bộ hitbox và kiểm tra trạng thái sinh tồn
        super.update(delta);
    }

    @Override
    protected void die() {
        // Khi nhà sụp đổ, gọi logic của cha để set active = false
        super.die();
        // Bạn của bro sẽ check isRemoved() để ngừng cho phép sản xuất lính từ nhà này.
    }

    // --- Hệ thống Getters chuyên nghiệp ---

    public StructureType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    /**
     * Kiểm tra xem nhà có phải loại cụ thể nào đó không.
     * Giúp code ở các lớp Manager ngắn gọn hơn.
     */
    public boolean isType(StructureType targetType) {
        return this.type == targetType;
    }
}
