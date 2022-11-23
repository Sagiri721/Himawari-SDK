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

import Components.Structs.Map;
import Components.Structs.TileSet;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class Window extends JFrame implements ActionListener {

    public String version = "1.0.0";

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
            String logsList = "<html><h1>Version Logs</h1><ul>";
            for (String log : versions)
                logsList += ("<li>" + log + "</li>");

            logsList += "</ul></html>";
            JLabel listLabel = new JLabel(logsList);
            listLabel.setFont(Style.CONTENT_FONT);
            listLabel.setBounds(0, 200, getWidth() / 2, 150);

            main.add(listLabel);
            main.add(explore);
        }

        JLabel versionLabel = new JLabel("version " + String.valueOf(version));
        versionLabel.setBounds(3, 530, 100, 20);

        add(versionLabel);
        add(main);
    }

    public String[] getVersions() {

        String[] logs = { "1.0.0: First version, allows basic project management",
                "1.1.0: Main page with information" };

        return logs;
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

                Functions.OpenPanelAsFrame(500, 400, "Project Wizard", new ProjectWizard(this), false);
                /*
                 * 
                 * 
                 */

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
