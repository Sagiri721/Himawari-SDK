import java.awt.*;
import java.io.FileWriter;

import com.google.gson.Gson;

public class Settings {

    public static String file_open_definition;
    public static Color background;

    public static String username;

    public static updateFile(){

        Gson g = new Gson();
        g.toJson(new SettingsRef(file_open_definition, background, username), new FileWriter("src/data/settings.json"));
    }

    class SettingsRef {

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
}
