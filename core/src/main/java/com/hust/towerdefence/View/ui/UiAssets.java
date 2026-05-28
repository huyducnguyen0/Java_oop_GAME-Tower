package com.hust.towerdefence.View.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

/**
 * Loads and owns UI-only assets.
 * Gameplay assets and map assets stay outside this class.
 */
public class UiAssets {
    private static final String HUD_BAR_BACKGROUND = "UI/Banners/Carved_3Slides.png";
    private static final String PANEL_BACKGROUND = "UI/Banners/Carved_3Slides.png";
    private static final String BUTTON_BLUE = "UI/Buttons/Button_Blue_3Slides.png";
    private static final String BUTTON_BLUE_DOWN = "UI/Buttons/Button_Blue_3Slides_Pressed.png";
    private static final String BUTTON_RED = "UI/Buttons/Button_Red_3Slides.png";
    private static final String BUTTON_RED_DOWN = "UI/Buttons/Button_Red_3Slides_Pressed.png";
    private static final String BUTTON_DISABLED = "UI/Buttons/Button_Disable_3Slides.png";
    private static final Color INK = new Color(0.17f, 0.09f, 0.04f, 1f);
    private static final Color TITLE = new Color(0.10f, 0.05f, 0.02f, 1f);
    private static final Color MUTED = new Color(0.42f, 0.29f, 0.18f, 1f);
    private static final Color WARNING = new Color(0.65f, 0.12f, 0.08f, 1f);

    private final Texture hudBarTexture;
    private final Texture panelTexture;
    private final Texture buttonBlueTexture;
    private final Texture buttonBlueDownTexture;
    private final Texture buttonRedTexture;
    private final Texture buttonRedDownTexture;
    private final Texture buttonDisabledTexture;
    private final BitmapFont font;
    private final NinePatchDrawable hudBarDrawable;
    private final NinePatchDrawable panelDrawable;
    private final Label.LabelStyle defaultLabelStyle;
    private final Label.LabelStyle titleLabelStyle;
    private final Label.LabelStyle mutedLabelStyle;
    private final Label.LabelStyle warningLabelStyle;
    private final TextButton.TextButtonStyle primaryButtonStyle;
    private final TextButton.TextButtonStyle redButtonStyle;

    public UiAssets() {
        hudBarTexture = new Texture(HUD_BAR_BACKGROUND);
        panelTexture = new Texture(PANEL_BACKGROUND);
        buttonBlueTexture = new Texture(BUTTON_BLUE);
        buttonBlueDownTexture = new Texture(BUTTON_BLUE_DOWN);
        buttonRedTexture = new Texture(BUTTON_RED);
        buttonRedDownTexture = new Texture(BUTTON_RED_DOWN);
        buttonDisabledTexture = new Texture(BUTTON_DISABLED);

        hudBarTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        panelTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        buttonBlueTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        buttonBlueDownTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        buttonRedTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        buttonRedDownTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        buttonDisabledTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        font = new BitmapFont();
        font.setColor(INK);
        font.getData().setScale(1.05f);

        hudBarDrawable = horizontalDrawable(hudBarTexture);
        panelDrawable = horizontalDrawable(panelTexture);
        defaultLabelStyle = new Label.LabelStyle(font, INK);
        titleLabelStyle = new Label.LabelStyle(font, TITLE);
        mutedLabelStyle = new Label.LabelStyle(font, MUTED);
        warningLabelStyle = new Label.LabelStyle(font, WARNING);
        primaryButtonStyle = createButtonStyle(buttonBlueTexture, buttonBlueDownTexture);
        redButtonStyle = createButtonStyle(buttonRedTexture, buttonRedDownTexture);
    }

    private TextButton.TextButtonStyle createButtonStyle(Texture up, Texture down) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(
            horizontalDrawable(up),
            horizontalDrawable(down),
            horizontalDrawable(buttonDisabledTexture),
            font
        );
        style.disabled = horizontalDrawable(buttonDisabledTexture);
        style.fontColor = TITLE;
        style.downFontColor = Color.WHITE;
        style.checkedFontColor = Color.WHITE;
        style.disabledFontColor = MUTED;
        return style;
    }

    // Hưng
    private NinePatchDrawable horizontalDrawable(Texture texture) {
        NinePatchDrawable drawable = new NinePatchDrawable(new NinePatch(texture, 16, 16, 0, 0));
        drawable.setMinWidth(0f);
        drawable.setMinHeight(0f);
        return drawable;
    }

    public NinePatchDrawable getHudBarDrawable() {
        return hudBarDrawable;
    }

    public NinePatchDrawable getPanelDrawable() {
        return panelDrawable;
    }

    public Label.LabelStyle getDefaultLabelStyle() {
        return defaultLabelStyle;
    }

    public Label.LabelStyle getTitleLabelStyle() {
        return titleLabelStyle;
    }

    public Label.LabelStyle getMutedLabelStyle() {
        return mutedLabelStyle;
    }

    public Label.LabelStyle getWarningLabelStyle() {
        return warningLabelStyle;
    }

    public TextButton.TextButtonStyle getPauseButtonStyle() {
        return redButtonStyle;
    }

    public TextButton.TextButtonStyle getPrimaryButtonStyle() {
        return primaryButtonStyle;
    }

    public void dispose() {
        hudBarTexture.dispose();
        panelTexture.dispose();
        buttonBlueTexture.dispose();
        buttonBlueDownTexture.dispose();
        buttonRedTexture.dispose();
        buttonRedDownTexture.dispose();
        buttonDisabledTexture.dispose();
        font.dispose();
    }
}
