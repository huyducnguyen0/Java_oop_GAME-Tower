import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Main extends JPanel implements Runnable {
    public enum GameState { MENU, PLAYING }

    private GameState currentState = GameState.MENU;
    private Thread thread;
    private double timer = 0;
    private int floatOff = 0;

    // Vùng click ẩn ở giữa để test chuyển màn hình
    private Rectangle playBtn = new Rectangle(320, 260, 160, 100);

    private ArrayList<Unit> guards = new ArrayList<>();
    private ArrayList<Point> stonePile = new ArrayList<>();
    private ArrayList<Point> goldPile = new ArrayList<>();
    private ArrayList<DecoItem> decoItems = new ArrayList<>();

    // Lớp nội (Inner class) để quản lý đồ trang trí có sắp xếp chiều sâu Z-index
    private class DecoItem {
        BufferedImage image;
        int x, y, width, height, y_sort;

        public DecoItem(BufferedImage image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x; this.y = y; this.width = width; this.height = height;
            this.y_sort = y + height;
        }
    }

    public Main() {
        this.setPreferredSize(new Dimension(800, 600));
        Assets.load();
        if (Assets.tinySwordsCursor != null) this.setCursor(Assets.tinySwordsCursor);
        initWorld();

        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (currentState == GameState.MENU && playBtn.contains(e.getPoint())) {
                    currentState = GameState.PLAYING;
                }
            }
        });
    }

    private void initWorld() {
        // --- 1. TÀI NGUYÊN (Xếp thành từng đống co cụm) ---
        // Đống Đá (Cánh trái)
        stonePile.add(new Point(30, 240)); stonePile.add(new Point(42, 235));
        stonePile.add(new Point(38, 248)); stonePile.add(new Point(52, 242));
        stonePile.add(new Point(60, 236)); stonePile.add(new Point(56, 250));

        // Đống Vàng (Cánh phải)
        goldPile.add(new Point(650, 230)); goldPile.add(new Point(662, 225));
        goldPile.add(new Point(658, 238)); goldPile.add(new Point(672, 232));
        goldPile.add(new Point(680, 226)); goldPile.add(new Point(676, 240));

        // --- 2. TRANG TRÍ RỪNG RẬM (Đã bổ sung rất nhiều bụi cỏ) ---
        // Rừng Cánh Trái (Quanh Lâu Đài)
        decoItems.add(new DecoItem(Assets.bush1, -20, 170, 70, 70));
        decoItems.add(new DecoItem(Assets.bush2, 30, 160, 60, 60));
        decoItems.add(new DecoItem(Assets.bush1, -30, 220, 65, 65));
        decoItems.add(new DecoItem(Assets.stump1, 10, 200, 50, 50));
        decoItems.add(new DecoItem(Assets.bush1, 140, 180, 70, 70));
        decoItems.add(new DecoItem(Assets.bush2, 100, 150, 55, 55));
        decoItems.add(new DecoItem(Assets.bush1, 10, 240, 70, 70));
        decoItems.add(new DecoItem(Assets.bush2, 220, 200, 60, 60));

        decoItems.add(new DecoItem(Assets.bush2, -10, 390, 50, 50));
        decoItems.add(new DecoItem(Assets.bush1, -40, 340, 60, 60));
        decoItems.add(new DecoItem(Assets.bush1, 230, 420, 60, 60));
        decoItems.add(new DecoItem(Assets.bush2, 280, 450, 50, 50));
        decoItems.add(new DecoItem(Assets.bush1, 180, 520, 65, 65));
        decoItems.add(new DecoItem(Assets.stump1, 80, 560, 45, 45));
        decoItems.add(new DecoItem(Assets.bush2, 20, 550, 60, 60));
        decoItems.add(new DecoItem(Assets.bush1, 120, 560, 55, 55));

        // Rừng Cánh Phải (Quanh Doanh Trại)
        decoItems.add(new DecoItem(Assets.bush2, 680, 180, 70, 70));
        decoItems.add(new DecoItem(Assets.bush1, 620, 170, 60, 60));
        decoItems.add(new DecoItem(Assets.bush2, 740, 200, 70, 70));
        decoItems.add(new DecoItem(Assets.bush1, 780, 240, 65, 65));
        decoItems.add(new DecoItem(Assets.bush1, 480, 240, 60, 60));
        decoItems.add(new DecoItem(Assets.bush2, 420, 210, 55, 55));
        decoItems.add(new DecoItem(Assets.stump1, 750, 300, 45, 45));
        decoItems.add(new DecoItem(Assets.bush1, 670, 310, 50, 50));
        decoItems.add(new DecoItem(Assets.bush2, 760, 350, 60, 60));

        decoItems.add(new DecoItem(Assets.bush2, 490, 380, 50, 50));
        decoItems.add(new DecoItem(Assets.bush1, 440, 420, 55, 55));
        decoItems.add(new DecoItem(Assets.bush2, 720, 480, 70, 70));
        decoItems.add(new DecoItem(Assets.bush1, 760, 520, 60, 60));
        decoItems.add(new DecoItem(Assets.bush2, 580, 540, 65, 65));
        decoItems.add(new DecoItem(Assets.stump1, 680, 530, 40, 40));

        // Viền rừng dưới đáy màn hình (Bottom Edge)
        decoItems.add(new DecoItem(Assets.bush1, 260, 560, 45, 45));
        decoItems.add(new DecoItem(Assets.bush2, 350, 570, 50, 50));
        decoItems.add(new DecoItem(Assets.bush1, 450, 550, 45, 45));
        decoItems.add(new DecoItem(Assets.bush2, 520, 560, 50, 50));

        // Sắp xếp lớp trang trí
        decoItems.sort((a, b) -> a.y_sort - b.y_sort);

        // --- 3. BÀY BINH BỐ TRẬN LÍNH CANH ---
        // Cung thủ đứng vững chãi trên thành
        guards.add(new Unit(20, 295, 60, Assets.archerIdle));
        guards.add(new Unit(190, 305, 60, Assets.archerIdle));
        guards.add(new Unit(500, 315, 60, Assets.archerIdle));
        guards.add(new Unit(640, 295, 60, Assets.archerIdle));

        // 3 Lính chém hình chữ V bảo vệ trước cổng lâu đài
        guards.add(new Unit(45, 485, 75, Assets.warriorAttack));
        guards.add(new Unit(85, 500, 75, Assets.warriorAttack));
        guards.add(new Unit(125, 485, 75, Assets.warriorAttack));

        // Lính hỗ trợ và lính khu quân sự
        guards.add(new Unit(10, 510, 65, Assets.priestHeal));
        guards.add(new Unit(520, 460, 70, Assets.lancerAttack));
        guards.add(new Unit(580, 480, 70, Assets.lancerAttack));
        guards.add(new Unit(650, 450, 70, Assets.pawnIdle));
        guards.add(new Unit(710, 470, 70, Assets.pawnIdle));
        guards.add(new Unit(690, 430, 65, Assets.archerIdle));
    }

    public void run() {
        while (thread != null) {
            timer += 0.05; floatOff = (int) (Math.sin(timer) * 8);
            repaint();
            try { Thread.sleep(16); } catch (Exception e) {}
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (currentState == GameState.MENU) drawMenu(g2);
        else drawPlaying(g2);
    }

    private void drawMenu(Graphics2D g2) {
        // 1. Nền
        for (int y = 0; y < 600; y += 64) for (int x = 0; x < 800; x += 64) g2.drawImage(Assets.grassTile, x, y, null);

        // 2. Mây che
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
        g2.drawImage(Assets.cloudImg, -60, -50 + (floatOff / 2), 480, 230, null);
        g2.drawImage(Assets.cloudImg, 420, -40 + (floatOff / 3), 460, 260, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // 3. Tài nguyên
        for (Point p : stonePile) g2.drawImage(Assets.stoneImg, p.x, p.y, 22, 22, null);
        for (Point p : goldPile) g2.drawImage(Assets.goldImg, p.x, p.y, 25, 25, null);

        // 4. Trang trí (Vẽ rừng bụi cỏ, gốc cây)
        for (DecoItem item : decoItems) {
            if (item.image != null) g2.drawImage(item.image, item.x, item.y, item.width, item.height, null);
        }

        // 5. Kiến trúc
        g2.drawImage(Assets.castle, -30, 260, 240, 240, null);
        g2.drawImage(Assets.barracks, 580, 260, 180, 180, null);
        g2.drawImage(Assets.tower, 180, 290, 80, 140, null);
        g2.drawImage(Assets.tower, 490, 300, 80, 140, null);
        g2.drawImage(Assets.archery, 680, 410, 100, 100, null);
        g2.drawImage(Assets.monastery, -10, 430, 80, 80, null);
        g2.drawImage(Assets.house1, 200, 440, 65, 65, null);
        g2.drawImage(Assets.house3, 560, 450, 65, 65, null);

        // 6. Lính canh
        for (Unit u : guards) u.draw(g2);

        // 7. Vignette
        float[] dist = {0f, 1f};
        Color[] colors = {new Color(0, 0, 0, 0), new Color(0, 0, 0, 145)};
        g2.setPaint(new RadialGradientPaint(new Point(400, 300), 550, dist, colors));
        g2.fillRect(0, 0, 800, 600);

        // 8. Tiêu đề
        g2.setFont(Assets.customFont.deriveFont(Font.BOLD, 45f));
        drawShadowText(g2, "TOWER DEFENSE", 150 + floatOff, Color.WHITE);
    }

    private void drawShadowText(Graphics2D g2, String t, int y, Color c) {
        FontMetrics fm = g2.getFontMetrics();
        int x = (800 - fm.stringWidth(t)) / 2;
        g2.setColor(new Color(0, 0, 0, 180)); g2.drawString(t, x + 3, y + 3);
        g2.setColor(c); g2.drawString(t, x, y);
    }

    private void drawPlaying(Graphics2D g) {
        g.setColor(Color.BLACK); g.fillRect(0, 0, 800, 600);
        g.setColor(Color.WHITE); g.drawString("ĐANG TRONG GAME...", 350, 300);
    }

    public void startGame() { thread = new Thread(this); thread.start(); }
    public static void main(String[] args) {
        JFrame win = new JFrame("The Kingdom of Tower Defense");
        Main game = new Main(); win.add(game); win.pack();
        win.setLocationRelativeTo(null); win.setDefaultCloseOperation(3);
        win.setVisible(true); game.startGame();
    }
}