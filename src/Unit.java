import java.awt.*;
import java.awt.image.BufferedImage;

public class Unit {
    private int x, y, size;
    private BufferedImage[] frames;
    private int currentFrame = 0;
    private long lastTime;
    private int speed = 120;

    public Unit(int x, int y, int size, BufferedImage[] frames) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.frames = frames;
        this.lastTime = System.currentTimeMillis();
    }

    public void draw(Graphics2D g2) {
        if (frames == null || frames.length == 0) return;
        if (System.currentTimeMillis() - lastTime > speed) {
            currentFrame = (currentFrame + 1) % frames.length;
            lastTime = System.currentTimeMillis();
        }
        g2.drawImage(frames[currentFrame], x, y, size, size, null);
    }
}