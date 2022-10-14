import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import java.awt.event.*;

public class Project extends JFrame implements KeyListener, ActionListener {

    String[] options = { "Sprites", "Sound", "Objects", "Maps" };
    JComboBox<String> box = new JComboBox<String>(options);
    JPanel res = new JPanel();

    JList<String> list = new JList<>();

    private File path, engineFiles;
    private File compiler;

    JMenu gamemenu = new JMenu("Game"), codeMenu = new JMenu("Code"), settingsMenu = new JMenu("Settings"),
            other = new JMenu("Misc");
    JMenuItem i0 = new JMenuItem("Run (f5)"), i1 = new JMenuItem("Open Game Code (f1)"),
            settings = new JMenuItem("Set default code editor"),
            close = new JMenuItem("Close Project"), open = new JMenuItem("Open Himawari Store");
    JMenuBar bar = new JMenuBar();

    JTabbedPane functions = new JTabbedPane();
    JPanel control;

    JButton newObject = new JButton("New Object"), newAlert = new JButton("Create Alert"),
            run = new JButton("LAUCH PROJECT");
    JButton website = new JButton("Website"), docs = new JButton("Documentation"), update = new JButton("Update"),
            quit = new JButton("QUIT");

    JButton openB = new JButton("Open file");
    JButton del = new JButton("Delete file");

    Project(File path) {

        this.path = path;
        this.engineFiles = new File(path.getAbsolutePath() + "/src/main/java/Assets");
        this.compiler = new File(path.getAbsolutePath() + "/../compile.bat");

        setTitle(getProjectTitle());
        setSize(800, 620);
        getContentPane().setBackground(Color.black);

        // Menu initialization

        gamemenu.add(i0);
        codeMenu.add(i1);
        settingsMenu.add(settings);
        other.add(open);
        other.add(close);

        bar.add(other);
        bar.add(gamemenu);
        bar.add(codeMenu);
        bar.add(settingsMenu);

        close.addActionListener(this);
        i0.addActionListener(this);
        i1.addActionListener(this);
        settings.addActionListener(this);

        setJMenuBar(bar);

        // User interface initization
        res.setBounds(5, 5, 350, 520);
        res.setBackground(Color.black);

        box.setSelectedIndex(-1);
        box.setBounds(0, 30, 350, 30);

        box.addActionListener(this);

        JLabel title0 = new JLabel("Resource Manager");
        title0.setBounds(5, 5, 300, 20);
        title0.setForeground(Color.white);

        list.setName("erasable");
        list.setBounds(5, 110, 340, 1000);
        list.setVisible(false);

        // File control panel
        openB.setBounds(5, 80, 110, 25);
        del.setBounds(120, 80, 110, 25);
        JButton view = new JButton("Inspect file");
        view.setBounds(235, 80, 110, 25);

        openB.addActionListener(this);
        del.addActionListener(this);
        view.addActionListener(this);

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {

                // Open file
                // openFile();
            }
        });

        res.add(view);
        res.add(del);
        res.add(openB);
        res.setLayout(null);
        res.add(box);
        res.add(title0);
        res.add(list);

        control = new JPanel();
        functions.setBounds(360, 5, 420, 520);
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
        JPanel inspector = new JPanel();

        functions.add("Inspector", inspector);

        // Footer
        JLabel version = new JLabel("Version: " + Main.version);
        version.setForeground(Color.white);
        version.setBounds(5, 530, 100, 20);

        Border b = BorderFactory.createBevelBorder(BevelBorder.RAISED);

        website.setBounds(140, 530, 110, 20);
        website.setBackground(Color.gray);
        website.setForeground(Color.white);
        website.setBorder(b);

        docs.setBounds(255, 530, 110, 20);
        docs.setBackground(Color.gray);
        docs.setForeground(Color.white);
        docs.setBorder(b);

        update.setBounds(400, 530, 110, 20);
        update.setBackground(Color.gray);
        update.setForeground(Color.white);
        update.setBorder(b);

        quit.setBounds(515, 530, 110, 20);
        quit.setBackground(Color.red);
        quit.setForeground(Color.black);
        quit.setBorder(b);

        run.setBounds(630, 530, 110, 20);
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

    private void updateResources() {

        String[] dirs = { "/Sprites", "/Sounds", "/Objects", "/Rooms" };
        list.setVisible(true);
        File spritesFolder = new File(engineFiles.getAbsolutePath() + dirs[box.getSelectedIndex()]);
        String[] files = new String[spritesFolder.listFiles().length];

        int index = 0;
        for (File f : spritesFolder.listFiles()) {

            files[index] = f.getName();
            index++;
        }

        list.setListData(files);
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
        } else if (e.getSource() == box) {

            switch (box.getSelectedIndex()) {

                case -1:

                    // Erase
                    break;
                default:

                    // Fetch sprites from

                    updateResources();
                    break;
            }
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
        } else if (e.getSource() == openB) {

            if (list.getSelectedIndex() == -1) {

                JOptionPane.showMessageDialog(null, "No file was selected", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }

            openFile();
        } else if (e.getSource() == del) {

            String[] dirs = { "/Sprites", "/Sounds", "/Objects", "/Rooms" };
            File file = new File(engineFiles.getAbsolutePath() + dirs[box.getSelectedIndex()]).listFiles()[list
                    .getSelectedIndex()];

            String command = "del " + file.getAbsolutePath();
            Functions.WriteAndRun(command, "delete_file.bat");

            updateResources();
        }
    }

    public void openFile() {

        String[] dirs = { "/Sprites", "/Sounds", "/Objects", "/Rooms" };
        File opened = new File(engineFiles.getAbsolutePath() + dirs[box.getSelectedIndex()]).listFiles()[list
                .getSelectedIndex()];

        if (box.getSelectedIndex() != 3) {

            try {

                File openfile = new File("src/functions/output/open_file.bat");
                FileWriter fw = new FileWriter(openfile);

                String command = opened.getAbsolutePath();
                fw.write(command);
                fw.close();

                Runtime.getRuntime().exec(openfile.getAbsolutePath());

            } catch (Exception e) {

                JOptionPane.showMessageDialog(null, "Trouble opening file", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {

            String dirpath = opened.getAbsolutePath();
            String tilesetPath = "";

            try {

                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setCurrentDirectory(new File(path + "/src/main/java/Assets/Sprites"));

                int option = fc.showDialog(null, "Choose tileset");

                // Import tileset
                if (option == JFileChooser.APPROVE_OPTION) {

                    File f = fc.getSelectedFile();

                    String s = f.getName().substring(f.getName().length() - 4);

                    if (s.equals(".png") || s.equals(".jpg")) {
                        // Valid image file
                        tilesetPath = f.getAbsolutePath();

                        tilesetPath = tilesetPath.replace("\\", "/");

                        JOptionPane.showMessageDialog(this, "Image succefully imported");
                    } else
                        JOptionPane.showMessageDialog(this, "Invalid image file");

                    TileSet tileset = new TileSet(tilesetPath,
                            Integer.parseInt(JOptionPane.showInputDialog(null, "Tile set size?")));

                    // Get map measures

                    Scanner file = new Scanner(dirpath + "/room-tiles.txt");
                    Integer dimensions = file.nextLine().split(" ").length;

                    Map map = new Map(tileset, dimensions, dimensions);
                    MapEditor editor = new MapEditor(map);

                    editor.importMap(dirpath);
                }

            } catch (Exception e) {

                JOptionPane.showMessageDialog(null, "There was a problem opening the map editor", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
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
