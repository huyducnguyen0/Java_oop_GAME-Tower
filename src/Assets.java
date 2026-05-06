import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Assets {
    public static Font customFont;
    public static BufferedImage grassTile, cloudImg, stoneImg, goldImg;
    public static Cursor tinySwordsCursor;
    public static BufferedImage castle, archery, barracks, monastery, tower, house1, house2, house3;
    public static BufferedImage[] archerIdle, warriorAttack, pawnIdle, lancerAttack, priestHeal;

    // Ảnh trang trí
    public static BufferedImage bush1, bush2, stump1;

    public static void load() {
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/ice-hockey.ttf")).deriveFont(Font.PLAIN, 40f);
            BufferedImage tileset = ImageIO.read(new File("res/Tilemap_color4.png"));
            grassTile = tileset.getSubimage(64, 64, 64, 64);

            stoneImg = ImageIO.read(new File("res/Rock2.png"));
            goldImg = ImageIO.read(new File("res/Gold_Resource.png"));
            cloudImg = ImageIO.read(new File("res/Clouds_05.png"));

            castle = ImageIO.read(new File("res/Castle.png"));
            tower = ImageIO.read(new File("res/Tower.png"));
            archery = ImageIO.read(new File("res/Archery.png"));
            barracks = ImageIO.read(new File("res/Barracks.png"));
            monastery = ImageIO.read(new File("res/Monastery.png"));
            house1 = ImageIO.read(new File("res/House1.png"));
            house2 = ImageIO.read(new File("res/House2.png"));
            house3 = ImageIO.read(new File("res/House3.png"));

            archerIdle = loadFrames("res/Archer_Idle.png", 6);
            warriorAttack = loadFrames("res/Warrior_Attack1.png", 4);
            pawnIdle = loadFrames("res/Pawn_Idle Axe.png", 8);
            lancerAttack = loadFrames("res/Lancer_Down_Attack.png", 3);
            priestHeal = loadFrames("res/Heal.png", 10);

            BufferedImage cur = ImageIO.read(new File("res/Cursor_02.png"));
            tinySwordsCursor = Toolkit.getDefaultToolkit().createCustomCursor(cur.getScaledInstance(32, 32, Image.SCALE_SMOOTH), new Point(16, 16), "Cur");

            // --- NẠP VẬT PHẨM TRANG TRÍ ---
            BufferedImage b1Sheet = ImageIO.read(new File("res/Bushe1.png"));
            bush1 = b1Sheet.getSubimage(0, 0, b1Sheet.getHeight(), b1Sheet.getHeight());

            BufferedImage b2Sheet = ImageIO.read(new File("res/Bushe2.png"));
            bush2 = b2Sheet.getSubimage(0, 0, b2Sheet.getHeight(), b2Sheet.getHeight());

            stump1 = ImageIO.read(new File("res/Stump 1.png"));

        } catch (Exception e) {
            System.err.println("Lỗi nạp Assets. Kiểm tra lại tên file trong thư mục res: " + e.getMessage());
        }
    }

    private static BufferedImage[] loadFrames(String path, int count) throws Exception {
        BufferedImage sheet = ImageIO.read(new File(path));
        BufferedImage[] f = new BufferedImage[count];
        for (int i = 0; i < count; i++) f[i] = sheet.getSubimage(i * 192, 0, 192, 192);
        return f;
    }
}