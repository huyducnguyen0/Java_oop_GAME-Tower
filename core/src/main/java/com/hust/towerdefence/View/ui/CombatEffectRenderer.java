package com.hust.towerdefence.View.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Systems.CombatVisualEvent;

public class CombatEffectRenderer {
    private static final String PLAYER_ARROW = "Units/Archer/Arrow.png";
    private static final String ENEMY_ARROW = "EnemyUnits/Archer/Arrow.png";
    private static final String DYNAMITE = "EnemyUnits/TNT/Dynamite/Dynamite.png";
    private static final String PLAYER_HEAL = "Units/Monk/Heal_Effect.png";
    private static final String ENEMY_HEAL = "EnemyUnits/Monk/Heal_Effect.png";

    private static final float ARROW_SPEED = 720f;
    private static final float DYNAMITE_SPEED = 420f;
    private static final float MIN_PROJECTILE_DURATION = 0.16f;
    private static final float MAX_PROJECTILE_DURATION = 0.55f;
    private static final float HEAL_DURATION = 0.55f;

    private final SpriteBatch batch;
    private final Texture playerArrow;
    private final Texture enemyArrow;
    private final Texture dynamite;
    private final Texture playerHeal;
    private final Texture enemyHeal;
    private final TextureRegion[] dynamiteFrames;
    private final TextureRegion[] playerHealFrames;
    private final TextureRegion[] enemyHealFrames;
    private final Array<ActiveEffect> effects = new Array<>();

    public CombatEffectRenderer() {
        batch = new SpriteBatch();
        playerArrow = loadTexture(PLAYER_ARROW);
        enemyArrow = loadTexture(ENEMY_ARROW);
        dynamite = loadTexture(DYNAMITE);
        playerHeal = loadTexture(PLAYER_HEAL);
        enemyHeal = loadTexture(ENEMY_HEAL);
        dynamiteFrames = split(dynamite, 64, 64);
        playerHealFrames = split(playerHeal, 192, 192);
        enemyHealFrames = split(enemyHeal, 192, 192);
    }

    public void addEvents(Array<CombatVisualEvent> events) {
        for (CombatVisualEvent event : events) {
            effects.add(new ActiveEffect(event));
        }
    }

    public void render(OrthographicCamera camera, float delta) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (int i = effects.size - 1; i >= 0; i--) {
            ActiveEffect effect = effects.get(i);
            effect.time += delta;
            if (effect.isFinished()) {
                effects.removeIndex(i);
                continue;
            }
            drawEffect(effect);
        }
        batch.end();
    }

    private void drawEffect(ActiveEffect effect) {
        if (effect.type == CombatVisualEvent.Type.HEAL) {
            drawHeal(effect);
        } else if (effect.type == CombatVisualEvent.Type.DYNAMITE) {
            drawProjectile(effect, getFrame(dynamiteFrames, effect.progress()), 42f);
        } else {
            Texture arrow = effect.team == BaseEntity.Team.ENEMY ? enemyArrow : playerArrow;
            drawProjectile(effect, new TextureRegion(arrow), 34f);
        }
    }

    private void drawProjectile(ActiveEffect effect, TextureRegion region, float size) {
        float progress = effect.progress();
        float x = MathUtils.lerp(effect.start.x, effect.end.x, progress);
        float y = MathUtils.lerp(effect.start.y, effect.end.y, progress);
        float angle = effect.end.cpy().sub(effect.start).angleDeg();
        batch.draw(region, x - size / 2f, y - size / 2f, size / 2f, size / 2f, size, size, 1f, 1f, angle);
    }

    private void drawHeal(ActiveEffect effect) {
        TextureRegion frame = getFrame(effect.team == BaseEntity.Team.ENEMY ? enemyHealFrames : playerHealFrames, effect.progress());
        float size = 72f;
        batch.draw(frame, effect.end.x - size / 2f, effect.end.y - size / 2f, size, size);
    }

    private TextureRegion getFrame(TextureRegion[] frames, float progress) {
        int index = Math.min(frames.length - 1, (int) (progress * frames.length));
        return frames[index];
    }

    private Texture loadTexture(String path) {
        Texture texture = new Texture(path);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return texture;
    }

    private TextureRegion[] split(Texture texture, int frameWidth, int frameHeight) {
        int count = Math.max(1, texture.getWidth() / frameWidth);
        TextureRegion[] frames = new TextureRegion[count];
        for (int i = 0; i < count; i++) {
            frames[i] = new TextureRegion(texture, i * frameWidth, 0, frameWidth, frameHeight);
        }
        return frames;
    }

    public void dispose() {
        playerArrow.dispose();
        enemyArrow.dispose();
        dynamite.dispose();
        playerHeal.dispose();
        enemyHeal.dispose();
        batch.dispose();
    }

    private static class ActiveEffect {
        private final CombatVisualEvent.Type type;
        private final BaseEntity.Team team;
        private final Vector2 start;
        private final Vector2 end;
        private final float duration;
        private float time;

        private ActiveEffect(CombatVisualEvent event) {
            type = event.getType();
            team = event.getTeam();
            start = event.getStart();
            end = event.getEnd();
            duration = computeDuration();
        }

        private float computeDuration() {
            if (type == CombatVisualEvent.Type.HEAL) {
                return HEAL_DURATION;
            }
            float speed = type == CombatVisualEvent.Type.DYNAMITE ? DYNAMITE_SPEED : ARROW_SPEED;
            return MathUtils.clamp(start.dst(end) / speed, MIN_PROJECTILE_DURATION, MAX_PROJECTILE_DURATION);
        }

        private float progress() {
            return Math.min(1f, time / duration);
        }

        private boolean isFinished() {
            return time >= duration;
        }
    }
}
