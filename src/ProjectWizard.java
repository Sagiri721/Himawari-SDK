import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.*;
import java.io.*;
import java.awt.image.*;
import java.awt.event.*;

public class ProjectWizard extends JPanel implements ActionListener{

    public JProgressBar progressBar = new JProgressBar(0, 100);
    public JTextField outputField = new JTextField("No output to display");
    public JComboBox<String> recents = new JComboBox<String>();
    private JButton createProject = Style.GetStyledButton("Create Project");
    private JButton openProject = Style.GetStyledButton("Open Project");

    private JCheckBox mainObject = new JCheckBox("Add a starting Object"), camera = new JCheckBox("Add a configured Camera Object"), 
    gson = new JCheckBox("Use Gson library"), jackson = new JCheckBox("Use Jackson library"), slf4j = new JCheckBox("use SLF4J library"), 
    bullet = new JCheckBox("Use JBullet library");

    public int template = 0;
    public static String clone = null;

    public ProjectWizard(JFrame main) {

        ProjectWizard.clone = null;
        // setBackground(Color.DARK_GRAY);
        setBounds(0, 0, main.getWidth(), main.getHeight());

        JLabel title = new JLabel("Himawari - Project Control", SwingConstants.LEFT);
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

        mainObject.setSelected(true);
        mainObject.setBounds(5, 210, 300, 30);
        camera.setBounds(5, 230, 300, 30);
        
        gson.setBounds(5, 250, 300, 30);
        gson.setSelected(true);

        jackson.setBounds(5, 270, 300, 30);

        slf4j.setBounds(240, 210, 300, 30);
        bullet.setBounds(240, 230, 300, 30);

        add(gson);
        add(jackson);
        add(slf4j);
        add(bullet);
        add(mainObject);
        add(camera);

        String[] recentProjects = Functions.getRecentProjects();
        String[] modelList = new String[recentProjects.length + 1];

        modelList[0] = "-";
        for (int i = 1; i < modelList.length; i++) modelList[i] = recentProjects[i-1];

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(modelList);

        recents.setModel(model);

        JLabel label = new JLabel("Recent projects: ");
        label.setBounds(5, 95, 100, 20);
        recents.setBounds(100, 95, 310, 20);
        
        openProject.setBounds(5, 50, 200, 40);
        
        outputField.setBounds(5, 150, 410, 25);
        outputField.setEditable(false);
        progressBar.setBounds(5, 135, 410, 10);
        
        add(recents);
        add(label);
        add(progressBar);
        add(outputField);
        add(openProject);
        add(title);

        createProject.addActionListener(this);
        openProject.addActionListener(this);

        JPanel templates = new JPanel();
        JScrollPane tempPanel = new JScrollPane(templates);
        tempPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        tempPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        tempPanel.setBounds(0, 300, 430, 215);

        JLabel temp = new JLabel("Project Templates"), sets = new JLabel("Creation settings");

        sets.setFont(Style.HEADER_FONT);
        sets.setBounds(5, 180, 410, 30);

        temp.setFont(Style.HEADER_FONT);
        temp.setBounds(5, 310, 300, 30);

        createProject.setBounds(215, 50, 200, 40);

        templates.setLayout(null);
        add(createProject);
        add(sets);
        add(temp);

        Template[] temps = Functions.getTemplates();

        int index = 0;
        for (Template t : temps) {

            t.setBounds(5 + (index * (t.getWidth() + 10)), 60, t.getWidth(), t.getHeight());
            if(index == template) t.setBackground(t.myBackground.darker());

            templates.add(t);
            index++;
        }
        templates.setPreferredSize(new Dimension(index * (210) + 5, 305));

        add(tempPanel);
        setLayout(null);

        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setLocationRelativeTo(null);
        main.setResizable(false);
    }

    public static class Template extends JPanel implements MouseListener {

        private JPanel self;
        private Color myBackground;

        private String clone = null;

        public Template(String name, String image, String description, String type, String clone) {

            this.self = this;
            this.myBackground = getBackground();

            this.clone = clone.trim() == "" ? null : clone;

            setSize(200, 140);
            setBackground(Style.MAIN_BACKGROUND);
            setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

            JLabel title = new JLabel(name, SwingConstants.LEFT);
            title.setForeground(Color.black);
            title.setBounds(5, 0, 500, 30);
            title.setFont(Style.FONT1);

            add(title);

            try {

                BufferedImage cover = ImageIO.read(new File("src\\res\\projects\\" + image));
                JLabel coverLabel = new JLabel(new ImageIcon(cover));
                coverLabel.setBorder(BorderFactory.createLineBorder(Color.black, 2));

                coverLabel.setBounds(5, 35, 190, 100);
                add(coverLabel);

            } catch (Exception e) {
            }

            addMouseListener(this);
            setFocusable(true);
            requestFocus();

            setToolTipText(description);
            setLayout(null);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            
            self.setBackground(myBackground.darker());
            ProjectWizard.clone = this.clone;
            
            //Unset other's colors
            for (Component c : getParent().getComponents()) {
            
                if(c == this) continue;
                if(c.getClass() == Template.class) c.setBackground(myBackground);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
    }

    public void updateProgress(String message, int amount) {

        progressBar.setValue(progressBar.getValue() + amount);
        progressBar.repaint();

        outputField.setText(message);

        System.out.println(message);
    }

    public void progressReset(){

        progressBar.setValue(0);
        progressBar.revalidate();
        progressBar.repaint();

        outputField.setText("Output cleared");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getSource() == createProject) {

            Functions.CreateProject(this, ProjectWizard.clone);
        } else if (e.getSource() == openProject) {

            if(recents.getSelectedIndex() == 0){
                
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    
                int result = fc.showDialog(this, "Open project");
                if (result == JFileChooser.APPROVE_OPTION) {
    
                    // Search for a valid project directory
                    File folder = fc.getSelectedFile();
    
                    if (!new File(folder.getAbsolutePath() + "/pom.xml").exists()) {
    
                        JOptionPane.showMessageDialog(null, "Inavalid directory, there is no pom.xml", "ERROR",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
    
                    Functions.addRecentProject(folder);
    
                    // Open the project
                    new Project(folder);
                }
            }else {

                new Project(new File((String) recents.getSelectedItem()));
            }
        }
    }
}
