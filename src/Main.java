import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JOptionPane;

import java.awt.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import Components.Structs.ObjectData;

public class Main {

    public static final String version = "pre-build";

    public static void main(String[] args) {

        openSettings();

        Functions.startTheme();

        new Style();
        new Window();

        /*
         * TileSet set = new TileSet("C:/Users/Utilizador/Desktop/Grass.png", 16);
         */
        // Map map = new Map(null, 100, 100);
        // Functions.openMapEditor(map);

        new Project(new File("C:\\touhou\\app"));
    }

    private static void openSettings() {

        String json = "";

        try {

            File settingsFile = new File("src/data/settings.json");
            Scanner s = new Scanner(settingsFile);

            while (s.hasNextLine())
                json += s.nextLine();

            s.close();

            GsonBuilder gb = new GsonBuilder();
            gb.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);

            Gson g = gb.create();
            g.fromJson(json, Settings.class);

        } catch (Exception e) {

            System.out.println("There was an error parsing settings");
            e.printStackTrace();
        }
    }
}
