
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Functions {

    // No mp3 / ogg support yet
    private static String[][] allowedFileExtensions = {
            { "png", "jpg", "jpeg", "jfif", "avif" }, // Images
            { "wav" }, // Sound
            { "otf", "ttf" },// Fonts
    };

    /**
     * Run a basic .bat file
     * 
     * @param file
     */
    public static void RunBatch(String file) {

        File f = new File("src/functions/" + file);

        try {

            Runtime.getRuntime().exec(f.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a .bat file and then executes it
     * 
     * @param command
     * @param file
     */
    public static void WriteAndRun(String command, String file) {

        File f = new File("src/functions/" + file);

        try {

            FileWriter fw = new FileWriter(f);
            fw.write(command);
            fw.close();

            Runtime.getRuntime().exec(f.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFileContents(File file) throws FileNotFoundException {
        Scanner s = new Scanner(file);
        String filetext = "";

        while (s.hasNextLine())
            filetext += s.nextLine();

        s.close();
        return filetext;
    }

    public static void CopyFilesTo(String folder) throws IOException {

        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int option = fc.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {

            File oldpath = new File(fc.getSelectedFile().getAbsolutePath());
            File newPath = new File(Project.engineFiles + "/" + folder + "/" + fc.getSelectedFile().getName());

            if (newPath.exists()) {

                JOptionPane.showMessageDialog(null,
                        "The file was already added before, or a file with same name already exists", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Files.copy(oldpath.toPath(), newPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
