
import java.awt.*;

public class SettingsRef {

    public String file_open_definition;
    public int[] background_color;
    public String open_alias;

    public String username;
    public String theme;

    public SettingsRef(String s, Color b, String u, String alias, String t) {

        file_open_definition = s;

        int[] temp = { b.getRed(), b.getBlue(), b.getGreen() };
        background_color = temp;

        open_alias = alias;
        username = u;
        theme = t;
    }
}