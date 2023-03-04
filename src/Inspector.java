import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import javax.swing.*;
import java.awt.event.*;

import Components.Structs.ObjectData;

import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

public class Inspector extends JPanel implements ActionListener {

    // List of all the components that can be added
    public static String[] components = {
            "Transform",
            "ImageRenderer",
            "Animator",
            "RectCollider",
            "Body",
            "Camera"
    };

    File curFile = null;
    JComboBox<String> compBox = new JComboBox<String>(components);

    JButton compSnippet = Style.GetStyledButton("Add component"), refresh = Style.GetStyledButton("Refresh components");
    public JTextArea snippetArea = new JTextArea();
    JLabel objectName = new JLabel("Object name", SwingConstants.CENTER), componentLabel = new JLabel("Components"), preview = new JLabel("");
    JButton copy = Style.GetStyledButton("Copy snippet"), details = Style.GetStyledButton("See file details");

    public JComponent[] inpectors = { compSnippet, compBox, copy, details };

    JPanel compPanel = new JPanel();
    JScrollPane scrollFrame;

    public Inspector() {

        // Make inspector unaccessible
        for (JComponent c : inpectors) {

            c.setEnabled(false);
        }

        JLabel l = new JLabel("Add a component");
        l.setBounds(5, 40, 200, 20);
        compBox.setBounds(110, 40, 150, 20);

        objectName.setBounds(5, 5, 420, 30);
        objectName.setFont(new Font("Monospace", Font.PLAIN, 22));
        compSnippet.setBounds(270, 40, 130, 20);

        snippetArea.setBounds(5, 70, 405, 100);
        snippetArea.setEditable(false);
        snippetArea.setLineWrap(true);

        details.setBounds(5, 220, 405, 30);

        details.addActionListener(this);
        compSnippet.addActionListener(this);

        copy.setBounds(5, 175, 150, 30);
        copy.addActionListener(this);

        componentLabel.setBounds(5, 265, 405, 30);
        componentLabel.setFont(Style.HEADER_FONT);

        compPanel.setBounds(5, 300, 405, 300);
        compPanel.setPreferredSize(new Dimension(405, 300));

        scrollFrame = new JScrollPane(compPanel);
        scrollFrame.setBounds(compPanel.getBounds());
        compPanel.setAutoscrolls(true);
        scrollFrame.setPreferredSize(new Dimension(405, 300));
        scrollFrame.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollFrame.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        compPanel.setLayout(null);
        compPanel.setBackground(Style.MAIN_BACKGROUND);

        preview.setBounds(5, 605, 170, 170);
        preview.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        add(preview);
        add(componentLabel);
        add(scrollFrame);
        add(details);
        add(copy);
        add(snippetArea);
        add(compSnippet);
        add(l);
        add(compBox);
        add(objectName);
        add(compBox);

        setLayout(null);
    }

    public void inspectObject(File objFile) {

        curFile = objFile;
        objectName.setText(objFile.getName());

        // Show components and stuff
        for (JComponent c : inpectors) {

            c.setEnabled(true);
        }

        // Get the components of object
        // Remove child components of panel
        compPanel.removeAll();
        // Clear snippet area
        snippetArea.setText("");

        ObjectData data = Arrays.stream(Project.objectInformation).filter(entry -> (entry.name + ".java").equals(objFile.getName())).findFirst().get();
        int index = 0;

        if(data == null){

            // This is a really bad way of doing it and it ignores local variables and shit so let's only use this method if we can not find an object reference
            HashMap<Components, String> comps = Functions.FindCompiledComponents(objFile);
    
            if (comps == null)
                return;
    
            for (Entry<Components, String> comEntry : comps.entrySet()) {
    
                Component cc = new Component(comEntry.getKey(), (145) * index);
                cc.setDetails("Variable name: " + comEntry.getValue());
    
                compPanel.add(cc);
    
                index++;
            }
        } else {

            for (String c : data.components) {
                
                Components comp = Components.getComponentbyString(c);
                Component cc = new Component(comp, (145) * index);
                
                switch(c)
                {
                    case "Transform":

                        cc.setDetails("Position: " + data.position.x + " | " + data.position.y + "<br>Angle: " + data.angle + "<br>Scale: " + data.scale.x + " | " + data.scale.y);
                    break;
                    case "ImageRenderer":

                        cc.setDetails("Visible: "+(data.visible?"yes" : "no")+"<br>Image: " + data.image + "<br>Dimensions: " + data.spriteDimensions.x + " | " + data.spriteDimensions.y + "<br>Layer " + data.layer);
                    break;
                }

                compPanel.add(cc);
                index++;
            }

            if(data.image != null){

                preview.setIcon(new ImageIcon(new ImageIcon(data.image).getImage().getScaledInstance(preview.getBounds().width, preview.getBounds().height, Image.SCALE_SMOOTH)));
                preview.repaint();
            }
        }

        compPanel.repaint();
        compPanel.setPreferredSize(new Dimension((int) compPanel.getSize().getWidth(), (145) * index + 5));
        scrollFrame.revalidate();
        scrollFrame.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == compSnippet) {

            snippetArea.setText(Components.getSnippet(compBox.getSelectedIndex())[0]);
        } else if (e.getSource() == copy) {

            StringSelection selection = new StringSelection(snippetArea.getText());
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();

            cb.setContents(selection, null);
            JOptionPane.showMessageDialog(null, "Snippet copied to clipboard");
        } else if (e.getSource() == details) {

            new FileDetailsWindow(curFile);
        }
    }

    public class Component extends JPanel {

        Components comp;
        JLabel name = new JLabel();
        JLabel details = new JLabel();

        Component(Components comp, int y) {

            this.comp = comp;
            name.setText(comp.getName());
            name.setFont(Style.FONT2);

            details.setText(comp.getDetails());

            JLabel icon = new JLabel(new ImageIcon("src/res/icons/" + comp.getName() + ".png"));
            setBackground(Style.SECONDARY_BACKGROUND);

            setBounds(5, y, 377, 140);
            name.setBounds(5, 5, 300, 20);

            icon.setBounds(270, 10, 32, 32);

            details.setFont(Style.CONTENT_FONT);
            details.setBounds(5, 20, 600, 120);

            setLayout(null);

            add(icon);
            add(details);
            add(name);
        }

        public void setDetails(String details) {

            this.details.setText("<html>"+details+"</html>");
        }
    }
}
