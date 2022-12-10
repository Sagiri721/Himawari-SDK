import javax.swing.BorderFactory;
import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.Color;
import java.awt.Font;

import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ProjectControl extends JPanel implements ActionListener {

    JButton newObject = new JButton("New Object"), newAlert = new JButton("Create Alert");

    public ProjectControl() {

        JLabel label0 = new JLabel("Control Panel");
        label0.setFont(Style.HEADER_FONT);
        label0.setForeground(Style.MAIN_TEXT_COLOR);
        label0.setBounds(5, 5, 200, 30);
        add(label0);

        JLabel create = new JLabel("Create", SwingConstants.CENTER);
        create.setBounds(5, 35, 160, 20);

        newObject.setBounds(5, 55, 160, 20);
        newObject.addActionListener(this);

        newAlert.setBounds(5, 80, 160, 20);
        newAlert.addActionListener(this);

        add(create);
        add(newObject);
        add(newAlert);

        setBackground(Style.SECONDARY_BACKGROUND);

        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        setLayout(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == newObject) {

            try {

                Scanner s = new Scanner(new File("src/templates/Object.txt"));

                String text = "";
                while (s.hasNextLine())
                    text += s.nextLine() + "\n";

                s.close();

                String object_name = JOptionPane.showInputDialog(null, "Object name?", "Create Object",
                        JOptionPane.INFORMATION_MESSAGE);

                if (object_name == null || object_name.trim() == "") {

                    JOptionPane.showMessageDialog(null, "Object name can't be empty", "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                text = text.replace("[Object]", object_name);
                text = text.replace("[Start]", "");

                Integer option = JOptionPane.showConfirmDialog(null, "Go to advanced configuration panel?",
                        "Advanced Configuration", JOptionPane.YES_NO_OPTION);
                // No: 1 Yes: 0

                if (option == 0) {

                    Functions.OpenPanelAsFrame(400, 600, "Advanced Object Creation", new ObjectWizard(object_name),
                            false);

                    return;
                } else {

                    File newFile = new File(Project.path + "/src/main/java/" + Project.projectName + "/Assets/Objects/"
                            + object_name + ".java");
                    if (newFile.exists()) {

                        JOptionPane.showMessageDialog(null, "There already exists an object with this name", "ERROR",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    FileWriter fw = new FileWriter(newFile);

                    fw.write(text);
                    fw.close();

                    JOptionPane.showMessageDialog(null, "Object Created");

                }

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }
}
