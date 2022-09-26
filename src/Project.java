import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import java.awt.event.*;

public class Project extends JFrame implements KeyListener, ActionListener {

    String[] options = { "Sprites", "Sound", "Objects", "Maps" };
    JComboBox<String> box = new JComboBox<String>(options);
    JPanel res = new JPanel();

    private File path, engineFiles;
    private File compiler;

    JMenu gamemenu = new JMenu("Game"), codeMenu = new JMenu("Code"), settingsMenu = new JMenu("Settings"),
            other = new JMenu("Misc");
    JMenuItem i0 = new JMenuItem("Run (f5)"), i1 = new JMenuItem("Open Game Code (f1)"),
            settings = new JMenuItem("Add Visual Studio Code as default code editor"),
            close = new JMenuItem("Close Project"), open = new JMenuItem("Open Himawari Store");
    JMenuBar bar = new JMenuBar();

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

        res.setLayout(null);
        res.add(box);
        res.add(title0);

        // Bullshit initialization

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

            Component[] cc = res.getComponents();
            for (Component c : cc) {

                if (c.getName() == "erasable")
                    res.remove(c);
            }

            switch (box.getSelectedIndex()) {

                case -1:

                    // Erase
                    break;
                case 0:

                    // Fetch sprites from

                    File spritesFolder = new File(engineFiles.getAbsolutePath() + "/Sprites");
                    String[] files = new String[spritesFolder.listFiles().length];

                    int index = 0;
                    for (File f : spritesFolder.listFiles()) {

                        files[index] = f.getName();
                        index++;
                    }

                    JList<String> list = new JList<String>(files);
                    list.setName("erasable");
                    list.setBounds(5, 80, 300, 20 * files.length);
                    res.add(list);

                    break;
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
