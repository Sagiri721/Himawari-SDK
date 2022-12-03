
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.awt.Color;
import java.awt.Font;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import Components.MapEditor;

import java.awt.event.*;

public class Project extends JFrame implements KeyListener, ActionListener {

    JPanel res = new JPanel(), control;

    public static Inspector inspector = new Inspector();
    public static File path, engineFiles;
    public static String projectName = "com/com/nonamerhythmgame";
    private File compiler;

    JMenu gamemenu = new JMenu("Game"), codeMenu = new JMenu("Code"), settingsMenu = new JMenu("Settings"),
            addResources = new JMenu("Manage Resources"),
            other = new JMenu("File");
    JMenuItem i0 = new JMenuItem("Run"), i1 = new JMenuItem("Open Game Code"),
            openFolder = new JMenuItem("Open game folder"),
            openRes = new JMenuItem("Open Resources Folder"),
            settings = new JMenuItem("Set default code editor"),
            close = new JMenuItem("Close Project"), open = new JMenuItem("Open Himawari Store");

    // Menu items to add resources
    JMenuItem addSprite = new JMenuItem("Add Image"), addMusic = new JMenuItem("Add Sound"),
            addFont = new JMenuItem("Add Font");

    JMenuBar bar = new JMenuBar();

    JTabbedPane functions = new JTabbedPane();

    JButton newObject = new JButton("New Object"), newAlert = new JButton("Create Alert"),
            run = new JButton("LAUCH PROJECT"), website = new JButton("Website"), docs = new JButton("Documentation"),
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
        settingsMenu.add(settings);
        other.add(open);
        other.add(close);

        addResources.add(openRes);
        addResources.add(addSprite);
        addResources.add(addMusic);
        addResources.add(addFont);

        bar.add(other);
        bar.add(gamemenu);
        bar.add(codeMenu);
        bar.add(addResources);
        bar.add(settingsMenu);

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
        res = new ResourceExplorer();
        res.setBounds(5, 5, 350, 825);
        res.setBackground(Style.FILE_EXPLORER_BACKGROUND);

        // File control panel

        control = new JPanel();
        functions.setBounds(1170, 5, 420, 825);
        control.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        control.setLayout(null);

        functions.add("Project", control);

        JLabel label0 = new JLabel("Control Panel");
        label0.setFont(new Font("Monospace", Font.PLAIN, 22));
        label0.setBounds(5, 5, 200, 30);
        control.add(label0);

        JLabel create = new JLabel("Create", SwingConstants.CENTER);
        create.setBounds(5, 35, 160, 20);

        newObject.setBounds(5, 55, 160, 20);
        newObject.addActionListener(this);

        newAlert.setBounds(5, 80, 160, 20);
        newAlert.addActionListener(this);

        control.add(create);
        control.add(newObject);
        control.add(newAlert);
        control.setLayout(null);

        control.setBackground(Color.GRAY);

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        } else if (e.getSource() == newObject) {

            try {

                Scanner s = new Scanner(new File("src/templates/Object.txt"));

                String text = "";
                while (s.hasNextLine())
                    text += s.nextLine() + "\n";

                String object_name = JOptionPane.showInputDialog(null, "Object name?", "Create Object",
                        JOptionPane.INFORMATION_MESSAGE);
                text = text.replace("[Object]", object_name);

                Integer option = JOptionPane.showConfirmDialog(null, "Go to advanced configuration panel?",
                        "Advanced Configuration", JOptionPane.YES_NO_OPTION);
                // No: 1 Yes: 0

                if (option == 0) {
                    // Do some more stuff
                }

                File newFile = new File(path + "/src/main/java/Assets/Objects/" + object_name + ".java");
                FileWriter fw = new FileWriter(newFile);

                fw.write(text);
                fw.close();

                JOptionPane.showMessageDialog(null, "Object Created");

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
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
