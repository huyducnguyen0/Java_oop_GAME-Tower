package com.hust.towerdefence.Model.Entities;

/**
 * DestructibleEntity là lớp dành cho mọi thứ có thể bị phá hủy (Thành, Lính, Quái).
 * Nó kế thừa từ GameObject để có vị trí và vùng va chạm.
 */
public abstract class DestructibleEntity extends GameObject {
    protected int hp;
    protected int maxHp;
    protected int def;
    protected int teamID; // 1: Phe ta, 2: Phe địch
    protected boolean active;

    public DestructibleEntity(float x, float y, float width, float height, int hp, int def, int teamID) {
        super(x, y, width, height);
        this.hp = hp;
        this.maxHp = hp;
        this.def = def;
        this.teamID = teamID;
        this.active = true;
    }

    /**
     * Hàm nhận sát thương chuẩn chỉnh.
     */
    public void takeDamage(int damage) {
        if (!active) return;

        // Sát thương thực tế = Sát thương nhận vào - Giáp (Ít nhất mất 1 máu)
        int actualDamage = Math.max(1, damage - this.def);
        this.hp -= actualDamage;

        if (this.hp <= 0) {
            this.hp = 0;
            this.active = false;
            onDeath();
        }
    }

    /**
     * Hàm hồi máu (Dùng khi lính rút về thành).
     */
    public void heal(int amount) {
        if (!active) return;
        this.hp = Math.min(hp + amount, maxHp);
    }

    /**
     * Mỗi loại thực thể khi chết sẽ có hiệu ứng khác nhau (Thành sập, lính biến mất).
     */
    protected abstract void onDeath();

    @Override
    public void dispose() {
        // Sau này giải phóng tài nguyên hình ảnh ở đây
    }

    // --- Getters ---
    public float getHpPercent() { return (float) hp / maxHp; }
    public boolean isActive() { return active; }
    public int getTeamID() { return teamID; }
}
