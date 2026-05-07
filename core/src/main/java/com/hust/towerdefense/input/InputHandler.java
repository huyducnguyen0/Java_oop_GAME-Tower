package com.hust.towerdefense.input;

import com.badlogic.gdx.InputAdapter;

public class InputHandler extends InputAdapter {
    public interface ClickListener {
        void onClick(int screenX, int screenY, int pointer, int button);
    }

    public interface HoverListener {
        void onHover(int screenX, int screenY);
    }

    private final Runnable onTouch;
    private final ClickListener clickListener;
    private final HoverListener hoverListener;

    public InputHandler(Runnable onTouch) {
        this.onTouch = onTouch;
        this.clickListener = null;
        this.hoverListener = null;
    }

    public InputHandler(ClickListener clickListener) {
        this.onTouch = null;
        this.clickListener = clickListener;
        this.hoverListener = null;
    }

    public InputHandler(ClickListener clickListener, HoverListener hoverListener) {
        this.onTouch = null;
        this.clickListener = clickListener;
        this.hoverListener = hoverListener;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (onTouch != null) onTouch.run();
        if (clickListener != null) clickListener.onClick(screenX, screenY, pointer, button);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (hoverListener != null) hoverListener.onHover(screenX, screenY);
        return false;
    }
}

