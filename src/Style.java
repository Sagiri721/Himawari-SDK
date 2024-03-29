import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

public class Style {

    public enum Theme {

        LIGHT,
        DARK,
        INTELLIJ,
        NONE,
    }

    public static Theme currentTheme = Theme.LIGHT;
    public static Color MAIN_BACKGROUND = null;
    public static Color SECONDARY_BACKGROUND = /* new Color(190, 190, 190) */ null;
    public static Color MAIN_TEXT_COLOR = null;
    public static Color MAP_EDITOR_TAB_COLOR = null;
    public static Color MAP_EDITOR_GRID = null;
    public static Color MAIN_BUTTON_COLOR = null;
    public static Color LAUCH_BUTTON_COLOR = null;
    public static Color CLOSE_BUTTON_COLOR = null;

    public static Color FILE_EXPLORER_BACKGROUND = null;

    public static final Border BUTTON_BORDER = BorderFactory.createBevelBorder(BevelBorder.RAISED);
    public static final Border SIMPLE_BORDER = BorderFactory.createLineBorder(Color.BLACK, 2);

    public static final float MAIN_TEXT_SIZE = 13;
    public static final float HEADER_SIZE = 22;

    public static Font CONTENT_FONT;
    public static Font HEADER_FONT;
    public static Font FONT1;
    public static Font FONT2;

    public Style() {

        try {

            Style.CONTENT_FONT = Font.createFont(Font.TRUETYPE_FONT,
                    getClass().getResource("res/fonts/Roboto-Light.ttf").openStream());
            Style.HEADER_FONT = Font.createFont(Font.TRUETYPE_FONT,
                    getClass().getResource("res/fonts/OpenSans-SemiBold.ttf").openStream());
            Style.FONT1 = Font.createFont(Font.TRUETYPE_FONT,
                    getClass().getResource("res/fonts/BomberUrban-Regular.otf").openStream());
            Style.FONT2 = Font.createFont(Font.TRUETYPE_FONT,
                    getClass().getResource("res/fonts/VictorMono-Bold.ttf").openStream());            

            Style.CONTENT_FONT = Style.CONTENT_FONT.deriveFont(Style.MAIN_TEXT_SIZE);
            Style.HEADER_FONT = Style.HEADER_FONT.deriveFont(Style.HEADER_SIZE);
            Style.FONT1 = Style.FONT1.deriveFont(Style.HEADER_SIZE);
            Style.FONT2 = Style.FONT2.deriveFont(18.0f);

        } catch (Exception e) {
        }

        // FlatLaf settings
        UIManager.put("Component.arc", 999);
        UIManager.put("ProgressBar.arc", 999);
        UIManager.put("Component.arrowType", "triangle");
        UIManager.put("TabbedPane.selectedBackground", Color.white);
    }

    public static JButton GetStyledButton(String text) {

        JButton button = new JButton(text);
        // button.setBorderPainted(false);
        // button.setFocusPainted(false);

        button.setFont(CONTENT_FONT);
        button.setBackground(MAIN_BACKGROUND);
        button.setForeground(MAIN_TEXT_COLOR);

        return button;
    }

    public static JPanel getPanel(String text) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(Color.white);

        panel.setBackground(new Color(33, 29, 28));

        panel.add(label);

        return panel;
    }
}