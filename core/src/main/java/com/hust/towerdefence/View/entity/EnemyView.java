package com.hust.towerdefence.View.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.hust.towerdefence.View.renderer.AnimationController;

public class EnemyView implements Disposable {

    private final Enemy enemy;
    private final AnimationController animationController;

    // Kích thước sprite theo loại enemy
    private final float spriteW;
    private final float spriteH;

    public EnemyView(Enemy enemy, AnimationController animationController) {
        this.enemy = enemy;
        this.animationController = animationController;

        // BOSS to hơn, FAST nhỏ hơn
        switch (enemy.getType()) {
            case BOSS:  spriteW = 80f; spriteH = 80f; break;
            case TANK:  spriteW = 48f; spriteH = 48f; break;
            case FAST:  spriteW = 24f; spriteH = 32f; break;
            default:    spriteW = 32f; spriteH = 40f; break; // BASIC
        }
    }

    /**
     * Render enemy tại vị trí hiện tại.
     * Gọi trong khi batch đang begin().
     */
    public void render(SpriteBatch batch, float delta) {
        if (enemy.getState() == Enemy.State.DEAD) return;

        TextureRegion frame = animationController.getEnemyFrame(enemy);

        if (frame != null) {
            // Có texture — vẽ sprite với alpha (fade out khi DYING)
            batch.setColor(1f, 1f, 1f, enemy.getAlpha());
            batch.draw(frame,
                enemy.getX() - spriteW / 2f,
                enemy.getY() - spriteH / 2f,
                spriteW, spriteH);
            batch.setColor(Color.WHITE);
        }
        // Nếu không có texture → renderPlaceholder() xử lý
    }

    /**
     * Vẽ placeholder bằng ShapeRenderer khi chưa có texture.
     * Gọi NGOÀI batch.begin()/end().
     */
    public void renderPlaceholder(ShapeRenderer shapeRenderer) {
        if (enemy.getState() == Enemy.State.DEAD) return;

        shapeRenderer.setColor(getEnemyColor());

        // BOSS dùng hình tròn, còn lại hình chữ nhật
        if (enemy.getType() == Enemy.EnemyType.BOSS) {
            shapeRenderer.circle(enemy.getX(), enemy.getY(), spriteW / 2f);
        } else {
            shapeRenderer.rect(
                enemy.getX() - spriteW / 2f,
                enemy.getY() - spriteH / 2f,
                spriteW, spriteH
            );
        }

        // AI state indicator — vẽ chấm tròn nhỏ bên trên
        // giúp debug trạng thái AI đang làm gì
        renderAIStateIndicator(shapeRenderer);
    }

    /**
     * Chấm màu nhỏ phía trên enemy cho biết AI state.
     * Xanh lá = WALKING, Đỏ = ATTACKING, Xám = IDLE/DYING
     */
    private void renderAIStateIndicator(ShapeRenderer shapeRenderer) {
        float dotX = enemy.getX();
        float dotY = enemy.getY() + spriteH / 2f + 8f;

        switch (enemy.getState()) {
            case WALKING:   shapeRenderer.setColor(Color.GREEN); break;
            case ATTACKING: shapeRenderer.setColor(Color.RED);   break;
            case DYING:     shapeRenderer.setColor(Color.GRAY);  break;
            default:        shapeRenderer.setColor(Color.WHITE); break;
        }
        shapeRenderer.circle(dotX, dotY, 4f);
    }

    /** Màu placeholder theo loại Enemy */
    private Color getEnemyColor() {
        switch (enemy.getType()) {
            case BASIC: return Color.RED;
            case FAST:  return new Color(1f, 0.5f, 0f, 1f); // cam
            case TANK:  return Color.PURPLE;
            case BOSS:  return Color.SCARLET;
            default:    return Color.RED;
        }
    }

    public Enemy getEntity() { return enemy; }

    public float getY() { return enemy.getY(); }

    @Override
    public void dispose() {
        // Texture do AnimationController quản lý
    }
}
