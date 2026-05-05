package com.hust.towerdefence.View.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.hust.towerdefence.View.renderer.AnimationController;

public class UnitView implements Disposable {

    private final Unit unit;
    private final AnimationController animationController;

    // Kích thước sprite placeholder
    private static final float SPRITE_W = 32f;
    private static final float SPRITE_H = 48f;

    public UnitView(Unit unit, AnimationController animationController) {
        this.unit = unit;
        this.animationController = animationController;
    }

    /**
     * Render unit tại vị trí hiện tại.
     * Gọi trong khi batch đang begin().
     */
    public void render(SpriteBatch batch, float delta) {
        // Bỏ qua nếu đã chết hoàn toàn
        if (unit.getState() == Unit.State.DEAD) return;

        // Lấy frame từ AnimationController
        TextureRegion frame = animationController.getUnitFrame(unit);

        if (frame != null) {
            // Có texture thực — vẽ sprite
            batch.setColor(1f, 1f, 1f, unit.getAlpha());
            batch.draw(frame,
                unit.getX() - SPRITE_W / 2f,
                unit.getY() - SPRITE_H / 2f,
                SPRITE_W, SPRITE_H);
            batch.setColor(Color.WHITE); // reset color
        } else {
            // Placeholder: vẽ hình chữ nhật màu theo loại unit
            // ShapeRenderer không dùng được trong batch.begin()
            // → tạm thời skip, sẽ vẽ trong renderPlaceholders() riêng
            // TODO: xóa block này khi có texture thực
        }
    }

    /**
     * Vẽ placeholder bằng ShapeRenderer khi chưa có texture.
     * Gọi NGOÀI batch.begin()/end().
     */
    public void renderPlaceholder(ShapeRenderer shapeRenderer) {
        if (unit.getState() == Unit.State.DEAD) return;

        shapeRenderer.setColor(getUnitColor());
        shapeRenderer.rect(
            unit.getX() - SPRITE_W / 2f,
            unit.getY() - SPRITE_H / 2f,
            SPRITE_W, SPRITE_H
        );

        // Vẽ hướng di chuyển (tam giác nhỏ phía trên)
        shapeRenderer.triangle(
            unit.getX(), unit.getY() + SPRITE_H / 2f + 10f,
            unit.getX() - 8f, unit.getY() + SPRITE_H / 2f,
            unit.getX() + 8f, unit.getY() + SPRITE_H / 2f
        );
    }

    /** Màu placeholder theo loại Unit */
    private Color getUnitColor() {
        switch (unit.getType()) {
            case MINER:    return Color.GOLD;
            case INFANTRY: return Color.BLUE;
            case ARCHER:   return Color.GREEN;
            default:       return Color.WHITE;
        }
    }

    public Unit getEntity() { return unit; }

    public float getY() { return unit.getY(); }

    @Override
    public void dispose() {
        // Dispose texture riêng nếu UnitView tự load
        // Hiện tại texture do AnimationController quản lý → không dispose ở đây
    }
}
