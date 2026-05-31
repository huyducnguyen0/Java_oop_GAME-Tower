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
 * Đã nâng cấp: Tích hợp 3 cơ chế cuộn (Vuốt kéo, Thanh Scrollbar bên phải, Giữ đè phím mũi tên UP/DOWN liên tục).
 * Bố cục được tối ưu lại không gian hiển thị lớn và chuyên nghiệp hơn.
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
    private final Texture scrollKnobTex; // Tài nguyên vẽ thanh cuộn dọc nhỏ nhỏ bên phải

    private final Stage stage;
    private final Table mainTable;
    private final Table levelTable;
    private final Table wikiTable;
    private final Table tutorialTable;
    private final Table aboutTable;

    // Chuyển scrollPane thành biến toàn cục để hàm render truy cập thời gian thực
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

        buttonFont = new BitmapFont();
        buttonFont.getData().setScale(3f);
        textFont = new BitmapFont();
        textFont.getData().setScale(2f);

        stage = new Stage(new FitViewport(1600, 900));

        // Tạo nền đen mờ cho hộp thoại UI
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0f, 0f, 0f, 0.35f));
        pixmap.fill();
        overlayTex = new Texture(pixmap);
        pixmap.dispose();
        TextureRegionDrawable darkBox = new TextureRegionDrawable(new TextureRegion(overlayTex));

        // TẠO ĐỒ HỌA CHO THANH SCROLLBAR BÊN PHẢI (Màu trắng mờ, bo góc gọn gàng)
        Pixmap scrollPixmap = new Pixmap(6, 1, Pixmap.Format.RGBA8888);
        scrollPixmap.setColor(new Color(1f, 1f, 1f, 0.4f)); // Trắng đục 40% nhìn cực sang
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
        // GIAO DIỆN CHỨC NĂNG 1: BẢNG MENU CHÍNH (MAIN TABLE)
        // ========================================================
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.padTop(120f);

        Table box1 = new Table();
        box1.setBackground(darkBox);
        box1.pad(30f, 80f, 30f, 80f);

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
        // GIAO DIỆN CHỨC NĂNG 2: BẢNG CHỌN MÀN CHƠI (LEVEL TABLE)
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
        // GIAO DIỆN CHỨC NĂNG 3: BẢNG GIỚI THIỆU CÓ CUỘN ĐA NĂNG (ABOUT TABLE)
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

        String introText = "Day la do an nghien cuu va phat trien bieu dien cua nhom chung minh tai SOICT HUST.\n\n"
            + "Tro choi ket hop giua chien thuat tha quan, xay dung cac thap phong thu chien luoc doc dao "
            + "va he thong luu tru thong tin ket qua tran dau thong qua co so du lieu SQL Server hien dai.\n\n"
            + "He thong AI doi thu cap do manh me se tu dong phan tich luong vang, khao sat tuyen duong "
            + "va sinh ra quai vat theo cac dot (waves) thach thuc kha nang bay binh bo tran cua ban.\n\n"
            + "Moi hanh dong mua linh, xay thap deu tieu ton tai nguyen. Hay quan ly kinh te that thong minh, "
            + "khai thac toi da suc manh cua Xa Thu hay Hiep Si de bao ve nha chinh khoi cuoc can quet huy diet!\n\n"
            + "San pham duoc xay dung tren nen tang LibGDX framework, phat huy tu duy lap trinh huong doi tuong "
            + "va cac mo hinh quan ly thuc the nang cao dung chuan mo hinh thiet ke hien hanh.\n\n"
            + "Chuc cac ban va cac thay co co nhung trai nghiem tuyet voi nhat voi tua game cua chung minh!";

        Label longTextLabel = new Label(introText, textStyle);
        longTextLabel.setWrap(true);

        scrollContent.add(longTextLabel).width(1050f).left().row();

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = scrollKnobDrawable;

        // Khởi tạo gán trực tiếp vào thuộc tính class toàn cục
        aboutScrollPane = new ScrollPane(scrollContent, scrollStyle);
        aboutScrollPane.setScrollingDisabled(true, false);
        aboutScrollPane.setFadeScrollBars(false);
        aboutScrollPane.setFlickScroll(true);

        // Đưa vùng cuộn lớn vào layout
        boxAbout.add(aboutScrollPane).width(1120f).height(420f).padBottom(25f).row();

        TextButton backFromAboutBtn = createAnimatedButton("BACK", btnStyle);
        boxAbout.add(backFromAboutBtn).center();

        aboutTable.add(boxAbout);

        // ========================================================
        // GIAO DIEN CHUC NANG 4: BANG TRA CUU CONG TRINH (WIKI TABLE)
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

        // --------------------------------------------------------
        // NAP TEXTURE VA CAT FRAME DAU TIEN LAM ICON CHINH XAC
        // --------------------------------------------------------
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

        // --------------------------------------------------------
        // THAY DOI MANH TAY KICH THUOC MAX TAM THEO Y ONG
        // --------------------------------------------------------
        float descWidth = 630f;   // Can doi lai do rong de chu to khong bi xuong dong qua vut vat
        float rowGap = 50f;       // Tang khoang cach giua cac hang cho thoai mai, chu to khong de len nhau
        float imgSize = 250f;     // TIEP TUC PHONG TO ANH LINH len 250f nhìn cuc ky ham ho
        float nameScale = 1.7f;   // PHONG TO MANH TAY tieu de ten linh
        float descScale = 1.5f;   // PHONG TO CHU MO TA doc sieu ro rang

        // --- 1. MINER ---
        Table mTable = new Table().left();
        Label lblMName = new Label("ACADEMY MINER (Train Miner)", subTitleStyle); lblMName.setFontScale(nameScale);
        Label lblMDesc = new Label("Don vi hau can cot loi cua doi hinh. Chuyen trach khai thac va dao vang tren ban do, cung cap nguon tai nguyen doi dao de toi uu hoa kinh te, giup ban de dang day nhanh tien do mua linh va xay dung phong tuyen.", textStyle);
        lblMDesc.setFontScale(descScale); lblMDesc.setWrap(true);
        mTable.add(lblMName).left().padBottom(10f).row();
        mTable.add(lblMDesc).width(descWidth).left();

        wikiScrollContent.add(minerImg).size(imgSize, imgSize).padRight(45f).padBottom(rowGap).left();
        wikiScrollContent.add(mTable).expandX().fillX().padBottom(rowGap).row();

        // --- 2. WARRIOR ---
        Table wTable = new Table().left();
        Label lblWName = new Label("TRAIN WARRIOR (Train Warrior)", subTitleStyle); lblWName.setFontScale(nameScale);
        Label lblWDesc = new Label("Luc luong can chien tien phong dung manh. Chuyen lam khac tinh chan cac nut that giao tranh nho so huu luong mau (HP) cuc ky trau bo, mac du sat thuong (DMG) gay ra co phan yeu hon.", textStyle);
        lblWDesc.setFontScale(descScale); lblWDesc.setWrap(true);
        wTable.add(lblWName).left().padBottom(10f).row();
        wTable.add(lblWDesc).width(descWidth).left();

        wikiScrollContent.add(warriorImg).size(imgSize, imgSize).padRight(45f).padBottom(rowGap).left();
        wikiScrollContent.add(wTable).expandX().fillX().padBottom(rowGap).row();

        // --- 3. ARCHER ---
        Table aTable = new Table().left();
        Label lblAName = new Label("TOWER ARCHER (Train Archer)", subTitleStyle); lblAName.setFontScale(nameScale);
        Label lblADesc = new Label("Don vi xa thu voi tam ban cuc xa va toc do ban on dinh. So huu luong sat thuong (DMG) va luong mau (HP) o muc trung binh, thich hop dat tren cao de ban tia quai vat bay hoac ke dich toc do cao.", textStyle);
        lblADesc.setFontScale(descScale); lblADesc.setWrap(true);
        aTable.add(lblAName).left().padBottom(10f).row();
        aTable.add(lblADesc).width(descWidth).left();

        wikiScrollContent.add(archerImg).size(imgSize, imgSize).padRight(45f).padBottom(rowGap).left();
        wikiScrollContent.add(aTable).expandX().fillX().padBottom(rowGap).row();

        // --- 4. LANCER ---
        Table lTable = new Table().left();
        Label lblLName = new Label("BARRACKS LANCER (Train Lancer)", subTitleStyle); lblLName.setFontScale(nameScale);
        Label lblLDesc = new Label("Chien binh thiet thuong voi don dam xuyen thau dien rong. So huu luong sat thuong (DMG) cuc ky to de can quet quai vat di theo cum, tuy nhien luong mau (HP) lai khong duoc trau bo nhu Chien Binh.", textStyle);
        lblLDesc.setFontScale(descScale); lblLDesc.setWrap(true);
        lTable.add(lblLName).left().padBottom(10f).row();
        lTable.add(lblLDesc).width(descWidth).left();

        wikiScrollContent.add(lancerImg).size(imgSize, imgSize).padRight(45f).padBottom(rowGap).left();
        wikiScrollContent.add(lTable).expandX().fillX().padBottom(rowGap).row();

        // --- 5. MONK (HEALER) ---
        Table moTable = new Table().left();
        Label lblMoName = new Label("ALTAR MONK (Train Monk)", subTitleStyle); lblMoName.setFontScale(nameScale);
        Label lblMoDesc = new Label("Thay phap ho tro va phuc hoi tinh than. Co kha nang trien khai cac vong ma phap hao quang chua lanh (Heal) de lien tuc hoi phuc trang thai, duy tri su song cho quan ta tai cac diem nong giao tranh.", textStyle);
        lblMoDesc.setFontScale(descScale); lblMoDesc.setWrap(true);
        moTable.add(lblMoName).left().padBottom(10f).row();
        moTable.add(lblMoDesc).width(descWidth).left();

        wikiScrollContent.add(monkImg).size(imgSize, imgSize).padRight(45f).padBottom(25f).left();
        wikiScrollContent.add(moTable).expandX().fillX().padBottom(25f).row();

        // --------------------------------------------------------
        // DUA NOI DUNG VAO THANH CUON SCROLLPANE VA ADD VAO BOX LON
        // --------------------------------------------------------
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
        // GIAO DIỆN CHỨC NĂNG 5: BẢNG HƯỚNG DẪN LUẬT CHƠI (TUTORIAL TABLE)
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
        box4.add(new Label("1. Mua thap va dat canh duong di de chan quai vat.", textStyle)).padBottom(15f).left().row();
        box4.add(new Label("2. Tieu diet quai vat de kiem them Vang (Gold).", textStyle)).padBottom(15f).left().row();
        box4.add(new Label("3. Su dung Vang de nang cap thap manh hon.", textStyle)).padBottom(15f).left().row();
        box4.add(new Label("4. Khong de quai vat di den nha chinh!", textStyle)).padBottom(30f).left().row();

        TextButton backFromTutorialBtn = createAnimatedButton("BACK", btnStyle);
        box4.add(backFromTutorialBtn).center();
        tutorialTable.add(box4);

        stage.addActor(mainTable);
        stage.addActor(levelTable);
        stage.addActor(aboutTable);
        stage.addActor(wikiTable);
        stage.addActor(tutorialTable);

        playBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { playClickSound(); mainTable.setVisible(false); levelTable.setVisible(true); } });
        // Đoạn gộp focus bàn phím của nhóm ông giữ nguyên để các xử lý bổ trợ hoạt động tốt nhất
        aboutBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { playClickSound(); mainTable.setVisible(false); aboutTable.setVisible(true); stage.setKeyboardFocus(aboutScrollPane); } });
        wikiBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playClickSound();
                mainTable.setVisible(false);
                wikiTable.setVisible(true);
                stage.setKeyboardFocus(wikiScrollPane); // Thêm dòng này để nhận phím cuộn ngay lập tức
            }
        });
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

        // [MỚI TÍCH HỢP] Kiểm tra đè giữ phím liên tục theo thời gian thực (real-time delta)
        if (aboutTable != null && aboutTable.isVisible() && aboutScrollPane != null) {
            float scrollSpeed = 250f * delta; // Đồng bộ hóa mượt mà dựa trên FPS của máy tính
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
