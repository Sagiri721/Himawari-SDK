
import java.awt.*;

public class SettingsRef {

    public String file_open_definition;
    public int[] background_color;
    public String open_alias;

    public String username;

    public SettingsRef(String s, Color b, String u, String alias) {

        file_open_definition = s;

        int[] temp = { b.getRed(), b.getBlue(), b.getGreen() };
        background_color = temp;

        open_alias = alias;
        username = u;
    }
}