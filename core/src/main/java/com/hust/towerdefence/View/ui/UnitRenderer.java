/*
package com.hust.towerdefence.View.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SnapshotArray;
import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.PawnHacHoa;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.TNT;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.WarriorHacHoa;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Archer;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Healer;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Lancer;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Miner;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Pawn;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Warrior;
import com.hust.towerdefence.Model.Entities.Tower.BaseTower;

public class UnitRenderer {
    private static final float UNIT_VISUAL_HEIGHT = 64f;
    private static final float FRAME_DURATION = 0.11f;
    private static final float LANCER_REFERENCE_SOURCE_HEIGHT = 78f;

    private final SpriteBatch batch;
    private final ObjectMap<String, Clip> clips;
    private float stateTime;

    public UnitRenderer() {
        batch = new SpriteBatch();
        clips = new ObjectMap<>();
        loadClips();
    }

    public void render(OrthographicCamera camera, SnapshotArray<BaseEntity> entities, float delta) {
        stateTime += delta;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (BaseEntity entity : entities) {
            if (!(entity instanceof CombatEntity) || entity instanceof BaseTower || entity.isRemoved()) continue;
            CombatEntity combatEntity = (CombatEntity) entity;
            if (combatEntity.isDead()) continue;
            drawUnit(combatEntity);
        }
        batch.end();
    }

    private void drawUnit(CombatEntity entity) {
        Clip clip = selectClip(entity);
        if (clip == null) return;

        TextureRegion frame = clip.getFrame(stateTime);
        float height;
        float width;
        if (entity instanceof Lancer) {
            float scale = UNIT_VISUAL_HEIGHT / LANCER_REFERENCE_SOURCE_HEIGHT;
            width = frame.getRegionWidth() * scale;
            height = frame.getRegionHeight() * scale;
        } else {
            height = UNIT_VISUAL_HEIGHT * heightMultiplier(entity);
            width = height * frame.getRegionWidth() / frame.getRegionHeight();
        }
        float x = entity.getX() - width / 2f;
        float y = entity.getY() - height * 0.18f;

        boolean flipX = shouldFaceLeft(entity);
        if (flipX) {
            batch.draw(frame, x + width, y, -width, height);
        } else {
            batch.draw(frame, x, y, width, height);
        }
    }

    private Clip selectClip(CombatEntity entity) {
        String teamPrefix = entity.getTeam() == BaseEntity.Team.ENEMY ? "enemy" : "player";
        CombatEntity.State state = entity.getCurrentState();

        if (entity instanceof Miner) {
            if (state == CombatEntity.State.MINING) return get(teamPrefix, "miner_mine");
            if (state == CombatEntity.State.RETURNING_HOME) return get(teamPrefix, "miner_gold");
            if (state == CombatEntity.State.GOING_TO_MINE || state == CombatEntity.State.MOVING) return get(teamPrefix, "miner_run");
            return get(teamPrefix, "miner_idle");
        }
        if (entity instanceof Archer) {
            if (state == CombatEntity.State.ATTACKING) return get(teamPrefix, "archer_attack");
            if (state == CombatEntity.State.MOVING) return get(teamPrefix, "archer_run");
            return get(teamPrefix, "archer_idle");
        }
        if (entity instanceof Warrior || entity instanceof WarriorHacHoa) {
            if (state == CombatEntity.State.ATTACKING) return get(teamPrefix, "warrior_attack");
            if (state == CombatEntity.State.MOVING) return get(teamPrefix, "warrior_run");
            return get(teamPrefix, "warrior_idle");
        }
        if (entity instanceof Healer) {
            if (state == CombatEntity.State.HEALING) return get(teamPrefix, "monk_heal");
            if (state == CombatEntity.State.MOVING) return get(teamPrefix, "monk_run");
            return get(teamPrefix, "monk_idle");
        }
        if (entity instanceof Lancer) {
            if (state == CombatEntity.State.ATTACKING) return get(teamPrefix, lancerAttackClip(entity));
            if (state == CombatEntity.State.MOVING) return get(teamPrefix, "lancer_run");
            return get(teamPrefix, "lancer_idle");
        }
        if (entity instanceof TNT) {
            return get(teamPrefix, "tnt");
        }
        if (entity instanceof Pawn || entity instanceof PawnHacHoa) {
            if (state == CombatEntity.State.ATTACKING) return get(teamPrefix, "pawn_attack");
            if (state == CombatEntity.State.MOVING) return get(teamPrefix, "pawn_run");
            return get(teamPrefix, "pawn_idle");
        }
        return null;
    }

    private Clip get(String teamPrefix, String clipName) {
        Clip clip = clips.get(teamPrefix + "_" + clipName);
        if (clip != null) return clip;
        return clips.get("player_" + clipName);
    }

    private boolean shouldFaceLeft(CombatEntity entity) {
        return entity.getFacing().x < -0.05f;
    }

    private String lancerAttackClip(CombatEntity entity) {
        float x = entity.getFacing().x;
        float y = entity.getFacing().y;
        float absX = Math.abs(x);
        float absY = Math.abs(y);

        if (absY > absX * 1.4f) {
            return y >= 0f ? "lancer_attack_up" : "lancer_attack_down";
        }
        if (absY > absX * 0.45f) {
            return y >= 0f ? "lancer_attack_up_right" : "lancer_attack_down_right";
        }
        return "lancer_attack_right";
    }

    private float heightMultiplier(CombatEntity entity) {
        if (entity instanceof Warrior || entity instanceof WarriorHacHoa) return 1.08f;
        if (entity instanceof TNT) return 0.9f;
        if (entity instanceof Healer) return 0.96f;
        return 1f;
    }

    private void loadClips() {
        loadTeam("player", "Units");
        loadTeam("enemy", "EnemyUnits");
        put("enemy_tnt", "EnemyUnits/TNT/Red/TNT_Red.png", 192, 192);
    }

    private void loadTeam(String prefix, String root) {
        put(prefix + "_archer_idle", root + "/Archer/Archer_Idle.png", 192, 192);
        put(prefix + "_archer_run", root + "/Archer/Archer_Run.png", 192, 192);
        put(prefix + "_archer_attack", root + "/Archer/Archer_Shoot.png", 192, 192);

        put(prefix + "_warrior_idle", root + "/Warrior/Warrior_Idle.png", 192, 192);
        put(prefix + "_warrior_run", root + "/Warrior/Warrior_Run.png", 192, 192);
        put(prefix + "_warrior_attack", root + "/Warrior/Warrior_Attack1.png", 192, 192);

        put(prefix + "_monk_idle", root + "/Monk/Idle.png", 192, 192);
        put(prefix + "_monk_run", root + "/Monk/Run.png", 192, 192);
        put(prefix + "_monk_heal", root + "/Monk/Heal.png", 192, 192);

        put(prefix + "_lancer_idle", root + "/Lancer/Lancer_Idle.png", 320, 320);
        put(prefix + "_lancer_run", root + "/Lancer/Lancer_Run.png", 320, 320);
        put(prefix + "_lancer_attack_right", root + "/Lancer/Lancer_Right_Attack.png", 320, 320);
        put(prefix + "_lancer_attack_up", root + "/Lancer/Lancer_Up_Attack.png", 320, 320);
        put(prefix + "_lancer_attack_down", root + "/Lancer/Lancer_Down_Attack.png", 320, 320);
        put(prefix + "_lancer_attack_up_right", root + "/Lancer/Lancer_UpRight_Attack.png", 320, 320);
        put(prefix + "_lancer_attack_down_right", root + "/Lancer/Lancer_DownRight_Attack.png", 320, 320);

        put(prefix + "_miner_idle", root + "/Pawn/Pawn_Idle Pickaxe.png", 192, 192);
        put(prefix + "_miner_run", root + "/Pawn/Pawn_Run Pickaxe.png", 192, 192);
        put(prefix + "_miner_mine", root + "/Pawn/Pawn_Interact Pickaxe.png", 192, 192);
        put(prefix + "_miner_gold", root + "/Pawn/Pawn_Run Gold.png", 192, 192);

        put(prefix + "_pawn_idle", root + "/Pawn/Pawn_Idle Knife.png", 192, 192);
        put(prefix + "_pawn_run", root + "/Pawn/Pawn_Run Knife.png", 192, 192);
        put(prefix + "_pawn_attack", root + "/Pawn/Pawn_Interact Knife.png", 192, 192);
    }

    private void put(String key, String path, int frameWidth, int frameHeight) {
        FileHandle file = Gdx.files.internal(path);
        if (!file.exists()) return;
        clips.put(key, new Clip(path, frameWidth, frameHeight));
    }

    public void dispose() {
        for (Clip clip : clips.values()) {
            clip.dispose();
        }
        batch.dispose();
    }

    private static class Clip {
        private final Texture texture;
        private final TextureRegion[] frames;

        private Clip(String path, int frameWidth, int frameHeight) {
            texture = new Texture(path);
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            int frameCount = Math.max(1, texture.getWidth() / frameWidth);
            Rectangle crop = computeCrop(path, frameWidth, frameHeight);
            frames = new TextureRegion[frameCount];
            for (int i = 0; i < frameCount; i++) {
                frames[i] = new TextureRegion(
                    texture,
                    i * frameWidth + (int) crop.x,
                    (int) crop.y,
                    (int) crop.width,
                    (int) crop.height
                );
            }
        }

        private TextureRegion getFrame(float stateTime) {
            int index = (int) (stateTime / FRAME_DURATION) % frames.length;
            return frames[index];
        }

        private void dispose() {
            texture.dispose();
        }

        private static Rectangle computeCrop(String path, int frameWidth, int frameHeight) {
            Pixmap pixmap = new Pixmap(Gdx.files.internal(path));
            int minX = frameWidth;
            int minY = frameHeight;
            int maxX = -1;
            int maxY = -1;
            for (int y = 0; y < frameHeight && y < pixmap.getHeight(); y++) {
                for (int x = 0; x < frameWidth && x < pixmap.getWidth(); x++) {
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
                return new Rectangle(0, 0, frameWidth, frameHeight);
            }
            return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
        }
    }
}
*/


// Hưng
package com.hust.towerdefence.View.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SnapshotArray;
import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.PawnHacHoa;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.TNT;
import com.hust.towerdefence.Model.Entities.Combat.Enemy.WarriorHacHoa;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Archer;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Healer;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Lancer;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Miner;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Pawn;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Warrior;
import com.hust.towerdefence.Model.Entities.Tower.BaseTower;

/**
 * Lớp quản lý việc vẽ các đơn vị chiến đấu (CombatEntity) lên màn hình.
 * Hỗ trợ bóc tách hoạt ảnh từ SpriteSheet đơn hàng (Single-row) và đa hàng (Multi-row).
 */
public class UnitRenderer {
    private static final float UNIT_VISUAL_HEIGHT = 64f;
    private static final float FRAME_DURATION = 0.11f;
    private static final float LANCER_REFERENCE_SOURCE_HEIGHT = 78f;

    private final SpriteBatch batch;
    private final ObjectMap<String, Clip> clips;
    private float stateTime;

    public UnitRenderer() {
        batch = new SpriteBatch();
        clips = new ObjectMap<>();
        loadClips();
    }

    public void render(OrthographicCamera camera, SnapshotArray<BaseEntity> entities, float delta) {
        stateTime += delta;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (BaseEntity entity : entities) {
            if (!(entity instanceof CombatEntity) || entity instanceof BaseTower || entity.isRemoved()) continue;
            CombatEntity combatEntity = (CombatEntity) entity;
            if (combatEntity.isDead()) continue;
            drawUnit(combatEntity);
        }
        batch.end();
    }

    private void drawUnit(CombatEntity entity) {
        Clip clip = selectClip(entity);
        if (clip == null) return;

        TextureRegion frame;
        CombatEntity.State state = entity.getCurrentState();

        // Xử lý cắt khung hình đặc thù cho các SpriteSheet đa hàng tổng hợp
        if (entity instanceof TNT) {
            if (state == CombatEntity.State.ATTACKING) {
                frame = clip.getFrameTNT(stateTime, 14, 17);
            } else if (state == CombatEntity.State.MOVING) {
                frame = clip.getFrameTNT(stateTime, 7, 11);
            } else {
                frame = clip.getFrameTNT(stateTime, 0, 5);
            }
        } else if (entity instanceof PawnHacHoa) {
            if (state == CombatEntity.State.ATTACKING) {
                frame = clip.getFramePawnHacHoa(stateTime, 14, 19);
            } else if (state == CombatEntity.State.MOVING) {
                frame = clip.getFramePawnHacHoa(stateTime, 7, 12);
            } else {
                frame = clip.getFramePawnHacHoa(stateTime, 0, 6);
            }
        } else {
            frame = clip.getFrame(stateTime);
        }

        // Tính toán kích thước hiển thị (Kéo giãn theo tỷ lệ khung gốc)
        float height;
        float width;
        if (entity instanceof Lancer) {
            float scale = UNIT_VISUAL_HEIGHT / LANCER_REFERENCE_SOURCE_HEIGHT;
            width = frame.getRegionWidth() * scale;
            height = frame.getRegionHeight() * scale;
        } else {
            height = UNIT_VISUAL_HEIGHT * heightMultiplier(entity);
            width = height * frame.getRegionWidth() / frame.getRegionHeight();
        }
        float x = entity.getX() - width / 2f;
        float y = entity.getY() - height * 0.18f;

        // Xử lý lật ảnh theo hướng di chuyển/nhìn của Entity
        boolean flipX = shouldFaceLeft(entity);
        if (flipX) {
            batch.draw(frame, x + width, y, -width, height);
        } else {
            batch.draw(frame, x, y, width, height);
        }
    }

    private Clip selectClip(CombatEntity entity) {
        String teamPrefix = entity.getTeam() == BaseEntity.Team.ENEMY ? "enemy" : "player";
        CombatEntity.State state = entity.getCurrentState();

        if (entity instanceof Miner) {
            if (state == CombatEntity.State.MINING) return get(teamPrefix, "miner_mine");
            if (state == CombatEntity.State.RETURNING_HOME) return get(teamPrefix, "miner_gold");
            if (state == CombatEntity.State.GOING_TO_MINE || state == CombatEntity.State.MOVING) return get(teamPrefix, "miner_run");
            return get(teamPrefix, "miner_idle");
        }
        if (entity instanceof Archer) {
            if (state == CombatEntity.State.ATTACKING) return get(teamPrefix, "archer_attack");
            if (state == CombatEntity.State.MOVING) return get(teamPrefix, "archer_run");
            return get(teamPrefix, "archer_idle");
        }
        if (entity instanceof Warrior || entity instanceof WarriorHacHoa) {
            if (state == CombatEntity.State.ATTACKING) return get(teamPrefix, "warrior_attack");
            if (state == CombatEntity.State.MOVING) return get(teamPrefix, "warrior_run");
            return get(teamPrefix, "warrior_idle");
        }
        if (entity instanceof Healer) {
            if (state == CombatEntity.State.HEALING) return get(teamPrefix, "monk_heal");
            if (state == CombatEntity.State.MOVING) return get(teamPrefix, "monk_run");
            return get(teamPrefix, "monk_idle");
        }
        if (entity instanceof Lancer) {
            if (state == CombatEntity.State.ATTACKING) return get(teamPrefix, lancerAttackClip(entity));
            if (state == CombatEntity.State.MOVING) return get(teamPrefix, "lancer_run");
            return get(teamPrefix, "lancer_idle");
        }
        if (entity instanceof TNT) {
            return get(teamPrefix, "tnt");
        }
        if (entity instanceof PawnHacHoa) {
            return clips.get("enemy_pawn_hachoa");
        }
        if (entity instanceof Pawn) {
            if (state == CombatEntity.State.ATTACKING) return get(teamPrefix, "pawn_attack");
            if (state == CombatEntity.State.MOVING) return get(teamPrefix, "pawn_run");
            return get(teamPrefix, "pawn_idle");
        }
        return null;
    }

    private Clip get(String teamPrefix, String clipName) {
        Clip clip = clips.get(teamPrefix + "_" + clipName);
        if (clip != null) return clip;
        return clips.get("player_" + clipName);
    }

    private boolean shouldFaceLeft(CombatEntity entity) {
        return entity.getFacing().x < -0.05f;
    }

    private String lancerAttackClip(CombatEntity entity) {
        float x = entity.getFacing().x;
        float y = entity.getFacing().y;
        float absX = Math.abs(x);
        float absY = Math.abs(y);

        if (absY > absX * 1.4f) {
            return y >= 0f ? "lancer_attack_up" : "lancer_attack_down";
        }
        if (absY > absX * 0.45f) {
            return y >= 0f ? "lancer_attack_up_right" : "lancer_attack_down_right";
        }
        return "lancer_attack_right";
    }

    private float heightMultiplier(CombatEntity entity) {
        if (entity instanceof Warrior || entity instanceof WarriorHacHoa) return 1.08f;
        if (entity instanceof TNT) return 0.9f;
        if (entity instanceof Healer) return 0.96f;
        return 1f;
    }

    private void loadClips() {
        loadTeam("player", "Units");
        loadTeam("enemy", "EnemyUnits");

        // Load các SpriteSheet tổng hợp đa hàng
        put("enemy_tnt", "EnemyUnits/TNT/Red/TNT_Red.png", 192, 192);
        put("enemy_pawn_hachoa", "EnemyUnits/Torch/Red/Torch_Red.png", 192, 192);
    }

    private void loadTeam(String prefix, String root) {
        put(prefix + "_archer_idle", root + "/Archer/Archer_Idle.png", 192, 192);
        put(prefix + "_archer_run", root + "/Archer/Archer_Run.png", 192, 192);
        put(prefix + "_archer_attack", root + "/Archer/Archer_Shoot.png", 192, 192);

        put(prefix + "_warrior_idle", root + "/Warrior/Warrior_Idle.png", 192, 192);
        put(prefix + "_warrior_run", root + "/Warrior/Warrior_Run.png", 192, 192);
        put(prefix + "_warrior_attack", root + "/Warrior/Warrior_Attack1.png", 192, 192);

        put(prefix + "_monk_idle", root + "/Monk/Idle.png", 192, 192);
        put(prefix + "_monk_run", root + "/Monk/Run.png", 192, 192);
        put(prefix + "_monk_heal", root + "/Monk/Heal.png", 192, 192);

        put(prefix + "_lancer_idle", root + "/Lancer/Lancer_Idle.png", 320, 320);
        put(prefix + "_lancer_run", root + "/Lancer/Lancer_Run.png", 320, 320);
        put(prefix + "_lancer_attack_right", root + "/Lancer/Lancer_Right_Attack.png", 320, 320);
        put(prefix + "_lancer_attack_up", root + "/Lancer/Lancer_Up_Attack.png", 320, 320);
        put(prefix + "_lancer_attack_down", root + "/Lancer/Lancer_Down_Attack.png", 320, 320);
        put(prefix + "_lancer_attack_up_right", root + "/Lancer/Lancer_UpRight_Attack.png", 320, 320);
        put(prefix + "_lancer_attack_down_right", root + "/Lancer/Lancer_DownRight_Attack.png", 320, 320);

        put(prefix + "_miner_idle", root + "/Pawn/Pawn_Idle Pickaxe.png", 192, 192);
        put(prefix + "_miner_run", root + "/Pawn/Pawn_Run Pickaxe.png", 192, 192);
        put(prefix + "_miner_mine", root + "/Pawn/Pawn_Interact Pickaxe.png", 192, 192);
        put(prefix + "_miner_gold", root + "/Pawn/Pawn_Run Gold.png", 192, 192);

        put(prefix + "_pawn_idle", root + "/Pawn/Pawn_Idle Knife.png", 192, 192);
        put(prefix + "_pawn_run", root + "/Pawn/Pawn_Run Knife.png", 192, 192);
        put(prefix + "_pawn_attack", root + "/Pawn/Pawn_Interact Knife.png", 192, 192);
    }

    private void put(String key, String path, int frameWidth, int frameHeight) {
        FileHandle file = Gdx.files.internal(path);
        if (!file.exists()) return;
        clips.put(key, new Clip(path, frameWidth, frameHeight));
    }

    public void dispose() {
        for (Clip clip : clips.values()) {
            clip.dispose();
        }
        batch.dispose();
    }

    /**
     * Lớp đóng gói kết cấu hình ảnh (Texture) và mảng các vùng cắt (TextureRegion).
     */
    private static class Clip {
        private final Texture texture;
        private final TextureRegion[] frames;
        private final int frameWidth;
        private final int frameHeight;

        private Clip(String path, int frameWidth, int frameHeight) {
            this.texture = new Texture(path);
            this.frameWidth = frameWidth;
            this.frameHeight = frameHeight;

            this.texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            int frameCount = Math.max(1, texture.getWidth() / frameWidth);
            Rectangle crop = computeCrop(path, frameWidth, frameHeight);

            frames = new TextureRegion[frameCount];
            for (int i = 0; i < frameCount; i++) {
                frames[i] = new TextureRegion(
                    texture,
                    i * frameWidth + (int) crop.x,
                    (int) crop.y,
                    (int) crop.width,
                    (int) crop.height
                );
            }
        }

        // Lấy frame tự động cho SpriteSheet đơn hàng (Single-row)
        private TextureRegion getFrame(float stateTime) {
            int index = (int) (stateTime / FRAME_DURATION) % frames.length;
            return frames[index];
        }

        // Lấy frame và bóp nhỏ khung riêng cho TNT (Phóng to kích thước hiển thị)
        private TextureRegion getFrameTNT(float stateTime, int startFrame, int endFrame) {
            int cols = Math.max(1, texture.getWidth() / 192);
            int totalActionFrames = endFrame - startFrame + 1;
            int currentIndex = startFrame + ((int) (stateTime / FRAME_DURATION) % totalActionFrames);

            int col = currentIndex % cols;
            int row = currentIndex / cols;

            return new TextureRegion(
                texture,
                col * 192 + 63,
                row * 192 + 73,
                66,
                66
            );
        }

        // Lấy frame và bóp nhỏ khung riêng cho PawnHacHoa để tăng kích cỡ lính trong game
        private TextureRegion getFramePawnHacHoa(float stateTime, int startFrame, int endFrame) {
            int cols = Math.max(1, texture.getWidth() / 192);
            int totalActionFrames = endFrame - startFrame + 1;
            int currentIndex = startFrame + ((int) (stateTime / FRAME_DURATION) % totalActionFrames);

            int col = currentIndex % cols;
            int row = currentIndex / cols;

            return new TextureRegion(
                texture,
                col * 192 + 50,
                row * 192 + 60,
                80,
                80
            );
        }

        private void dispose() {
            texture.dispose();
        }

        // Tính toán loại bỏ vùng trống trong suốt quanh frame hình bằng thuật toán quét Pixel Alpha
        private static Rectangle computeCrop(String path, int frameWidth, int frameHeight) {
            Pixmap pixmap = new Pixmap(Gdx.files.internal(path));
            int minX = frameWidth;
            int minY = frameHeight;
            int maxX = -1;
            int maxY = -1;

            for (int y = 0; y < frameHeight && y < pixmap.getHeight(); y++) {
                for (int x = 0; x < frameWidth && x < pixmap.getWidth(); x++) {
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
                return new Rectangle(0, 0, frameWidth, frameHeight);
            }
            return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
        }
    }
}
