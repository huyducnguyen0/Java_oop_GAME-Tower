package com.hust.towerdefence.View.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hust.towerdefence.MainGame;

/**
 * Màn hình Menu chính của trò chơi (Main Menu Screen).
 * Đã nâng cấp: Tách phân nhánh Wiki ALLY/ENEMY sử dụng asset nút bấm 3Slides nguyên bản,
 * Tối ưu hóa giải phóng bộ nhớ Texture, chuẩn hóa tên tiếng Anh - mô tả tiếng Việt.
 */
public class MainMenuScreen implements Screen {
    private final MainGame game;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;

    private static final int NUM_FRAMES = 51;
    private final Texture[] bgTextures;
    private final Animation<TextureRegion> bgAnimation;
    private float stateTime = 0f;

    private final BitmapFont buttonFont;
    private final BitmapFont textFont;
    private final Texture overlayTex;
    private final Texture scrollKnobTex;

    // Asset nút bấm phân nhánh dùng cơ chế NinePatch bảo toàn góc viền
    // === CÁC BIẾN TEXTURE ĐỂ LÀM HIỆU ỨNG NÚT BẤM ===
    private final Texture btnBlueTex;
    private final Texture btnBluePressedTex;
    private final Texture btnRedTex;
    private final Texture btnRedPressedTex;
    private final Texture btnHoverTex;

    private final Stage stage;
    private final Table mainTable;
    private final Table levelTable;
    private final Table wikiSelectTable; // Bảng trung gian chọn Phe
    private final Table wikiAllyTable;   // Bảng chi tiết quân ta
    private final Table wikiEnemyTable;  // Bảng chi tiết quân địch
    private final Table tutorialTable;
    private final Table aboutTable;

    private ScrollPane aboutScrollPane;
    private ScrollPane allyScrollPane;
    private ScrollPane enemyScrollPane;
    private ScrollPane tutorialScrollPane;

    // Quản lý Sprite Sheets của toàn bộ hệ thống binh chủng
    private Texture minerSheet, warriorSheet, archerSheet, lancerSheet, monkSheet;
    private Texture enemyWarriorSheet, tntSheet, torchSheet;

    public MainMenuScreen(MainGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1600, 900);
        batch = new SpriteBatch();

        if (game.audioManager != null) {
            game.audioManager.playMenuMusic();
        }

        // Khởi tạo và nạp động mảng khung hình nền (Background Animation)
        bgTextures = new Texture[NUM_FRAMES];
        TextureRegion[] animFrames = new TextureRegion[NUM_FRAMES];
        for (int i = 0; i < NUM_FRAMES; i++) {
            String fileName = String.format("bg_frames/ezgif-frame-%03d.jpg", i + 1);
            bgTextures[i] = new Texture(Gdx.files.internal(fileName));
            animFrames[i] = new TextureRegion(bgTextures[i]);
        }
        bgAnimation = new Animation<>(1f / 12f, animFrames);

        // ========================================================
        // TÍCH HỢP FONT TIẾNG VIỆT ROBOTO
        // ========================================================
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Regular.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        String vietnameseChars = "aAàÀảẢãÃáÁạẠăĂằẰẳẲẵẴắẮặẶâÂầẦẩẨẫẪấẤậẬbBcCdDđĐeEèÈẻẺẽẼéÉẹẸêÊềỀểỂễỄếẾệỆfFgGhHiIìÌỉỈĩĨíÍịỊjJkKlLmMnNoOòÒỏỎõÕóÓọỌôÔồỒổỔỗỖốỐộỘơƠờỜởỞỡỠớỚợỢpPqQrRsStTuUùÙủỦũŨúÚụỤưƯừỪửỬữỮứỨựỰvVwWxXyYỳỲỷỶỹỸýÝỵỴzZ0123456789!@#$%^&*()_+-=[]{}|;':,./<>?\"\\";
        parameter.characters = vietnameseChars;

        parameter.size = 45;
        buttonFont = generator.generateFont(parameter);

        parameter.size = 28;
        textFont = generator.generateFont(parameter);
        generator.dispose();

        stage = new Stage(new FitViewport(1600, 900));

        // Tạo màn mờ làm nền cho các bảng Box giao diện
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0f, 0f, 0f, 0.35f));
        pixmap.fill();
        overlayTex = new Texture(pixmap);
        pixmap.dispose();
        TextureRegionDrawable darkBox = new TextureRegionDrawable(new TextureRegion(overlayTex));

        // Khởi tạo thanh cuộn cho thanh kéo ScrollPane
        Pixmap scrollPixmap = new Pixmap(6, 1, Pixmap.Format.RGBA8888);
        scrollPixmap.setColor(new Color(1f, 1f, 1f, 0.4f));
        scrollPixmap.fill();
        scrollKnobTex = new Texture(scrollPixmap);
        scrollPixmap.dispose();
        TextureRegionDrawable scrollKnobDrawable = new TextureRegionDrawable(new TextureRegion(scrollKnobTex));

        // Định dạng text style cơ bản
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = buttonFont;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.overFontColor = Color.GOLD;
        btnStyle.downFontColor = Color.GRAY;

        Label.LabelStyle textStyle = new Label.LabelStyle(textFont, Color.WHITE);
        Label.LabelStyle subTitleStyle = new Label.LabelStyle(buttonFont, Color.GOLD);

        // Khởi tạo các Texture trạng thái nút bấm (đảm bảo đường dẫn chính xác)
        btnBlueTex = new Texture(Gdx.files.internal("UI/Buttons/Button_Blue_3Slides.png"));
        btnBluePressedTex = new Texture(Gdx.files.internal("UI/Buttons/Button_Blue_3Slides_Pressed.png"));
        btnRedTex = new Texture(Gdx.files.internal("UI/Buttons/Button_Red_3Slides.png"));
        btnRedPressedTex = new Texture(Gdx.files.internal("UI/Buttons/Button_Red_3Slides_Pressed.png"));
        btnHoverTex = new Texture(Gdx.files.internal("UI/Buttons/Button_Hover_3Slides.png"));

        // Cắt ghép góc bo 15px bằng NinePatch để không móp méo
        NinePatch bluePatch = new NinePatch(btnBlueTex, 15, 15, 0, 0);
        NinePatch bluePressedPatch = new NinePatch(btnBluePressedTex, 15, 15, 0, 0);
        NinePatch redPatch = new NinePatch(btnRedTex, 15, 15, 0, 0);
        NinePatch redPressedPatch = new NinePatch(btnRedPressedTex, 15, 15, 0, 0);
        NinePatch hoverPatch = new NinePatch(btnHoverTex, 15, 15, 0, 0);

        // Style đầy đủ trạng thái cho ALLY
        TextButton.TextButtonStyle allyBtnStyle = new TextButton.TextButtonStyle();
        allyBtnStyle.font = buttonFont;
        allyBtnStyle.up = new NinePatchDrawable(bluePatch);
        allyBtnStyle.checked = new NinePatchDrawable(bluePatch);
        allyBtnStyle.over = new NinePatchDrawable(hoverPatch); // Di chuột vào đổi viền vàng sáng
        allyBtnStyle.down = new NinePatchDrawable(bluePressedPatch); // Bấm vào đổi texture lún xuống
        allyBtnStyle.fontColor = Color.WHITE;
        allyBtnStyle.overFontColor = Color.GOLD;

        // Style đầy đủ trạng thái cho ENEMY
        TextButton.TextButtonStyle enemyBtnStyle = new TextButton.TextButtonStyle();
        enemyBtnStyle.font = buttonFont;
        enemyBtnStyle.up = new NinePatchDrawable(redPatch);
        enemyBtnStyle.checked = new NinePatchDrawable(redPatch);
        enemyBtnStyle.over = new NinePatchDrawable(hoverPatch);
        enemyBtnStyle.down = new NinePatchDrawable(redPressedPatch);
        enemyBtnStyle.fontColor = Color.WHITE;
        enemyBtnStyle.overFontColor = Color.GOLD;

        // ========================================================
        // GIAO DIỆN CHỨC NĂNG 1: MENU CHÍNH
        // ========================================================
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.padTop(120f);

        Table box1 = new Table();
        box1.setBackground(darkBox);
        box1.pad(30f, 80f, 30f, 80f);

        TextButton playBtn = createAnimatedButton("PLAY GAME", btnStyle);
        TextButton aboutBtn = createAnimatedButton("ABOUT GAME", btnStyle);
        TextButton wikiBtn = createAnimatedButton("UNIT INFO", btnStyle);
        TextButton tutorialBtn = createAnimatedButton("TUTORIAL", btnStyle);
        TextButton quitBtn = createAnimatedButton("QUIT GAME", btnStyle);

        box1.add(playBtn).padBottom(18f).row();
        box1.add(aboutBtn).padBottom(18f).row();
        box1.add(wikiBtn).padBottom(18f).row();
        box1.add(tutorialBtn).padBottom(18f).row();
        box1.add(quitBtn);
        mainTable.add(box1);

        // ========================================================
        // GIAO DIỆN CHỨC NĂNG 2: CHỌN MÀN CHƠI
        // ========================================================
        levelTable = new Table();
        levelTable.setFillParent(true);
        levelTable.setVisible(false);
        levelTable.padTop(120f);

        Table box2 = new Table();
        box2.setBackground(darkBox);
        box2.pad(40f, 80f, 40f, 80f);

        Label levelTitle = new Label("- SELECT MAP -", subTitleStyle);
        TextButton lv1Btn = createAnimatedButton("MAP 1: GRASSLAND", btnStyle);
        TextButton lv2Btn = createAnimatedButton("MAP 2: Midnight Valley", btnStyle);
        TextButton backFromLvBtn = createAnimatedButton("BACK TO MENU", btnStyle);

        box2.add(levelTitle).padBottom(40f).row();
        box2.add(lv1Btn).padBottom(25f).row();
        box2.add(lv2Btn).padBottom(40f).row();
        box2.add(backFromLvBtn);
        levelTable.add(box2);

        // ========================================================
        // GIAO DIỆN CHỨC NĂNG 3: GIỚI THIỆU
        // ========================================================
        aboutTable = new Table();
        aboutTable.setFillParent(true);
        aboutTable.setVisible(false);
        aboutTable.padTop(180f);

        Table boxAbout = new Table();
        boxAbout.setBackground(darkBox);
        boxAbout.pad(25f, 60f, 25f, 60f);
        boxAbout.add(new Label("- ABOUT TOWER DEFENSE -", subTitleStyle)).padBottom(25f).center().row();

        Table scrollContent = new Table().left().top();
        String introText = "Chào mừng thầy cô và các bạn đã đến với Thế Giới Chiến Thuật của chúng mình!\n\n"
            + "Đây là dự án game thủ thành chiến lược được nghiên cứu và phát triển bởi nhóm học sinh chúng mình tại SOICT HUST. "
            + "Trò chơi đưa bạn vào vai một vị chỉ huy tối cao với nhiệm vụ bảo vệ nhà chính trước các đợt càn quét quỷ quyệt.\n\n"
            + "Điểm độc đáo của game nằm ở cơ chế quản lý tài nguyên và điều binh khiển tướng thời gian thực. "
            + "Kinh tế của bạn phụ thuộc hoàn toàn vào các Nhà Thợ Mỏ (Miner House) - nơi sản sinh ra những phu vàng chăm chỉ. "
            + "Từ nguồn tài nguyên quý giá này, bạn phải tính toán để đầu tư vào các Nhà Lính (Barracks), tuyển chọn "
            + "các binh chủng như Hiệp sĩ, Xạ thủ, Giáo binh hay Tu sĩ. Trận đấu đòi hỏi tư duy phân phối kinh tế đỉnh cao, "
            + "sự nhanh nhạy trong việc điều phối lính chặn đường và khả năng bày binh bố trận vô cùng chiến thuật.\n\n"
            + "Phía bên kia chiến tuyến, bạn không hề đối đầu với những cỗ máy vô hồn. Kẻ địch sở hữu một hệ thống AI sinh lính "
            + "cực kỳ thông minh và linh hoạt. Hệ thống này sẽ tự động khảo sát tuyến đường di chuyển, phân tích lượng vàng, "
            + "và liên tục tính toán để sinh ra các chủng loại quái vật theo từng đợt (waves) với độ khó tăng dần, "
            + "thách thức mọi giới hạn phòng thủ của người chơi.\n\n"
            + "Về mặt kỹ thuật, sản phẩm được xây dựng trên nền tảng LibGDX framework, vận dụng triệt để tư duy lập trình "
            + "hướng đối tượng (OOP) thông qua các mô hình quản lý thực thể nâng cao (Entity Component System), tối ưu hóa "
            + "bộ nhớ và xử lý va chạm mượt mà.\n\n"
            + "Nhóm chúng mình xin chân thành cảm ơn các thầy cô giáo đã hướng dẫn, cùng toàn thể các bạn đã trải nghiệm "
            + "và ủng hộ sản phẩm đồ án này. Chúc mọi người có những giờ phút giải trí và đấu trí thật bùng nổ!";

        Label longTextLabel = new Label(introText, textStyle);
        longTextLabel.setWrap(true);
        scrollContent.add(longTextLabel).width(1050f).left().row();

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = scrollKnobDrawable;

        aboutScrollPane = new ScrollPane(scrollContent, scrollStyle);
        aboutScrollPane.setScrollingDisabled(true, false);
        aboutScrollPane.setFadeScrollBars(false);
        aboutScrollPane.setFlickScroll(true);

        boxAbout.add(aboutScrollPane).width(1120f).height(340f).padBottom(20f).row();
        TextButton backFromAboutBtn = createAnimatedButton("BACK", btnStyle);
        boxAbout.add(backFromAboutBtn).center();
        aboutTable.add(boxAbout);

        // ========================================================
        // GIAO DIỆN CHỨC NĂNG 4: BẢNG TRUNG GIAN CHỌN PHE (WIKI SELECTION)
        // ========================================================
        wikiSelectTable = new Table();
        wikiSelectTable.setFillParent(true);
        wikiSelectTable.setVisible(false);
        wikiSelectTable.padTop(120f);

        Table boxSelect = new Table();
        boxSelect.setBackground(darkBox);
        boxSelect.pad(50f, 100f, 50f, 100f);
        boxSelect.add(new Label("- BATTLE MANUAL -", subTitleStyle)).padBottom(45f).center().row();

        // 2 Nút phân nhánh áp dụng Asset chính xác từ bộ Blue/Red_3Slides
        // Đổi tham số truyền vào từ btnStyle thành allyBtnStyle và enemyBtnStyle tương ứng
        TextButton allyBranchBtn = new TextButton("ALLY", allyBtnStyle);
        TextButton enemyBranchBtn = new TextButton("ENEMY", enemyBtnStyle);
        TextButton backFromSelectBtn = createAnimatedButton("BACK TO MENU", btnStyle);

        // Kéo giãn chiều rộng 340px, chiều cao 75px cho cân xứng form nút bấm
        boxSelect.add(allyBranchBtn).width(340f).height(75f).padBottom(25f).row();
        boxSelect.add(enemyBranchBtn).width(340f).height(75f).padBottom(40f).row();
        boxSelect.add(backFromSelectBtn).center();
        wikiSelectTable.add(boxSelect);

        // ========================================================
        // KHỞI TẠO VÀ PHÂN TÁCH SPRITE SHEET CHO ICON WIKI
        // ========================================================
        minerSheet = new Texture(Gdx.files.internal("Units/Pawn/Pawn_Idle Gold.png"));
        warriorSheet = new Texture(Gdx.files.internal("Units/Warrior/Warrior_Idle.png"));
        archerSheet = new Texture(Gdx.files.internal("Units/Archer/Archer_Idle.png"));
        lancerSheet = new Texture(Gdx.files.internal("Units/Lancer/Lancer_Idle.png"));
        monkSheet = new Texture(Gdx.files.internal("Units/Monk/Idle.png"));

        // Nạp thêm Asset quân địch phục vụ giao diện hiển thị Enemy Wiki
        enemyWarriorSheet = new Texture(Gdx.files.internal("EnemyUnits/Warrior/Warrior_Idle.png"));
        tntSheet = new Texture(Gdx.files.internal("EnemyUnits/TNT/Red/TNT_Red.png"));
        torchSheet = new Texture(Gdx.files.internal("EnemyUnits/Torch/Red/Torch_Red.png"));

        float descWidth = 630f;
        float rowGap = 50f;
        float imgSize = 200f; // Scale lại kích thước icon vừa vặn form trượt

        // ========================================================
        // CHỨC NĂNG 4A: WIKI ĐỒNG MINH (ALLY FACTION)
        // ========================================================
        wikiAllyTable = new Table();
        wikiAllyTable.setFillParent(true);
        wikiAllyTable.setVisible(false);
        wikiAllyTable.padTop(230f);

        Table boxAlly = new Table();
        boxAlly.setBackground(darkBox);
        boxAlly.pad(25f, 45f, 25f, 45f);
        boxAlly.add(new Label("- ALLY INFORMATION -", subTitleStyle)).padBottom(20f).center().row();

        Table allyScrollContent = new Table().left().top();

        // 1. Miner
        com.badlogic.gdx.scenes.scene2d.ui.Image minerImg = new com.badlogic.gdx.scenes.scene2d.ui.Image(TextureRegion.split(minerSheet, 192, 192)[0][0]);
        Table mTable = new Table().left();
        mTable.add(new Label("MINER (Thợ Mỏ)", subTitleStyle)).left().padBottom(10f).row();
        Label lblMDesc = new Label("Đơn vị hậu cần cốt lõi của đội hình. Chuyên trách khai thác và đào vàng trên bản đồ, cung cấp nguồn tài nguyên dồi dào để tối ưu hóa kinh tế, giúp bạn dễ dàng đẩy nhanh tiến độ mua lính và xây dựng phòng tuyến.", textStyle);
        lblMDesc.setWrap(true);
        mTable.add(lblMDesc).width(descWidth).left();
        allyScrollContent.add(minerImg).size(imgSize, imgSize).padRight(45f).padBottom(rowGap).left();
        allyScrollContent.add(mTable).expandX().fillX().padBottom(rowGap).row();

        // 2. Knight
        com.badlogic.gdx.scenes.scene2d.ui.Image warriorImg = new com.badlogic.gdx.scenes.scene2d.ui.Image(TextureRegion.split(warriorSheet, 192, 192)[0][0]);
        Table wTable = new Table().left();
        wTable.add(new Label("KNIGHT (Hiệp Sĩ)", subTitleStyle)).left().padBottom(10f).row();
        Label lblWDesc = new Label("Lực lượng cận chiến tiên phong dũng mãnh. Chuyên làm khắc tinh chặn các nút thắt giao tranh nhờ sở hữu lượng máu (HP) cực kỳ trâu bò, mặc dù sát thương (ATK) gây ra có phần yếu hơn.", textStyle);
        lblWDesc.setWrap(true);
        wTable.add(lblWDesc).width(descWidth).left();
        allyScrollContent.add(warriorImg).size(imgSize, imgSize).padRight(45f).padBottom(rowGap).left();
        allyScrollContent.add(wTable).expandX().fillX().padBottom(rowGap).row();

        // 3. Archer
        com.badlogic.gdx.scenes.scene2d.ui.Image archerImg = new com.badlogic.gdx.scenes.scene2d.ui.Image(TextureRegion.split(archerSheet, 192, 192)[0][0]);
        Table aTable = new Table().left();
        aTable.add(new Label("ARCHER (Xạ Thủ)", subTitleStyle)).left().padBottom(10f).row();
        Label lblADesc = new Label("Đơn vị xạ thủ với tầm bắn cực xa và tốc độ bắn ổn định. Sở hữu lượng sát thương (DMG) và lượng máu (HP) ở mức trung bình, thích hợp đặt trên cao để bắn tỉa quái vật bay hoặc kẻ địch tốc độ cao.", textStyle);
        lblADesc.setWrap(true);
        aTable.add(lblADesc).width(descWidth).left();
        allyScrollContent.add(archerImg).size(imgSize, imgSize).padRight(45f).padBottom(rowGap).left();
        allyScrollContent.add(aTable).expandX().fillX().padBottom(rowGap).row();

        // 4. Lancer
        com.badlogic.gdx.scenes.scene2d.ui.Image lancerImg = new com.badlogic.gdx.scenes.scene2d.ui.Image(TextureRegion.split(lancerSheet, 320, 320)[0][0]);
        Table lTable = new Table().left();
        lTable.add(new Label("LANCER (Giáo Binh)", subTitleStyle)).left().padBottom(10f).row();
        Label lblLDesc = new Label("Chiến binh thiết thương với đòn đâm xuyên thấu diện rộng. Sở hữu lượng sát thương (DMG) cực kỳ to để càn quét quái vật đi theo cụm, tuy nhiên lượng máu (HP) lại không được trâu bò như Hiệp Sĩ.", textStyle);
        lblLDesc.setWrap(true);
        lTable.add(lblLDesc).width(descWidth).left();
        allyScrollContent.add(lancerImg).size(imgSize, imgSize).padRight(45f).padBottom(rowGap).left();
        allyScrollContent.add(lTable).expandX().fillX().padBottom(rowGap).row();

        // 5. Monk
        com.badlogic.gdx.scenes.scene2d.ui.Image monkImg = new com.badlogic.gdx.scenes.scene2d.ui.Image(TextureRegion.split(monkSheet, 192, 192)[0][0]);
        Table moTable = new Table().left();
        moTable.add(new Label("MONK (Tu Sĩ)", subTitleStyle)).left().padBottom(10f).row();
        Label lblMoDesc = new Label("Thầy pháp hỗ trợ và phục hồi tinh thần. Có khả năng triển khai các vòng ma pháp hào quang chữa lành (Heal) để liên tục hồi phục trạng thái, duy trì sự sống cho quân ta tại các điểm nóng giao tranh.", textStyle);
        lblMoDesc.setWrap(true);
        moTable.add(lblMoDesc).width(descWidth).left();
        allyScrollContent.add(monkImg).size(imgSize, imgSize).padRight(45f).padBottom(25f).left();
        allyScrollContent.add(moTable).expandX().fillX().padBottom(25f).row();

        allyScrollPane = new ScrollPane(allyScrollContent, scrollStyle);
        allyScrollPane.setScrollingDisabled(true, false);
        allyScrollPane.setFadeScrollBars(false);
        allyScrollPane.setFlickScroll(true);

        boxAlly.add(allyScrollPane).width(960f).height(410f).padBottom(15f).row();
        TextButton backFromAllyBtn = createAnimatedButton("BACK", btnStyle);
        boxAlly.add(backFromAllyBtn).center();
        wikiAllyTable.add(boxAlly);

        // ========================================================
        // CHỨC NĂNG 4B: WIKI KẺ ĐỊCH (ENEMY FACTION)
        // ========================================================
        wikiEnemyTable = new Table();
        wikiEnemyTable.setFillParent(true);
        wikiEnemyTable.setVisible(false);
        wikiEnemyTable.padTop(230f);

        Table boxEnemy = new Table();
        boxEnemy.setBackground(darkBox);
        boxEnemy.pad(25f, 45f, 25f, 45f);
        boxEnemy.add(new Label("- ENEMY INFORMATION -", subTitleStyle)).padBottom(20f).center().row();

        Table enemyScrollContent = new Table().left().top();

        // 1. Enemy Warrior
        com.badlogic.gdx.scenes.scene2d.ui.Image eWarriorImg = new com.badlogic.gdx.scenes.scene2d.ui.Image(TextureRegion.split(enemyWarriorSheet, 192, 192)[0][0]);
        Table ewTable = new Table().left();
        ewTable.add(new Label("WARRIOR (Chiến Binh Địch)", subTitleStyle)).left().padBottom(10f).row();
        Label lblEWDesc = new Label("Quân tiên phong hung hãn của phe hắc hóa. Chỉ số phòng thủ cao, giáp dày và di chuyển chậm, đóng vai trò gánh chịu sát thương chính để đập tan hệ thống phòng ngự của quân ta.\n\n💡 Mẹo: Nên dùng Lancer của ta để chặn đánh, tận dụng ATK cao để phá giáp!", textStyle);
        lblEWDesc.setWrap(true);
        ewTable.add(lblEWDesc).width(descWidth).left();
        enemyScrollContent.add(eWarriorImg).size(imgSize, imgSize).padRight(45f).padBottom(rowGap).left();
        enemyScrollContent.add(ewTable).expandX().fillX().padBottom(rowGap).row();

        // 2. Bomber
        com.badlogic.gdx.scenes.scene2d.ui.Image tntImg = new com.badlogic.gdx.scenes.scene2d.ui.Image(TextureRegion.split(tntSheet, 192, 192)[0][0]);
        Table tntTable = new Table().left();
        tntTable.add(new Label("BOMBER (Kẻ Ném Bom)", subTitleStyle)).left().padBottom(10f).row();
        Label lblTntDesc = new Label("Kẻ phá hoại điên cuồng mang theo bọc thuốc nổ công phá cực lớn. Có khả năng ném bộc phá từ khoảng cách xa, gây sát thương diện rộng (AoE) hủy diệt lên các cụm phòng thủ.\n\n💡 Mẹo: Nên dùng Hiệp sĩ có HP cao để chống chịu sát thương bom, tạo khoảng trống cho hàng sau an toàn tấn công!", textStyle);
        lblTntDesc.setWrap(true);
        tntTable.add(lblTntDesc).width(descWidth).left();
        enemyScrollContent.add(tntImg).size(imgSize, imgSize).padRight(45f).padBottom(rowGap).left();
        enemyScrollContent.add(tntTable).expandX().fillX().padBottom(rowGap).row();

        // 3. Torch Bearer
        com.badlogic.gdx.scenes.scene2d.ui.Image torchImg = new com.badlogic.gdx.scenes.scene2d.ui.Image(TextureRegion.split(torchSheet, 192, 192)[0][0]);
        Table torchTable = new Table().left();
        torchTable.add(new Label("TORCH BEARER (Kẻ Cầm Đuốc)", subTitleStyle)).left().padBottom(10f).row();
        Label lblTorchDesc = new Label("Binh chủng cuồng tín đột kích nhanh. Cầm vũ khí rực lửa tông thẳng vào đội hình ta với các cú chém thiêu đốt dồn dập, tạo áp lực khổng lồ lên các chiến binh cận chiến vòng ngoài.\n\n💡 Mẹo: Đối thủ có HP và ATK cực cao, nên phối hợp dùng Hiệp sĩ để chống chịu sát thương và Monk đứng sau hồi HP liên tục!", textStyle);
        lblTorchDesc.setWrap(true);
        torchTable.add(lblTorchDesc).width(descWidth).left();
        enemyScrollContent.add(torchImg).size(imgSize, imgSize).padRight(45f).padBottom(25f).left();
        enemyScrollContent.add(torchTable).expandX().fillX().padBottom(25f).row();

        enemyScrollPane = new ScrollPane(enemyScrollContent, scrollStyle);
        enemyScrollPane.setScrollingDisabled(true, false);
        enemyScrollPane.setFadeScrollBars(false);
        enemyScrollPane.setFlickScroll(true);

        boxEnemy.add(enemyScrollPane).width(960f).height(410f).padBottom(15f).row();
        TextButton backFromEnemyBtn = createAnimatedButton("BACK", btnStyle);
        boxEnemy.add(backFromEnemyBtn).center();
        wikiEnemyTable.add(boxEnemy);

        // ========================================================
        // GIAO DIỆN CHỨC NĂNG 5: HƯỚNG DẪN LUẬT CHƠI (ĐÃ CẬP NHẬT SCROLL)
        // ========================================================
        tutorialTable = new Table();
        tutorialTable.setFillParent(true);
        tutorialTable.setVisible(false);
        tutorialTable.padTop(180f); // Hạ thấp xuống một chút cho cân đối giống About

        Table box4 = new Table();
        box4.setBackground(darkBox);
        box4.pad(25f, 60f, 25f, 60f);
        box4.add(new Label("- HOW TO PLAY -", subTitleStyle)).padBottom(25f).center().row();

        Table tutorialScrollContent = new Table().left().top();

        // --- NỘI DUNG HƯỚNG DẪN ---
        String ruleText = "[ CƠ CHẾ ĐIỀU BINH & KINH TẾ ]\n"
            + "- Mua và nâng cấp lính bằng cách Click trực tiếp vào các tòa nhà tương ứng trên bản đồ căn cứ của bạn.\n"
            + "- Thợ Mỏ (Miner) là nguồn cung cấp Vàng duy nhất trong trò chơi. Hãy đầu tư mua Thợ Mỏ từ sớm để thiết lập một nền kinh tế vững chắc trước khi nghĩ đến việc kích hoạt quân đội.\n\n"
            + "[ HỆ THỐNG PHÒNG THỦ TUYẾN ĐƯỜNG ]\n"
            + "- Dọc theo tuyến đường hành quân của cả hai phe đều có các Tháp phòng thủ kiên cố canh giữ.\n"
            + "- Bạn bắt buộc phải phá hủy hệ thống Tháp phòng thủ của địch để mở đường tiến quân, đồng thời phải bảo vệ nghiêm ngặt các Tháp bên mình nhằm làm giảm áp lực càn quét từ quân đối phương.\n\n"
            + "[ ĐIỀU KIỆN THẮNG BẠI ]\n"
            + "- CHIẾN THẮNG: Lực lượng quân ta vượt qua mọi tầng phòng thủ và phá hủy thành công Nhà Chính của phe địch.\n"
            + "- THẤT BẠI: Để quái vật/quân địch tràn vào và đánh sập hoàn toàn Nhà Chính của ta.\n\n"
            + "MẸO CHIẾN THUẬT: Đừng chỉ tập trung mua lính chiến đấu. Một chuỗi cung ứng Vàng ổn định kết hợp với việc điều phối lính chặn đường hợp lý mới là chìa khóa để đập tan các đợt Waves quỷ quyệt từ AI!";

        Label tutorialTextLabel = new Label(ruleText, textStyle);
        tutorialTextLabel.setWrap(true);
        tutorialScrollContent.add(tutorialTextLabel).width(1050f).left().row();

        // Tạo ScrollPane cho Tutorial
        tutorialScrollPane = new ScrollPane(tutorialScrollContent, scrollStyle);
        tutorialScrollPane.setScrollingDisabled(true, false);
        tutorialScrollPane.setFadeScrollBars(false);
        tutorialScrollPane.setFlickScroll(true);

        box4.add(tutorialScrollPane).width(1120f).height(340f).padBottom(20f).row();

        TextButton backFromTutorialBtn = createAnimatedButton("BACK", btnStyle);
        box4.add(backFromTutorialBtn).center();
        tutorialTable.add(box4);

        // Quản lý tập trung các Layer bảng biểu vào Stage
        stage.addActor(mainTable);
        stage.addActor(levelTable);
        stage.addActor(aboutTable);
        stage.addActor(wikiSelectTable);
        stage.addActor(wikiAllyTable);
        stage.addActor(wikiEnemyTable);
        stage.addActor(tutorialTable);

        // ========================================================
        // THIẾT LẬP LOGIC ĐIỀU HƯỚNG SỰ KIỆN (LISTENERS)
        // ========================================================
        playBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                mainTable.setVisible(false);
                levelTable.setVisible(true);
            }
        });
        aboutBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                mainTable.setVisible(false);
                aboutTable.setVisible(true);
                stage.setKeyboardFocus(aboutScrollPane);
            }
        });
        tutorialBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                mainTable.setVisible(false);
                tutorialTable.setVisible(true);
            }
        });
        quitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                Gdx.app.exit();
            }
        });

        // Cơ chế rẽ nhánh bách khoa toàn thư
        wikiBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                mainTable.setVisible(false);
                wikiSelectTable.setVisible(true);
            }
        });
        allyBranchBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                wikiSelectTable.setVisible(false);
                wikiAllyTable.setVisible(true);
                stage.setKeyboardFocus(allyScrollPane);
            }
        });
        enemyBranchBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                wikiSelectTable.setVisible(false);
                wikiEnemyTable.setVisible(true);
                stage.setKeyboardFocus(enemyScrollPane);
            }
        });

        // Hệ thống nút Back điều hướng quay lại tầng Menu trước đó
        backFromLvBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                levelTable.setVisible(false);
                mainTable.setVisible(true);
            }
        });
        backFromAboutBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                aboutTable.setVisible(false);
                mainTable.setVisible(true);
            }
        });
        backFromTutorialBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                tutorialTable.setVisible(false);
                mainTable.setVisible(true);
            }
        });
        backFromSelectBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                wikiSelectTable.setVisible(false);
                mainTable.setVisible(true);
            }
        });
        // Sửa lại đoạn listener của nút BACK từ Wiki Đồng Minh về bảng chọn
        backFromAllyBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();

                // RESET TRẠNG THÁI NÚT BẤM TẠI ĐÂY
                allyBranchBtn.setChecked(false);

                wikiAllyTable.setVisible(false);
                wikiSelectTable.setVisible(true);
            }
        });

        // Sửa lại đoạn listener của nút BACK từ Wiki Kẻ Địch về bảng chọn
        backFromEnemyBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();

                // RESET TRẠNG THÁI NÚT BẤM TẠI ĐÂY
                enemyBranchBtn.setChecked(false);

                wikiEnemyTable.setVisible(false);
                wikiSelectTable.setVisible(true);
            }
        });

        lv1Btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                if (game.audioManager != null) {
                    game.audioManager.stopMenuMusic();
                    game.audioManager.playGameplayMusic();
                }
                game.setScreen(new DemoModelScreen(game));
            }
        });

        lv2Btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                if (game.audioManager != null) {
                    game.audioManager.stopMenuMusic();
                    game.audioManager.playGameplayMusic();
                }
                game.setScreen(new DemoModelScreen2(game));
            }
        });
    }

    private void playClickSound() {
        if (game.audioManager != null) {
            game.audioManager.playScreenClick();
        }
    }

    private TextButton createAnimatedButton(final String text, TextButton.TextButtonStyle style) {
        final TextButton button = new TextButton(text, style);
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                button.setText("> " + text + " <");
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                button.setText(text);
            }
        });
        return button;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float scrollSpeed = 250f * delta;
        // Xử lý bắt phím cuộn mượt cho từng vùng ScrollPane tương ứng khi mở màn hình hiển thị
        if (aboutTable != null && aboutTable.isVisible() && aboutScrollPane != null) {
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
                aboutScrollPane.setScrollY(aboutScrollPane.getScrollY() + scrollSpeed);
            else if (Gdx.input.isKeyPressed(Input.Keys.UP))
                aboutScrollPane.setScrollY(aboutScrollPane.getScrollY() - scrollSpeed);
        }
        if (wikiAllyTable != null && wikiAllyTable.isVisible() && allyScrollPane != null) {
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
                allyScrollPane.setScrollY(allyScrollPane.getScrollY() + scrollSpeed);
            else if (Gdx.input.isKeyPressed(Input.Keys.UP))
                allyScrollPane.setScrollY(allyScrollPane.getScrollY() - scrollSpeed);
        }
        if (wikiEnemyTable != null && wikiEnemyTable.isVisible() && enemyScrollPane != null) {
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
                enemyScrollPane.setScrollY(enemyScrollPane.getScrollY() + scrollSpeed);
            else if (Gdx.input.isKeyPressed(Input.Keys.UP))
                enemyScrollPane.setScrollY(enemyScrollPane.getScrollY() - scrollSpeed);
        }

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = bgAnimation.getKeyFrame(stateTime, true);

        batch.begin();
        batch.draw(currentFrame, 0, 0, 1600, 900);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, 1600, 900);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        // Giải phóng triệt để bộ nhớ các Sprite Sheet để tránh tràn VRAM
        if (minerSheet != null) minerSheet.dispose();
        if (warriorSheet != null) warriorSheet.dispose();
        if (archerSheet != null) archerSheet.dispose();
        if (lancerSheet != null) lancerSheet.dispose();
        if (monkSheet != null) monkSheet.dispose();
        if (enemyWarriorSheet != null) enemyWarriorSheet.dispose();
        if (tntSheet != null) tntSheet.dispose();
        if (torchSheet != null) torchSheet.dispose();

        // === GIẢI PHÓNG BỘ NHỚ TEXTURE HIỆU ỨNG NÚT BẤM ===
        if (btnBlueTex != null) btnBlueTex.dispose();
        if (btnBluePressedTex != null) btnBluePressedTex.dispose();
        if (btnRedTex != null) btnRedTex.dispose();
        if (btnRedPressedTex != null) btnRedPressedTex.dispose();
        if (btnHoverTex != null) btnHoverTex.dispose();

        batch.dispose();
        if (bgTextures != null) {
            for (Texture tex : bgTextures) {
                if (tex != null) tex.dispose();
            }
        }
        if (overlayTex != null) overlayTex.dispose();
        if (scrollKnobTex != null) scrollKnobTex.dispose();
        buttonFont.dispose();
        textFont.dispose();
        stage.dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        System.out.println("MainMenuScreen ẩn -> Đã dọn dẹp Input của Menu.");
    }
}
