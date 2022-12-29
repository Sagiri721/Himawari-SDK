import java.io.IOException;
import org.jsoup.nodes.Element;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

}
