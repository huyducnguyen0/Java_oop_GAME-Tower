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
    private static final String BUTTON_UP = "UI/Buttons/Button_Hover_3Slides.png";
    private static final String BUTTON_DOWN = "UI/Buttons/Button_Disable_3Slides.png";
    private static final Color INK = new Color(0.18f, 0.10f, 0.05f, 1f);

    private final Texture hudBarTexture;
    private final Texture buttonUpTexture;
    private final Texture buttonDownTexture;
    private final BitmapFont font;
    private final NinePatchDrawable hudBarDrawable;
    private final Label.LabelStyle defaultLabelStyle;
    private final TextButton.TextButtonStyle pauseButtonStyle;

    public UiAssets() {
        hudBarTexture = new Texture(HUD_BAR_BACKGROUND);
        buttonUpTexture = new Texture(BUTTON_UP);
        buttonDownTexture = new Texture(BUTTON_DOWN);

        hudBarTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        buttonUpTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        buttonDownTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        font = new BitmapFont();
        font.setColor(INK);
        font.getData().setScale(1.12f);

        hudBarDrawable = new NinePatchDrawable(new NinePatch(hudBarTexture, 64, 64, 0, 0));
        defaultLabelStyle = new Label.LabelStyle(font, INK);
        pauseButtonStyle = new TextButton.TextButtonStyle(
            new NinePatchDrawable(new NinePatch(buttonUpTexture, 64, 64, 0, 0)),
            new NinePatchDrawable(new NinePatch(buttonDownTexture, 64, 64, 0, 0)),
            new NinePatchDrawable(new NinePatch(buttonDownTexture, 64, 64, 0, 0)),
            font
        );
        pauseButtonStyle.fontColor = INK;
        pauseButtonStyle.downFontColor = Color.WHITE;
        pauseButtonStyle.checkedFontColor = Color.WHITE;
    }

    public NinePatchDrawable getHudBarDrawable() {
        return hudBarDrawable;
    }

    public NinePatchDrawable getPanelDrawable() {
        return hudBarDrawable;
    }

    public Label.LabelStyle getDefaultLabelStyle() {
        return defaultLabelStyle;
    }

    public TextButton.TextButtonStyle getPauseButtonStyle() {
        return pauseButtonStyle;
    }

    public void dispose() {
        hudBarTexture.dispose();
        buttonUpTexture.dispose();
        buttonDownTexture.dispose();
        font.dispose();
    }
}
