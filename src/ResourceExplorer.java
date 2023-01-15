import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
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
            view = Style.GetStyledButton("Inspect file"), add = Style.GetStyledButton("+");

    JComboBox<String> box = new JComboBox<String>(options);
    JList<String> list = new JList<>();

    public ResourceExplorer() {

        JLabel title0 = new JLabel("Resource Manager");
        title0.setBounds(5, 5, 300, 20);

        // get inverted color
        // float[] rgb = Style.FILE_EXPLORER_BACKGROUND.getRGBComponents(null);
        // title0.setForeground(new Color(1.0f - rgb[0], 1.0f - rgb[1], 1.0f - rgb[2]));

        list.setName("erasable");
        list.setBounds(5, 110, 340, 900);
        list.setVisible(false);
        list.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {

                    if (box.getSelectedIndex() != 2)
                        return;

                    // Double-click detected
                    String[] dirs = { "/Sprites", "/Sounds", "/Objects", "/Rooms" };
                    File opened = new File(Project.engineFiles.getAbsolutePath() + dirs[box.getSelectedIndex()])
                            .listFiles()[list.getSelectedIndex()];

                    new CodeEditor(opened);
                }
            }
        });

        openB.setBounds(5, 80, 110, 25);
        del.setBounds(120, 80, 110, 25);
        view.setBounds(235, 80, 110, 25);

        openB.addActionListener(this);
        del.addActionListener(this);
        view.addActionListener(this);

        box.setSelectedIndex(-1);
        box.setBounds(0, 30, 350 - 32 - 5, 30);

        box.addActionListener(this);
        add.setBounds(350 - 32, 30, 32, 32);
        add.addActionListener(this);

        add(add);
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

            if (list.getSelectedIndex() == -1) {

                JOptionPane.showMessageDialog(null, "No object was selected", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (box.getSelectedIndex() == 2) {

                Project.inspector.inspectObject(new File(Project.engineFiles.getAbsolutePath() +
                        "/Objects").listFiles()[list.getSelectedIndex()]);
            } else {

                String[] dirs = { "/Sprites", "/Sounds", "/Objects", "/Rooms" };
                File file = new File(Project.engineFiles.getAbsolutePath() + dirs[box.getSelectedIndex()])
                        .listFiles()[list
                                .getSelectedIndex()];

                new FileDetailsWindow(file);
            }

        } else if (e.getSource() == add) {

            JFrame chooser = new JFrame("Media chooser");
            chooser.setSize(335, 200);

            JLabel titLabel = new JLabel("Choose a media to upload");
            titLabel.setFont(Style.HEADER_FONT);
            titLabel.setBounds(5, 5, 350, 25);

            JButton sprite = new JButton(new ImageIcon("src/res/utils/sprite.png")),
                    sound = new JButton(new ImageIcon("src/res/utils/sound.png")),
                    fonts = new JButton(new ImageIcon("src/res/utils/font.png"));

            sprite.setBounds(5, 50, 100, 100);
            sound.setBounds(110, 50, 100, 100);
            fonts.setBounds(215, 50, 100, 100);

            sprite.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    try {
                        Functions.CopyFilesTo("Sprites");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    chooser.setVisible(false);
                    chooser.dispose();
                }
            });

            sound.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    try {
                        Functions.CopyFilesTo("Sprites");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    chooser.setVisible(false);
                    chooser.dispose();
                }
            });

            fonts.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    try {
                        Functions.CopyFilesTo("Sprites");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    chooser.setVisible(false);
                    chooser.dispose();
                }
            });

            chooser.add(sprite);
            chooser.add(sound);
            chooser.add(fonts);
            chooser.add(titLabel);

            chooser.setResizable(false);
            chooser.setLocationRelativeTo(null);
            chooser.setLayout(null);
            chooser.setVisible(true);
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
        list.repaint();
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
            int tilesetSize = 32;

            try {

                File[] mapdata = opened.listFiles();
                File img = null;
                for (File f : mapdata) {

                    if (f.getName().contains("tiles-")) {

                        img = f;

                        tilesetPath = f.getAbsolutePath();
                        tilesetSize = Integer.parseInt(
                                f.getName().substring(f.getName().lastIndexOf("-") + 1, f.getName().lastIndexOf(".")));
                    }
                }

                if (img == null) {

                    JFileChooser fc = new JFileChooser();
                    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fc.setCurrentDirectory(new File(
                            Project.path.getAbsolutePath() + "/src/main/java/" + Project.projectName
                                    + "/Assets/Sprites"));

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

                        tilesetSize = Integer.parseInt(JOptionPane.showInputDialog(null, "Tile set size?"));

                    } else {

                        return;
                    }
                }

                // Get map measures
                TileSet tileset = new TileSet(tilesetPath, tilesetSize);

                Scanner file = new Scanner(dirpath + "/room-tiles.txt");
                Integer dimensions = file.nextLine().split(" ").length;

                Map map = new Map(tileset, dimensions, dimensions);
                MapEditor.map = map;
                Project.preview.importMap(opened.getAbsolutePath());

                file.close();

            } catch (Exception e) {

                JOptionPane.showMessageDialog(null, "There was a problem opening the map editor", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}
