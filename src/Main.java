import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class Main {

    public static final String version = "pre-build";
    private static DiscordRichPresence presence;
    
    public static void main(String[] args) {

        openSettings();
        initDiscordPresence();

        Functions.startTheme();

        new Style();
        new Window();

        /*
         * TileSet set = new TileSet("C:/Users/Utilizador/Desktop/Grass.png", 16);
         */
        // Map map = new Map(null, 100, 100);
        // Functions.openMapEditor(map);

        new Project(new File("C:\\thenewgame\\app"));
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

    public static void initDiscordPresence(){

        String appID = "1091732166192406709";
        DiscordEventHandlers handlers = new DiscordEventHandlers();

        DiscordRPC.discordInitialize(appID, handlers, true, "");
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000;
        presence.details = "Editing a Himawari2D game!";
        presence.state = "Idle";
        presence.largeImageKey = "novo_projeto_15_";

        DiscordRPC.discordUpdatePresence(presence);
        Main.presence = presence;

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()){

                DiscordRPC.discordRunCallbacks();
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {}
            }
        });
    }
    
    public static void UpdateDiscordPresence(String status) {
        
        Main.presence.state = status;
        DiscordRPC.discordUpdatePresence(Main.presence);
    }
}
