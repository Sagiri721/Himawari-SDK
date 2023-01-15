import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import com.google.gson.JsonObject;

public class LibraryManager extends JFrame {

    JPanel libraries = new JPanel();

    public LibraryManager() {

        JLabel label = new JLabel("Himawari Library Manager");
        label.setFont(Style.HEADER_FONT);
        label.setForeground(Style.MAIN_TEXT_COLOR);

        label.setBounds(5, 5, 320, 30);
        getContentPane().setBackground(Style.SECONDARY_BACKGROUND);

        libraries.setLayout(null);
        libraries.setBackground(Style.MAIN_BACKGROUND);

        fetchLibs();

        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(libraries, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBounds(5, 40, 480, 580);
        add(scrollPane);
        add(label);

        setLayout(null);
        setSize(500, 700);
        setTitle("Himawari library manager");
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void fetchLibs() {

        try {

            JsonObject[] libs = WebsiteConnector.getLibrariesFromSite();
            if (libs == null) {

                JOptionPane.showMessageDialog(null, "Connection to website refused\nCheck your internet connection",
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);

                JLabel icon = new JLabel(
                        new ImageIcon(new ImageIcon("src/res/internet.png").getImage().getScaledInstance(100, 100,
                                Image.SCALE_SMOOTH)));
                icon.setBounds(5, 5, 100, 100);
                libraries.add(icon);

                return;
            }

            int index = 0;
            for (JsonObject jsonObject : libs) {

                String nam = jsonObject.get("name").toString();
                String desc = jsonObject.get("desc").toString();
                String uploader = jsonObject.get("uploader").toString();
                String date = jsonObject.get("time").toString();
                String maven = jsonObject.get("maven").toString();

                if (desc.length() > 214) {

                    desc = desc.substring(0, 214) + " (...)\"";
                }

                JPanel panel = new JPanel();
                BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
                panel.setLayout(layout);

                panel.setBounds(5, 5 + (225 * index), 410, 220);

                Box box = Box.createVerticalBox();

                JLabel name = new JLabel("<html><div style=\"margin: 5px;\"><h1>" + nam.substring(1, nam.length() - 1)
                        + "</h1></div></html>");
                name.setAlignmentX(SwingConstants.CENTER);
                box.add(name);

                JLabel details = new JLabel("<html><div style=\"margin: 5px; width: 100%\"><p>"
                        + desc.substring(1, desc.length() - 1) + "</p><br><hr></div></html>");
                details.setAlignmentX(SwingConstants.CENTER);
                box.add(details);

                JLabel uploadInfo = new JLabel("<html><div style=\"margin: 5px; width: 100%\"><p>Uploaded by "
                        + uploader.substring(1, uploader.length() - 1) + " at " + date.substring(1, date.length() - 1)
                        + "</p></div></html>");
                uploadInfo.setAlignmentX(SwingConstants.CENTER);
                box.add(uploadInfo);

                JButton downloadButton = Style.GetStyledButton("Add to project");
                JLabel invalid = new JLabel("No download available");

                downloadButton.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

                        Project.addDependendyString(maven.substring(1, maven.length() - 1));
                    }

                });

                if (!maven.contains("\"")) {

                    box.add(invalid);
                } else {

                    box.add(downloadButton);
                }

                panel.add(box);

                index++;
                libraries.add(panel);
            }

            libraries.setPreferredSize(new Dimension(libraries.getBounds().width, 5 + 225 * index));

        } catch (Exception e) {

            e.printStackTrace();
        }

    }
}