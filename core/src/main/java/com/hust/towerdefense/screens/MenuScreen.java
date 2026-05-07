package com.hust.towerdefense.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hust.towerdefense.MainGame;
import com.hust.towerdefense.input.InputHandler;

public class MenuScreen implements Screen {

    private enum Difficulty {EASY, NORMAL, HARD}

    private static final float UI_WIDTH = 800f;
    private static final float UI_HEIGHT = 480f;

    private final MainGame game;
    private final BitmapFont font;
    private final ShapeRenderer shapes;
    private final Viewport viewport;
    private final Vector3 tmp = new Vector3();

    private final Rectangle startRect = new Rectangle();
    private final Rectangle guideRect = new Rectangle();
    private final Rectangle difficultyRect = new Rectangle();
    private final Rectangle exitRect = new Rectangle();

    private Difficulty difficulty = Difficulty.NORMAL;
    private int hoveredIndex = -1;
    private InputHandler input;

    public MenuScreen(MainGame game) {
        this.game = game;
        this.font = new BitmapFont();
        this.shapes = new ShapeRenderer();
        this.viewport = new FitViewport(UI_WIDTH, UI_HEIGHT);
    }

    @Override
    public void show() {
        layout();
        input = new InputHandler(
                (screenX, screenY, pointer, button) -> handleClick(screenX, screenY),
                (screenX, screenY) -> hoveredIndex = hitTest(screenX, screenY)
        );
        Gdx.input.setInputProcessor(input);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        game.getBatch().setProjectionMatrix(viewport.getCamera().combined);
        shapes.setProjectionMatrix(viewport.getCamera().combined);

        // background boxes
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        drawButton(0, startRect);
        drawButton(1, guideRect);
        drawButton(2, difficultyRect);
        drawButton(3, exitRect);
        shapes.end();

        game.getBatch().begin();
        font.draw(game.getBatch(), "TOWER DEFENSE", 280, UI_HEIGHT - 60);
        font.draw(game.getBatch(), "START GAME", startRect.x + 30, startRect.y + startRect.height - 18);
        font.draw(game.getBatch(), "HUONG DAN", guideRect.x + 30, guideRect.y + guideRect.height - 18);
        font.draw(game.getBatch(), "DO KHO: " + difficulty.name(), difficultyRect.x + 30, difficultyRect.y + difficultyRect.height - 18);
        font.draw(game.getBatch(), "THOAT", exitRect.x + 30, exitRect.y + exitRect.height - 18);
        font.draw(game.getBatch(), "Click vao o de chon", 280, 70);
        game.getBatch().end();
    }

    @Override
    public void dispose() {
        if (Gdx.input.getInputProcessor() == input) {
            Gdx.input.setInputProcessor(null);
        }
        shapes.dispose();
        font.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        layout();
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    private void layout() {
        float w = 420f;
        float h = 54f;
        float x = (UI_WIDTH - w) / 2f;
        float top = UI_HEIGHT - 140f;
        float gap = 14f;

        startRect.set(x, top - h, w, h);
        guideRect.set(x, top - (h + gap) * 2f + gap, w, h);
        difficultyRect.set(x, top - (h + gap) * 3f + gap * 2f, w, h);
        exitRect.set(x, top - (h + gap) * 4f + gap * 3f, w, h);
    }

    private void drawButton(int idx, Rectangle r) {
        boolean hovered = idx == hoveredIndex;
        if (hovered) {
            shapes.setColor(0.25f, 0.55f, 0.95f, 1f);
        } else {
            shapes.setColor(0.15f, 0.15f, 0.15f, 1f);
        }
        shapes.rect(r.x, r.y, r.width, r.height);
    }

    private int hitTest(int screenX, int screenY) {
        viewport.unproject(tmp.set(screenX, screenY, 0));
        float x = tmp.x;
        float y = tmp.y;
        if (startRect.contains(x, y)) return 0;
        if (guideRect.contains(x, y)) return 1;
        if (difficultyRect.contains(x, y)) return 2;
        if (exitRect.contains(x, y)) return 3;
        return -1;
    }

    private void handleClick(int screenX, int screenY) {
        int hit = hitTest(screenX, screenY);
        switch (hit) {
            case 0:
                game.setScreen(new PlayScreen());
                dispose();
                break;
            case 1:
                game.setScreen(new InstructionsScreen(game));
                dispose();
                break;
            case 2:
                switch (difficulty) {
                    case EASY:
                        difficulty = Difficulty.NORMAL;
                        break;
                    case NORMAL:
                        difficulty = Difficulty.HARD;
                        break;
                    case HARD:
                        difficulty = Difficulty.EASY;
                        break;
                    default:
                        difficulty = Difficulty.NORMAL;
                        break;
                }
                break;
            case 3:
                Gdx.app.exit();
                break;
            default:
                break;
        }
    }
}

