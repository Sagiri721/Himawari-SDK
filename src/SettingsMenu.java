import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;

import java.awt.event.*;
import java.util.Arrays;
import java.util.List;

public class SettingsMenu extends JFrame {
 
    private String[] titles = {"System & Preferences", "Physics simulation", "Input axis"};
    private JPanel systemPanel = new JPanel(), physicsPanel = new JPanel(), inputPanel = new JPanel();
    
    public static final String[] keys = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "left", "right", "up", "down"};
    public static final String[] codeEditor = {"Visual Studio Code", "Vim", "Neovim", "Emacs", "Notepad"};
    public static final String[] themes = {"light", "dark", "intellij", "mac-light", "mac-dark", "custom"};

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
        setUpInput();
        setUpPhysics();

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

        List<String> editorAlias = Arrays.asList(new String[] {"code", "vim", "nvim", "emacs", "notepad"});
        JLabel t = new JLabel("Theme: "), e = new JLabel("Code editor: "), delay = new JLabel("Frame delay: ");
        t.setBounds(5, 50, 120, 20);
        e.setBounds(5, 80, 120, 20);
        delay.setBounds(5, 110, 120, 20);

        JComboBox<String> theme = new JComboBox<String>(themes), editor = new JComboBox<String>(codeEditor);
        theme.setBounds(80, 50, 150,20);
        theme.setSelectedItem(Settings.theme);

        theme.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                
                Settings.theme = (String) theme.getSelectedItem();
                Settings.updateFile();

                JOptionPane.showMessageDialog(null, "Restart the app to see the changes", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
            }

        });

        editor.setSelectedIndex(editorAlias.indexOf(Settings.open_alias));
        editor.setBounds(80, 80, 150, 20);

        editor.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                
                Settings.open_alias = editorAlias.get(editor.getSelectedIndex());
                Settings.updateFile();

                JOptionPane.showMessageDialog(null, "The default code editor was changed with success", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JSpinner d = new JSpinner();
        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 100, 1);
        d.setModel(model);
        d.setValue(Project.gameData.delay);
        Functions.placeComponentWithLabel("Frame delay: ", d, systemPanel, 5, 110, 60);

        JCheckBox fixed = new JCheckBox();
        Functions.placeComponentWithLabel("Fixed DeltaTime", fixed, systemPanel, 5, 140, 100);

        //systemPanel.add(delay);
        systemPanel.add(e);
        systemPanel.add(editor);
        systemPanel.add(t);
        systemPanel.add(theme);
    }

    private void setUpInput(){

        JLabel x = new JLabel("Input axis X");
        JLabel y = new JLabel("Input axis Y");

        JLabel t = new JLabel("Threshold");

        JComboBox<String> xkeyLeft1 = new JComboBox<String>(keys);
        JComboBox<String> xkeyRight1 = new JComboBox<String>(keys);
        JComboBox<String> xkeyLeft2 = new JComboBox<String>(keys);
        JComboBox<String> xkeyRight2 = new JComboBox<String>(keys);

        JSpinner th = new JSpinner(), th2 = new JSpinner();
        th.setBounds(300, 80, 100, 30);
        th2.setBounds(300, 200, 100, 30);

        th.setValue(Project.gameData.limit);

        xkeyLeft1.setBounds(5, 80, 100, 30);
        xkeyRight1.setBounds(140, 80, 100, 30);
        xkeyLeft2.setBounds(5, 120, 100, 30);
        xkeyRight2.setBounds(140, 120, 100, 30);

        x.setBounds(5, 50, 200, 20);
        t.setBounds(300, 50, 200, 20);

        y.setBounds(5, 160, 100, 30);
        
        JComboBox<String> ykeyLeft1 = new JComboBox<String>(keys);
        JComboBox<String> ykeyRight1 = new JComboBox<String>(keys);
        JComboBox<String> ykeyLeft2 = new JComboBox<String>(keys);
        JComboBox<String> ykeyRight2 = new JComboBox<String>(keys);

        ykeyLeft1.setBounds(5, 200, 100, 30);
        ykeyRight1.setBounds(140, 200, 100, 30);
        ykeyLeft2.setBounds(5, 240, 100, 30);
        ykeyRight2.setBounds(140, 240, 100, 30);

        inputPanel.add(xkeyLeft1);
        inputPanel.add(xkeyLeft2);
        inputPanel.add(xkeyRight1);
        inputPanel.add(xkeyRight2);
        inputPanel.add(ykeyLeft1);
        inputPanel.add(ykeyLeft2);
        inputPanel.add(ykeyRight1);
        inputPanel.add(ykeyRight2);
        inputPanel.add(x);
        inputPanel.add(y);
        inputPanel.add(t);
        inputPanel.add(th);
    }

    private void setUpPhysics(){
        
        JSpinner sp = new JSpinner(), cap = new JSpinner(), details = new JSpinner();
        sp.setModel(new SpinnerNumberModel(1, 1, 20, 1));
        cap.setModel(new SpinnerNumberModel(60, 1, 200, 1));
        details.setModel(new SpinnerNumberModel(1, 0.1, 10, 0.1));

        JCheckBox acap = new JCheckBox(), ignore = new JCheckBox();

        Functions.placeComponentWithLabel("Gravity constant", sp, physicsPanel, 5, 50, 60);
        Functions.placeComponentWithLabel("Acceleration cap", acap, physicsPanel, 5, 80, 60);
        Functions.placeComponentWithLabel("Acceleration cap value", cap, physicsPanel, 5, 110, 60);
        Functions.placeComponentWithLabel("Raycast details", details, physicsPanel, 5, 140, 60);
        Functions.placeComponentWithLabel("Raycast ignore self collision", ignore, physicsPanel, 5, 170, 60);

        sp.setValue(Project.gameData.grav);
        cap.setValue(Project.gameData.capValue);
        details.setValue(Project.gameData.details);
        acap.setSelected(Project.gameData.cap);
        ignore.setSelected(Project.gameData.ignore);
    }
}
