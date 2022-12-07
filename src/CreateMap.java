
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;

import Components.Structs.Map;
import Components.Structs.TileSet;

import java.awt.event.*;
import java.io.File;

public class CreateMap extends JFrame implements ActionListener {

    int mode;

    public String tilesetPath;

    File f;

    JButton button0 = new JButton("Import tile set image"), button1 = new JButton("Create tile set");
    JSpinner sp = new JSpinner(), sp2 = new JSpinner();

    public CreateMap(int mode) {
        this.mode = mode;

        DrawOptions();
    }

    void DrawOptions() {
        setSize(400, 200);

        JLabel label0 = new JLabel("Create a tile set, just advance to start without tileset"),
                label1 = new JLabel("Set the tile size");
        label0.setBounds(5, 5, 300, 20);
        button1.setBounds(5, 100, 140, 30);
        button1.addActionListener(this);
        sp.setBounds(5, 60, 100, 30);
        label1.setBounds(105, 50, 120, 30);

        switch (mode) {
            case 0:
                // Creating a tileset for the map
                setTitle("Create tile set");

                button0.setBounds(5, 30, 150, 20);
                button0.addActionListener(this);

                add(button0);
                add(sp);
                break;
            case 1:
                label0.setText("Create Map");

                button1.setText("Start editing");
                label1.setText("Width and height");
                label1.setBounds(5, 25, 100, 50);
                break;
        }

        add(button1);
        add(label0);
        add(sp);
        add(label1);

        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button0 && mode == 0) {

            JFileChooser fc = new JFileChooser();
            int i = fc.showOpenDialog(this);

            if (i == JFileChooser.APPROVE_OPTION) {
                f = fc.getSelectedFile();

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

        if (e.getSource() == button1) {

            if (mode == 0) {

                try {

                    Window.tileset = new TileSet(f.getAbsolutePath(), (int) sp.getValue());
                } catch (Exception ee) {

                    Window.tileset = null;
                }

                new CreateMap(1);

                setVisible(false);
                dispose();
            } else {
                // If its mode 1
                if ((int) sp.getValue() > 0) {

                    if (((int) sp2.getValue() * (int) sp.getValue()) > 500 * 500) {

                        JOptionPane.showMessageDialog(null, "The maximum is 500x500, you can't surpass that limit");
                    } else {

                        Map map = new Map(Window.tileset, (int) sp.getValue(),
                                (int) sp.getValue());

                        Functions.openMapEditor(map);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Can't create map");
                }

            }
        }
    }

}
