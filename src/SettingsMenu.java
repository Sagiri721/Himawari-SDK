import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.event.*;

public class SettingsMenu extends JFrame {
 
    String[] titles = {"System & Preferences", "Physics simulation", "Input axis"};
    String[] codeEditor = {"Visual Studio Code", "Vim", "Neovim", "Emacs", "Notepad"};
    JPanel systemPanel = new JPanel(), physicsPanel = new JPanel(), inputPanel = new JPanel();

    String[] themes = {"light", "dark", "intellij", "mac-light", "mac-dark", "custom"};

    public SettingsMenu(int tab) {

        setTitle("Settings menu");

        JTabbedPane main = new JTabbedPane();
        
        JLabel title = new JLabel(titles[0]), title2 = new JLabel(titles[1]), title3 = new JLabel(titles[2]);

        title.setFont(Style.HEADER_FONT);
        title2.setFont(Style.HEADER_FONT);
        title3.setFont(Style.HEADER_FONT);

        title.setBounds(5, 5, 400, 30);
        title2.setBounds(title.getBounds());
        title3.setBounds(title.getBounds());

        main.add(systemPanel, "System");
        main.add(physicsPanel, "Physics");
        main.add(inputPanel, "Input");

        main.setTabPlacement(JTabbedPane.LEFT);
        main.setSelectedIndex(tab);
        add(main);

        setUpSystem();

        systemPanel.add(title);
        physicsPanel.add(title2);
        inputPanel.add(title3);

        systemPanel.setLayout(null);
        physicsPanel.setLayout(null);
        inputPanel.setLayout(null);

        setResizable(false);
        setSize(500, 500);
        setVisible(true);
    }

    private void setUpSystem() {

        JLabel t = new JLabel("Theme: ");
        t.setBounds(5, 50, 120, 20);

        JComboBox<String> theme = new JComboBox<String>(themes);
        theme.setBounds(80, 50, 150,20);
        theme.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                
                Settings.theme = (String) theme.getSelectedItem();
                Settings.updateFile();

                JOptionPane.showMessageDialog(null, "Restart the app to see the changes", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
            }

        });

        systemPanel.add(t);
        systemPanel.add(theme);
    }
}
