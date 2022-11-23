import java.awt.Color;
import java.awt.Font;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

public class Style {

    public enum Theme {

        LIGHT,
        DARK
    }

    public static Theme currentTheme = Theme.LIGHT;
    public static Color MAIN_BACKGROUND = Color.WHITE;
    public static Color SECONDARY_BACKGROUND = new Color(190, 190, 190);
    public static Color MAIN_TEXT_COLOR = Color.BLACK;
    public static Color MAP_EDITOR_TAB_COLOR = Color.GRAY.darker();
    public static Color MAP_EDITOR_GRID = Color.WHITE;
    public static Color MAIN_BUTTON_COLOR = Color.WHITE;
    public static Color LAUCH_BUTTON_COLOR = Color.GREEN;
    public static Color CLOSE_BUTTON_COLOR = Color.GREEN;

    public static Color FILE_EXPLORER_BACKGROUND = Color.BLUE;

    public static final Border BUTTON_BORDER = BorderFactory.createBevelBorder(BevelBorder.RAISED);
    public static final Border SIMPLE_BORDER = BorderFactory.createLineBorder(Color.BLACK, 2);

    public static final float MAIN_TEXT_SIZE = 13;
    public static final float HEADER_SIZE = 22;

    public static Font CONTENT_FONT;
    public static Font HEADER_FONT;

    public static void LoadResources() {

        try {

            Style.CONTENT_FONT = Font.createFont(Font.TRUETYPE_FONT, new File("res/fonts/Roboto-Light.ttf"));
            Style.HEADER_FONT = Font.createFont(Font.TRUETYPE_FONT, new File("res/fonts/OpenSans-SemiBold.ttf"));

            Style.CONTENT_FONT.deriveFont(Style.MAIN_TEXT_SIZE);
            Style.HEADER_FONT.deriveFont(Style.HEADER_SIZE);

        } catch (Exception e) {
        }
    }

    public static JButton GetStyledButton(String text) {

        JButton button = new JButton(text);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.setFont(HEADER_FONT);
        button.setBackground(MAIN_BACKGROUND);
        button.setForeground(MAIN_TEXT_COLOR);

        return button;
    }
}