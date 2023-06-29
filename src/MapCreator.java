import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import Components.Structs.Map;

import java.awt.Image;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class MapCreator extends JFrame implements ActionListener{

    JSpinner sp = new JSpinner(), sp2 = new JSpinner();
    JButton tileset = new JButton("Choose tileset");
    JButton create = new JButton("Create map");
    JTextField name = new JTextField("my_room");

    public String tilesetPath;
    
    public MapCreator() {

        setTitle("Map wizard");
        setSize(260, 400);

        JLabel label = new JLabel("Map wizard");
        label.setFont(Style.HEADER_FONT);
        label.setBounds(5, 5, 200, 30);

        name.setBounds(5, 40, 230, 30);

        JLabel label1 = new JLabel("Set the tile size"), label2 = new JLabel("Set the map size");

        label1.setBounds(5, 80, 200, 20);
        sp.setBounds(90, 80, 80, 20);

        tileset.addActionListener(this);
        tileset.setBounds(5, 105, 230, 30);

        label2.setBounds(5, 150, 90, 20);
        sp2.setBounds(90, 150, 80, 20);

        create.setBounds(5, 180, 230, 30);

        create.addActionListener(this);

        JLabel icon = new JLabel(new ImageIcon(new ImageIcon("src/res/room_wizard.png").getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
        icon.setBounds(-30, 210-67 , 300, 300);

        add(icon);
        add(create);
        add(label2);
        add(sp2);
        add(label1);
        add(label);
        add(tileset);
        add(sp);
        add(name);

        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getSource() == create) {

            if ((int) sp.getValue() > 0) {

                if (((int) sp2.getValue() * (int) sp.getValue()) > 500 * 500) {

                    JOptionPane.showMessageDialog(null, "The maximum is 500x500, you can't surpass that limit");
                } else {

                    if(name.getText().trim() == ""){
                        JOptionPane.showMessageDialog(null, "The room name can't be empty");
                        return;
                    }

                    name.setText(name.getText().replace(" ", "-"));
                    //Room file
                    File f = new File(Project.engineFiles.getAbsoluteFile() + "/Rooms/" + name.getText());
                    if(f.mkdir() == false) {

                        JOptionPane.showMessageDialog(null, "There was a problem creating the map", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    File tileset = new File(tilesetPath == null ? "" : tilesetPath);

                    if(tileset.exists()) {

                        try {

                            String newFile = f.getAbsolutePath() + "/tiles-" + (int)(sp.getValue()) + (tileset.getName().substring(tileset.getName().lastIndexOf(".")));
                            Files.copy(tileset.toPath(), new File(newFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }

                    try {
                        File tiles = new File(f.getAbsolutePath() + "/room-tiles.txt");

                        String out = "";
                        for(int i = 0; i < (int) sp2.getValue(); i++) 
                        for(int j = 0; j < (int) sp2.getValue(); j++)
                        {
                            out += (j==(int)sp2.getValue()-1 ? "0\n" : "0 ");
                        }

                        FileOutputStream fos = new FileOutputStream(tiles.getAbsolutePath());
                        fos.write(out.getBytes());
                        fos.flush();
                        fos.close();

                        File objs = new File(f.getAbsolutePath() + "/room-objects.txt");
                        objs.createNewFile();

                    } catch (Exception ee) {
                        
                        ee.printStackTrace();
                    }

                    Project.preview.importMap(f.getAbsolutePath());
                    setVisible(false);
                    dispose();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Tileset size can't be negative or 0");
            }
            
        }else if (e.getSource() == tileset) {

            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(Project.engineFiles.getAbsoluteFile() + "/Sprites"));
            int i = fc.showOpenDialog(this);

            if (i == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();

                String s = f.getName().substring(f.getName().length() - 4);

                if (s.equals(".png") || s.equals(".jpg")) {
                    // Valid image file
                    tilesetPath = f.getAbsolutePath();
                    tilesetPath = tilesetPath.replace("\\", "/");

                    JOptionPane.showMessageDialog(this, "Image succefully imported");
                } else
                    JOptionPane.showMessageDialog(this, "Invalid image file");
            }
        }
    }
}
