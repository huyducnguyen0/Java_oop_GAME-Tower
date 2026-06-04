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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hust.towerdefence.MainGame;

/**
 * Màn hình Menu chính của trò chơi (Main Menu Screen).
 * Đã nâng cấp: Tích hợp 3 cơ chế cuộn, fix Font Tiếng Việt.
 * Nút bấm tiếng Anh, nội dung mô tả tiếng Việt có dấu.
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

    private final Stage stage;
    private final Table mainTable;
    private final Table levelTable;
    private final Table wikiTable;
    private final Table tutorialTable;
    private final Table aboutTable;

    private ScrollPane aboutScrollPane;
    private ScrollPane wikiScrollPane;
    private Texture minerSheet, warriorSheet, archerSheet, lancerSheet, monkSheet;

    public MainMenuScreen(MainGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1600, 900);
        batch = new SpriteBatch();

        if (game.audioManager != null) {
            game.audioManager.playMenuMusic();
        }

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

        // Font chữ to cho nút bấm và tiêu đề
        parameter.size = 45;
        buttonFont = generator.generateFont(parameter);

        // Font chữ nhỏ hơn cho mô tả văn bản
        parameter.size = 28;
        textFont = generator.generateFont(parameter);

        generator.dispose(); // Giải phóng bộ nhớ
        // ========================================================

        stage = new Stage(new FitViewport(1600, 900));

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0f, 0f, 0f, 0.35f));
        pixmap.fill();
        overlayTex = new Texture(pixmap);
        pixmap.dispose();
        TextureRegionDrawable darkBox = new TextureRegionDrawable(new TextureRegion(overlayTex));

        Pixmap scrollPixmap = new Pixmap(6, 1, Pixmap.Format.RGBA8888);
        scrollPixmap.setColor(new Color(1f, 1f, 1f, 0.4f));
        scrollPixmap.fill();
        scrollKnobTex = new Texture(scrollPixmap);
        scrollPixmap.dispose();
        TextureRegionDrawable scrollKnobDrawable = new TextureRegionDrawable(new TextureRegion(scrollKnobTex));

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = buttonFont;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.overFontColor = Color.GOLD;
        btnStyle.downFontColor = Color.GRAY;

        Label.LabelStyle textStyle = new Label.LabelStyle(textFont, Color.WHITE);
        Label.LabelStyle subTitleStyle = new Label.LabelStyle(buttonFont, Color.GOLD);

        // ========================================================
        // GIAO DIỆN CHỨC NĂNG 1: MENU CHÍNH
        // ========================================================
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.padTop(120f);

        Table box1 = new Table();
        box1.setBackground(darkBox);
        box1.pad(30f, 80f, 30f, 80f);

        // ĐỂ LẠI NÚT TIẾNG ANH
        TextButton playBtn = createAnimatedButton("PLAY GAME", btnStyle);
        TextButton aboutBtn = createAnimatedButton("ABOUT GAME", btnStyle);
        TextButton wikiBtn = createAnimatedButton("TOWER INFO", btnStyle);
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
        TextButton lv2Btn = createAnimatedButton("MAP 2: STONE TOWER", btnStyle);
        TextButton backFromLvBtn = createAnimatedButton("BACK TO MENU", btnStyle);

        box2.add(levelTitle).padBottom(40f).row();
        box2.add(lv1Btn).padBottom(25f).row();
        box2.add(lv2Btn).padBottom(40f).row();
        box2.add(backFromLvBtn);
        levelTable.add(box2);

        // ========================================================
        // GIAO DIỆN CHỨC NĂNG 3: GIỚI THIỆU (NỘI DUNG TIẾNG VIỆT CÓ DẤU)
        // ========================================================
        aboutTable = new Table();
        aboutTable.setFillParent(true);
        aboutTable.setVisible(false);
        aboutTable.padTop(60f);

        Table boxAbout = new Table();
        boxAbout.setBackground(darkBox);
        boxAbout.pad(35f, 60f, 35f, 60f);

        boxAbout.add(new Label("- ABOUT TOWER DEFENSE -", subTitleStyle)).padBottom(25f).center().row();

        Table scrollContent = new Table();
        scrollContent.left().top();

        // GIỮ TIẾNG VIỆT CÓ DẤU XỊN SÒ Ở ĐÂY
        String introText = "Đây là đồ án nghiên cứu và phát triển biểu diễn của nhóm chúng mình tại SOICT HUST.\n\n"
            + "Trò chơi kết hợp giữa chiến thuật thả quân, xây dựng các tháp phòng thủ chiến lược độc đáo "
            + "và hệ thống lưu trữ thông tin kết quả trận đấu thông qua cơ sở dữ liệu SQL Server hiện đại.\n\n"
            + "Hệ thống AI đối thủ cấp độ mạnh mẽ sẽ tự động phân tích lượng vàng, khảo sát tuyến đường "
            + "và sinh ra quái vật theo các đợt (waves) thách thức khả năng bày binh bố trận của bạn.\n\n"
            + "Mỗi hành động mua lính, xây tháp đều tiêu tốn tài nguyên. Hãy quản lý kinh tế thật thông minh, "
            + "khai thác tối đa sức mạnh của Xạ Thủ hay Hiệp Sĩ để bảo vệ nhà chính khỏi cuộc càn quét hủy diệt!\n\n"
            + "Sản phẩm được xây dựng trên nền tảng LibGDX framework, phát huy tư duy lập trình hướng đối tượng "
            + "và các mô hình quản lý thực thể nâng cao đúng chuẩn thiết kế hiện hành.\n\n"
            + "Chúc các bạn và các thầy cô có những trải nghiệm tuyệt vời nhất với tựa game của chúng mình!";

        Label longTextLabel = new Label(introText, textStyle);
        longTextLabel.setWrap(true);

        scrollContent.add(longTextLabel).width(1050f).left().row();

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = scrollKnobDrawable;

        aboutScrollPane = new ScrollPane(scrollContent, scrollStyle);
        aboutScrollPane.setScrollingDisabled(true, false);
        aboutScrollPane.setFadeScrollBars(false);
        aboutScrollPane.setFlickScroll(true);

        boxAbout.add(aboutScrollPane).width(1120f).height(420f).padBottom(25f).row();

        TextButton backFromAboutBtn = createAnimatedButton("BACK", btnStyle);
        boxAbout.add(backFromAboutBtn).center();

        aboutTable.add(boxAbout);

        // ========================================================
        // GIAO DIỆN CHỨC NĂNG 4: WIKI THÁP & QUÂN ĐỘI (TIẾNG VIỆT CÓ DẤU)
        // ========================================================
        wikiTable = new Table();
        wikiTable.setFillParent(true);
        wikiTable.setVisible(false);
        wikiTable.padTop(230f);

        Table box3 = new Table();
        box3.setBackground(darkBox);
        box3.pad(25f, 45f, 25f, 45f);

        box3.add(new Label("- TOWER & UNIT ENCYCLOPEDIA -", subTitleStyle)).padBottom(20f).center().row();

        Table wikiScrollContent = new Table();
        wikiScrollContent.left().top();

        minerSheet = new Texture(Gdx.files.internal("Units/Pawn/Pawn_Idle Gold.png"));
        TextureRegion minerIcon = TextureRegion.split(minerSheet, 192, 192)[0][0];
        com.badlogic.gdx.scenes.scene2d.ui.Image minerImg = new com.badlogic.gdx.scenes.scene2d.ui.Image(minerIcon);

        warriorSheet = new Texture(Gdx.files.internal("Units/Warrior/Warrior_Idle.png"));
        TextureRegion warriorIcon = TextureRegion.split(warriorSheet, 192, 192)[0][0];
        com.badlogic.gdx.scenes.scene2d.ui.Image warriorImg = new com.badlogic.gdx.scenes.scene2d.ui.Image(warriorIcon);

        archerSheet = new Texture(Gdx.files.internal("Units/Archer/Archer_Idle.png"));
        TextureRegion archerIcon = TextureRegion.split(archerSheet, 192, 192)[0][0];
        com.badlogic.gdx.scenes.scene2d.ui.Image archerImg = new com.badlogic.gdx.scenes.scene2d.ui.Image(archerIcon);

        lancerSheet = new Texture(Gdx.files.internal("Units/Lancer/Lancer_Idle.png"));
        TextureRegion lancerIcon = TextureRegion.split(lancerSheet, 320, 320)[0][0];
        com.badlogic.gdx.scenes.scene2d.ui.Image lancerImg = new com.badlogic.gdx.scenes.scene2d.ui.Image(lancerIcon);

        monkSheet = new Texture(Gdx.files.internal("Units/Monk/Idle.png"));
        TextureRegion monkIcon = TextureRegion.split(monkSheet, 192, 192)[0][0];
        com.badlogic.gdx.scenes.scene2d.ui.Image monkImg = new com.badlogic.gdx.scenes.scene2d.ui.Image(monkIcon);

        float descWidth = 630f;
        float rowGap = 50f;
        float imgSize = 250f;

        // --- 1. THỢ MỎ ---
        Table mTable = new Table().left();
        Label lblMName = new Label("ACADEMY MINER (Thợ Mỏ)", subTitleStyle);
        Label lblMDesc = new Label("Đơn vị hậu cần cốt lõi của đội hình. Chuyên trách khai thác và đào vàng trên bản đồ, cung cấp nguồn tài nguyên dồi dào để tối ưu hóa kinh tế, giúp bạn dễ dàng đẩy nhanh tiến độ mua lính và xây dựng phòng tuyến.", textStyle);
        lblMDesc.setWrap(true);
        mTable.add(lblMName).left().padBottom(10f).row();
        mTable.add(lblMDesc).width(descWidth).left();
        wikiScrollContent.add(minerImg).size(imgSize, imgSize).padRight(45f).padBottom(rowGap).left();
        wikiScrollContent.add(mTable).expandX().fillX().padBottom(rowGap).row();

        // --- 2. CHIẾN BINH ---
        Table wTable = new Table().left();
        Label lblWName = new Label("TRAIN WARRIOR (Chiến Binh)", subTitleStyle);
        Label lblWDesc = new Label("Lực lượng cận chiến tiên phong dũng mãnh. Chuyên làm khắc tinh chặn các nút thắt giao tranh nhờ sở hữu lượng máu (HP) cực kỳ trâu bò, mặc dù sát thương (DMG) gây ra có phần yếu hơn.", textStyle);
        lblWDesc.setWrap(true);
        wTable.add(lblWName).left().padBottom(10f).row();
        wTable.add(lblWDesc).width(descWidth).left();
        wikiScrollContent.add(warriorImg).size(imgSize, imgSize).padRight(45f).padBottom(rowGap).left();
        wikiScrollContent.add(wTable).expandX().fillX().padBottom(rowGap).row();

        // --- 3. XẠ THỦ ---
        Table aTable = new Table().left();
        Label lblAName = new Label("TOWER ARCHER (Xạ Thủ)", subTitleStyle);
        Label lblADesc = new Label("Đơn vị xạ thủ với tầm bắn cực xa và tốc độ bắn ổn định. Sở hữu lượng sát thương (DMG) và lượng máu (HP) ở mức trung bình, thích hợp đặt trên cao để bắn tỉa quái vật bay hoặc kẻ địch tốc độ cao.", textStyle);
        lblADesc.setWrap(true);
        aTable.add(lblAName).left().padBottom(10f).row();
        aTable.add(lblADesc).width(descWidth).left();
        wikiScrollContent.add(archerImg).size(imgSize, imgSize).padRight(45f).padBottom(rowGap).left();
        wikiScrollContent.add(aTable).expandX().fillX().padBottom(rowGap).row();

        // --- 4. GIÁO BINH ---
        Table lTable = new Table().left();
        Label lblLName = new Label("BARRACKS LANCER (Giáo Binh)", subTitleStyle);
        Label lblLDesc = new Label("Chiến binh thiết thương với đòn đâm xuyên thấu diện rộng. Sở hữu lượng sát thương (DMG) cực kỳ to để càn quét quái vật đi theo cụm, tuy nhiên lượng máu (HP) lại không được trâu bò như Chiến Binh.", textStyle);
        lblLDesc.setWrap(true);
        lTable.add(lblLName).left().padBottom(10f).row();
        lTable.add(lblLDesc).width(descWidth).left();
        wikiScrollContent.add(lancerImg).size(imgSize, imgSize).padRight(45f).padBottom(rowGap).left();
        wikiScrollContent.add(lTable).expandX().fillX().padBottom(rowGap).row();

        // --- 5. TU SĨ ---
        Table moTable = new Table().left();
        Label lblMoName = new Label("ALTAR MONK (Tu Sĩ Hỗ Trợ)", subTitleStyle);
        Label lblMoDesc = new Label("Thầy pháp hỗ trợ và phục hồi tinh thần. Có khả năng triển khai các vòng ma pháp hào quang chữa lành (Heal) để liên tục hồi phục trạng thái, duy trì sự sống cho quân ta tại các điểm nóng giao tranh.", textStyle);
        lblMoDesc.setWrap(true);
        moTable.add(lblMoName).left().padBottom(10f).row();
        moTable.add(lblMoDesc).width(descWidth).left();
        wikiScrollContent.add(monkImg).size(imgSize, imgSize).padRight(45f).padBottom(25f).left();
        wikiScrollContent.add(moTable).expandX().fillX().padBottom(25f).row();

        ScrollPane.ScrollPaneStyle wikiScrollStyle = new ScrollPane.ScrollPaneStyle();
        wikiScrollStyle.vScrollKnob = scrollKnobDrawable;

        wikiScrollPane = new ScrollPane(wikiScrollContent, wikiScrollStyle);
        wikiScrollPane.setScrollingDisabled(true, false);
        wikiScrollPane.setFadeScrollBars(false);
        wikiScrollPane.setFlickScroll(true);

        box3.add(wikiScrollPane).width(960f).height(410f).padBottom(15f).row();

        TextButton backFromWikiBtn = createAnimatedButton("BACK", btnStyle);
        box3.add(backFromWikiBtn).center();

        wikiTable.add(box3);

        // ========================================================
        // GIAO DIỆN CHỨC NĂNG 5: HƯỚNG DẪN LUẬT CHƠI (TIẾNG VIỆT)
        // ========================================================
        tutorialTable = new Table();
        tutorialTable.setFillParent(true);
        tutorialTable.setVisible(false);
        tutorialTable.padTop(120f);

        Table box4 = new Table();
        box4.setBackground(darkBox);
        box4.pad(40f, 60f, 40f, 60f);

        Label tutorialTitle = new Label("- HOW TO PLAY -", subTitleStyle);
        box4.add(tutorialTitle).padBottom(30f).center().row();

        // HƯỚNG DẪN TIẾNG VIỆT CÓ DẤU
        box4.add(new Label("1. Mua tháp và đặt dọc theo đường đi để chặn quái vật.", textStyle)).padBottom(15f).left().row();
        box4.add(new Label("2. Tiêu diệt quái vật để kiếm thêm Vàng (Gold).", textStyle)).padBottom(15f).left().row();
        box4.add(new Label("3. Sử dụng Vàng để mua thêm lính và nâng cấp căn cứ.", textStyle)).padBottom(15f).left().row();
        box4.add(new Label("4. Tuyệt đối không để quái vật đi đến Nhà Chính!", textStyle)).padBottom(30f).left().row();

        TextButton backFromTutorialBtn = createAnimatedButton("BACK", btnStyle);
        box4.add(backFromTutorialBtn).center();
        tutorialTable.add(box4);

        stage.addActor(mainTable);
        stage.addActor(levelTable);
        stage.addActor(aboutTable);
        stage.addActor(wikiTable);
        stage.addActor(tutorialTable);

        playBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { playClickSound(); mainTable.setVisible(false); levelTable.setVisible(true); } });
        aboutBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { playClickSound(); mainTable.setVisible(false); aboutTable.setVisible(true); stage.setKeyboardFocus(aboutScrollPane); } });
        wikiBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { playClickSound(); mainTable.setVisible(false); wikiTable.setVisible(true); stage.setKeyboardFocus(wikiScrollPane); } });
        tutorialBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { playClickSound(); mainTable.setVisible(false); tutorialTable.setVisible(true); } });
        quitBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { playClickSound(); Gdx.app.exit(); } });

        backFromLvBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { playClickSound(); levelTable.setVisible(false); mainTable.setVisible(true); } });
        backFromAboutBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { playClickSound(); aboutTable.setVisible(false); mainTable.setVisible(true); } });
        backFromWikiBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { playClickSound(); wikiTable.setVisible(false); mainTable.setVisible(true); } });
        backFromTutorialBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { playClickSound(); tutorialTable.setVisible(false); mainTable.setVisible(true); } });

        lv1Btn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                if (game.audioManager != null) {
                    game.audioManager.stopMenuMusic();
                    game.audioManager.playGameplayMusic();
                }
                game.setScreen(DemoModelScreen.class);
            }
        });

        lv2Btn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                System.out.println("Map 2 đang trong quá trình phát triển hoàn thiện!");
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

        if (aboutTable != null && aboutTable.isVisible() && aboutScrollPane != null) {
            float scrollSpeed = 250f * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                aboutScrollPane.setScrollY(aboutScrollPane.getScrollY() + scrollSpeed);
            } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                aboutScrollPane.setScrollY(aboutScrollPane.getScrollY() - scrollSpeed);
            }
        }

        if (wikiTable != null && wikiTable.isVisible() && wikiScrollPane != null) {
            float scrollSpeed = 250f * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                wikiScrollPane.setScrollY(wikiScrollPane.getScrollY() + scrollSpeed);
            } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                wikiScrollPane.setScrollY(wikiScrollPane.getScrollY() - scrollSpeed);
            }
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
        if (minerSheet != null) minerSheet.dispose();
        if (warriorSheet != null) warriorSheet.dispose();
        if (archerSheet != null) archerSheet.dispose();
        if (lancerSheet != null) lancerSheet.dispose();
        if (monkSheet != null) monkSheet.dispose();

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

    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        System.out.println("MainMenuScreen ẩn -> Đã dọn dẹp Input của Menu.");
    }
}
