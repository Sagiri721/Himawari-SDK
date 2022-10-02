import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;
import java.awt.Color;

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
import javax.swing.LayoutStyle;
import javax.swing.border.BevelBorder;

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
            settings = new JMenuItem("Add Visual Studio Code as default code editor"),
            close = new JMenuItem("Close Project"), open = new JMenuItem("Open Himawari Store");
    JMenuBar bar = new JMenuBar();

    JPanel control;

    JButton newObject = new JButton("New Object"), newAlert = new JButton("Create Alert");

    Project(File path) {

        this.path = path;
        this.engineFiles = new File(path.getAbsolutePath() + "/src/main/java/Assets");
        this.compiler = new File(path.getAbsolutePath() + "/../compile.bat");

        setTitle(getProjectTitle());
        setSize(800, 600);
        getContentPane().setBackground(new Color(255, 255, 153));

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
        res.setBounds(5, 5, 350, 530);
        res.setBackground(Color.black);

        box.setSelectedIndex(-1);
        box.setBounds(0, 30, 350, 30);

        box.addActionListener(this);

        JLabel title0 = new JLabel("Resource Manager");
        title0.setBounds(5, 5, 300, 20);
        title0.setForeground(Color.white);

        list.setName("erasable");
        list.setBounds(5, 80, 340, 1000);
        list.setVisible(false);

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {

                // Open file
                openFile();
            }
        });

        res.setLayout(null);
        res.add(box);
        res.add(title0);
        res.add(list);

        control = new JPanel();
        control.setBounds(360, 5, 420, 530);
        control.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        control.setLayout(null);

        JLabel label0 = new JLabel("Control Panel");
        label0.setBounds(5, 5, 100, 30);
        control.add(label0);

        newObject.setBounds(5, 30, 160, 20);
        newObject.addActionListener(this);

        newAlert.setBounds(5, 55, 160, 20);
        newAlert.addActionListener(this);

        control.add(newObject);
        control.add(newAlert);
        control.setLayout(null);

        // Bullshit initialization

        add(control);
        add(res);

        addKeyListener(this);
        setFocusable(true);
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

        if (e.getSource() == i0) {

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
                + "\\src\\main\\java" + filePath + "\nNotepad " + file;
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
