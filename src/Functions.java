import java.io.File;
import java.util.Scanner;

public class Functions {

    /**
     * Run a basic .bat file
     * 
     * @param file
     */
    public static void RunBatch(String file) {

        File f = new File("src/functions/" + file);
        String filetext = "";

        try {

            /*
             * Scanner s = new Scanner(f);
             * 
             * while (s.hasNextLine())
             * filetext += s.nextLine();
             * 
             * s.close();
             */

            Runtime.getRuntime().exec(f.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
