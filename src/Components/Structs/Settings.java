package Components.Structs;

import java.io.File;
import java.io.FileWriter;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

public class Settings {

    public String name;
    public String company;
    public int[] version = new int[3];
    public String product_info;
    public boolean display_cursor;
    public boolean start_fullscreen;
    public boolean use_splashscreen;
    public String splashscreen_path;
    public boolean allow_fullscreen;
    public boolean resize_window;
    public String icon;

    public void updateSettingsFile() {

        try {

            Gson g = new Gson();
            Settings r = this;
            String text = g.toJson(r);

            FileWriter fw = new FileWriter(new File("src/data/settings.json"));

            fw.write(text);
            fw.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, "Error saving to local settings", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}
