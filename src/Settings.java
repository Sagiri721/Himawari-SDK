import java.awt.*;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Settings {

    public static String file_open_definition;
    public static int[] background;
    public static String open_alias;
    public static String theme;

    public static String username;
    public static String uid;

    public Settings(String s, int[] b, String u, String alias, String t, String id) {

        file_open_definition = s;
        background = b;

        open_alias = alias;
        username = u;
        theme = t;
        uid = id;
    }

    public static void updateFile() {

        try {

            GsonBuilder b = new GsonBuilder();
            b.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);

            Gson g = b.create();
            String text = g.toJson(new Settings(file_open_definition, background, username, open_alias, theme, uid));

            FileWriter fw = new FileWriter(new File("src/data/settings.json"));

            fw.write(text);
            fw.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, "Error saving to local settings", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}
