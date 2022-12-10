import java.io.File;

import java.awt.Font;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.BevelBorder;

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

    JComboBox<String> compBox = new JComboBox<String>(components);

    JButton compSnippet = new JButton("Add component");
    public JTextArea snippetArea = new JTextArea();
    JLabel objectName = new JLabel("Object name", SwingConstants.CENTER);
    JButton copy = new JButton("Copy snippet");

    public JComponent[] inpectors = { compSnippet, compBox, copy };

    public Inspector() {

        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        // Make inspector unaccessible
        for (JComponent c : inpectors) {

            c.setEnabled(false);
        }

        JLabel l = new JLabel("Add a component");
        l.setBounds(5, 40, 200, 20);
        compBox.setBounds(110, 40, 150, 20);

        objectName.setBounds(5, 5, 420, 20);
        objectName.setFont(new Font("Monospace", Font.PLAIN, 22));
        compSnippet.setBounds(270, 40, 130, 20);

        snippetArea.setBounds(5, 70, 405, 100);
        snippetArea.setEditable(false);
        snippetArea.setLineWrap(true);

        compSnippet.addActionListener(this);

        copy.setBounds(5, 175, 150, 30);
        copy.addActionListener(this);

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

        objectName.setText(objFile.getName());

        // Show components and stuff
        for (JComponent c : inpectors) {

            c.setEnabled(true);
        }
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
        }
    }

}
