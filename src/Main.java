import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JOptionPane;

import java.awt.*;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.google.gson.Gson;

public class Main {

    public static final String version = "pre-build";

    public static void main(String[] args) {

        openSettings();

        switch (Settings.theme) {

            case "light":
                FlatLightLaf.setup();
                break;
            case "dark":
                FlatDarkLaf.setup();
                break;
            case "intellij":
                FlatIntelliJLaf.setup();
                break;
            case "mac-light":
                FlatMacLightLaf.setup();
                break;
            case "mac-dark":
                FlatMacDarkLaf.setup();
                break;
            case "custom":
                break;
        }

        new Style();
        new Window();

        /*
         * TileSet set = new TileSet("C:/Users/Utilizador/Desktop/Grass.png", 16);
         */
        // Map map = new Map(null, 100, 100);
        // Functions.openMapEditor(map);

        new Project(new File("C:\\my-game\\mygame"));
    }

    private static void openSettings() {

        String json = "";

        try {

            File settingsFile = new File("src/data/settings.json");
            Scanner s = new Scanner(settingsFile);

            while (s.hasNextLine())
                json += s.nextLine();

            s.close();
            Gson g = new Gson();

            SettingsRef settings = g.fromJson(json, SettingsRef.class);

            Settings.file_open_definition = settings.file_open_definition;
            Settings.background = new Color(settings.background_color[0], settings.background_color[1],
                    settings.background_color[2]);
            Settings.username = settings.username;
            Settings.open_alias = settings.open_alias;
            Settings.theme = settings.theme;

        } catch (Exception e) {

            System.out.println("There was an error parsing settings");
            e.printStackTrace();
        }
    }

    class SettingsRef {

        public String file_open_definition;
        public int[] background_color;

        public String username;
        public String open_alias;
        public String theme;
    }
}
