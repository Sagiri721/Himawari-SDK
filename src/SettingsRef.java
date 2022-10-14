
import java.awt.*;

public class SettingsRef {

    public String file_open_definition;
    public int[] background_color;

    public String username;

    public SettingsRef(String s, Color b, String u) {

        file_open_definition = s;

        int[] temp = { b.getRed(), b.getBlue(), b.getGreen() };
        background_color = temp;

        username = u;
    }
}