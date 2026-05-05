package com.hust.towerdefence.View.renderer;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

public class AnimationController implements Disposable {

    // Cache animation theo key "TYPE_STATE"
    // VD: "MINER_WALKING", "BASIC_ATTACKING"
    private final Map<String, Animation<TextureRegion>> animations = new HashMap<>();

    // Frame duration mặc định
    private static final float FRAME_DURATION_WALK   = 0.1f;
    private static final float FRAME_DURATION_ATTACK = 0.12f;
    private static final float FRAME_DURATION_DIE    = 0.15f;
    private static final float FRAME_DURATION_IDLE   = 0.2f;

    public AnimationController() {
        // TODO: load TextureAtlas thực tế ở đây
        // Ví dụ:
        // TextureAtlas atlas = new TextureAtlas("sprites/units.atlas");
        // loadUnitAnimations(atlas);
        // loadEnemyAnimations(atlas);
    }

    // -------------------------------------------------------
    // PUBLIC API
    // -------------------------------------------------------

    /**
     * Lấy TextureRegion hiện tại cho Unit dựa trên state + stateTime.
     * Trả về null nếu chưa có texture (placeholder mode).
     */
    public TextureRegion getUnitFrame(Unit unit) {
        String key = buildKey(unit.getType().name(), unit.getState().name());
        return getFrame(key, unit.getStateTime());
    }

    /**
     * Lấy TextureRegion hiện tại cho Enemy dựa trên state + stateTime.
     */
    public TextureRegion getEnemyFrame(Enemy enemy) {
        String key = buildKey(enemy.getType().name(), enemy.getState().name());
        return getFrame(key, enemy.getStateTime());
    }

    /**
     * Kiểm tra animation một lần đã chạy xong chưa.
     * Dùng để biết khi nào DYING animation kết thúc → chuyển sang DEAD.
     */
    public boolean isAnimationFinished(String typeKey, String stateKey, float stateTime) {
        String key = buildKey(typeKey, stateKey);
        Animation<TextureRegion> anim = animations.get(key);
        if (anim == null) return true;
        return anim.isAnimationFinished(stateTime);
    }

    // -------------------------------------------------------
    // INTERNAL HELPERS
    // -------------------------------------------------------

    private TextureRegion getFrame(String key, float stateTime) {
        Animation<TextureRegion> anim = animations.get(key);
        if (anim == null) return null; // chưa có texture, UnitView sẽ vẽ placeholder
        return anim.getKeyFrame(stateTime, shouldLoop(key));
    }

    /** DYING và DEAD không loop, các state khác loop */
    private boolean shouldLoop(String key) {
        return !key.contains("DYING") && !key.contains("DEAD");
    }

    private String buildKey(String type, String state) {
        return type + "_" + state;
    }

    /**
     * Đăng ký animation thủ công — gọi từ ngoài sau khi load atlas.
     * VD: animationController.register("MINER_WALKING", frames, 0.1f, LOOP);
     */
    public void register(String key,
                         TextureRegion[] frames,
                         float frameDuration,
                         Animation.PlayMode playMode) {
        animations.put(key, new Animation<>(frameDuration, frames));
        animations.get(key).setPlayMode(playMode);
    }

    @Override
    public void dispose() {
        // TextureAtlas dispose ở đây nếu AnimationController sở hữu nó
        // atlas.dispose();
        animations.clear();
    }
}
