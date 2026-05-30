/*
package com.hust.towerdefence.View.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.BuildingZone;
import com.hust.towerdefence.Model.Managers.EconomyManager;

public class PurchasePanel {
    private static final float PANEL_WIDTH = 220f;
    private static final float PANEL_HEIGHT = 96f;
    private static final float PANEL_Y_OFFSET = 22f;
    private static final float SCREEN_MARGIN = 14f;
    private static final Color INK = new Color(0.17f, 0.09f, 0.04f, 1f);
    private static final Color MUTED = new Color(0.42f, 0.29f, 0.18f, 1f);
    private static final Color WARNING = new Color(0.65f, 0.12f, 0.08f, 1f);

    private final GameWorld gameWorld;
    private final Table panel;
    private final Label titleLabel;
    private final Label buildingLabel;
    private final Label costLabel;
    private final TextButton buyButton;

    private PurchaseOption selectedOption;
    private BuildingZone selectedZone;

    public PurchasePanel(Stage stage, GameWorld gameWorld, UiAssets assets) {
        this.gameWorld = gameWorld;
        this.panel = new Table();
        this.titleLabel = new Label("", assets.getTitleLabelStyle());
        this.buildingLabel = new Label("", assets.getMutedLabelStyle());
        this.costLabel = new Label("", assets.getDefaultLabelStyle());
        this.buyButton = new TextButton("Buy", assets.getPrimaryButtonStyle());

        buildLayout(assets);
        stage.addActor(panel);
        panel.setVisible(false);

        buyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buySelectedUnit();
            }
        });
    }

    private void buildLayout(UiAssets assets) {
        panel.setBackground(assets.getPanelDrawable());
        panel.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        panel.pad(12, 16, 12, 16);
        panel.defaults().center();

        titleLabel.setAlignment(Align.center);
        buildingLabel.setAlignment(Align.center);
        costLabel.setAlignment(Align.left);
        buyButton.getLabel().setAlignment(Align.center);

        panel.add(titleLabel).center().expandX().fillX().height(22).colspan(2);
        panel.row();
        panel.add(buildingLabel).center().expandX().fillX().height(18).colspan(2).padBottom(4);
        panel.row();
        panel.add(costLabel).left().expandX().fillX().height(30).padRight(8);
        panel.add(buyButton).width(76).height(30);
    }

    public void show(BuildingZone zone) {
        selectedZone = zone;
        selectedOption = PurchaseOption.fromBuilding(zone);
        if (selectedOption == null) {
            hide();
            return;
        }
        panel.setVisible(true);
        updateTextAndState();
    }

    public void hide() {
        selectedZone = null;
        selectedOption = null;
        panel.setVisible(false);
    }

    public void update(OrthographicCamera worldCamera, float screenWidth, float screenHeight) {
        if (selectedZone == null || selectedOption == null) return;
        updateTextAndState();

        Vector2 center = selectedZone.getCenter();
        Vector3 projected = worldCamera.project(new Vector3(center.x, center.y, 0f));
        float x = MathUtils.clamp(projected.x - PANEL_WIDTH / 2f, SCREEN_MARGIN, screenWidth - PANEL_WIDTH - SCREEN_MARGIN);
        float y = MathUtils.clamp(projected.y + PANEL_Y_OFFSET, SCREEN_MARGIN, screenHeight - PANEL_HEIGHT - SCREEN_MARGIN);
        panel.setPosition(x, y);
    }

    private void updateTextAndState() {
        if (selectedOption == null) return;
        int gold = gameWorld.getEconomyManager().getGold();
        boolean canBuy = gold >= selectedOption.cost;
        titleLabel.setText("Train " + selectedOption.unitName);
        buildingLabel.setText(selectedZone == null ? "" : selectedZone.getDisplayName());
        costLabel.setText("Gold " + selectedOption.cost);
        costLabel.setColor(canBuy ? INK : WARNING);
        buildingLabel.setColor(canBuy ? MUTED : WARNING);
        buyButton.setDisabled(!canBuy);
    }

    private void buySelectedUnit() {
        if (selectedOption == null || buyButton.isDisabled()) return;

        switch (selectedOption) {
            case WARRIOR:
                gameWorld.spawnWarrior();
                break;
            case ARCHER:
                gameWorld.spawnArcher();
                break;
            case HEALER:
                gameWorld.spawnHealer();
                break;
            case LANCER:
                gameWorld.spawnLancer();
                break;
            case MINER:
                gameWorld.spawnMiner();
                break;
            default:
                break;
        }
        updateTextAndState();
    }

    private enum PurchaseOption {
        WARRIOR("Warrior", EconomyManager.COST_WARRIOR),
        ARCHER("Archer", EconomyManager.COST_ARCHER),
        HEALER("Monk", EconomyManager.COST_HEALER),
        LANCER("Lancer", EconomyManager.COST_LANCER),
        MINER("Miner", EconomyManager.COST_MINER);

        private final String unitName;
        private final int cost;

        PurchaseOption(String unitName, int cost) {
            this.unitName = unitName;
            this.cost = cost;
        }

        private static PurchaseOption fromBuilding(BuildingZone zone) {
            if (zone == null) return null;
            String building = zone.getName();
            if ("barrack".equals(building)) return WARRIOR;
            if ("archery".equals(building)) return ARCHER;
            if ("monastery".equals(building)) return HEALER;
            if ("house1".equals(building)) return LANCER;
            if ("house2".equals(building)) return MINER;
            return null;
        }
    }
}
*/

/*// Hưng
package com.hust.towerdefence.View.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.BuildingZone;
import com.hust.towerdefence.Model.Managers.EconomyManager;

public class PurchasePanel {
    private static final float PANEL_WIDTH = 220f;
    private static final float PANEL_HEIGHT = 96f;
    private static final float PANEL_Y_OFFSET = 22f;
    private static final float SCREEN_MARGIN = 14f;
    private static final Color INK = new Color(0.17f, 0.09f, 0.04f, 1f);
    private static final Color MUTED = new Color(0.42f, 0.29f, 0.18f, 1f);
    private static final Color WARNING = new Color(0.65f, 0.12f, 0.08f, 1f);

    private final GameWorld gameWorld;
    private final Table panel;
    private final Label titleLabel;
    private final Label buildingLabel;
    private final Label costLabel;
    private final TextButton buyButton;

    private PurchaseOption selectedOption;
    private BuildingZone selectedZone;

    public PurchasePanel(Stage stage, GameWorld gameWorld, UiAssets assets) {
        this.gameWorld = gameWorld;
        this.panel = new Table();
        this.titleLabel = new Label("", assets.getTitleLabelStyle());
        this.buildingLabel = new Label("", assets.getMutedLabelStyle());
        this.costLabel = new Label("", assets.getDefaultLabelStyle());
        this.buyButton = new TextButton("Buy", assets.getPrimaryButtonStyle());

        buildLayout(assets);
        stage.addActor(panel);
        panel.setVisible(false);

        buyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buySelectedUnit();
            }
        });
    }

    private void buildLayout(UiAssets assets) {
        panel.setBackground(assets.getPanelDrawable());
        panel.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        panel.pad(12, 16, 12, 16);
        panel.defaults().center();

        titleLabel.setAlignment(Align.center);
        buildingLabel.setAlignment(Align.center);
        costLabel.setAlignment(Align.left);
        buyButton.getLabel().setAlignment(Align.center);

        panel.add(titleLabel).center().expandX().fillX().height(22).colspan(2);
        panel.row();
        panel.add(buildingLabel).center().expandX().fillX().height(18).colspan(2).padBottom(4);
        panel.row();
        panel.add(costLabel).left().expandX().fillX().height(30).padRight(8);
        panel.add(buyButton).width(76).height(30);
    }

    public void show(BuildingZone zone) {
        selectedZone = zone;
        selectedOption = PurchaseOption.fromBuilding(zone);
        if (selectedOption == null) {
            hide();
            return;
        }
        panel.setVisible(true);
        updateTextAndState();
    }

    public void hide() {
        selectedZone = null;
        selectedOption = null;
        panel.setVisible(false);
    }

    public void update(OrthographicCamera worldCamera, float screenWidth, float screenHeight) {
        if (selectedZone == null || selectedOption == null) return;
        updateTextAndState();

        Vector2 center = selectedZone.getCenter();
        Vector3 projected = worldCamera.project(new Vector3(center.x, center.y, 0f));
        float x = MathUtils.clamp(projected.x - PANEL_WIDTH / 2f, SCREEN_MARGIN, screenWidth - PANEL_WIDTH - SCREEN_MARGIN);
        float y = MathUtils.clamp(projected.y + PANEL_Y_OFFSET, SCREEN_MARGIN, screenHeight - PANEL_HEIGHT - SCREEN_MARGIN);
        panel.setPosition(x, y);
    }

    private void updateTextAndState() {
        if (selectedOption == null) return;
        int gold = gameWorld.getEconomyManager().getGold();
        boolean canBuy = gold >= selectedOption.cost;
        titleLabel.setText("Train " + selectedOption.unitName);
        buildingLabel.setText(selectedZone == null ? "" : selectedZone.getDisplayName());
        costLabel.setText("Gold " + selectedOption.cost);
        costLabel.setColor(canBuy ? INK : WARNING);
        buildingLabel.setColor(canBuy ? MUTED : WARNING);

        // GIỮ NGUYÊN GỐC: Đặt disabled theo lượng tiền thực tế
        buyButton.setDisabled(!canBuy);
    }

    private void buySelectedUnit() {
        if (selectedOption == null || buyButton.isDisabled()) return;

        switch (selectedOption) {
            case WARRIOR:
                gameWorld.spawnWarrior();
                break;
            case ARCHER:
                gameWorld.spawnArcher();
                break;
            case HEALER:
                gameWorld.spawnHealer();
                break;
            case LANCER:
                gameWorld.spawnLancer();
                break;
            case MINER:
                gameWorld.spawnMiner();
                break;
            default:
                break;
        }

        // SỬA ĐÚNG DÒNG NÀY: Ép nút nhả trạng thái Checked dính chuột của LibGDX ra lập tức.
        // Điều này đảm bảo nút nháy nảy lên một cái mượt mà, và giữ nguyên màu xanh tươi nếu tiền vẫn đủ mua tiếp!
        buyButton.setChecked(false);

        updateTextAndState();
    }

    private enum PurchaseOption {
        WARRIOR("Warrior", EconomyManager.COST_WARRIOR),
        ARCHER("Archer", EconomyManager.COST_ARCHER),
        HEALER("Monk", EconomyManager.COST_HEALER),
        LANCER("Lancer", EconomyManager.COST_LANCER),
        MINER("Miner", EconomyManager.COST_MINER);

        private final String unitName;
        private final int cost;

        PurchaseOption(String unitName, int cost) {
            this.unitName = unitName;
            this.cost = cost;
        }

        private static PurchaseOption fromBuilding(BuildingZone zone) {
            if (zone == null) return null;
            String building = zone.getName();
            if ("barrack".equals(building)) return WARRIOR;
            if ("archery".equals(building)) return ARCHER;
            if ("monastery".equals(building)) return HEALER;
            if ("house1".equals(building)) return LANCER;
            if ("house2".equals(building)) return MINER;
            return null;
        }
    }
}*/


// Hưng
package com.hust.towerdefence.View.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.hust.towerdefence.Model.GameWorld;
import com.hust.towerdefence.Model.Managers.BuildingZone;
import com.hust.towerdefence.Model.Managers.EconomyManager;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Soldier;

/**
 * Bảng điều khiển mua lính và nâng cấp tháp/nhà công trình.
 * Đã được chuẩn hóa cấu trúc để rạch ròi giữa logic Buy và Upgrade.
 */
public class PurchasePanel {
    // --- Các hằng số cấu hình kích thước và vị trí Giao diện ---
    private static final float PANEL_WIDTH = 240f;
    private static final float PANEL_HEIGHT = 155f;
    private static final float PANEL_Y_OFFSET = 22f;
    private static final float SCREEN_MARGIN = 14f;

    // --- Bảng Màu Hệ Thống Điều Khiển UI ---
    private static final Color INK = new Color(0.17f, 0.09f, 0.04f, 1f);     // Nâu đậm (Đủ điều kiện, hiển thị rõ nét)
    private static final Color MUTED = new Color(0.42f, 0.29f, 0.18f, 1f);   // Nâu mờ (Khi đã đạt cấp tối đa hoặc thiếu tiền)
    private static final Color WARNING = new Color(0.65f, 0.12f, 0.08f, 1f); // Màu đỏ cảnh báo (Dành riêng cho khối Buy khi thiếu tiền)

    // --- Thành phần Core Logic Kết Nối Mô Hình Game ---
    private final GameWorld gameWorld;
    private final Table panel;
    private PurchaseOption selectedOption;
    private BuildingZone selectedZone;

    // --- Các Nhãn Hiển Thị Văn Bản (Labels) ---
    private final Label titleLabel;
    private final Label buildingLabel;
    private final Label hpLabel;
    private final Label statsLabel; // Nhãn dùng chung hiển thị DMG và RNG
    private final Label costLabel;
    private final Label upgradeCostLabel;

    // --- Các Nút Tương Tác (Buttons) ---
    private final TextButton buyButton;
    private final TextButton upgradeButton;

    // --- Lưu Trữ Trạng thái Cấp độ hiện tại của từng loại công trình ---
    private int warriorLevel = 1;
    private int archerLevel = 1;
    private int healerLevel = 1;
    private int lancerLevel = 1;
    private int minerLevel = 1;

    /**
     * Hàm khởi tạo PurchasePanel, thiết lập Listener tương tác và nạp UI vào Stage
     */
    public PurchasePanel(Stage stage, GameWorld gameWorld, UiAssets assets) {
        this.gameWorld = gameWorld;
        this.panel = new Table();

        // Khởi tạo các thành phần giao diện từ Assets hệ thống
        this.titleLabel = new Label("", assets.getTitleLabelStyle());
        this.buildingLabel = new Label("", assets.getMutedLabelStyle());
        this.hpLabel = new Label("", assets.getDefaultLabelStyle());
        this.statsLabel = new Label("", assets.getDefaultLabelStyle());
        this.costLabel = new Label("", assets.getDefaultLabelStyle());
        this.upgradeCostLabel = new Label("", assets.getDefaultLabelStyle());

        this.buyButton = new TextButton("Buy", assets.getPrimaryButtonStyle());

        // Tạo một Style riêng biệt hoàn toàn cho nút Upgrade bằng cách copy từ style gốc để tránh xung đột
        TextButton.TextButtonStyle upgradeStyle = new TextButton.TextButtonStyle(assets.getPrimaryButtonStyle());
        this.upgradeButton = new TextButton("Upgrade", upgradeStyle);

        // Xây dựng bố cục Layout và đưa vào Stage vẽ
        buildLayout(assets);
        stage.addActor(panel);
        panel.setVisible(false);

        // Đăng ký sự kiện nhấn nút Buy (Dùng ChangeListener truyền thống)
        buyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buySelectedUnit();
            }
        });

        // Đổi sang ClickListener để chỉ bắt chính xác sự kiện click chuột, tránh lặp loop tín hiệu khi đổi trạng thái nút
        upgradeButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                upgradeSelectedBuilding();
            }
        });
    }

    /**
     * Sắp xếp lưới Layout ô bảng cho Panel bằng Table UI Component
     */
    private void buildLayout(UiAssets assets) {
        panel.setBackground(assets.getPanelDrawable());
        panel.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        panel.pad(10, 12, 10, 12);
        panel.defaults().center();

        titleLabel.setAlignment(Align.center);
        buildingLabel.setAlignment(Align.center);
        hpLabel.setAlignment(Align.left);
        statsLabel.setAlignment(Align.left);
        costLabel.setAlignment(Align.left);
        upgradeCostLabel.setAlignment(Align.left);

        buyButton.getLabel().setAlignment(Align.center);
        upgradeButton.getLabel().setAlignment(Align.center);

        // Hàng 1 & 2: Tiêu đề lính và tên công trình
        panel.add(titleLabel).center().expandX().fillX().height(20).colspan(2);
        panel.row();
        panel.add(buildingLabel).center().expandX().fillX().height(16).colspan(2).padBottom(4);
        panel.row();

        // Hàng 3 & 4: Thông số kỹ thuật chi tiết của đơn vị lính (HP, DMG, RNG)
        panel.add(hpLabel).left().expandX().fillX().height(18).colspan(2);
        panel.row();
        panel.add(statsLabel).left().expandX().fillX().height(18).colspan(2).padBottom(6);
        panel.row();

        // Hàng 5: Giá mua và nút Mua (Buy)
        panel.add(costLabel).left().expandX().fillX().height(26).padRight(4);
        panel.add(buyButton).width(88).height(26).padBottom(4);
        panel.row();

        // Hàng 6: Giá nâng cấp và nút Nâng cấp (Upgrade)
        panel.add(upgradeCostLabel).left().expandX().fillX().height(26).padRight(4);
        panel.add(upgradeButton).width(88).height(26);
    }

    /**
     * Hiển thị bảng điều khiển khi người chơi chọn vào một ô công trình hợp lệ
     */
    public void show(BuildingZone zone) {
        selectedZone = zone;
        selectedOption = PurchaseOption.fromBuilding(zone);
        if (selectedOption == null) {
            hide();
            return;
        }
        panel.setVisible(true);
        updateTextAndState();
    }

    /**
     * Ẩn bảng điều khiển và giải phóng các mục tiêu đang chọn
     */
    public void hide() {
        selectedZone = null;
        selectedOption = null;
        panel.setVisible(false);
    }

    /**
     * Hàm cập nhật chạy liên tục mỗi khung hình (render loop) để ghim tọa độ theo camera và quét dữ liệu realtime
     */
    public void update(OrthographicCamera worldCamera, float screenWidth, float screenHeight) {
        if (selectedZone == null || selectedOption == null) return;

        // Quét và cập nhật màu sắc văn bản, bật/tắt trạng thái nền nút theo ví tiền hiện hành liên tục
        updateButtonStatesRealtime();

        // Ghim vị trí Panel UI nổi theo tọa độ thế giới của ô công trình
        Vector2 center = selectedZone.getCenter();
        Vector3 projected = worldCamera.project(new Vector3(center.x, center.y, 0f));
        float x = MathUtils.clamp(projected.x - PANEL_WIDTH / 2f, SCREEN_MARGIN, screenWidth - PANEL_WIDTH - SCREEN_MARGIN);
        float y = MathUtils.clamp(projected.y + PANEL_Y_OFFSET, SCREEN_MARGIN, screenHeight - PANEL_HEIGHT - SCREEN_MARGIN);
        panel.setPosition(x, y);
    }

    /**
     * Quét lượng tiền của người chơi realtime để điều khiển chính xác trạng thái hiển thị của các nút bấm
     */
    private void updateButtonStatesRealtime() {
        if (selectedOption == null) return;

        int gold = gameWorld.getEconomyManager().getGold();
        int currentLv = getCurrentLevelOfSelectedOption();

        // Thực hiện xử lý quét độc lập hoàn toàn cho hai khối Buy và Upgrade
        handleBuyButtonRealtime(gold);
        handleUpgradeButtonRealtime(gold, currentLv);
    }

    /**
     * Xử lý quét Realtime chuyên biệt cho khối BUY (Mua lính)
     */
    private void handleBuyButtonRealtime(int gold) {
        boolean canBuy = gold >= selectedOption.cost;

        // Đủ tiền -> chữ màu nâu đậm (INK), Thiếu tiền -> chữ cảnh báo màu đỏ (WARNING)
        costLabel.setColor(canBuy ? INK : WARNING);

        // Cập nhật trạng thái kích hoạt nút để LibGDX tự động chuyển đổi giữa nền xanh tươi và xám mờ
        if (buyButton.isDisabled() != !canBuy) {
            buyButton.setDisabled(!canBuy);
        }
    }

    /**
     * Xử lý quét Realtime chuyên biệt cho khối UPGRADE (Nâng cấp) - Chỉ có đúng 2 dạng hiển thị rõ ràng
     */
    private void handleUpgradeButtonRealtime(int gold, int currentLv) {
        if (currentLv >= 3) {
            // TRẠNG THÁI 1: Đã kịch cấp tối đa -> Ép mờ nền (disabled = true) và đổi chữ sang màu mờ (MUTED)
            upgradeButton.setDisabled(true);
            upgradeCostLabel.setColor(MUTED);
        } else {
            int upgradeCost = selectedOption.getUpgradeCostForLevel(currentLv);
            boolean canUpgrade = gold >= upgradeCost;

            if (canUpgrade) {
                // TRẠNG THÁI 2A: Chưa max + ĐỦ TIỀN -> Mở khóa nút nền xanh (disabled = false) và đặt chữ nâu đậm (INK)
                upgradeButton.setDisabled(false);
                upgradeCostLabel.setColor(INK);
            } else {
                // TRẠNG THÁI 2B: Chưa max + THIẾU TIỀN -> Khóa nút dạng nền mờ (disabled = true) và chuyển chữ về mờ (MUTED)
                upgradeButton.setDisabled(true);
                upgradeCostLabel.setColor(MUTED);
            }
        }
    }

    /**
     * Thiết lập nội dung chữ văn bản tĩnh cho các Label khi mở bảng UI hoặc ngay sau khi hoàn thành nâng cấp
     */
    private void updateTextAndState() {
        if (selectedOption == null) return;
        int currentLv = getCurrentLevelOfSelectedOption();

        // 1. Cập nhật các nội dung văn bản cơ bản
        titleLabel.setText("Train " + selectedOption.unitName + " (Lv " + currentLv + ")");
        buildingLabel.setText(selectedZone == null ? "" : selectedZone.getDisplayName());
        costLabel.setText("Gold " + selectedOption.cost);

        // 2. Nạp lại thông số chỉ số sức mạnh của lính (HP, DMG, RNG) dựa trên cấp độ
        updateUnitStatsText(currentLv);

        // 3. Xử lý chuỗi văn bản hiển thị cho giá nâng cấp công trình
        if (currentLv >= 3) {
            upgradeCostLabel.setText("Upgrade: Max");
        } else {
            int upgradeCost = selectedOption.getUpgradeCostForLevel(currentLv);
            upgradeCostLabel.setText("Upgrade: " + upgradeCost);
        }

        // 4. Đồng bộ hóa ngay trạng thái nút bấm lập tức
        updateButtonStatesRealtime();
    }

    /**
     * Tính toán và hiển thị thông số chỉ số (HP, DMG, RNG) theo từng mốc cấp độ cụ thể của lính
     */
    private void updateUnitStatsText(int level) {
        int hp = 0;
        int dmg = 0;
        int rng = 0;

        switch (selectedOption) {
            case WARRIOR:
                hp = 120 + (level - 1) * 40;
                dmg = 15 + (level - 1) * 5;
                rng = 40;
                break;
            case ARCHER:
                hp = 80 + (level - 1) * 25;
                dmg = 12 + (level - 1) * 4;
                rng = 180 + (level - 1) * 20;
                break;
            case HEALER:
                hp = 90 + (level - 1) * 30;
                dmg = 10 + (level - 1) * 3;
                rng = 120;
                break;
            case LANCER:
                hp = 110 + (level - 1) * 35;
                dmg = 18 + (level - 1) * 6;
                rng = 60;
                break;
            case MINER:
                hp = 100 + (level - 1) * 30;
                dmg = 8 + (level - 1) * 2;
                rng = 35;
                break;
        }

        hpLabel.setText("HP: " + hp);
        statsLabel.setText("DMG: " + dmg + "      RNG: " + rng);
    }

    /**
     * Lấy ra cấp độ hiện tại của loại công trình/lính đang được chọn
     */
    private int getCurrentLevelOfSelectedOption() {
        if (selectedOption == null) return 1;
        switch (selectedOption) {
            case WARRIOR: return warriorLevel;
            case ARCHER:  return archerLevel;
            case HEALER:  return healerLevel;
            case LANCER:  return lancerLevel;
            case MINER:   return minerLevel;
            default:      return 1;
        }
    }

    /**
     * Tăng cấp độ công trình lên 1 bậc (Tối đa là cấp 3)
     */
    private void incrementLevelOfSelectedOption() {
        if (selectedOption == null) return;
        switch (selectedOption) {
            case WARRIOR: if (warriorLevel < 3) warriorLevel++; break;
            case ARCHER:  if (archerLevel < 3) archerLevel++; break;
            case HEALER:  if (healerLevel < 3) healerLevel++; break;
            case LANCER:  if (lancerLevel < 3) lancerLevel++; break;
            case MINER:   if (minerLevel < 3) minerLevel++; break;
        }
    }

    /**
     * Thực thi Logic gọi Spawn mua một đơn vị lính mới và gán cấp độ tương ứng
     */
    private void buySelectedUnit() {
        if (selectedOption == null || buyButton.isDisabled()) return;

        switch (selectedOption) {
            case WARRIOR: gameWorld.spawnWarrior(); break;
            case ARCHER:  gameWorld.spawnArcher(); break;
            case HEALER:  gameWorld.spawnHealer(); break;
            case LANCER:  gameWorld.spawnLancer(); break;
            case MINER:   gameWorld.spawnMiner(); break;
            default: break;
        }

        // Đồng bộ cấp độ cho lính vừa sinh ra nếu công trình đã được nâng cấp trước đó
        int currentLv = getCurrentLevelOfSelectedOption();
        if (currentLv > 1 && !gameWorld.getEntityManager().getSoldiers().isEmpty()) {
            Soldier newest = gameWorld.getEntityManager().getSoldiers().peek();
            if (newest != null) {
                newest.setLevel(currentLv);
            }
        }

        // Nhả trạng thái dính chuột của LibGDX ngay lập tức để tạo hiệu ứng nhấp nháy phản hồi mượt mà
        buyButton.setChecked(false);
        updateTextAndState();
    }

    /**
     * Thực thi Logic trừ vàng nâng cấp công trình và đồng bộ cấp độ cho toàn bộ lính hiện có trên bản đồ
     */
    private void upgradeSelectedBuilding() {
        // Chặn tương tác ngay từ đầu nếu nút đang hiển thị mờ dạng vô hiệu hóa (Khi thiếu tiền hoặc kịch cấp)
        if (selectedOption == null || upgradeButton.isDisabled()) return;

        int currentLv = getCurrentLevelOfSelectedOption();
        if (currentLv >= 3) return;

        int cost = selectedOption.getUpgradeCostForLevel(currentLv);
        int gold = gameWorld.getEconomyManager().getGold();

        // Kiểm tra điều kiện ví tiền hợp lệ trước khi thực hiện giao dịch trừ vàng nâng cấp
        if (gold >= cost && cost > 0) {
            gameWorld.getEconomyManager().spendGold(cost);

            incrementLevelOfSelectedOption();
            int newLevel = getCurrentLevelOfSelectedOption();

            // Quét danh sách thực thể để nâng cấp chỉ số đồng loạt cho toàn bộ lính cùng loại đang chiến đấu
            String targetOptionName = selectedOption.name();
            for (Soldier s : gameWorld.getEntityManager().getSoldiers()) {
                if (s != null && s.getClass().getSimpleName().toUpperCase().contains(targetOptionName)) {
                    s.setLevel(newLevel);
                } else if (selectedOption == PurchaseOption.HEALER && s != null && "Healer".equals(s.getClass().getSimpleName())) {
                    s.setLevel(newLevel);
                }
            }

            // CHỈ CẬP NHẬT CHỮ VĂN BẢN TẠI ĐÂY (Để hàm update vòng lặp sau tự quét ví tiền và quyết định màu nền nút)
            titleLabel.setText("Train " + selectedOption.unitName + " (Lv " + newLevel + ")");
            updateUnitStatsText(newLevel);
            if (newLevel >= 3) {
                upgradeCostLabel.setText("Upgrade: Max");
            } else {
                int nextUpgradeCost = selectedOption.getUpgradeCostForLevel(newLevel);
                upgradeCostLabel.setText("Upgrade: " + nextUpgradeCost);
            }
        }

        // Ép nút nhả trạng thái Checked dính chuột của LibGDX ngay lập tức để tạo hiệu ứng nhấn nháy nảy lên chuẩn chỉ
        upgradeButton.setChecked(false);
    }

    /**
     * Bộ dữ liệu cấu hình thông tin định danh, giá mua lính và mảng chi phí nâng cấp của từng loại Tháp
     */
    private enum PurchaseOption {
        WARRIOR("Warrior", EconomyManager.COST_WARRIOR, new int[]{120, 300, 0}),
        ARCHER("Archer", EconomyManager.COST_ARCHER, new int[]{60, 180, 0}),
        HEALER("Monk", EconomyManager.COST_HEALER, new int[]{100, 250, 0}),
        LANCER("Lancer", EconomyManager.COST_LANCER, new int[]{70, 220, 0}),
        MINER("Miner", EconomyManager.COST_MINER, new int[]{80, 200, 0});

        private final String unitName;
        private final int cost;
        private final int[] upgradeCosts;

        PurchaseOption(String unitName, int cost, int[] upgradeCosts) {
            this.unitName = unitName;
            this.cost = cost;
            this.upgradeCosts = upgradeCosts;
        }

        /**
         * Trả về chi phí nâng cấp dựa trên mức cấp độ hiện hành của công trình (Chỉ số mảng bắt đầu từ 0)
         */
        public int getUpgradeCostForLevel(int currentLevel) {
            if (currentLevel >= 1 && currentLevel <= upgradeCosts.length) {
                return upgradeCosts[currentLevel - 1];
            }
            return 0;
        }

        /**
         * Ánh xạ định danh chuỗi tên vùng công trình từ TiledMap/Logic sang đối tượng enum tương ứng
         */
        private static PurchaseOption fromBuilding(BuildingZone zone) {
            if (zone == null) return null;
            String building = zone.getName();
            if ("barrack".equals(building)) return WARRIOR;
            if ("archery".equals(building)) return ARCHER;
            if ("monastery".equals(building)) return HEALER;
            if ("house1".equals(building)) return LANCER;
            if ("house2".equals(building)) return MINER;
            return null;
        }
    }
}
