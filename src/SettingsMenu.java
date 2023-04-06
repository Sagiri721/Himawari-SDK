import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import Components.Structs.EngineData;

import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsMenu extends JFrame implements ActionListener {
 
    private String[] titles = {"System & Preferences", "Physics simulation", "Input axis"};
    private JPanel systemPanel = new JPanel(), physicsPanel = new JPanel(), inputPanel = new JPanel();
    
    public static String[] keys;
    public static final String[] codeEditor = {"Visual Studio Code", "Vim", "Neovim", "Emacs", "Notepad"};
    public static final String[] themes = {"light", "dark", "intellij", "mac-light", "mac-dark", "custom"};

    public SettingsMenu(int tab) {

        // Get keys
        Field[] fields = KeyEvent.class.getDeclaredFields();
        List<String> keyList = new ArrayList<String>();

        for (java.lang.Object o : Arrays.stream(fields).filter(f -> f.getName().startsWith("VK")).toArray()) {

            Field f = (Field) o;
            if(Modifier.isFinal(f.getModifiers()))
                try {
                    keyList.add(KeyEvent.getKeyText((Integer) f.get(null)));
                } catch (IllegalArgumentException | IllegalAccessException e) { e.printStackTrace(); }
        }

        keys = keyList.toArray(new String[keyList.size()]);
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

    JSpinner d = new JSpinner();
    JCheckBox fixed = new JCheckBox();

    private void setUpSystem() {

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(5, 420, 200, 30);
        saveButton.addActionListener(this);

        systemPanel.add(saveButton);

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

        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 100, 1);
        d.setModel(model);
        d.setValue(Project.gameData.delay);
        Functions.placeComponentWithLabel("Frame delay: ", d, systemPanel, 5, 110, 60);

        Functions.placeComponentWithLabel("Fixed DeltaTime", fixed, systemPanel, 5, 140, 100);

        //systemPanel.add(delay);
        systemPanel.add(e);
        systemPanel.add(editor);
        systemPanel.add(t);
        systemPanel.add(theme);
    }

    JComboBox<String> xkeyLeft1;
    JComboBox<String> xkeyRight1;
    JComboBox<String> xkeyLeft2;
    JComboBox<String> xkeyRight2;

    JComboBox<String> ykeyLeft1;
    JComboBox<String> ykeyRight1;
    JComboBox<String> ykeyLeft2 ;
    JComboBox<String> ykeyRight2;

    JSpinner th = new JSpinner(), th2 = new JSpinner();

    private void setUpInput(){

        xkeyLeft1 = new JComboBox<String>(keys);
        xkeyLeft2 = new JComboBox<String>(keys);
        xkeyRight1 = new JComboBox<String>(keys);
        xkeyRight2 = new JComboBox<String>(keys);
        ykeyLeft1 = new JComboBox<String>(keys);
        ykeyLeft2 = new JComboBox<String>(keys);
        ykeyRight1 = new JComboBox<String>(keys);
        ykeyRight2 = new JComboBox<String>(keys);

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(5, 420, 200, 30);
        saveButton.addActionListener(this);

        inputPanel.add(saveButton);

        JLabel x = new JLabel("Input axis X");
        JLabel y = new JLabel("Input axis Y");

        JLabel t = new JLabel("Threshold");


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
        
        ykeyLeft1.setBounds(5, 200, 100, 30);
        ykeyRight1.setBounds(140, 200, 100, 30);
        ykeyLeft2.setBounds(5, 240, 100, 30);
        ykeyRight2.setBounds(140, 240, 100, 30);

        // Set the correct keys
        String[] listKeys = new String[Project.gameData.map.length];

        int index = 0;
        for(char key : Project.gameData.map){

            int keyValue = (byte) key;
            String name = KeyEvent.getKeyText(keyValue);

            listKeys[index] = name;
            index++;
        }

        xkeyLeft1.setSelectedItem(listKeys[0]);
        xkeyLeft2.setSelectedItem(listKeys[4]);
        xkeyRight1.setSelectedItem(listKeys[1]);
        xkeyRight2.setSelectedItem(listKeys[5]);

        ykeyLeft1.setSelectedItem(listKeys[2]);
        ykeyLeft2.setSelectedItem(listKeys[6]);
        ykeyRight1.setSelectedItem(listKeys[3]);
        ykeyRight2.setSelectedItem(listKeys[7]);
        
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

    JSpinner sp = new JSpinner(), cap = new JSpinner(), details = new JSpinner();
    JCheckBox acap = new JCheckBox(), ignore = new JCheckBox();

    private void setUpPhysics(){
        
        JButton saveButton = new JButton("Save");
        saveButton.setBounds(5, 420, 200, 30);
        saveButton.addActionListener(this);

        physicsPanel.add(saveButton);

        sp.setModel(new SpinnerNumberModel(1, 1, 20, 1));
        cap.setModel(new SpinnerNumberModel(60, 1, 200, 1));
        details.setModel(new SpinnerNumberModel(1, 0.1, 10, 0.1));

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

    @Override
    public void actionPerformed(ActionEvent e) {
    
        // Update data
        Project.gameData.delay = (Integer) (d.getValue());
        Project.gameData.fixed = fixed.isSelected();
        Project.gameData.cap = acap.isSelected();
        Project.gameData.capValue = (Float) cap.getValue(); 
        Project.gameData.details = (Float) details.getValue();
        Project.gameData.grav = (Float) sp.getValue();
        Project.gameData.ignore = ignore.isSelected();
        Project.gameData.limit = (Integer) th.getValue();

        char[] newCharacters = new char[Project.gameData.map.length];
        newCharacters[0] = (char) (byte) convertNametoCode(xkeyLeft1.getSelectedItem());
        newCharacters[1] = (char) (byte) convertNametoCode(xkeyRight1.getSelectedItem());
        newCharacters[2] = (char) (byte) convertNametoCode(ykeyLeft1.getSelectedItem());
        newCharacters[3] = (char) (byte) convertNametoCode(ykeyRight1.getSelectedItem());
        newCharacters[4] = (char) (byte) convertNametoCode(xkeyLeft2.getSelectedItem());
        newCharacters[5] = (char) (byte) convertNametoCode(xkeyRight2.getSelectedItem());
        newCharacters[6] = (char) (byte) convertNametoCode(ykeyLeft2.getSelectedItem());
        newCharacters[7] = (char) (byte) convertNametoCode(ykeyRight2.getSelectedItem());

        // Write to engine file
        File settingsFile = new File(Project.engineAssetFiles + "/MyGameData.json");
        Gson g = new Gson();

        EngineData objToWrite = Project.gameData;

        try {

            String text = g.toJson(objToWrite);
            
            FileWriter fw = new FileWriter(settingsFile);
            fw.write(text);
            fw.close();

        } catch (JsonIOException | IOException e1){ 
        }
    }

    private Byte convertNametoCode(java.lang.Object object){

        for(Field f : KeyEvent.class.getDeclaredFields()){
    
            try {

                if(!Modifier.isFinal(f.getModifiers())) continue;

                String text = KeyEvent.getKeyText((Integer) f.get(null));
                if(text.equals((String) object)){
                    
                    return (byte) Integer.parseInt(f.get(null).toString());
                } 

            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return 0;
    } 
}