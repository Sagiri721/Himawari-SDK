import java.io.File;
import java.util.Scanner;

import javax.swing.JOptionPane;
import java.awt.*;
import com.google.gson.Gson;

import Components.Structs.Map;

public class Main {

    public static final String version = "pre-build";

    public static void main(String[] args) {

        Style.LoadResources();
        openSettings();

        new Window();

        /*
         * TileSet set = new TileSet("C:/Users/Utilizador/Desktop/Grass.png", 16);
         */
        Map map = new Map(null, 100, 100);
        // Functions.openMapEditor(map);

        // new Project(new File("D:/TIAGO/program/himawari/my-app"));
    }

    private static void openSettings() {

        String json = "";

        try {

            File settingsFile = new File("src/data/settings.json");
            Scanner s = new Scanner(settingsFile);

            while (s.hasNextLine())
                json += s.nextLine();

            Gson g = new Gson();

            SettingsRef settings = g.fromJson(json, SettingsRef.class);

            Settings.file_open_definition = settings.file_open_definition;
            Settings.background = new Color(settings.background_color[0], settings.background_color[1],
                    settings.background_color[2]);
            Settings.username = settings.username;

        } catch (Exception e) {

            System.out.println("There was an error parsing settings");
            e.printStackTrace();
        }
    }

    class SettingsRef {

        public String file_open_definition;
        public int[] background_color;

        public String username;
    }
}
