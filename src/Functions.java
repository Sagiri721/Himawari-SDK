
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Components.MapEditor;
import Components.Structs.Map;

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

            Process p = Runtime.getRuntime().exec(f.getAbsolutePath());
            p.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap<Components, String> FindCompiledComponents(File frontObject) {

        // Get .class directory
        String path = frontObject.getAbsolutePath();
        path = path.replace("src\\main\\java", "target\\classes").replace(".java", ".class");

        File classFile = new File(path);

        if (classFile.exists()) {

            // Find components
            try {

                Functions.Write(
                        Project.disk + " \ncd " + classFile.getParentFile().getAbsolutePath() + "\njavap -private "
                                + classFile.getName(),
                        "precompile_object.bat");
                Process proc = Runtime.getRuntime()
                        .exec("src\\functions\\precompile_object.bat");
                BufferedReader outReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                HashMap<Components, String> compMap = new HashMap<Components, String>();

                String s = null;
                while ((s = outReader.readLine()) != null) {

                    if (s.contains("Engine.Components.")) {

                        String type = s.substring(s.lastIndexOf('.') + 1, s.lastIndexOf(' '));
                        String name = s.substring(s.lastIndexOf(' '));
                        name.substring(0, name.length() - 1);

                        compMap.put(Components.getComponentbyString(type), name);
                    }
                }

                outReader.close();

                return compMap;

            } catch (IOException e) {
                e.printStackTrace();

                return null;
            }
        } else {

            return null;
        }
    }

    public static void RunBatchCmd(String file) {

        File f = new File("src/functions/" + file);

        try {

            Process p = Runtime.getRuntime().exec("cmd /c start \"\" " + f.getAbsolutePath());
            p.waitFor();

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

            Runtime.getRuntime().exec(f.getAbsolutePath()).waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Write(String command, String file) {

        File f = new File("src/functions/" + file);

        try {

            FileWriter fw = new FileWriter(f);
            fw.write(command);
            fw.close();

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

    public static void openMapEditor(Map map) {

        JFrame mapEditor = new JFrame();
        mapEditor.setSize(800, 830);
        mapEditor.setTitle("Map editing tool");

        MapEditor editor = new MapEditor(map, Optional.<File>empty());
        editor.setBounds(0, 0, 800, 830);

        mapEditor.setLayout(null);
        mapEditor.setLayout(null);
        mapEditor.add(editor);
        mapEditor.setResizable(false);
        mapEditor.setVisible(true);
    }

    public static void openMapEditorEducated(Map map, File mapPath) {

        JFrame mapEditor = new JFrame();
        mapEditor.setSize(800, 830);
        mapEditor.setTitle("Map editing tool");

        MapEditor editor = new MapEditor(map, Optional.<File>of(mapPath));
        editor.importMap(mapPath.getAbsolutePath());

        editor.setBounds(0, 0, 800, 830);

        mapEditor.setLayout(null);
        mapEditor.setLayout(null);
        mapEditor.add(editor);
        mapEditor.setResizable(false);
        mapEditor.setVisible(true);
    }

    public static JPanel getMapEditor(String path) {

        MapEditor editor = new MapEditor(new Map(null, 100, 100), Optional.<File>empty());
        editor.setObjectModelsFromFolder(path + "/Objects");
        editor.spriteFolder = path + "/Sprites";
        return editor;
    }

    public static JFrame OpenPanelAsFrame(int w, int h, String name, JPanel contents, boolean resizable) {

        JFrame project = new JFrame();
        project.setTitle(name);
        project.setSize(w, h);
        project.setLocationRelativeTo(null);
        project.setResizable(resizable);

        project.add(contents);

        project.setVisible(true);

        return project;
    }

    /**
     * 
     * CREATE PROJECT FUNCTION AND UTILS
     * 
     */

    public static void CreateProject(int template) {

        File folder, script = new File("src\\functions\\project_creation.bat"),
                script2 = new File("src\\functions\\engine_retrieve.bat");
        String company, name, artifact;

        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int o = fc.showOpenDialog(null);

        if (o == JFileChooser.APPROVE_OPTION) {

            folder = fc.getSelectedFile();

            company = JOptionPane.showInputDialog(null, "Company:");
            name = JOptionPane.showInputDialog(null, "Name:");
            artifact = JOptionPane.showInputDialog(null, "artifact:");

            if (company.trim() == "" || name.trim() == "" || artifact.trim() == "") {

                JOptionPane.showMessageDialog(null, "Creation failed", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Scanner scanner;
            try {
                scanner = new Scanner(script);

                String command = "", command2 = "";
                while (scanner.hasNext()) {
                    command += scanner.nextLine() + "\n";
                }

                scanner.close();
                scanner = new Scanner(script2);
                while (scanner.hasNext()) {
                    command2 += scanner.nextLine() + "\n";
                }

                scanner.close();

                command = command.replace("[folder]", folder.getAbsolutePath());
                command = command.replace("[company]", company);
                command = command.replace("[name]", name);
                command = command.replace("[artifact]", artifact);

                command2 = command2.replace("[folder]", folder.getAbsolutePath());
                command2 = command2.replace("[company]", company);
                command2 = command2.replace("[name]", name);
                command2 = command2.replace("[artifact]", artifact);

                FileWriter fw = new FileWriter(new File("src/functions/output/out.bat"));
                fw.write(command);
                fw.close();

                fw = new FileWriter(new File("src/functions/output/clone.bat"));
                fw.write(command2);
                fw.close();

                // Execute command
                Runtime run = Runtime.getRuntime();
                Process pr = run.exec(new File("src/functions/output/out.bat").getAbsolutePath());

                pr.waitFor();

                JOptionPane.showMessageDialog(null, "Maven project creation: status " +
                        pr.exitValue());

                pr = run.exec(new File("src/functions/output/clone.bat").getAbsolutePath());

                pr.waitFor();

                JOptionPane.showMessageDialog(null, "Engine import: status " +
                        pr.exitValue());

                String path = folder.getAbsolutePath() +
                        "\\" + artifact + "\\src\\main\\java\\com\\" + company
                        + "\\" + name;

                refactorAll(path + "\\Himawari-2d", "com." + company + "." + name,
                        folder.getAbsolutePath() + "\\" + artifact);

                JOptionPane.showMessageDialog(null, "Refactoring completed");

                File enginefolder = new File(path + "\\Himawari-2d\\Engine"),
                        assetsfolder = new File(path + "\\Himawari-2d\\Assets"),
                        mainfile = new File(path + "\\Himawari-2d\\Main.java");

                // Move the files
                Files.move(Paths.get(enginefolder.getAbsolutePath()), Paths.get(path +
                        "\\Engine"));

                Files.move(Paths.get(assetsfolder.getAbsolutePath()), Paths.get(path +
                        "\\Assets"));

                Files.move(Paths.get(mainfile.getAbsolutePath()), Paths.get(path +
                        "\\Main.java"));

                deleteDirectory(new File(path + "\\Himawari-2d"));
                deleteDirectory(new File(path + "\\App.java"));

                JOptionPane.showMessageDialog(null, "Project files organized");

                String pack = "com." + company + "." + name;
                // Refactor main file
                try {

                    Scanner s = new Scanner(new File(path + "\\Main.java"));

                    String contents = "";
                    while (s.hasNextLine()) {
                        contents += s.nextLine() + "\n";
                    }

                    contents = contents.replace("package", "package " + pack);
                    contents = contents.replace("import Engine", "import " + pack + ".Engine");
                    contents = contents.replace("import Assets", "import " + pack + ".Assets");

                    s.close();
                    fw = new FileWriter(new File(path + "\\Main.java"));
                    fw.write(contents);
                    fw.close();

                } catch (Exception ee) {

                }

            } catch (IOException | InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    private static void refactorAll(String path, String pack, String origin) {

        // Refactor engine files
        searchFolder(path + "\\Engine", pack);
        searchFolder(path + "\\Assets", pack);
        // Refactor pom.xml
        File pom = new File(origin + "\\pom.xml");
        Scanner s;
        try {

            s = new Scanner(pom);
            String contents = "";

            while (s.hasNextLine())
                contents += s.nextLine() + "\n";
            s.close();

            contents.replace("</dependencies>", "    <dependency>\n" +
                    "<groupId>com.googlecode.json-simple</groupId>\n" +
                    "<artifactId>json-simple</artifactId>\n" +
                    "<version>1.1</version>" +
                    "</dependency>" +
                    "</dependencies>\n");

            FileWriter fw = new FileWriter(pom);
            fw.write(contents);
            fw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void searchFolder(String folder, String pack) {

        File[] f = new File(folder).listFiles();
        for (File file : f) {

            if (file.getName().contains(".")) {

                if (file.getName().contains(".java")) {
                    try {

                        // Refactor this
                        Scanner s = new Scanner(file);
                        String contents = "";

                        while (s.hasNextLine())
                            contents += s.nextLine() + "\n";

                        s.close();

                        contents = contents.replace("import Engine", "import " + pack + ".Engine");
                        contents = contents.replace("import Assets", "import " + pack + ".Assets");
                        contents = contents.replace("package ", "package " + pack + ".");

                        FileWriter fw = new FileWriter(file);
                        fw.write(contents);
                        fw.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {

                searchFolder(folder + "\\" + file.getName(), pack);
            }
        }
    }

    private static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public static void OpenFile(File file) {

        try {

            File openfile = new File("src/functions/output/open_file.bat");
            FileWriter fw = new FileWriter(openfile);

            String command = file.getAbsolutePath();
            fw.write(command);
            fw.close();

            Runtime.getRuntime().exec(openfile.getAbsolutePath());

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, "Trouble opening file", "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
