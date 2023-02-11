
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.management.ThreadInfo;
import java.util.Scanner;
import java.util.function.Function;
import java.awt.Color;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import Components.MapEditor;

import java.awt.event.*;

public class Project extends JFrame implements KeyListener, ActionListener {

    public static Inspector inspector = new Inspector();
    public static File path, engineFiles, assetFiles;
    public static String projectName = "", disk = "C:";
    private File compiler;

    JMenu gamemenu = new JMenu("Game"), codeMenu = new JMenu("Code"), settingsMenu = new JMenu("Settings"),
            addResources = new JMenu("Manage Resources"),
            other = new JMenu("File"),
            build = new JMenu("Build Project"), git = new JMenu("Git");

    JMenuItem i0 = new JMenuItem("Run"), i1 = new JMenuItem("Open Game Code"),
            openFolder = new JMenuItem("Open game folder"),
            openRes = new JMenuItem("Open Resources Folder"),
            settings = new JMenuItem("Set default code editor"), buildSettings = new JMenuItem("Tweak build settings"),
            close = new JMenuItem("Close Project"), open = new JMenuItem("Open Himawari Store"),
            buildJar = new JMenuItem("Build .JAR File (recomended)"), buildExe = new JMenuItem("Build .EXE File"),
            commit = new JMenuItem("Commit to Git"), push = new JMenuItem("Push to GitHub"),
            pull = new JMenuItem("Pull from GitHub"), init = new JMenuItem("Initialize Git repository"),
            remote = new JMenuItem("Add remote repository"), branch = new JMenuItem("Change branch"),
            readdremote = new JMenuItem("Change remote repository url"),
            importLibrary = new JMenuItem("Import himawari libraries"), addDepend = new JMenuItem("Add dependencies"),
            newComp = new JMenuItem("Import scriptable components"), recompile = new JMenuItem("Recompile objects"),
            openPomFile = new JMenuItem("Open pom.xml"), plugin = new JMenuItem("Import plugin"), system = new JMenuItem("System & Preferences"),
            physics = new JMenuItem("Game physics menu"), input =  new JMenuItem("Game input icon");

    // Menu items to add resources
    JMenuItem addSprite = new JMenuItem("Add Image"), addMusic = new JMenuItem("Add Sound"),
            addFont = new JMenuItem("Add Font");

    JMenuBar bar = new JMenuBar();

    JTabbedPane functions = new JTabbedPane();

    JButton run = Style.GetStyledButton("LAUCH PROJECT"), website = Style.GetStyledButton("Website"),
            docs = Style.GetStyledButton("Documentation"),
            update = Style.GetStyledButton("Update"),
            quit = Style.GetStyledButton("QUIT");

    public static MapEditor preview;

    Project(File path) {

        Project.path = path;
        fetchProjectLocalConfig();
        Project.engineFiles = new File(path.getAbsolutePath() + "/src/main/java/" + projectName + "/Assets");
        this.compiler = new File(path.getParentFile().getAbsolutePath() + "/compile.bat");

        // Map preview
        JPanel editor = Functions.getMapEditor(engineFiles.getAbsolutePath(), new File(engineFiles.getAbsolutePath() + "/Rooms"));
        editor.setBounds(365, 0, editor.getWidth() - 10, editor.getHeight());
        preview = (MapEditor) editor;

        setTitle(getProjectTitle());
        setSize(1610, 930);

        // Find the main room and open it

        File[] rooms = new File(Project.engineFiles + "/Rooms").listFiles();
        if (rooms != null && rooms.length > 0) {

            preview.importMap(rooms[0].getAbsolutePath());
            preview.setSavePath(rooms[0]);
        }

        // Menu initialization

        git.add(init);
        git.add(remote);
        git.add(readdremote);
        git.addSeparator();

        git.add(commit);
        git.add(push);
        git.add(pull);
        git.addSeparator();

        git.add(branch);

        gamemenu.add(i0);
        gamemenu.add(openFolder);

        codeMenu.add(openPomFile);
        codeMenu.add(i1);
        codeMenu.add(recompile);

        settingsMenu.add(system);
        settingsMenu.add(input);
        settingsMenu.add(physics);
        settingsMenu.add(new JSeparator());
        settingsMenu.add(buildSettings);
        settingsMenu.add(settings);

        other.add(open);
        other.add(close);
        build.add(buildJar);
        build.add(buildExe);

        other.add(build);

        addResources.add(openRes);
        addResources.addSeparator();
        addResources.add(addDepend);
        addResources.add(importLibrary);
        addResources.add(newComp);
        addResources.addSeparator();
        addResources.add(addSprite);
        addResources.add(addMusic);
        addResources.add(addFont);
        addResources.add(new JSeparator());
        addResources.add(plugin);

        bar.add(other);
        bar.add(gamemenu);
        bar.add(codeMenu);
        bar.add(addResources);
        bar.add(git);
        bar.add(settingsMenu);

        openPomFile.addActionListener(this);
        plugin.addActionListener(this);
        recompile.addActionListener(this);
        addDepend.addActionListener(this);
        importLibrary.addActionListener(this);
        newComp.addActionListener(this);
        readdremote.addActionListener(this);
        init.addActionListener(this);
        remote.addActionListener(this);
        push.addActionListener(this);
        commit.addActionListener(this);
        pull.addActionListener(this);
        branch.addActionListener(this);
        buildSettings.addActionListener(this);
        openRes.addActionListener(this);
        openFolder.addActionListener(this);
        addSprite.addActionListener(this);
        addMusic.addActionListener(this);
        close.addActionListener(this);
        i0.addActionListener(this);
        i1.addActionListener(this);
        settings.addActionListener(this);
        addFont.addActionListener(this);

        setJMenuBar(bar);

        // User interface initization
        JPanel res = new ResourceExplorer();
        res.setBounds(5, 5, 350, 825);
        // res.setBackground(Style.FILE_EXPLORER_BACKGROUND);

        // File control panel

        JPanel control = new ProjectControl();
        functions.setBounds(1170, 5, 420, 825);

        functions.add("Project", control);

        // Inspector initialization
        functions.add("Inspector", inspector);

        // Footer
        JLabel version = new JLabel("Version: " + Main.version);
        // version.setForeground(Color.white);
        version.setBounds(705, 850, 100, 20);

        Border b = BorderFactory.createBevelBorder(BevelBorder.RAISED);

        website.setBounds(140 - 120, 840, 110, 40);
        docs.setBounds(255 - 120, 840, 130, 40);
        update.setBounds(400 - 120, 840, 110, 40);
        quit.setBounds(515 - 120, 840, 110, 40);
        run.setBounds(630 - 120, 840, 160, 40);

        quit.addActionListener(this);
        run.addActionListener(this);
        website.addActionListener(this);
        update.addActionListener(this);
        docs.addActionListener(this);

        // Bullshit initialization

        // Add footer
        add(website);
        add(version);
        add(update);
        add(docs);
        add(quit);
        add(run);

        // Add panels
        add(editor);
        add(functions);
        add(res);

        addKeyListener(this);
        setFocusable(false);
        setFocusTraversalKeysEnabled(false);

        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private String getProjectTitle() {

        return path.getName();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_F5) {

            // Run game
            runGame();
        } else if (e.getKeyCode() == KeyEvent.VK_F1) {

            // Open main code file
            try {

                openCodeFile("", "Main.java");

            } catch (Exception ee) {

                ee.printStackTrace();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == i0 || e.getSource() == run) {

            runGame();
        } else if (e.getSource() == i1) {

            try {

                openCodeFile("", "Main.java");

            } catch (Exception ee) {

                ee.printStackTrace();
            }

        } else if (e.getSource() == close) {

            setVisible(false);
            dispose();
        } else if (e.getSource() == settings) {
            String[] options = new String[] { "Visual Studio Code", "Vim" };
            String[] alias = new String[] { "code", "vim" };

            int response = JOptionPane.showOptionDialog(null, "What editor integration would you like you use",
                    "Editor",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);

            if (response == -1)
                return;

            // Write to settings
            Settings.open_alias = alias[response];
            Settings.updateFile();

        } else if (e.getSource() == quit) {

            setVisible(false);
            dispose();
        } else if (e.getSource() == website) {

            // Open website
            Functions.RunBatch("open_site.bat");
        } else if (e.getSource() == addSprite) {

            try {

                Functions.CopyFilesTo("Sprites");

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource() == addMusic) {

            try {

                Functions.CopyFilesTo("Sounds");

            } catch (IOException e1) {
                e1.printStackTrace();
            }

        } else if (e.getSource() == addFont) {

            try {

                Functions.CopyFilesTo("Fonts");

            } catch (IOException e1) {
                e1.printStackTrace();
            }

        } else if (e.getSource() == openFolder) {

            openFolder(path.getAbsolutePath());
        } else if (e.getSource() == openRes) {

            openFolder(engineFiles.getAbsolutePath());
        } else if (e.getSource() == buildSettings) {

            Functions.OpenPanelAsFrame(650, 400, "Build settings", new BuildSettings(), false);

        } else if (e.getSource() == init) {

            // Check for git
            File gitfolder = new File(Project.path + "\\..\\.git");
            if (gitfolder.exists()) {

                int opt = JOptionPane.showConfirmDialog(null,
                        "There is already a git repository initialized on this folder.\nDo you want to reinitialize it?",
                        "WARNING", JOptionPane.YES_NO_OPTION);

                if (opt == 1) {

                    return;
                }
            }

            Functions.Write(disk + "\ncd " + Project.path + "\\..\ngit init", "git/init.bat");
            Functions.RunBatchCmd("git/init.bat");

        } else if (e.getSource() == remote) {

            String link = JOptionPane.showInputDialog(null,
                    "Input the link to your GitHub repository (.git is optional)", "Git Connection");

            if (link == null) {
                return;
            }

            if (link.length() <= 4) {

                JOptionPane.showMessageDialog(null, "Invalid link", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (link.substring(link.length() - 4, link.length()) != ".git") {

                link += ".git";
            }

            Functions.Write(disk + "\ncd " + Project.path + "\\..\ngit remote add origin " + link, "git/remote.bat");
            Functions.RunBatchCmd("git/remote.bat");
        } else if (e.getSource() == commit) {

            String commit = JOptionPane.showInputDialog(null,
                    "Commit message", "Commit");

            if (commit == null) {
                return;
            }

            Functions.Write(disk + "\ncd " + Project.path + "\\..\ngit add .\ngit commit -m\"" + commit + "\"",
                    "git/commit.bat");
            Functions.RunBatchCmd("git/commit.bat");

        } else if (e.getSource() == push) {

            String branch = JOptionPane.showInputDialog(null,
                    "Input the branch you want to push to", "master");

            Functions.Write(disk + "\ncd " + Project.path + "\\..\ngit add .\ngit push origin " + branch,
                    "git/push.bat");
            Functions.RunBatchCmd("git/push.bat");

        } else if (e.getSource() == pull) {

            String branch = JOptionPane.showInputDialog(null,
                    "Input the branch you want to pull from", "master");

            Functions.Write(disk + "\ncd " + Project.path + "\\..\ngit add .\ngit pull origin " + branch,
                    "git/pull.bat");
            Functions.RunBatchCmd("git/pull.bat");

        } else if (e.getSource() == branch) {

            String branch = JOptionPane.showInputDialog(null,
                    "Input the branch you want to checkout", "master");

            if (branch == null) {
                return;
            }

            Functions.Write(disk + "\ncd " + Project.path + "\\..\ngit add .\ngit checkout \"" + branch + "\"",
                    "git/branch.bat");
            Functions.RunBatchCmd("git/branch.bat");
        } else if (e.getSource() == readdremote) {

            String link = JOptionPane.showInputDialog(null,
                    "Input the link to your new GitHub repository (.git is optional)", "Git Connection");

            if (link == null) {
                return;
            }

            if (link.length() <= 4) {

                JOptionPane.showMessageDialog(null, "Invalid link", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (link.substring(link.length() - 4, link.length()) != ".git") {

                link += ".git";
            }

            Functions.Write(disk + "\ncd " + Project.path + "\\..\ngit remote set-url origin " + link,
                    "git/re-remote.bat");
            Functions.RunBatchCmd("git/re-remote.bat");
        } else if (e.getSource() == addDepend) {

            JFrame frame = new JFrame();

            JTextArea text = new JTextArea();
            text.setBounds(0, 0, 400, 200);
            JScrollPane scroll = new JScrollPane(text,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

            JButton depen = Style.GetStyledButton("Append Dependencies");
            depen.setBounds(0, 312, 367, 30);

            frame.add(depen);
            frame.add(scroll);

            depen.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    Project.addDependendyString(text.getText());
                    frame.setVisible(false);
                    frame.dispose();
                }

            });

            frame.setTitle("Input");
            frame.setSize(400, 400);
            frame.setVisible(true);
            frame.setLayout(null);
        } else if (e.getSource() == importLibrary) {

            new LibraryManager();
        } else if (e.getSource() == recompile) {

            Functions.Write(disk + "\ncd " + path.getAbsolutePath() + "\nmvn compiler:compile", "recompile_objs.bat");
            Functions.RunBatchCmd("recompile_objs.bat");
        } else if (e.getSource() == openPomFile) {

            Functions.OpenFile(new File(path + "\\pom.xml"));
        } else if (e.getSource() == plugin) {

            new PluginManager();
        }
    }

    private void openFolder(String path) {

        String command = "explorer " + path;
        Functions.WriteAndRun(command, "open_explorer.bat");
    }

    public void openCodeFile(String filePath, String file) throws Exception {

        File f = new File("src/functions/output/open_code.bat");
        FileWriter fw = new FileWriter(f);

        String command = disk + " \n cd " + compiler.getParentFile().getParentFile().getAbsolutePath()
                + "\\src\\main\\java\\" + projectName + "\\" + filePath + "\n" + Settings.open_alias
                + " Main.java";
        fw.write(command);
        fw.close();

        Functions.RunBatchCmd("output\\" + f.getName());
    }

    public static void addDependendyString(String depen) {

        File pom = new File(Project.path.getAbsolutePath() + "\\pom.xml");

        try {

            Scanner s = new Scanner(pom);

            String newText = "";
            while (s.hasNext()) {

                String line = s.nextLine();
                if (line.contains("<dependencies>"))
                    line = line.replace("<dependencies>", "<dependencies>\n" + depen);

                newText += line + "\n";
            }

            s.close();
            FileWriter fw = new FileWriter(pom);
            fw.write(newText);
            fw.close();

            JOptionPane.showMessageDialog(null, "pom.xml file was updated successfully", "SUCCESS",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e1) {

            e1.printStackTrace();
        }
    }

    public void runGame() {

        try {

            // Run compiler

            String command = disk + " \n cd " + compiler.getParentFile().getAbsolutePath() + "\ncmd /c start \"\" "
                    + compiler.getAbsolutePath();

            File c = new File("src/functions/output/run.bat");
            FileWriter fw = new FileWriter(c);
            fw.write(command);
            fw.close();

            Process p = Runtime.getRuntime().exec(c.getAbsolutePath());
            p.waitFor();

        } catch (IOException e1) {

            JOptionPane.showMessageDialog(null, "Error executing Scripts", "ERROR", JOptionPane.ERROR_MESSAGE);
            e1.printStackTrace();
        } catch (InterruptedException e1) {

            e1.printStackTrace();
        }

    }

    private void fetchProjectLocalConfig() {

        String text;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Get folder structure from pom.xml
        try {

            DocumentBuilder builder = factory.newDocumentBuilder();
            text = Functions.getFileContents(new File(Project.path + "\\pom.xml"));

            // get the xml object
            StringBuilder xmlBuilder = new StringBuilder();
            xmlBuilder.append(text);
            ByteArrayInputStream input = new ByteArrayInputStream(
                    xmlBuilder.toString().getBytes("UTF-8"));

            Document doc = builder.parse(input);
            Element root = doc.getDocumentElement();

            Node name = root.getElementsByTagName("groupId").item(0);
            Project.projectName = name.getTextContent().replace(".", "/");

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "No pom.xml file was found, various errors might be thrown", "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SAXException | IOException | ParserConfigurationException e) {
            JOptionPane.showMessageDialog(null, "There seems to be an error within your pom.xml file", "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        // Get local harddrive project partition
        // D:\TIAGO\school\Estagio RFA
        Project.disk = Project.path.getAbsolutePath().split(":")[0] + ":";
    }
}
