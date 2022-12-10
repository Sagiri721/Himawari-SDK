import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.event.*;

import com.google.gson.Gson;

import Components.Structs.Settings;

public class BuildSettings extends JPanel implements ActionListener {

    private JButton setIcon = Style.GetStyledButton("Set icon"), done = Style.GetStyledButton("Apply changes");
    private File icon;

    JTextField namefield = new JTextField(), comfield = new JTextField(),
            infofield = new JTextField();

    JCheckBox cursBox = new JCheckBox(), fullBox = new JCheckBox(), allowBox = new JCheckBox(),
            resBox = new JCheckBox();

    JSpinner v1 = new JSpinner(), v2 = new JSpinner(), v3 = new JSpinner();

    public BuildSettings() {

        Settings set = new Settings();
        JLabel title = new JLabel("Build Settings");
        title.setFont(Style.HEADER_FONT);
        title.setBounds(5, 0, 300, 40);
        title.setForeground(Style.MAIN_TEXT_COLOR);

        JLabel pictute = new JLabel(new ImageIcon("src/res/buildsettings.png"));
        pictute.setBounds(430, 120, 150, 150);
        add(pictute);

        add(title);
        try {

            Gson gson = new Gson();
            File newFile = new File(
                    Project.path + "/src/main/java/" + Project.projectName + "/Assets/Options/engine-options.json");

            Reader reader = Files.newBufferedReader(Paths.get(newFile.getAbsolutePath()));

            set = gson.fromJson(reader, Settings.class);

            if (set == null) {

                JOptionPane.showMessageDialog(null, "No options file found", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }

        } catch (Exception e) {

            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "No File was found exception", "ERROR", JOptionPane.ERROR_MESSAGE);
        }

        setBackground(new Color(244, 207, 223));
        JLabel name = new JLabel("Name: "), company = new JLabel("Company: "), version = new JLabel("Version: "),
                info = new JLabel("Product info: "), cursor = new JLabel("Show cursor: "),
                start = new JLabel("Start fullscreen: "), allowF = new JLabel("Allow fullscreen: "),
                allowR = new JLabel("Allow Resize: "),
                icon = new JLabel("Icon: ");

        v1.setValue(Integer.valueOf(set.version[0]));
        v2.setValue(Integer.valueOf(set.version[1]));
        v3.setValue(Integer.valueOf(set.version[2]));

        namefield.setText(set.name);
        comfield.setText(set.company);
        infofield.setText(set.product_info);

        cursBox.setSelected(set.display_cursor);
        fullBox.setSelected(set.start_fullscreen);
        allowBox.setSelected(set.allow_fullscreen);
        resBox.setSelected(set.resize_window);

        setIcon.addActionListener(this);
        done.addActionListener(this);

        JComponent[] component = { namefield, comfield, infofield, v1, cursBox, fullBox, allowBox, resBox, setIcon };
        JLabel[] labels = { name, company, info, version, cursor, start, allowF, allowR, icon };

        int i = 0;
        for (JLabel l : labels) {

            l.setBounds(5, 30 + i * 32, 300, 30);

            if (component[i] == v1) {

                component[i].setBounds(100, 30 + i * 32, 50, 30);
                v2.setBounds(160, 30 + i * 32, 50, 30);
                v3.setBounds(220, 30 + i * 32, 50, 30);

                add(v2);
                add(v3);
            } else {

                component[i].setBounds(100, 30 + i * 32, 300, 30);
            }

            add(l);
            add(component[i]);
            i++;
        }

        done.setBounds(5, 30 + i * 32 + 5, 300 + 95, 30);
        add(done);

        setLayout(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == setIcon) {

            JFileChooser fc = new JFileChooser(Project.path);
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int res = fc.showDialog(null, "Select Icon");

            if (res == JFileChooser.APPROVE_OPTION) {

                icon = fc.getSelectedFile();
            }

        } else if (e.getSource() == done) {

            // Save to json
            Settings settings = new Settings();

            settings.name = namefield.getText();
            settings.company = comfield.getText();

            int[] v = { (Integer) (v1.getValue()), (Integer) (v2.getValue()), (Integer) (v3.getValue()) };
            settings.product_info = infofield.getText();
            settings.version = v;

            settings.display_cursor = cursBox.isSelected();
            settings.start_fullscreen = fullBox.isSelected();
            settings.allow_fullscreen = allowBox.isSelected();
            settings.resize_window = resBox.isSelected();

            settings.icon = icon.getAbsolutePath();

            // Save to file

            try {

                Gson gson = new Gson();
                Writer writer = new FileWriter(Project.path + "/src/main/java/" + Project.projectName
                        + "/Assets/Options/engine-options.json");

                gson.toJson(settings, writer);

                writer.close();

            } catch (Exception ee) {

                JOptionPane.showMessageDialog(null, "No options file found", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(null, "Options saved", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}