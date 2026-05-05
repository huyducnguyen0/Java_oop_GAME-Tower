package com.hust.towerdefence.View.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EffectView implements Disposable {

    private final SpriteBatch batch;

    // Pool đơn giản — list các effect đang active
    private final List<Effect> activeEffects = new ArrayList<>();

    // Pre-allocate pool để tránh GC trong combat
    private final List<Effect> pool = new ArrayList<>();
    private static final int POOL_SIZE = 50;

    public EffectView(SpriteBatch batch) {
        this.batch = batch;
        // Khởi tạo pool sẵn
        for (int i = 0; i < POOL_SIZE; i++) {
            pool.add(new Effect());
        }
    }

    /**
     * Spawn effect tại vị trí — gọi từ GameRenderer khi nhận combat event.
     * effectType: "HIT", "EXPLOSION", "HEAL", "BUFF"
     */
    public void spawnEffect(float x, float y, String effectType) {
        Effect effect = obtainFromPool();
        effect.init(x, y, effectType);
        activeEffects.add(effect);
    }

    /**
     * Render tất cả effect đang active + dọn dẹp effect hết TTL.
     * Gọi trong khi batch đang begin().
     */
    public void render(float delta) {
        Iterator<Effect> it = activeEffects.iterator();
        while (it.hasNext()) {
            Effect effect = it.next();
            effect.update(delta);

            if (effect.isDone()) {
                // Trả về pool thay vì GC
                it.remove();
                returnToPool(effect);
            } else {
                effect.render(batch);
            }
        }
    }

    // --- Pool management ---

    private Effect obtainFromPool() {
        if (!pool.isEmpty()) {
            return pool.remove(pool.size() - 1);
        }
        return new Effect(); // pool cạn → tạo mới
    }

    private void returnToPool(Effect effect) {
        effect.reset();
        if (pool.size() < POOL_SIZE) {
            pool.add(effect);
        }
    }

    @Override
    public void dispose() {
        activeEffects.clear();
        pool.clear();
    }

    // -------------------------------------------------------
    // Inner class Effect — đại diện 1 VFX instance
    // -------------------------------------------------------
    private static class Effect {
        float x, y;
        float ttl;         // time to live (giây)
        float elapsed;
        String type;
        boolean active;

        // Config theo type
        float maxRadius;
        Color color;

        void init(float x, float y, String type) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.elapsed = 0f;
            this.active = true;

            switch (type) {
                case "EXPLOSION":
                    ttl = 0.4f; maxRadius = 40f;
                    color = new Color(1f, 0.4f, 0f, 1f); // oranage
                    break;
                case "HIT":
                    ttl = 0.2f; maxRadius = 15f;
                    color = Color.WHITE.cpy();
                    break;
                case "HEAL":
                    ttl = 0.5f; maxRadius = 20f;
                    color = Color.GREEN.cpy();
                    break;
                case "BUFF":
                    ttl = 0.6f; maxRadius = 25f;
                    color = Color.CYAN.cpy();
                    break;
                default:
                    ttl = 0.3f; maxRadius = 15f;
                    color = Color.WHITE.cpy();
            }
        }

        void update(float delta) {
            elapsed += delta;
        }

        boolean isDone() {
            return elapsed >= ttl;
        }

        /**
         * Vẽ effect bằng cách scale + fade theo progress.
         * TODO: thay bằng TextureRegion animation khi có asset.
         */
        void render(SpriteBatch batch) {
            // Placeholder: không vẽ gì trong batch
            // ShapeRenderer cần gọi riêng — xem renderShapes()
        }

        /**
         * Vẽ placeholder hình tròn mở rộng + fade out.
         * Gọi với ShapeRenderer.begin() từ ngoài.
         */
        void renderShape(ShapeRenderer shapeRenderer) {
            float progress = elapsed / ttl;           // 0 → 1
            float radius   = maxRadius * progress;    // mở rộng
            float alpha    = 1f - progress;           // fade out

            shapeRenderer.setColor(color.r, color.g, color.b, alpha);
            shapeRenderer.circle(x, y, radius);
        }

        void reset() {
            active = false;
            elapsed = 0f;
        }
    }
}
