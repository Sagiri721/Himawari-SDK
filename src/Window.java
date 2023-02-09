import javax.swing.BorderFactory;
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
import javax.swing.border.Border;

import com.google.gson.Gson;

import Components.Structs.Map;
import Components.Structs.TileSet;
import Components.Structs.Versions;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Window extends JFrame implements ActionListener {

    public String version = "1.0.0";

    JMenu menu = new JMenu("Project Manager Tool"), mapmenu = new JMenu("Map Editor Tool"),
            account = new JMenu("Account");
    JMenuItem i0 = new JMenuItem("New Map"), i1 = new JMenuItem("Load Map"),
            createP = new JMenuItem("Create Project"), loadP = new JMenuItem("Load Project"),
            login = new JMenuItem("Login");
    JMenuBar mb = new JMenuBar();

    public static TileSet tileset;
    public static Map map;

    public static boolean internet;

    Image netIcon = new ImageIcon("src/res/internet.png").getImage().getScaledInstance(100, 100,
            java.awt.Image.SCALE_SMOOTH);
    JLabel label = new JLabel(new ImageIcon(netIcon), SwingConstants.CENTER);

    // User Data
    JLabel name_label = new JLabel("Hello there " + Settings.username, SwingConstants.CENTER);

    JFrame window;

    Window() {

        window = this;
        internet = InternetConnection();

        setTitle("Himawari tools");
        setSize(700, 610);
        setResizable(false);

        DefineComponents();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setVisible(true);

        getContentPane().setBackground(Style.MAIN_BACKGROUND);
    }

    private void DefineComponents() {

        name_label.setFont(Style.CONTENT_FONT);
        mapmenu.add(i0);
        mapmenu.add(i1);

        menu.add(createP);
        menu.add(loadP);

        account.add(login);

        JPanel user = new JPanel();
        user.setLayout(null);
        user.setBounds(0, 0, 700, 30);

        name_label.setBounds(5, 5, 700, 20);
        name_label.setForeground(Color.black);
        user.add(name_label);

        label.setBounds(5, 50, 100, 100);

        login.addActionListener(this);
        i0.addActionListener(this);
        i1.addActionListener(this);
        createP.addActionListener(this);
        loadP.addActionListener(this);

        mb.add(menu);
        mb.add(mapmenu);
        mb.add(account);

        setJMenuBar(mb);

        add(user);

        JPanel main = new JPanel();
        main.setBounds(0, 30, getWidth(), 500);
        main.setBackground(Style.MAIN_BACKGROUND);
        main.setLayout(null);

        if (!Window.internet)
            add(label);
        else {

            // Add all the internet based components
            JLabel explore = new JLabel(new ImageIcon("src/res/explore.jpg"));
            explore.setBounds(0, 0, main.getWidth(), 200);

            String[] versions = getVersions();
            String[] mapversions = getMapVersions();
            String logsList = "<html><h1>Version Logs</h1><p>Editor Versions</p><ul>";
            for (String log : versions)
                logsList += ("<li>" + log + "</li>");

            logsList += "</ul><p>MapEditor Versions</p><ul>";
            for (String log : mapversions)
                logsList += ("<li>" + log + "</li>");
            logsList += "</ul></html>";
            JLabel listLabel = new JLabel(logsList);
            listLabel.setBounds(5, 250, getWidth() - 20, listLabel.getPreferredSize().height + 20);

            main.add(listLabel);
            main.add(explore);
        }

        JLabel versionLabel = new JLabel("version " + String.valueOf(version));
        versionLabel.setBounds(3, 530, 100, 20);

        add(versionLabel);
        add(main);
    }

    public String[] getVersions() {

        Versions versions = null;

        try {

            Gson gson = new Gson();
            Reader reader = Files
                    .newBufferedReader(Paths.get(new File("src/data/EngineVersions.json").getAbsolutePath()));
            versions = gson.fromJson(reader, Versions.class);

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        return versions.versions;
    }

    public String[] getMapVersions() {

        Versions versions = null;

        try {

            Gson gson = new Gson();
            Reader reader = Files
                    .newBufferedReader(Paths.get(new File("src/data/mapEditorVersions.json").getAbsolutePath()));
            versions = gson.fromJson(reader, Versions.class);

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        return versions.versions;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == i0) {

            new CreateMap(0);

        } else if (e.getSource() == createP) {

            if (internet) {

                Functions.OpenPanelAsFrame(500, 400, "Project Wizard", new ProjectWizard(this), false);

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
            } else if (e.getSource() == login) {

                if (internet) {

                    Functions.OpenPanelAsFrame(420, 200, "Login activity", new Login(), false);
                } else {

                    JOptionPane.showMessageDialog(null, "No internet connection", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } else if (e.getSource() == i1) {

                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int opt = fc.showDialog(null, "Choose map");

                if (opt == JFileChooser.APPROVE_OPTION) {

                    Functions.openMapEditorEducated(new Map(null, 100, 100), fc.getSelectedFile());
                }
            }
        }
    }

    private boolean InternetConnection() {

        Process process;
        try {
            process = java.lang.Runtime.getRuntime().exec("ping www.google.com");

            int x = process.waitFor();
            if (x == 0) {
                return true;
            } else {
                return false;
            }

        } catch (IOException e) {
        } catch (InterruptedException e) {
        }

        return false;
    }
}
