import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.awt.*;

public class MapEditor {

    public Map map;
    JFrame main;
    JLabel prev, ll, pos;

    private int displayX, displayY, maxSize;
    private int x = 0, y = 0;

    int layer = 0;

    JButton l1 = new JButton(new ImageIcon("src/res/layer1.png")),
            l2 = new JButton(new ImageIcon("src/res/layer2.png"));

    JButton export = new JButton(new ImageIcon("src/res/export.png")),
            importB = new JButton(new ImageIcon("src/res/import.png"));

    int[][] mapOutput;
    List<Object> objects = new ArrayList<>();
    int curTile = 0;

    Object currentObject = null;
    BufferedImage oImg;

    MapEditor(Map map) {

        try {
            oImg = ImageIO.read(new File("src/res/object.png"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        this.map = map;
        displayX = map.w;
        displayY = map.h;

        maxSize = displayX;

        mapOutput = new int[map.w][map.h];

        main = new JFrame();

        main.setTitle("Map editing tool");
        main.setSize(800, 800);
        main.setLayout(null);

        JPanel panel = new DrawPanel();
        panel.setBounds(0, 32, 790, 790);
        panel.setBackground(Color.WHITE);

        JLabel tilePreview = new JLabel("Current tile: ");
        tilePreview.setBounds(37, 0, 100, 32);
        ImageIcon i = getPreview();
        prev = new JLabel(i);
        prev.setBounds(70, 0, 100, 32);

        JButton b = new JButton(new ImageIcon("src/res/change.png"));
        b.setBounds(0, 0, 32, 32);

        JButton o = new JButton(new ImageIcon("src/res/objects.png"));
        o.setToolTipText("Choose an object to place");
        o.setBounds(303, 0, 32, 32);

        o.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    String name = JOptionPane.showInputDialog(null, "Object name: ");
                    int angle = Integer.parseInt(JOptionPane.showInputDialog(null, "Object rotation (degrees): ", 0));

                    int scaleX = Integer.parseInt(JOptionPane.showInputDialog(null, "Object scale X: ", 1));
                    int scaleY = Integer.parseInt(JOptionPane.showInputDialog(null, "Object scale Y: ", 1));

                    if (angle < 0 || angle > 360) {

                        JOptionPane.showMessageDialog(null, "Invalid angle", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    } else if (scaleX <= 0 || scaleY <= 0 || scaleX > 100 || scaleY > 100) {

                        JOptionPane.showMessageDialog(null, "Invalid scale for 1-100", "ERROR",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    } else {

                        Object o = new Object();
                        o.name = name;
                        o.angle = angle;
                        o.w = scaleX;
                        o.h = scaleY;

                        currentObject = o;

                        main.getComponentAt(0, 32).repaint();
                    }

                } catch (Exception ee) {

                    JOptionPane.showMessageDialog(null, "Invalid input", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        main.add(o);

        l1.setBounds(139, 0, 32, 32);
        l1.setEnabled(false);
        l2.setBounds(176, 0, 32, 32);

        ll = new JLabel((layer == 0 ? "Layer: Tiles" : "Layer: Objects"));
        ll.setBounds(211, 0, 100, 32);

        importB.setBounds(639, 0, 32, 32);
        importB.setToolTipText("Import map");

        importB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fc = new JFileChooser();

                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int o = fc.showOpenDialog(null);

                if (o == JFileChooser.APPROVE_OPTION) {

                    String dirPath = fc.getSelectedFile().getAbsolutePath();

                    importMap(dirPath);
                }
            }

        });

        main.add(l1);
        main.add(l2);
        main.add(ll);
        main.add(importB);

        export.setBounds(602, 0, 32, 32);
        export.setToolTipText("Export map");

        export.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fc = new JFileChooser();

                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int o = fc.showOpenDialog(null);

                if (o == JFileChooser.APPROVE_OPTION) {
                    String savePath = fc.getSelectedFile().getAbsolutePath();

                    String outTiles = "";
                    String outObjects = "";

                    for (int i = 0; i < map.h; i++) {
                        for (int j = 0; j < map.w; j++) {

                            outTiles += mapOutput[j][i] + (j < map.w - 1 ? " " : "");
                        }

                        outTiles += "\n";
                    }

                    for (Object oo : objects) {

                        outObjects += oo.name + " " + getRealCoords(oo.x) + "-" + getRealCoords(oo.y) + " "
                                + oo.angle + " " + oo.w + "-" + oo.h
                                + "\n";
                    }

                    String roomName = JOptionPane.showInputDialog(null, "Room name: ");
                    // Output the files
                    File dir = new File(savePath + "\\room" + roomName);
                    if (!dir.exists()) {

                        dir.mkdir();
                        File tiles = new File(dir.getAbsolutePath() + "\\room-tiles.txt");
                        try {

                            tiles.createNewFile();
                            FileWriter fw = new FileWriter(tiles);
                            fw.write(outTiles);
                            fw.close();

                            if (outObjects != "") {

                                // Create objects file
                                File objs = new File(dir.getAbsolutePath() + "\\room-objects.txt");
                                objs.createNewFile();

                                fw = new FileWriter(objs);
                                fw.write(outObjects);
                                fw.close();
                            }

                            JOptionPane.showMessageDialog(null, "Export complete", "SUCCESS",
                                    JOptionPane.INFORMATION_MESSAGE);

                        } catch (IOException e1) {
                            JOptionPane.showMessageDialog(null, "Export canceled", "ERROR", JOptionPane.ERROR_MESSAGE);
                            e1.printStackTrace();
                        }
                    } else {

                        JOptionPane.showMessageDialog(null, "Room already exists", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }

                } else {

                    JOptionPane.showMessageDialog(null, "Export canceled", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        main.add(export);

        JButton settings = new JButton(new ImageIcon("src/res/settings.png")),
                position = new JButton(new ImageIcon("src/res/position.png"));
        settings.setBounds(345, 0, 32, 32);
        position.setBounds(383, 0, 32, 32);

        position.setToolTipText("Change display position");
        settings.setToolTipText("Change window settings");

        pos = new JLabel("Root point: 0,0");
        pos.setBounds(420, 0, 100, 32);

        main.add(pos);
        main.add(position);
        main.add(settings);

        l1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                layer = 0;
                l2.setEnabled(true);
                l1.setEnabled(false);

                ll.setText((layer == 0 ? "Layer: Tiles" : "Layer: Objects"));
            }

        });

        l2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                layer = 1;
                l1.setEnabled(true);
                l2.setEnabled(false);

                ll.setText((layer == 0 ? "Layer: Tiles" : "Layer: Objects"));
            }

        });

        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                String newTile = JOptionPane.showInputDialog(null, "New tile id:", curTile);

                try {

                    curTile = Integer.parseInt(newTile);
                    prev.setIcon(getPreview());
                } catch (Exception ee) {

                    JOptionPane.showMessageDialog(null, "Invalid id", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        settings.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    int size = Integer.parseInt(JOptionPane.showInputDialog(null, "Window block width", displayX));

                    if (size <= 0 || size > maxSize) {

                        JOptionPane.showMessageDialog(null, "Width out of bounds " + size + " for 1-" + maxSize,
                                "ERROR", JOptionPane.ERROR_MESSAGE);
                    } else {

                        displayX = size;
                        displayY = size;
                        main.getComponentAt(0, 32).repaint();
                    }
                } catch (Exception ee) {

                    JOptionPane.showMessageDialog(null, "Invalid input", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        position.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    int xx = Integer.parseInt(JOptionPane.showInputDialog(null, "x position?", x));
                    int yy = Integer.parseInt(JOptionPane.showInputDialog(null, "y position?", y));

                    if ((xx >= 0 && yy >= 00) && ((x + xx) <= maxSize && (y + yy) <= maxSize)) {

                        x = xx;
                        y = yy;

                        main.getComponentAt(0, 32).repaint();
                        pos.setText("Root point: " + x + "," + y);
                    } else {

                        JOptionPane.showMessageDialog(null,
                                "Position out of bounds " + xx + "," + yy + " for 0,0-" + maxSize + "," + maxSize,
                                "ERROR", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception ee) {

                    JOptionPane.showMessageDialog(null, "Invalid input", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        JButton reset = new JButton(new ImageIcon("src/res/reset.png"));
        reset.setBounds(530, 0, 32, 32);
        reset.setToolTipText("Settings reset");

        reset.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                curTile = 0;
                displayX = maxSize;
                displayY = maxSize;

                x = 0;
                y = 0;

                layer = 0;
                l2.setEnabled(true);
                l1.setEnabled(false);

                ll.setText((layer == 0 ? "Layer: Tiles" : "Layer: Objects"));
                pos.setText("Root point: " + x + "," + y);

                currentObject = null;

                main.getComponentAt(0, 32).repaint();
            }

        });

        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setLocationRelativeTo(null);
        // main.setLayout(null);
        main.setResizable(false);
        main.setVisible(true);

        main.add(reset);
        main.add(b);
        main.add(prev);
        main.add(tilePreview);
        main.add(panel);

        main.getContentPane().setBackground(Color.white);

        b.setToolTipText("Change the current tile");
        l1.setToolTipText("Change to tile placing mode");
        l2.setToolTipText("Change to object placing mode");

        panel.addMouseListener(new MouseInputListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

                int _x = e.getPoint().x;
                int _y = e.getPoint().y;

                int x_ = _x + (x * (800 / (displayX))), y_ = _y + (y * (800 / (displayY)));

                int tarX = x_ - (x_ % (800 / displayX));
                int tarY = y_ - (y_ % (800 / displayY));

                if (e.getButton() == 1) {

                    // Get relative position position

                    int xx = _x / (800 / (displayX));
                    int yy = _y / (800 / (displayY));

                    if (layer == 0) {

                        try {

                            mapOutput[x + xx][y + yy] = curTile;
                        } catch (Exception ee) {

                            JOptionPane.showMessageDialog(null, "Can't place tiles outside of map bounds", "ERROR",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } else {

                        if (currentObject != null) {

                            currentObject.x = x + (_x / (800 / displayX));
                            currentObject.y = y + (_y / (800 / displayY));

                            objects.add(currentObject);

                            Object o = new Object();
                            o.name = currentObject.name;
                            o.h = currentObject.h;
                            o.w = currentObject.w;
                            o.angle = currentObject.angle;

                            currentObject = o;
                        } else {

                            JOptionPane.showMessageDialog(null, "No object specified", "ERROR",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    main.getComponentAt(0, 32).repaint();
                } else if (e.getButton() == 3) {

                    // Delete objects

                    for (Iterator<Object> iterator = objects.iterator(); iterator.hasNext();) {
                        Object integer = iterator.next();
                        if (integer.matches(tarX, tarY)) {
                            iterator.remove();
                            break;
                        }
                    }

                    main.getComponentAt(0, 32).repaint();
                } else if (e.getButton() == 2) {

                    // Display info
                    for (Iterator<Object> i = objects.iterator(); i.hasNext();) {
                        Object o = i.next();

                        if (o.matches(tarX, tarY)) {

                            String info = o.infoDump();
                            JOptionPane.showMessageDialog(null, info, "OBJECT INFO", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }

            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }

        });
    }

    public ImageIcon getPreview() {

        BufferedImage i = map.tileSet.getTileOf(curTile);

        return new ImageIcon(i);
    }

    class DrawPanel extends JPanel {

        @Override
        public void paintComponent(Graphics g) {

            int xx = 800 / (displayX);
            int yy = 800 / (displayY);

            for (int i = 0; i < displayX; i++) {
                for (int j = 0; j < displayY; j++) {

                    try {
                        g.drawImage(map.tileSet.getTileOf(mapOutput[x + i][y + j]), i * xx, j * yy, xx, yy, null);
                    } catch (Exception e) {
                    }
                }
            }

            // Draw the actual map grid
            // The grid should extend for the entirety of the map
            // The grid cells should be the size of the tileset

            for (int i = 0; i < 800; i += xx) {

                g.drawLine(0, i, 800, i);
            }

            for (int j = 0; j < 800; j += yy) {

                g.drawLine(j, 0, j, 800);
            }

            for (Object o : objects) {

                g.drawImage(oImg, (o.x - x) * xx, (o.y - y) * yy, (xx * 2) / 3,
                        (yy * 2) / 3, null);
            }
        }
    }

    private int getRealCoords(int coord) {

        return coord * map.tileSet.size;
    }

    public void importMap(String dirPath) {

        try {
            File tiles = new File(dirPath + "\\room-tiles.txt");
            File obj = new File(dirPath + "\\room-objects.txt");

            String inTiles = "";
            String inObj = "";

            Scanner s = new Scanner(tiles);
            while (s.hasNextLine())
                inTiles += s.nextLine() + "\n";

            s.close();
            s = new Scanner(obj);
            while (s.hasNextLine())
                inObj += s.nextLine() + "\n";
            s.close();

            // Get the info from the objects
            objects.clear();

            for (String line : inObj.split("\n")) {

                String[] info = line.split(" ");

                Object oo = new Object();
                oo.name = info[0];
                oo.x = (Integer.parseInt(info[1].split("-")[0])) / map.tileSet.size;
                oo.y = (Integer.parseInt(info[1].split("-")[1])) / map.tileSet.size;

                oo.w = Integer.parseInt(info[3].split("-")[0]);
                oo.h = Integer.parseInt(info[3].split("-")[1]);

                oo.angle = Integer.parseInt(info[2]);

                objects.add(oo);
            }

            // Get the tiles
            boolean set = false;
            int count = 0;

            importTiles: {

                for (String line : inTiles.split("\n")) {

                    String[] indTiles = line.split(" ");

                    if (!set) {

                        // Set the matrix
                        int size = indTiles.length;
                        mapOutput = new int[size][size];

                        maxSize = size;
                        displayX = size;
                        displayY = size;

                        set = true;
                    }

                    for (int i = 0; i < indTiles.length; i++) {

                        try {
                            mapOutput[i][count] = Integer.parseInt(indTiles[i]);
                        } catch (Exception ee) {
                            ee.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Error importing tiles", "ERROR",
                                    JOptionPane.ERROR_MESSAGE);

                            break importTiles;
                        }
                    }

                    count++;
                }
            }

            main.getComponentAt(0, 32).repaint();

        } catch (Exception ee) {

            JOptionPane.showMessageDialog(null, "Unable to open files", "ERROR", JOptionPane.ERROR_MESSAGE);
            ee.printStackTrace();
        }
    }
}
