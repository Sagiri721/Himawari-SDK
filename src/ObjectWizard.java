import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import java.awt.Image;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Dimension;

public class ObjectWizard extends JPanel implements ActionListener {

    String name;
    JButton done;

    JPanel compPanel = new JPanel();
    JScrollPane scrollFrame;

    JButton imageB = Style.GetStyledButton("Add Image"), addComp = Style.GetStyledButton("Add Component");
    JLabel image = new JLabel();

    JComboBox<String> comps = new JComboBox<String>(Components.fetchAll());
    HashMap<Components, String> comMap = new HashMap<Components, String>();

    JTextField tag = new JTextField();
    JSpinner layer = new JSpinner();

    String imagerenderer = "";

    File newFile;

    public ObjectWizard(String name) {

        this.name = name;
        try {

            newFile = new File(Project.path + "/src/main/java/" + Project.projectName + "/Assets/Objects/"
                    + name + ".java");

        } catch (Exception e) {
        }

        JLabel nameLabel = new JLabel("Object Wizard for: " + name);

        nameLabel.setBounds(5, 5, 200, 30);
        comps.setBounds(5, 80, 200, 30);
        addComp.setBounds(220, 80, 150, 30);
        addComp.addActionListener(this);

        JLabel compJLabel = new JLabel("Components");
        compJLabel.setBounds(5, 40, 200, 30);

        compPanel.setBounds(5, 130, 375, 300);
        compPanel.setPreferredSize(new Dimension(375, 300));

        scrollFrame = new JScrollPane(compPanel);
        scrollFrame.setBounds(compPanel.getBounds());
        compPanel.setAutoscrolls(true);
        scrollFrame.setPreferredSize(new Dimension(375, 300));
        scrollFrame.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollFrame.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        Functions.placeComponentWithLabel("Layer", layer, this, 5, 450, 100);
        Functions.placeComponentWithLabel("Tag", tag, this, 5, 480, 150);

        SpinnerModel model = new SpinnerNumberModel(0, 0, 99, 1);
        layer.setModel(model);

        compPanel.setLayout(null);
        compPanel.setBackground(Style.MAIN_BACKGROUND);

        done = Style.GetStyledButton("Create Object");
        done.setBounds(5, 530, 375, 30);

        done.addActionListener(this);

        add(done);
        add(compJLabel);
        add(scrollFrame);
        add(addComp);
        add(comps);
        add(nameLabel);
        setLayout(null);

        setSize(getWidth() + 1, getHeight());
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == imageB) {

            JFileChooser fc = new JFileChooser(
                    Project.path + "/src/main/java/" + Project.projectName + "/Assets/Sprites");
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int res = fc.showDialog(null, "Choose image");
            if (res == JFileChooser.APPROVE_OPTION) {

                ImageIcon ico = new ImageIcon(fc.getSelectedFile().getAbsolutePath());
                image.setIcon(new ImageIcon(ico.getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));

                imagerenderer = fc.getSelectedFile().getName();
            }
        } else if (e.getSource() == addComp) {

            String[] info = Components.getSnippet(comps.getSelectedIndex());
            String snippet = info[0];

            Components c = Components.getComponentbyString(String.valueOf(comps.getSelectedItem()));

            Component cc = new Component(c, (55) * comMap.size());

            // Before putting check if already exists
            for (Entry<Components, String> entry : comMap.entrySet()) {

                if (entry.getKey() == c) {

                    JOptionPane.showMessageDialog(null, "This component was already added", "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            cc.setDetails(info[1]);

            compPanel.add(cc);
            compPanel.repaint();

            comMap.put(c, snippet);

            compPanel.setPreferredSize(new Dimension((int) compPanel.getSize().getWidth(), (55) * comMap.size() + 5));
            scrollFrame.revalidate();
            scrollFrame.repaint();

        } else if (e.getSource() == done) {

            String startMethod = "";

            for (java.util.Map.Entry<Components, String> entry : comMap.entrySet()) {

                startMethod += "\n//Automatically generated by Himawari GUI, " + entry.getKey().getName() + " Component"
                        + "\n";
                startMethod += entry.getValue();
            }

            startMethod += "setLayer("+((Integer) layer.getValue())+");\n";
            startMethod += "setTag(\""+tag.getText()+"\");\n";

            try {

                Scanner s = new Scanner(new File("src/templates/Object.txt"));
                String text = "";

                while (s.hasNextLine())
                    text += s.nextLine() + "\n";

                s.close();

                
                text = text.replace("[package]", (Project.projectName.replace("/", ".") + "."));
                text = text.replace("[Object]", this.name);
                text = text.replace("[Start]", startMethod);

                FileWriter fw = new FileWriter(newFile);

                fw.write(text);
                fw.close();

                JOptionPane.showMessageDialog(null, "Object created", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);

                SwingUtilities.getWindowAncestor(this).setVisible(false);
                SwingUtilities.getWindowAncestor(this).dispose();

            } catch (Exception ee) {

                JOptionPane.showMessageDialog(null, "Error creating object", "ERROR", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    public class Component extends JPanel {

        Components comp;
        JLabel name = new JLabel();
        JLabel details = new JLabel();

        JButton delete = Style.GetStyledButton("X");

        Component(Components comp, int y) {

            this.comp = comp;
            name.setText(comp.getName());
            details.setText(comp.getDetails());

            JLabel icon = new JLabel(new ImageIcon("src/res/icons/" + comp.getName() + ".png"));

            setBounds(5, y, 370, 50);
            //System.out.println("Nome: " + comp.getName() + " | Y: " + y);

            name.setBounds(5, 5, 320, 20);

            icon.setBounds(250, 10, 32, 32);

            details.setFont(Style.CONTENT_FONT);
            details.setBounds(5, 20, 320, 20);

            delete.setBounds(292, 10, 55, 32);
            delete.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    removeComponent(comp);
                }

            });

            setBorder(Style.BUTTON_BORDER);

            setLayout(null);

            add(delete);
            add(icon);
            add(details);
            add(name);
        }

        public void setDetails(String details) {

            this.details.setText(details);
        }
    }

    public void removeComponent(Components c) {

        comMap.remove(c);

        compPanel.removeAll();
        compPanel.revalidate();

        int index = 0;
        for (java.util.Map.Entry<Components, String> entry : comMap.entrySet()) {

            System.out.println(entry.getKey().getName());
            Component cc = new Component(entry.getKey(), index * 50);
            compPanel.add(cc);

            index++;
        }

        compPanel.repaint();
    }
}
