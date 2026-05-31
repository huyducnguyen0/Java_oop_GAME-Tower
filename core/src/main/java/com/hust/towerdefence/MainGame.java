package com.hust.towerdefence;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.hust.towerdefence.View.screens.DemoModelScreen;
import com.hust.towerdefence.View.screens.MainMenuScreen;
import com.hust.towerdefence.View.ui.AudioManager;
import com.hust.towerdefence.View.ui.UiAssets;
import java.util.HashMap;
import java.util.Map;

/**
 * Lớp cấu hình và quản lý vòng đời chính của Game.
 * Kế thừa lớp Game của LibGDX để hỗ trợ cơ chế chuyển đổi đa màn hình (Screen).
 */
public class MainGame extends Game {
    // ==========================================
    // CẤU HÌNH THÔNG SỐ THẾ GIỚI (WORLD SETTINGS)
    // ==========================================
    public static final float WORLD_HEIGHT = 9f;       // Chiều cao thế giới ảo (đơn vị mét/vùng nhìn)
    public static final float WORLD_WIDTH = 16f;       // Chiều rộng thế giới ảo (tỷ lệ chuẩn 16:9)
    public static final float UNIT_SCALE = 1f / 16f;   // Tỷ lệ chuyển đổi đơn vị (Scale pixel lên đơn vị thế giới)

    // ==========================================
    // THÀNH PHẦN ĐỒ HỌA & INPUT KHỞI TẠO TẬP TRUNG
    // ==========================================
    private Batch batch;                               // Đối tượng vẽ tích hợp (SpriteBatch) dùng chung toàn game
    private InputMultiplexer inputMultiplexer;         // Bộ quản lý sự kiện đầu vào mặc định ban đầu

    // Cache lưu trữ các Screen để tránh việc khởi tạo lại gây tốn bộ nhớ và mất trạng thái game
    private final Map<Class<? extends Screen>, Screen> screenCache = new HashMap<>();

    // ==========================================
    // TÀI NGUYÊN & ÂM THANH (QUẢN LÝ BỞI HƯNG)
    // ==========================================
    public UiAssets assets;                            // Quản lý nạp/giải phóng hình ảnh, giao diện UI
    public AudioManager audioManager;                  // Hệ thống phát/tắt nhạc nền và hiệu ứng âm thanh

    @Override
    public void create() {
        // Cấu hình chế độ Log để debug hệ thống thuận tiện
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        // Khởi tạo bộ gom Input mặc định ban đầu cho hệ thống
        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);

        // Khởi tạo luồng vẽ SpriteBatch dùng chung
        batch = new SpriteBatch();

        // [Hưng] Khởi tạo tài nguyên UI và hệ thống quản lý âm thanh
        assets = new UiAssets();
        audioManager = new AudioManager(assets);

        // [Hưng] Kích hoạt nhạc nền của màn hình Menu chính ngay khi vào game
        audioManager.playMenuMusic();

        // Đăng ký trước toàn bộ các màn hình vào hệ thống Cache để sẵn sàng chuyển đổi
        addScreen(new DemoModelScreen(this));          // Màn hình gameplay thực nghiệm
        addScreen(new MainMenuScreen(this));           // Màn hình Menu chính của trò chơi

        // Thiết lập màn hình đầu tiên xuất hiện khi chạy Game là MainMenuScreen
        setScreen(MainMenuScreen.class);
    }

    // ==========================================
    // CƠ CHẾ QUẢN LÝ CACHE CÁC MÀN HÌNH (SCREENS)
    // ==========================================

    /**
     * Đăng ký một màn hình mới vào bộ nhớ Cache.
     * @param screen Đối tượng Screen cụ thể cần lưu trữ.
     */
    public void addScreen(Screen screen) {
        screenCache.put(screen.getClass(), screen);
    }

    /**
     * Chuyển đổi màn hình hiển thị dựa trên Class định danh.
     * Tự động trích xuất thực thể sẵn có từ bộ nhớ Cache.
     * @param screenClass Tên lớp của Screen cần chuyển đến (Ví dụ: MainMenuScreen.class)
     */
    public void setScreen(Class<? extends Screen> screenClass) {
        Screen screen = screenCache.get(screenClass);
        if (screen == null) {
            throw new GdxRuntimeException("Màn hình " + screenClass.getSimpleName() + " chưa được đăng ký trong Cache!");
        }
        // Gọi hàm cấu trúc của LibGDX để thực hiện chuyển Screen an toàn
        super.setScreen(screen);
    }

    /**
     * Loại bỏ một Screen ra khỏi bộ nhớ Cache khi không còn nhu cầu sử dụng.
     */
    public void removeScreen(Screen screen) {
        screenCache.remove(screen.getClass());
    }

    // ==========================================
    // VÒNG ĐỜI LÀM MỚI VÀ GIẢI PHÓNG (LIFECYCLE)
    // ==========================================

    @Override
    public void render() {
        // Dọn dẹp bộ đệm màn hình bằng màu nền đen trước khi vẽ khung hình mới
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Chuyển quyền cập nhật khung hình xuống Screen hiện tại đang kích hoạt
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        // Đồng bộ lại kích thước hiển thị khi cửa sổ game bị co giãn
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        // Duyệt toàn bộ danh sách Screen trong Cache để giải phóng bộ nhớ RAM/VRAM của từng màn hình
        for (Screen screen : screenCache.values()) {
            screen.dispose();
        }
        screenCache.clear();

        // Giải phóng luồng vẽ chung
        batch.dispose();

        // [Hưng] Giải phóng toàn bộ AssetManager, Atlas hình ảnh giao diện
        if (assets != null) {
            assets.dispose();
        }
    }

    // ==========================================
    // BỘ HÀM TIỆN ÍCH TRÍCH XUẤT CÔNG CỤ (GETTERS)
    // ==========================================

    /**
     * Trả về luồng vẽ Batch chung để các Screen con sử dụng chung một SpriteBatch, tối ưu hiệu năng.
     */
    public Batch getBatch() {
        return batch;
    }

    // ==========================================
    // QUẢN LÝ ĐA TẦNG SỰ KIỆN CHUỘT/BÀN PHÍM (INPUT)
    // ==========================================

    /**
     * Thiết lập và phân tầng lại độ ưu tiên nhận chuột/bàn phím cho hệ thống.
     * Giải quyết triệt để lỗi xung đột dính phím/liệt chuột khi chuyển giao giữa Menu và Game.
     * @param processors Mảng danh sách các bộ xử lý Input theo thứ tự ưu tiên (Thằng đứng trước nhận trước).
     */
    public void setInputProcessors(com.badlogic.gdx.InputProcessor... processors) {
        InputMultiplexer multiplexer = new InputMultiplexer();
        for (com.badlogic.gdx.InputProcessor p : processors) {
            if (p != null) {
                multiplexer.addProcessor(p);
            }
        }
        // Ghi đè bộ xử lý tổ hợp mới nhất lên toàn bộ hệ thống Input của LibGDX
        Gdx.input.setInputProcessor(multiplexer);
        System.out.println("MainGame: Đã nạp bộ Multiplexer mới, xóa bỏ hoàn toàn bộ cũ của Menu.");
    }
}
