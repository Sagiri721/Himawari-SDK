import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.awt.image.*;

public class ProjectWizard extends JPanel implements ActionListener{

    public JProgressBar progressBar = new JProgressBar(0, 100);
    public JTextField outputField = new JTextField("No output to display");
    public JComboBox<String> recents = new JComboBox<String>(new String[] {"---"});
    private JButton createProject = Style.GetStyledButton("Create Project");

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

        JLabel label = new JLabel("Recent projects: ");
        label.setBounds(5, 95, 100, 20);
        recents.setBounds(100, 95, 310, 20);
        
        JButton openProject = Style.GetStyledButton("Open Project");
        openProject.setBounds(5, 50, 200, 40);
        
        outputField.setBounds(5, 150, 410, 20);
        outputField.setEditable(false);
        progressBar.setBounds(5, 135, 410, 10);
        
        add(recents);
        add(label);
        add(progressBar);
        add(outputField);
        add(openProject);
        add(title);

        createProject.addActionListener(this);

        JPanel templates = new JPanel();
        templates.setBounds(5, 170, 410, 255);
        templates.setBackground(Style.SECONDARY_BACKGROUND);

        JLabel temp = new JLabel("Project Templates");
        temp.setFont(new Font("Verdana", Font.BOLD, 18));
        temp.setBounds(5, 5, 300, 20);

        createProject.setBounds(215, 50, 200, 40);

        templates.setLayout(null);
        add(createProject);
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

    public void updateProgress(String message, int amount) {

        progressBar.setValue(progressBar.getValue() + amount);
        progressBar.repaint();

        outputField.setText(message);
    }

    public void progressReset(){

        progressBar.setValue(0);
        progressBar.repaint();

        outputField.setText("Output cleared");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getSource() == createProject) {

            Functions.CreateProject(0, this);
        }
    }
}
