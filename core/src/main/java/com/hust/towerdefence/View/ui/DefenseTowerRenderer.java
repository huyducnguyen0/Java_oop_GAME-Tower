package com.hust.towerdefence.View.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.SnapshotArray;
import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Tower.BaseTower;
import com.hust.towerdefence.Model.Entities.Tower.DefenseTower;

public class DefenseTowerRenderer {
    private static final String PLAYER_TOWER = "Buildings/Tower.png";
    private static final String ENEMY_TOWER = "EnemyBuildings/Wood_Tower/Tower.png";
    private static final String PLAYER_ARCHER_IDLE = "Units/Archer/Archer_Idle.png";
    private static final String PLAYER_ARCHER_SHOOT = "Units/Archer/Archer_Shoot.png";
    private static final String ENEMY_ARCHER_IDLE = "EnemyUnits/Archer/Archer_Idle.png";
    private static final String ENEMY_ARCHER_SHOOT = "EnemyUnits/Archer/Archer_Shoot.png";
    private static final String DESTROY_EFFECT = "Destroyed_Effect/Explosion_02.png";
    private static final float UNIT_VISUAL_HEIGHT = 64f;
    private static final float TOWER_ARCHER_PLATFORM_RATIO = 0.58f;
    private static final float ARCHER_FRAME_DURATION = 0.09f;
    private static final int ARCHER_FRAME_SIZE = 192;
    private static final int EXPLOSION_FRAME_SIZE = 192;
    private static final int EXPLOSION_FRAME_COUNT = 10;

    private final Texture playerTower;
    private final Texture enemyTower;
    private final Texture destroyEffect;
    private final TextureRegion playerTowerRegion;
    private final TextureRegion enemyTowerRegion;
    private final ArcherClip playerArcherIdle;
    private final ArcherClip playerArcherShoot;
    private final ArcherClip enemyArcherIdle;
    private final ArcherClip enemyArcherShoot;
    private final TextureRegion[] destroyFrames;
    private final SpriteBatch batch;
    private float stateTime;

    public DefenseTowerRenderer() {
        playerTower = new Texture(PLAYER_TOWER);
        enemyTower = new Texture(ENEMY_TOWER);
        destroyEffect = new Texture(DESTROY_EFFECT);
        playerTower.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        enemyTower.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        destroyEffect.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        playerTowerRegion = new TextureRegion(playerTower);
        enemyTowerRegion = new TextureRegion(enemyTower);
        playerArcherIdle = new ArcherClip(PLAYER_ARCHER_IDLE);
        playerArcherShoot = new ArcherClip(PLAYER_ARCHER_SHOOT);
        enemyArcherIdle = new ArcherClip(ENEMY_ARCHER_IDLE);
        enemyArcherShoot = new ArcherClip(ENEMY_ARCHER_SHOOT);
        destroyFrames = new TextureRegion[EXPLOSION_FRAME_COUNT];
        for (int i = 0; i < EXPLOSION_FRAME_COUNT; i++) {
            destroyFrames[i] = new TextureRegion(destroyEffect, i * EXPLOSION_FRAME_SIZE, 0, EXPLOSION_FRAME_SIZE, EXPLOSION_FRAME_SIZE);
        }
        batch = new SpriteBatch();
    }

    public void render(OrthographicCamera camera, SnapshotArray<BaseTower> towers) {
        stateTime += Gdx.graphics.getDeltaTime();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (int i = 0; i < towers.size; i++) {
            BaseTower tower = towers.get(i);
            if (!(tower instanceof DefenseTower) || tower.isRemoved()) continue;
            DefenseTower defenseTower = (DefenseTower) tower;
            if (defenseTower.isDestroying()) {
                drawDestroyEffect(defenseTower);
            } else if (!defenseTower.isDead()) {
                drawTower(defenseTower);
                drawTowerArcher(defenseTower, towers);
            }
        }
        batch.end();
    }

    private void drawTower(DefenseTower tower) {
        TextureRegion texture = tower.getTeam() == BaseEntity.Team.ENEMY ? enemyTowerRegion : playerTowerRegion;
        float width = tower.getWidth();
        float height = width * texture.getRegionHeight() / texture.getRegionWidth();
        float x = tower.getX() - width / 2f;
        float y = tower.getY() - tower.getHeight() / 2f;
        batch.draw(texture, x, y, width, height);
    }

    private void drawTowerArcher(DefenseTower tower, SnapshotArray<BaseTower> towers) {
        ArcherClip clip;
        if (tower.getTeam() == BaseEntity.Team.ENEMY) {
            clip = tower.getCurrentState() == CombatEntity.State.ATTACKING ? enemyArcherShoot : enemyArcherIdle;
        } else {
            clip = tower.getCurrentState() == CombatEntity.State.ATTACKING ? playerArcherShoot : playerArcherIdle;
        }
        TextureRegion archer = clip.getFrame(stateTime);
        float towerWidth = tower.getWidth();
        float towerHeight = towerWidth * 2f;
        float archerHeight = UNIT_VISUAL_HEIGHT;
        float archerWidth = archerHeight * archer.getRegionWidth() / archer.getRegionHeight();
        float x = tower.getX() - archerWidth / 2f;
        float y = tower.getY() - tower.getHeight() / 2f + towerHeight * TOWER_ARCHER_PLATFORM_RATIO;
        if (shouldFaceLeft(tower, towers)) {
            batch.draw(archer, x + archerWidth, y, -archerWidth, archerHeight);
        } else {
            batch.draw(archer, x, y, archerWidth, archerHeight);
        }
    }

    private boolean shouldFaceLeft(DefenseTower tower, SnapshotArray<BaseTower> towers) {
        DefenseTower closestSameTeamTower = null;
        float closestDistance = Float.MAX_VALUE;
        for (int i = 0; i < towers.size; i++) {
            BaseTower other = towers.get(i);
            if (!(other instanceof DefenseTower) || other == tower || other.isRemoved()) continue;
            DefenseTower otherTower = (DefenseTower) other;
            if (otherTower.getTeam() != tower.getTeam()) continue;

            float distance = tower.getPosition().dst2(otherTower.getPosition());
            if (distance < closestDistance) {
                closestDistance = distance;
                closestSameTeamTower = otherTower;
            }
        }

        return closestSameTeamTower != null && tower.getX() > closestSameTeamTower.getX();
    }

    private void drawDestroyEffect(DefenseTower tower) {
        int frameIndex = Math.min(EXPLOSION_FRAME_COUNT - 1, (int) (tower.getDestroyProgress() * EXPLOSION_FRAME_COUNT));
        TextureRegion frame = destroyFrames[frameIndex];
        float size = tower.getWidth() * 1.7f;
        float x = tower.getX() - size / 2f;
        float y = tower.getY() - tower.getHeight() / 2f + tower.getWidth() * 0.2f;
        batch.draw(frame, x, y, size, size);
    }

    public void dispose() {
        playerTower.dispose();
        enemyTower.dispose();
        playerArcherIdle.dispose();
        playerArcherShoot.dispose();
        enemyArcherIdle.dispose();
        enemyArcherShoot.dispose();
        destroyEffect.dispose();
        batch.dispose();
    }

    private static class ArcherClip {
        private final Texture texture;
        private final TextureRegion[] frames;

        private ArcherClip(String path) {
            texture = new Texture(path);
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            int frameCount = Math.max(1, texture.getWidth() / ARCHER_FRAME_SIZE);
            Rectangle crop = computeCrop(path);
            frames = new TextureRegion[frameCount];
            for (int i = 0; i < frameCount; i++) {
                frames[i] = new TextureRegion(
                    texture,
                    i * ARCHER_FRAME_SIZE + (int) crop.x,
                    (int) crop.y,
                    (int) crop.width,
                    (int) crop.height
                );
            }
        }

        private TextureRegion getFrame(float time) {
            int index = (int) (time / ARCHER_FRAME_DURATION) % frames.length;
            return frames[index];
        }

        private void dispose() {
            texture.dispose();
        }

        private static Rectangle computeCrop(String path) {
            Pixmap pixmap = new Pixmap(Gdx.files.internal(path));
            int minX = ARCHER_FRAME_SIZE;
            int minY = ARCHER_FRAME_SIZE;
            int maxX = -1;
            int maxY = -1;
            for (int y = 0; y < ARCHER_FRAME_SIZE && y < pixmap.getHeight(); y++) {
                for (int x = 0; x < ARCHER_FRAME_SIZE && x < pixmap.getWidth(); x++) {
                    int alpha = pixmap.getPixel(x, y) & 0xff;
                    if (alpha > 10) {
                        if (x < minX) minX = x;
                        if (y < minY) minY = y;
                        if (x > maxX) maxX = x;
                        if (y > maxY) maxY = y;
                    }
                }
            }
            pixmap.dispose();
            if (maxX < minX || maxY < minY) {
                return new Rectangle(0, 0, ARCHER_FRAME_SIZE, ARCHER_FRAME_SIZE);
            }
            return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
        }
    }
}
