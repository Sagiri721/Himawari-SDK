

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Components.Structs.TileSet;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class TilesetPreview extends JPanel {

    MapEditor m;

    public TilesetPreview(MapEditor editor) {
        
        m = editor;
        setBounds(0, 0, 220, 500);

        JButton quickButton = new JButton("Quick Selection");
        quickButton.setBounds(5, 5, 150, 25);

        quickButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    m.curTile = Integer.valueOf(JOptionPane.showInputDialog(null, "Tile index"));
                    m.prev.setIcon(m.getPreview());

                } catch (Exception ee) {
                }
            }

        });

        add(quickButton);

        BufferedImage[] images = MapEditor.map.tileSet.sprites;
        int index = 0;
        for (BufferedImage icon : images) {

            Tile t = new Tile(index, icon);
            t.setBounds(5, (69 * index) + 30, 200, 64);

            add(t);
            index++;
        }

        setPreferredSize(new Dimension(220, 69 * index + 5));

        repaint();
        setLayout(null);
    }

    public void reload(){

        JButton quickButton = new JButton("Quick Selection");
        quickButton.setBounds(5, 5, 150, 25);

        quickButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    m.curTile = Integer.valueOf(JOptionPane.showInputDialog(null, "Tile index"));
                    m.prev.setIcon(m.getPreview());

                } catch (Exception ee) {
                }
            }

        });

        add(quickButton);

        BufferedImage[] images = MapEditor.map.tileSet.sprites;
        int index = 0;
        for (BufferedImage icon : images) {

            Tile t = new Tile(index, icon);
            t.setBounds(5, (69 * index) + 30, 200, 64);

            add(t);
            index++;
        }

        setPreferredSize(new Dimension(220, 69 * index + 5));

        repaint();
        setLayout(null);
    }

    private class Tile extends JPanel {

        public Tile(int i, BufferedImage preview) {

            JLabel name = new JLabel("Tile number " + i);
            JLabel icon = new JLabel(new ImageIcon(preview.getScaledInstance(64, 64, Image.SCALE_SMOOTH)));

            JButton choose = new JButton("Choose tile");

            name.setBounds(5, 5, 300, 20);
            icon.setBounds(120, 5, 64, 64);
            choose.setBounds(5, 30, 100, 30);

            choose.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    m.curTile = i;
                    m.prev.setIcon(m.getPreview());
                }

            });

            add(name);
            add(icon);
            add(choose);

            setLayout(null);
        }
    }
}