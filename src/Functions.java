
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.Desktop;

import Components.Structs.EngineData;
import Components.Structs.Map;
import Components.Structs.ObjectData;

public class Functions {

    // No mp3 / ogg support yet
    private static String[][] allowedFileExtensions = {
            { "png", "jpg", "jpeg", "jfif", "avif" }, // Images
            { "wav", "mp3", "ogg" }, // Sound
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

    public static String getFileContentsLined(File file) throws FileNotFoundException {
        Scanner s = new Scanner(file);
        String filetext = "";

        while (s.hasNextLine())
            filetext += s.nextLine() + "\n";

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

    public static void openMapEditor(Map map, File savePath) {

        JFrame mapEditor = new JFrame();
        mapEditor.setSize(800, 830);
        mapEditor.setTitle("Map editing tool");

        MapEditor editor = new MapEditor(map, Optional.<File>empty(), savePath);
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

        MapEditor editor = new MapEditor(map, Optional.<File>of(mapPath), mapPath);
        editor.importMap(mapPath.getAbsolutePath());

        editor.setBounds(0, 0, 800, 830);

        mapEditor.setLayout(null);
        mapEditor.setLayout(null);
        mapEditor.add(editor);
        mapEditor.setResizable(false);
        mapEditor.setVisible(true);
    }

    public static JPanel getMapEditor(String path, File savePath) {

        MapEditor editor = new MapEditor(new Map(null, 100, 100), Optional.<File>empty(), savePath);
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

    public static void startTheme(){

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
    }

    /**
     * 
     * CREATE PROJECT FUNCTION AND UTILS
     * 
     */

    public static void CreateProject(ProjectWizard pw, String clone) {

        long startMilis = System.currentTimeMillis();

        pw.progressReset();
        pw.updateProgress("Project creation started...", 0);

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
                
                String disk = folder.getAbsolutePath().substring(0, 1);
                command = command.replace("[folder]", folder.getAbsolutePath());
                command = command.replace("[company]", company);
                command = command.replace("[name]", name);
                command = command.replace("[artifact]", artifact);
                command = command.replace("[disk]", disk);

                command2 = command2.replace("[folder]", folder.getAbsolutePath());
                command2 = command2.replace("[company]", company);
                command2 = command2.replace("[name]", name);
                command2 = command2.replace("[artifact]", artifact);
                command2 = command2.replace("[disk]", disk);

                clone = clone == null ? "empty https://github.com/Sagiri721/Himawari-2d.git" : clone;
                command2 = command2.replace("[temp]", clone);

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

                pw.updateProgress("Maven project creation status: " + pr.exitValue(), 20);

                //Adapt disk
                pr = run.exec(new File("src/functions/output/clone.bat").getAbsolutePath());

                pr.waitFor();

                pw.updateProgress("Engine code import status: " + pr.exitValue(), 20);

                String path = folder.getAbsolutePath() +
                        "\\" + artifact + "\\src\\main\\java\\com\\" + company
                        + "\\" + name;

                String folderName = clone.substring(clone.lastIndexOf("/") + 1, clone.lastIndexOf("."));
                System.out.println(folderName);

                refactorAll(path + "\\" + folderName, "com." + company + "." + name,
                        folder.getAbsolutePath() + "\\" + artifact);

                File enginefolder = new File(path + "\\" + folderName + "\\Engine"),
                        assetsfolder = new File(path + "\\" + folderName + "\\Assets"),
                        mainfile = new File(path + "\\" + folderName + "\\Main.java");

                // Move the files
                Files.move(Paths.get(enginefolder.getAbsolutePath()), Paths.get(path +
                        "\\Engine"));

                Files.move(Paths.get(assetsfolder.getAbsolutePath()), Paths.get(path +
                        "\\Assets"));

                Files.move(Paths.get(mainfile.getAbsolutePath()), Paths.get(path +
                        "\\Main.java"));

                deleteDirectory(new File(path + "\\" + folderName));
                deleteDirectory(new File(path + "\\App.java"));

                pw.updateProgress("Project files reorganized", 20);

                String pack = "com." + company + "." + name;
                // Refactor main file
                try {

                    Scanner s = new Scanner(new File(path + "\\Main.java"));

                    String contents = "";
                    while (s.hasNextLine()) {
                        contents += s.nextLine() + "\n";
                    }

                    contents = "package "+pack+";\n" + contents;
                    contents = contents.replace("import Engine", "import " + pack + ".Engine");
                    contents = contents.replace("import Assets", "import " + pack + ".Assets");

                    s.close();
                    fw = new FileWriter(new File(path + "\\Main.java"));
                    fw.write(contents);
                    fw.close();

                } catch (Exception ee) {
                }

                pw.updateProgress("Engine files refactored", 20);

                // Create the utils

                File changer = new File(folder.getAbsolutePath() + "/Changer.java");
                File compile = new File(folder.getAbsolutePath() + "/compile.bat");
                File export = new File(folder.getAbsolutePath() + "/export_references.bat");

                FileWriter w = new FileWriter(changer);

                String text = getFileContents(new File("src\\templates\\Changer.txt"));
                text = text.replace("[package]", pack.replace(".", "\\\\"));
                text = text.replace("[artifact]", artifact);
                w.write(text);

                w.close();
                w = new FileWriter(compile);

                text = getFileContentsLined(new File("src\\templates\\Compile.txt"));
                text = text.replace("[artifact]", artifact);
                text = text.replace("[pack]", "com." + company + "." + name + ".");
                
                w.write(text);
                w.close();
                
                w = new FileWriter(export);
                
                text = getFileContentsLined(new File("src\\templates\\ExportRefs.txt"));
                text = text.replace("[artifact]", artifact);
                text = text.replace("[pack]", "com." + company + "." + name + ".");
                
                w.write(text);
                w.close();
                
                pw.updateProgress("Project util files created", 20);

                JOptionPane.showMessageDialog(null, "Project " + artifact + " was created in " + (System.currentTimeMillis() - startMilis) / 1000 + " seconds", "Project created",
                        JOptionPane.INFORMATION_MESSAGE);

                System.out.println(changer.getAbsolutePath());

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

            // Add maven dependencies
            contents = contents.replace("<maven.compiler.source>1.7</maven.compiler.source>", "<maven.compiler.source>1.8</maven.compiler.source>");
            contents = contents.replace("<maven.compiler.target>1.7</maven.compiler.target>", "<maven.compiler.target>1.8</maven.compiler.target>");

            contents = contents.replace("</dependencies>", "<dependency>\n" +
                    "<groupId>com.googlecode.json-simple</groupId>\n" +
                    "<artifactId>json-simple</artifactId>\n" +
                    "<version>1.1</version>\n" +
                    "</dependency><dependency>\n" +
                    "<groupId>javazoom</groupId>\n" +
                    "<artifactId>jlayer</artifactId>\n" +
                    "<version>1.0.1</version>\n" +
                    "</dependency>\n" +
                    "<dependency>\n" +
                    "<groupId>com.google.guava</groupId>\n" +
                    "<artifactId>guava</artifactId>\n" +
                    "<version>31.0.1-jre</version>\n" +
                    "</dependency>\n" +
                    "<dependency>\n" +
                    "<groupId>org.xerial</groupId>\n" +
                    "<artifactId>sqlite-jdbc</artifactId>\n" +
                    "<version>3.40.0.0</version>\n" +
                    "</dependency>\n" +
                    "<dependency>\n" +
                    "<groupId>org.openjfx</groupId>\n" +
                    "<artifactId>javafx-controls</artifactId>\n" +
                    "<version>15.0.1</version>\n" +
                    "</dependency>\n" +
                    "<dependency>"+
                    "<groupId>com.google.code.gson</groupId>"+
                    "<artifactId>gson</artifactId>"+
                    "<version>2.10.1</version>"+
                    "</dependency>"+
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
        if(f == null) {

            JOptionPane.showMessageDialog(null, "Couldn't read the folder " + folder, "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
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

                        contents = contents.replace("System.getProperty(\"user.dir\") + \"/src/main/java", "System.getProperty(\"user.dir\") + \"/src/main/java/" + pack.replace(".", "/"));

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

    public static String getPOM() throws FileNotFoundException{

        File pom = new File(Project.path + "\\pom.xml");
        Scanner s = new Scanner(pom);

        String text = "";
        while(s.hasNextLine()) text+= s.nextLine() + "\n";

        s.close();
        return text;
    }

    public static void writePOM(String text) throws IOException{

        File pom = new File(Project.path + "\\pom.xml");
        FileWriter fw = new FileWriter(pom);

        fw.write(text);

        fw.close();
    }

    public static void addRecentFolder(String path) {

        File file = new File("src/data/recentProjects.json");
        Gson g = new Gson();

        try {
            
            Scanner s = new Scanner(file);
            String json = "";
            while (s.hasNextLine())
                json += s.nextLine();

            s.close();
            
            JsonArray l = (g.fromJson(json, JsonObject.class)).get("recents").getAsJsonArray();
            l.add(path);

            FileWriter fw = new FileWriter(file);
            fw.write("{\"recents\": "+l.toString()+"}");
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getWindowHTML(String view) {

        try {
         
            Scanner s = new Scanner(new File("src/windows/" + view));
            String html = "";

            while (s.hasNext()) html += s.nextLine();
            s.close();

            return html;
        } catch (Exception e) {
            
            return "<html>Failed to retrive HTML<html>";
        }
    }

    public static JScrollPane toScrollPane(JEditorPane pane, int x, int y, int w, int h) {

        pane.setEnabled(true);
        pane.setFocusable(true);

        pane.addHyperlinkListener(e -> {

            if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                Desktop desktop = Desktop.getDesktop();
                try {
                  desktop.browse(e.getURL().toURI());
                } catch (Exception ex) {
                  ex.printStackTrace();
                }
              }
        });

        pane.setEditable(false);
        JScrollPane js = new JScrollPane(pane);
        js.setBounds(x, y, w, h);

        return js;
    }

    public static void addRecentProject(File project){

        Gson g = new Gson();

        try(Reader reader = Files.newBufferedReader(Paths.get("src/data/recentProjects.json"))){

            String[] recentArray = g.fromJson(reader, Recent.class).recents;
            if (recentArray.length >= 10) {

                for(int i = recentArray.length-1; i > 0; i--) { recentArray[i] = recentArray[i-1]; }
                recentArray[0] = project.getAbsolutePath();
            }else {

                List<String> l = Arrays.asList(recentArray);
                l = new ArrayList<String>(l);

                l.add(0, project.getAbsolutePath());
                recentArray = l.toArray(new String[l.size()]);
            }

            //Write recents out
            Recent r = new Recent();
            r.recents = recentArray;
            //g.toJson(r, new FileWriter(new File("src/data/recentProjects.json")));
            String json = g.toJson(r);
            FileWriter fw = new FileWriter(new File("src/data/recentProjects.json"));

            fw.write(json);
            fw.close();

        } catch(IOException e) {

        }
    }

    public static class Recent { public Recent(){} public String[] recents;}

    public static String[] getRecentProjects(){

        Gson g = new Gson();
        try(Reader reader = Files.newBufferedReader(Paths.get("src/data/recentProjects.json"))){ 

            return g.fromJson(reader, Recent.class).recents;

        }catch(IOException e){
            
            return null;
        }
    }

    public static ProjectWizard.Template[] getTemplates(){

        Gson g = new Gson();

        try(Reader reader = Files.newBufferedReader(Paths.get("src/data/Templates.json"))){

            Projects projs = g.fromJson(reader, Projects.class);
            ProjectWizard.Template[] templates = new ProjectWizard.Template[projs.projects.length];

            for (int i = 0; i < templates.length; i++) {
                
                templates[i] = new ProjectWizard.Template(projs.projects[i].name,
                projs.projects[i].image,
                projs.projects[i].desc,
                projs.projects[i].type,
                projs.projects[i].clone);
            }

            return templates;

        }catch (IOException e) {

            JOptionPane.showMessageDialog(null, "Error fetching templates", "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private class Projects {public Template[] projects;}

    private class Template{

        public String name;
        public String image;
        public String desc;
        public String type;
        public String clone;
    }

    public static void placeComponentWithLabel(String label, JComponent comp, JPanel pane, int x, int y, int w){

        JLabel l = new JLabel(label);
        int stringWidth = l.getFontMetrics(l.getFont()).stringWidth(label);

        l.setBounds(x, y, stringWidth, 20);

        w = w == -1 ? comp.getWidth() - stringWidth - 10 : w;
        comp.setBounds(stringWidth + 10 + x, y, w, 20);

        pane.add(comp);
        pane.add(l);
    }

    public static void importGameMetaData(){

        File exporterFile = Project.exportFile;
        if(exporterFile.exists()) {

            Runtime run = Runtime.getRuntime();
            try {
            
                Process p = run.exec(exporterFile.getAbsolutePath());
                p.waitFor();
                
                Gson gson = new Gson();
                ObjectData[] objs = gson.fromJson(new FileReader(Project.engineAssetFiles.getAbsolutePath() + "/MyObjectDataDump.json"), ObjectData[].class);
                EngineData data = gson.fromJson(new FileReader(Project.engineAssetFiles.getAbsolutePath() + "/MyGameData.json"), EngineData.class);

                Project.objectInformation = objs;
                Project.gameData = data;
            
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
            
                JOptionPane.showMessageDialog(null, "There was a problem exporting your project's references", "ERROR", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }else {

            JOptionPane.showMessageDialog(null, "No file capable of exporting the project's references was found", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void showError(String message) {

        JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    public static void showMessage(String message) {

        JOptionPane.showMessageDialog(null, message, "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
    }

    public static String ask(String question){

        return JOptionPane.showInputDialog(null, question);
    }

    public static void WriteObjectFile(String contents, String name) throws IOException {

        File f;
        while (true){

            f = new File(Project.engineFiles + "/Objects/" + name);
    
            if(f.exists()){
    
                Functions.showError("Object " + name + " already exists! Please input a new name");
                name = ask("New name?");

                if(!name.toLowerCase().contains(".java")) name = name += ".java";
                continue;
            }

            break;
        }

        FileWriter fw = new FileWriter(f);        

        fw.write(contents);
        fw.close();
    }
}
