package com.hust.towerdefence.View.ui;

public class AudioManager {
    private final UiAssets assets;
    private float soundVolume = 0.6f; // Âm lượng hiệu ứng (0.0 đến 1.0)
    private float musicVolume = 0.4f; // Âm lượng nhạc nền (0.0 đến 1.0) - nên để nhỏ hơn để tránh chói tai

    public AudioManager(UiAssets assets) {
        this.assets = assets;
    }

    // --- LOGIC NHẠC NỀN (MUSIC) ---
    public void playMenuMusic() {
        if (assets.menuBgm == null) return;
        assets.menuBgm.setLooping(true);
        assets.menuBgm.setVolume(musicVolume);
        if (!assets.menuBgm.isPlaying()) {
            assets.menuBgm.play();
        }
    }

    public void stopMenuMusic() {
        if (assets.menuBgm != null && assets.menuBgm.isPlaying()) {
            assets.menuBgm.stop();
        }
    }

    public void playGameplayMusic() {
        if (assets.gameplayBgm == null) return;
        assets.gameplayBgm.setLooping(true);
        assets.gameplayBgm.setVolume(musicVolume);
        if (!assets.gameplayBgm.isPlaying()) {
            assets.gameplayBgm.play();
        }
    }

    public void stopGameplayMusic() {
        if (assets.gameplayBgm != null && assets.gameplayBgm.isPlaying()) {
            assets.gameplayBgm.stop();
        }
    }

    // --- LOGIC HIỆU ỨNG (SOUND) ---
    public void playScreenClick() {
        if (assets.screenClickSound != null) {
            assets.screenClickSound.play(soundVolume);
        }
    }

    public void playBuySuccess() {
        if (assets.buySuccessSound != null) {
            assets.buySuccessSound.play(soundVolume);
        }
    }

    public void playUpgradeSuccess() {
        if (assets.upgradeSuccessSound != null) {
            assets.upgradeSuccessSound.play(soundVolume);
        }
    }

    // Hàm này giúp sau này làm nút Mute/Unmute hoặc thanh kéo Volume ở cài đặt
    public void setVolumes(float musicVol, float soundVol) {
        this.musicVolume = musicVol;
        this.soundVolume = soundVol;
        if (assets.menuBgm != null) assets.menuBgm.setVolume(musicVol);
        if (assets.gameplayBgm != null) assets.gameplayBgm.setVolume(musicVol);
    }
}
