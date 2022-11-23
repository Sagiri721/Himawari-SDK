import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import Components.*;
import javax.swing.*;

import Components.Structs.Map;
import Components.Structs.TileSet;

import java.awt.*;

public class ResourceExplorer extends JPanel implements ActionListener {

    // List of all the folders the engine has to handle
    protected static final String[] options = { "Sprites", "Sound", "Objects", "Maps" };

    JButton openB = Style.GetStyledButton("Open file"), del = Style.GetStyledButton("Delete file"),
            view = Style.GetStyledButton("Inspect file");

    JComboBox<String> box = new JComboBox<String>(options);
    JList<String> list = new JList<>();

    public ResourceExplorer() {

        JLabel title0 = new JLabel("Resource Manager");
        title0.setBounds(5, 5, 300, 20);

        // get inverted color
        float[] rgb = Style.FILE_EXPLORER_BACKGROUND.getRGBComponents(null);
        title0.setForeground(new Color(1.0f - rgb[0], 1.0f - rgb[1], 1.0f - rgb[2]));

        list.setName("erasable");
        list.setBounds(5, 110, 340, 900);
        list.setVisible(false);

        openB.setBounds(5, 80, 110, 25);
        del.setBounds(120, 80, 110, 25);
        view.setBounds(235, 80, 110, 25);

        openB.addActionListener(this);
        del.addActionListener(this);
        view.addActionListener(this);

        box.setSelectedIndex(-1);
        box.setBounds(0, 30, 350, 30);

        box.addActionListener(this);

        add(view);
        add(del);
        add(openB);
        add(box);
        add(title0);
        add(list);
        setLayout(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == box) {

            switch (box.getSelectedIndex()) {

                case -1:
                    // Erase
                    break;
                default:
                    // Fetch sprites from
                    updateResources();
                    break;
            }
        } else if (e.getSource() == openB) {

            if (list.getSelectedIndex() == -1) {

                JOptionPane.showMessageDialog(null, "No file was selected", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }

            openFile();
        } else if (e.getSource() == del) {

            if (list.getSelectedIndex() == -1) {

                JOptionPane.showMessageDialog(null, "No file was selected", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] dirs = { "/Sprites", "/Sounds", "/Objects", "/Rooms" };
            File file = new File(Project.engineFiles.getAbsolutePath() + dirs[box.getSelectedIndex()]).listFiles()[list
                    .getSelectedIndex()];

            String command = "del " + file.getAbsolutePath();
            Functions.WriteAndRun(command, "delete_file.bat");

            updateResources();
        } else if (e.getSource() == view) {

            if (list.getSelectedIndex() == -1 || box.getSelectedIndex() != 2) {

                JOptionPane.showMessageDialog(null, "No object was selected", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // inspectObject(new File(Project.engineFiles.getAbsolutePath() +
            // "/Objects").listFiles()[list.getSelectedIndex()]);
        }
    }

    public void updateResources() {

        String[] dirs = { "/Sprites", "/Sounds", "/Objects", "/Rooms" };
        list.setVisible(true);
        File spritesFolder = new File(Project.engineFiles.getAbsolutePath() + dirs[box.getSelectedIndex()]);

        if (spritesFolder.listFiles() == null)
            return;

        String[] files = new String[spritesFolder.listFiles().length];

        int index = 0;
        for (File f : spritesFolder.listFiles()) {

            files[index] = f.getName();
            index++;
        }

        list.setListData(files);
    }

    public void openFile() {

        String[] dirs = { "/Sprites", "/Sounds", "/Objects", "/Rooms" };
        File opened = new File(Project.engineFiles.getAbsolutePath() + dirs[box.getSelectedIndex()]).listFiles()[list
                .getSelectedIndex()];

        if (box.getSelectedIndex() != 3) {

            try {

                File openfile = new File("src/functions/output/open_file.bat");
                FileWriter fw = new FileWriter(openfile);

                String command = opened.getAbsolutePath();
                fw.write(command);
                fw.close();

                Runtime.getRuntime().exec(openfile.getAbsolutePath());

            } catch (Exception e) {

                JOptionPane.showMessageDialog(null, "Trouble opening file", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {

            String dirpath = opened.getAbsolutePath();
            String tilesetPath = "";

            try {

                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setCurrentDirectory(new File(Project.path + "/src/main/java/Assets/Sprites"));

                int option = fc.showDialog(null, "Choose tileset");

                // Import tileset
                if (option == JFileChooser.APPROVE_OPTION) {

                    File f = fc.getSelectedFile();

                    String s = f.getName().substring(f.getName().length() - 4);

                    if (s.equals(".png") || s.equals(".jpg")) {
                        // Valid image file
                        tilesetPath = f.getAbsolutePath();

                        tilesetPath = tilesetPath.replace("\\", "/");

                        JOptionPane.showMessageDialog(this, "Image succefully imported");
                    } else
                        JOptionPane.showMessageDialog(this, "Invalid image file");

                    TileSet tileset = new TileSet(tilesetPath,
                            Integer.parseInt(JOptionPane.showInputDialog(null, "Tile set size?")));

                    // Get map measures

                    Scanner file = new Scanner(dirpath + "/room-tiles.txt");
                    Integer dimensions = file.nextLine().split(" ").length;

                    Map map = new Map(tileset, dimensions, dimensions);
                    MapEditor.map = map;
                    Project.preview.importMap(opened.getAbsolutePath());

                    file.close();
                }

            } catch (Exception e) {

                JOptionPane.showMessageDialog(null, "There was a problem opening the map editor", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
