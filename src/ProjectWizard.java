import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.*;
import java.io.*;
import java.awt.image.*;

public class ProjectWizard extends JPanel {

    public ProjectWizard(JFrame main) {

        // setBackground(Color.DARK_GRAY);
        setBounds(0, 0, main.getWidth(), main.getHeight());

        JLabel title = new JLabel("Himawari - Project Wizard", SwingConstants.LEFT);
        title.setForeground(Style.MAIN_TEXT_COLOR);
        title.setFont(Style.HEADER_FONT);
        title.setBounds(5, 5, getWidth(), 30);

        try {

            BufferedImage img = ImageIO.read(new File("src/res/wizard.png"));
            Image newImg = img.getScaledInstance(150, 150, BufferedImage.SCALE_SMOOTH);

            JLabel icon = new JLabel(new ImageIcon(newImg));
            icon.setBounds(340, 30, newImg.getWidth(null), newImg.getHeight(null));

            // add(icon);

        } catch (Exception e) {
        }

        JButton openProject = Style.GetStyledButton("Open Project");

        openProject.setBounds(5, 50, 200, 40);

        add(openProject);
        add(title);

        JButton createProject = Style.GetStyledButton("Create Project");
        JPanel templates = new JPanel();
        templates.setBounds(5, 100, 475, 255);
        templates.setBackground(Style.SECONDARY_BACKGROUND);

        JLabel temp = new JLabel("Project Templates");
        temp.setFont(new Font("Verdana", Font.BOLD, 18));
        temp.setBounds(5, 5, 300, 20);

        createProject.setBounds(230, 5, 200, 20);

        templates.setLayout(null);
        templates.add(createProject);
        templates.add(temp);

        Template[] temps = { new Template("EMPTY PROJECT", "empty.png",
                "An empty canvas for you to create your games upon"),
                new Template("2D PLATFORMER", "platformer.png",
                        "Creates a little game set up with 2D physics and platforming movement") };

        int index = 0;
        for (Template t : temps) {

            t.setBounds(5 + (index * (t.getWidth() + 10)), 60, t.getWidth(), t.getHeight());
            templates.add(t);
            index++;
        }
        add(templates);
        setLayout(null);

        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setLocationRelativeTo(null);
        main.setResizable(false);
    }

    class Template extends JPanel {

        public Template(String name, String image, String description) {

            setSize(200, 140);
            setBackground(Style.MAIN_BACKGROUND);
            setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

            JLabel title = new JLabel(name, SwingConstants.LEFT);
            title.setForeground(Color.black);
            title.setBounds(5, 0, 400, 30);
            title.setFont(new Font("Impact", Font.PLAIN, 18));
            add(title);

            try {

                BufferedImage cover = ImageIO.read(new File("src\\res\\projects\\" + image));
                JLabel coverLabel = new JLabel(new ImageIcon(cover));
                coverLabel.setBorder(BorderFactory.createLineBorder(Color.black, 2));

                coverLabel.setBounds(5, 35, 190, 100);
                add(coverLabel);

            } catch (Exception e) {
            }

            setToolTipText(description);
            setLayout(null);
        }
    }
}
