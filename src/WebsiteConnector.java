import java.io.IOException;
import org.jsoup.nodes.Element;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WebsiteConnector {

    public static final String url = "http://localhost:5000/";

    public static JsonObject[] getLibrariesFromSite() throws IOException {

        String final_url = url + "recordLibraries";

        String doc;
        try {

            doc = Jsoup.connect(final_url).ignoreContentType(true).execute().body();
        } catch (Exception e) {

            return null;
        }

        JsonArray jo = (JsonArray) JsonParser.parseString(doc);

        JsonObject[] libs = new JsonObject[jo.size()];

        for (int i = 0; i < libs.length; i++) {

            libs[i] = (JsonObject) jo.get(i);
        }

        return libs;
    }

    public static java.lang.Object[] isLoginValid(String name, String password){

        try {
            String d = Jsoup.connect(url + "recordFromName")
            .method(Connection.Method.POST).header("Content-Type", "application/json")
            .ignoreContentType(true).requestBody("{\"name\": \""+name+"\"}").execute().body();

            JsonObject jo = (JsonObject) ((JsonArray) JsonParser.parseString(d)).get(0);

            String pass = jo.get("password").getAsString();
            String id = jo.get("_id").getAsString();
            return new java.lang.Object[] {pass.equals(password), id};

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new java.lang.Object[] {false, null};
    }
}
