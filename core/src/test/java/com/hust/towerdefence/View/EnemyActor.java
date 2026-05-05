package com.hust.towerdefence.View;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.Map;

public class EnemyActor extends Actor{
    private final Enemy enemy;

    // Animation object được inject từ ngoài (AssetManager / AnimationFactory)
    private final Map<State, Animation<TextureRegion>> animations;

    private static final float BAR_WIDTH  = 40f;
    private static final float BAR_HEIGHT = 5f;
    private static final float BAR_OFFSET = 24f;

    // ShapeRenderer dùng chung, inject từ GameStage
    private final ShapeRenderer shapeRenderer;

    public EnemyActor(Enemy enemy,
                      Map<State, Animation<TextureRegion>> animations,
                      ShapeRenderer shapeRenderer) {
        this.enemy         = enemy;
        this.animations    = animations;
        this.shapeRenderer = shapeRenderer;

        // Đồng bộ kích thước Actor với frame đầu tiên
        TextureRegion firstFrame = animations.get(State.WALKING).getKeyFrame(0);
        setSize(firstFrame.getRegionWidth(), firstFrame.getRegionHeight());
        setOrigin(getWidth() / 2f, getHeight() / 2f); // Xoay quanh tâm
    }

    // -------------------------------------------------------
    // Scene2D gọi act() mỗi frame — đồng bộ Actor với Model
    // -------------------------------------------------------
    @Override
    public void act(float delta) {
        super.act(delta);

        // Đồng bộ vị trí Actor theo Model (AIController đã cập nhật enemy.x/y)
        setPosition(
            enemy.getX() - getWidth()  / 2f,
            enemy.getY() - getHeight() / 2f
        );

        // Đồng bộ góc quay theo orientation của Steerable
        setRotation((float) Math.toDegrees(enemy.getOrientation()));

        // Tự xóa Actor khỏi Stage khi DEAD
        if (enemy.getState() == State.DEAD) {
            remove();
        }
    }

    // -------------------------------------------------------
    // Scene2D gọi draw() mỗi frame
    // -------------------------------------------------------
    @Override
    public void draw(Batch batch, float parentAlpha) {
        State state = enemy.getState();
        Animation<TextureRegion> anim = animations.getOrDefault(state, animations.get(State.IDLE));
        TextureRegion frame = anim.getKeyFrame(enemy.getStateTime());

        // Áp alpha từ Model (dùng khi enemy đang fade out lúc DYING)
        batch.setColor(1f, 1f, 1f, enemy.getAlpha() * parentAlpha);

        batch.draw(frame,
            getX(), getY(),
            getOriginX(), getOriginY(),
            getWidth(), getHeight(),
            getScaleX(), getScaleY(),
            getRotation()
        );

        batch.setColor(Color.WHITE); // Reset màu

        // Vẽ health bar
        drawHealthBar(batch);
    }

    // -------------------------------------------------------
    // Health bar — tắt batch, dùng ShapeRenderer, bật lại batch
    // -------------------------------------------------------
    private void drawHealthBar(Batch batch) {
        float ratio = Math.max(0f, (float) enemy.getHp() / enemy.getMaxHp());
        float barX  = enemy.getX() - BAR_WIDTH / 2f;
        float barY  = enemy.getY() + BAR_OFFSET;

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Nền đỏ
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(barX, barY, BAR_WIDTH, BAR_HEIGHT);

        // Phần máu còn lại
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(barX, barY, BAR_WIDTH * ratio, BAR_HEIGHT);

        shapeRenderer.end();

        batch.begin();
    }
}
