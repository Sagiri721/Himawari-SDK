import java.awt.*;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

public class Settings {

    public static String file_open_definition;
    public static Color background;
    public static String open_alias;

    public static String username;

    public static void updateFile() {

        try {

            Gson g = new Gson();
            SettingsRef r = new SettingsRef(file_open_definition, background, username, open_alias);
            String text = g.toJson(r);

            FileWriter fw = new FileWriter(new File("src/data/settings.json"));

            fw.write(text);
            fw.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, "Error saving to local settings", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}
