
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Color;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import Components.MapEditor;

import java.awt.event.*;

public class Project extends JFrame implements KeyListener, ActionListener {

    public static Inspector inspector = new Inspector();
    public static File path, engineFiles;
    public static String projectName = "com/com/nonamerhythmgame";
    private File compiler;

    JMenu gamemenu = new JMenu("Game"), codeMenu = new JMenu("Code"), settingsMenu = new JMenu("Settings"),
            addResources = new JMenu("Manage Resources"),
            other = new JMenu("File"),
            build = new JMenu("Build Project");
    JMenuItem i0 = new JMenuItem("Run"), i1 = new JMenuItem("Open Game Code"),
            openFolder = new JMenuItem("Open game folder"),
            openRes = new JMenuItem("Open Resources Folder"),
            settings = new JMenuItem("Set default code editor"), buildSettings = new JMenuItem("Tweak build settings"),
            close = new JMenuItem("Close Project"), open = new JMenuItem("Open Himawari Store"),
            buildJar = new JMenuItem("Build .JAR File (recomended)"), buildExe = new JMenuItem("Build .EXE File");

    // Menu items to add resources
    JMenuItem addSprite = new JMenuItem("Add Image"), addMusic = new JMenuItem("Add Sound"),
            addFont = new JMenuItem("Add Font");

    JMenuBar bar = new JMenuBar();

    JTabbedPane functions = new JTabbedPane();

    JButton run = new JButton("LAUCH PROJECT"), website = new JButton("Website"), docs = new JButton("Documentation"),
            update = new JButton("Update"),
            quit = new JButton("QUIT");

    public static MapEditor preview;

    Project(File path) {

        Project.path = path;
        Project.engineFiles = new File(path.getAbsolutePath() + "/src/main/java/" + projectName + "/Assets");
        this.compiler = new File(path.getAbsolutePath() + "/../compile.bat");

        // Map preview
        JPanel editor = Functions.getMapEditor(engineFiles.getAbsolutePath());
        editor.setBounds(365, 0, editor.getWidth() - 10, editor.getHeight());
        preview = (MapEditor) editor;

        setTitle(getProjectTitle());
        setSize(1610, 930);
        getContentPane().setBackground(Color.black);

        // Menu initialization

        gamemenu.add(i0);
        gamemenu.add(openFolder);
        codeMenu.add(i1);
        settingsMenu.add(buildSettings);
        settingsMenu.add(settings);
        other.add(open);
        other.add(close);
        build.add(buildJar);
        build.add(buildExe);

        other.add(build);

        addResources.add(openRes);
        addResources.add(addSprite);
        addResources.add(addMusic);
        addResources.add(addFont);

        bar.add(other);
        bar.add(gamemenu);
        bar.add(codeMenu);
        bar.add(addResources);
        bar.add(settingsMenu);

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
        res.setBackground(Style.FILE_EXPLORER_BACKGROUND);

        // File control panel

        JPanel control = new ProjectControl();
        functions.setBounds(1170, 5, 420, 825);

        functions.add("Project", control);

        // Inspector initialization
        functions.add("Inspector", inspector);

        // Footer
        JLabel version = new JLabel("Version: " + Main.version);
        version.setForeground(Color.white);
        version.setBounds(5, 840, 100, 20);

        Border b = BorderFactory.createBevelBorder(BevelBorder.RAISED);

        website.setBounds(140, 840, 110, 20);
        website.setBackground(Color.gray);
        website.setForeground(Color.white);
        website.setBorder(b);

        docs.setBounds(255, 840, 110, 20);
        docs.setBackground(Color.gray);
        docs.setForeground(Color.white);
        docs.setBorder(b);

        update.setBounds(400, 840, 110, 20);
        update.setBackground(Color.gray);
        update.setForeground(Color.white);
        update.setBorder(b);

        quit.setBounds(515, 840, 110, 20);
        quit.setBackground(Color.red);
        quit.setForeground(Color.black);
        quit.setBorder(b);

        run.setBounds(630, 840, 110, 20);
        run.setBackground(Color.green);
        run.setForeground(Color.black);
        run.setBorder(b);

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

            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int option = fc.showDialog(null, "Set app");

            if (option == JFileChooser.APPROVE_OPTION) {

                // Write to settings
                Settings.file_open_definition = fc.getSelectedFile().getAbsolutePath();
                Settings.updateFile();
            }
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
        }
    }

    private void openFolder(String path) {

        String command = "explorer " + path;
        Functions.WriteAndRun(command, "open_explorer.bat");
    }

    public void openCodeFile(String filePath, String file) throws Exception {

        File f = new File("src/functions/output/open_code.bat");
        FileWriter fw = new FileWriter(f);

        String command = "D: \n cd " + compiler.getParentFile().getParentFile().getAbsolutePath()
                + "\\src\\main\\java" + filePath + "\n" + Settings.file_open_definition + " + file";
        fw.write(command);
        fw.close();

        Runtime.getRuntime().exec(f.getAbsolutePath());
    }

    public void runGame() {

        if (compiler.exists()) {
            try {

                // Run compiler

                String command = "D: \n cd " + compiler.getParentFile().getAbsolutePath() + "\ncmd /c start \"\" "
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

        } else {

            JOptionPane.showMessageDialog(null, "No compiler found!", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}
