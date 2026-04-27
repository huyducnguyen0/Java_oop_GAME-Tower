package com.hust.towerdefence.Model.Entities;

public class Unit extends GameEntity {
    public enum UnitType { MINER, INFANTRY, ARCHER }
    public enum Action { MINE, ATTACK, SHOOT }

    private UnitType type;
    private float spd;
    private int atk;
    private float range;
    private float lastActionTime = 0;
    private int goldCarried = 0;
    private float targetX, targetY;
    private int level = 1;  // Cấp độ nhân vật
    private Action currentAction = Action.ATTACK;  // Hành động hiện tại

    public Unit(UnitType type, float x, float y, int hp, int atk, float spd, float range) {
        super(x, y, hp);
        this.type = type;
        this.atk = atk;
        this.spd = spd;
        this.range = range;
        this.targetX = x;
        this.targetY = y;

        // Set hành động mặc định theo loại
        switch (type) {
            case MINER: this.currentAction = Action.MINE; break;
            case INFANTRY: this.currentAction = Action.ATTACK; break;
            case ARCHER: this.currentAction = Action.SHOOT; break;
        }
    }

    // Thực hiện hành động
    public void performAction() {
        switch (currentAction) {
            case MINE:
                // Logic đào vàng (sẽ implement trong EconomySystem)
                state = State.ATTACKING;  // Sử dụng ATTACKING cho animation
                break;
            case ATTACK:
                // Logic chém (sẽ implement trong CombatSystem)
                state = State.ATTACKING;
                break;
            case SHOOT:
                // Logic bắn (sẽ implement trong CombatSystem)
                state = State.ATTACKING;
                break;
        }
    }

    // Nâng cấp nhân vật
    public boolean upgrade() {
        int cost = getUpgradeCost();
        // Kiểm tra đủ vàng (sẽ check trong EconomySystem)
        // if (gold >= cost) {
            level++;
            // Tăng stats theo cấp
            atk += 5;
            hp += 20;
            maxHp += 20;
            spd += 0.5f;
            if (type == UnitType.ARCHER) {
                range += 50;
            }
            return true;
        // }
        // return false;
    }

    // Chi phí nâng cấp
    public int getUpgradeCost() {
        return level * 100;  // Càng cao cấp càng đắt
    }

    // Set hành động
    public void setAction(Action action) {
        this.currentAction = action;
    }

    public Action getCurrentAction() { return currentAction; }
    public int getLevel() { return level; }

    public void setTarget(float tx, float ty) {
        this.targetX = tx;
        this.targetY = ty;
    }

    public float getTargetX() { return targetX; }
    public float getTargetY() { return targetY; }

    public UnitType getType() { return type; }
    public float getSpd() { return spd; }
    public int getAtk() { return atk; }
    public float getRange() { return range; }
    public int getGoldCarried() { return goldCarried; }
    public void setGoldCarried(int gold) { this.goldCarried = gold; }
    public float getLastActionTime() { return lastActionTime; }
    public void setLastActionTime(float time) { this.lastActionTime = time; }
    public void setType(UnitType type) { this.type = type; }
    public void setSpd(float spd) { this.spd = spd; }
    public void setAtk(int atk) { this.atk = atk; }
    public void setRange(float range) { this.range = range; }
    public void setState(State state) { this.state = state; }
}
