import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Window extends JFrame implements ActionListener {

    JMenu menu = new JMenu("Project Manager Tool"), mapmenu = new JMenu("Map Editor Tool"),
            account = new JMenu("Account");
    JMenuItem i0 = new JMenuItem("New Map"), i1 = new JMenuItem("Load Map"),
            createP = new JMenuItem("Create Project"), loadP = new JMenuItem("Load Project"),
            username = new JMenuItem("Change Username");
    JMenuBar mb = new JMenuBar();

    public static TileSet tileset;
    public static Map map;

    public static boolean internet;

    Image netIcon = new ImageIcon("src/res/internet.png").getImage().getScaledInstance(100, 100,
            java.awt.Image.SCALE_SMOOTH);
    JLabel label = new JLabel(new ImageIcon(netIcon));

    // User Data
    JLabel name_label = new JLabel("Hello there " + Settings.username, SwingConstants.CENTER);

    JFrame window;

    Window() {

        window = this;
        internet = InternetConnection();

        setTitle("Himawari tools");
        setSize(700, 600);
        setResizable(false);

        DefineComponents();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setVisible(true);

        getContentPane().setBackground(Color.white);
    }

    private void DefineComponents() {
        mapmenu.add(i0);
        mapmenu.add(i1);

        menu.add(createP);
        menu.add(loadP);

        account.add(username);

        JPanel user = new JPanel();
        user.setLayout(null);
        user.setBounds(0, 0, 700, 30);

        name_label.setBounds(5, 5, 700, 20);
        name_label.setForeground(Color.black);
        user.add(name_label);

        label.setBounds(5, 50, 100, 100);

        i0.addActionListener(this);
        i1.addActionListener(this);
        createP.addActionListener(this);
        loadP.addActionListener(this);
        username.addActionListener(this);

        mb.add(menu);
        mb.add(mapmenu);
        mb.add(account);

        setJMenuBar(mb);

        add(user);
        add(label);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == i0) {

            new CreateMap(0);
        } else if (e.getSource() == username) {

            String name = JOptionPane.showInputDialog(null, "What's your username?");

            Settings.username = name;
            Settings.updateFile();

            name_label.setText("Hello there " + Settings.username);

        } else if (e.getSource() == createP) {

            if (internet) {

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

                        JOptionPane.showMessageDialog(null, "Creation failed", "ERROR", JOptionPane.ERROR_MESSAGE);
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

                        JOptionPane.showMessageDialog(null, "Maven project creation: status " + pr.exitValue());

                        pr = run.exec(new File("src/functions/output/clone.bat").getAbsolutePath());

                        pr.waitFor();

                        JOptionPane.showMessageDialog(null, "Engine import: status " + pr.exitValue());

                        String path = folder.getAbsolutePath() + "\\" + artifact + "\\src\\main\\java\\com\\" + company
                                + "\\" + name;

                        refactorAll(path + "\\Himawari-2d", "com." + company + "." + name,
                                folder.getAbsolutePath() + "\\" + artifact);

                        JOptionPane.showMessageDialog(null, "Refactoring completed");

                        File enginefolder = new File(path + "\\Himawari-2d\\Engine"),
                                assetsfolder = new File(path + "\\Himawari-2d\\Assets"),
                                mainfile = new File(path + "\\Himawari-2d\\Main.java");

                        // Move the files
                        Files.move(Paths.get(enginefolder.getAbsolutePath()), Paths.get(path + "\\Engine"));

                        Files.move(Paths.get(assetsfolder.getAbsolutePath()), Paths.get(path + "\\Assets"));

                        Files.move(Paths.get(mainfile.getAbsolutePath()), Paths.get(path + "\\Main.java"));

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

            } else {

                JOptionPane.showMessageDialog(null, "No internet connection", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        } else {

            if (e.getSource() == loadP) {

                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int result = fc.showDialog(this, "Open project");
                if (result == JFileChooser.APPROVE_OPTION) {

                    // Search for a valid project directory
                    File folder = fc.getSelectedFile();

                    if (!new File(folder.getAbsolutePath() + "/pom.xml").exists()) {

                        JOptionPane.showMessageDialog(null, "Inavalid directory, there is no pom.xml", "ERROR",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Open the project
                    new Project(folder);
                }
            }
        }
    }

    private boolean InternetConnection() {

        try {
            URL url = new URL("https://www.google.com");
            URLConnection conn = url.openConnection();
            conn.connect();

            return true;

        } catch (MalformedURLException e) {

            return false;
        } catch (IOException e) {

            return false;
        }

    }

    public void refactorAll(String path, String pack, String origin) {

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

    public void searchFolder(String folder, String pack) {

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

    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}
