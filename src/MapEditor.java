

import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;

import Components.Structs.Map;
import Components.Structs.ObjectData;
import Components.Structs.TileSet;
import Components.Structs.Vec2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

public class MapEditor extends JPanel implements ChangeListener {

    public static Map map;
    public JLabel prev, ll, pos;
    public File saveDir = null;

    private int displayX, displayY, maxSize;
    private int x = 0, y = 0;
    private boolean fastScroll = false;

    public static String[] modes = { "One-by-One", "Line", "Rectangle", "Flood fill" };
    public static int paintMode = 0;
    private Point drag = null, mousePos = new Point();
    private MapEditor self;
    
    public static boolean pathing = false;
    public static List<Vec2> points = new ArrayList<Vec2>();

    int layer = 0;

    JButton l1 = new JButton(new ImageIcon("src/res/layer1.png")),
            l2 = new JButton(new ImageIcon("src/res/layer2.png")),
            l3 = new JButton(new ImageIcon("src/res/layer3.png"));

    JButton export = new JButton(new ImageIcon("src/res/export.png")),
            importB = new JButton(new ImageIcon("src/res/import.png")),
            importTileset = new JButton("Import Tileset"),
            importObjectList = new JButton("Import Objects"), shotcuts = new JButton("Shortcuts"),
            objectList = new JButton(new ImageIcon("src/res/objectlist.png")),
            parent = new JButton(new ImageIcon("src/res/parent.png"));

    JLabel saved = new JLabel("Up to date"), paintIndicator = new JLabel(modes[paintMode]);

    private int[][] mapOutput;
    public static List<Object> objects = new ArrayList<>();
    private Object selected = null;
    public double sensibility = 5;
    public int curTile = 0;

    Object currentObject = null;
    BufferedImage oImg;
    boolean showGrid = true;

    JTabbedPane panels = new JTabbedPane();
    JPanel mapEditing = new JPanel(), dataEditor = new JPanel();

    String[] objectModels = {};
    int current = 0;

    JTextArea box = new JTextArea();
    JButton nextObject = new JButton("^"), importObjects = new JButton("Import Object List");

    JSpinner scaleX = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)),
            scaleY = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)),
            rotation = new JSpinner(new SpinnerNumberModel(0, 0, 360, 90));

    public String spriteFolder = "";

    public void setSavePath(File path) {this.saveDir = path; }

    public MapEditor(Map map, Optional<File> mapLocation, File savePath) {

        self = this;
        this.saveDir = savePath;
        System.out.println(saveDir);
        setBounds(0, 0, 800, 830);
        setLayout(null);

        try {
            oImg = ImageIO.read(new File("src/res/object.png"));
        } catch (IOException e1) {

            e1.printStackTrace();
            return;
        }

        if (map.tileSet == null) {

            try {

                // Search for tile file, if it doesn't exist then create with default tile set
                TileSet tiles = new TileSet("src/res/defaultTile.png", 16);
                File file = null;
                Integer size = 0;

                findtiles: if (mapLocation.isPresent()) {

                    File[] f = mapLocation.get().listFiles();
                    for (File file2 : f) {

                        if (file2.getName().contains("tiles-"))
                            file = file2;
                    }

                    if (file == null)
                        break findtiles;

                    if (file.getName().contains("tiles-")) {

                        try {

                            String[] split = file.getName().split("tiles-");
                            size = Integer.valueOf(
                                    split[split.length - 1].substring(0, split[split.length - 1].lastIndexOf(".")));

                        } catch (Exception e) {

                            e.printStackTrace();
                            break findtiles;
                        }

                    } else {
                        break findtiles;
                    }

                    tiles = new TileSet(file.getAbsolutePath(), size);
                }

                MapEditor.map = new Map(tiles, map.w, map.h);

            } catch (Exception e) {

                e.printStackTrace();
                return;
            }
        } else
            MapEditor.map = map;

        displayX = map.w;
        displayY = map.h;

        maxSize = displayX;

        mapOutput = new int[map.w][map.h];

        // setTitle("Map editing tool");
        // setSize(800, 830);

        JPanel panel = new DrawPanel();
        panel.setBounds(0, 62, 790, 790);
        panel.setBackground(Color.WHITE);

        saved.setOpaque(true);
        saved.setBounds(5, 10, 80, 20);
        saved.setBackground(Color.WHITE);
        panel.add(saved);
        
        JLabel tilePreview = new JLabel("Current tile: ");
        tilePreview.setBounds(37, 0, 100, 32);
        ImageIcon i = getPreview();
        prev = new JLabel(i);
        prev.setBounds(70, 0, 100, 32);

        JButton b = new JButton(new ImageIcon("src/res/change.png"));
        b.setBounds(0, 0, 32, 32);

        JButton o = new JButton(new ImageIcon("src/res/objects.png"));
        o.setToolTipText("Choose an object to place");
        o.setBounds(303 + 37, 0, 32, 32);

        o.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    defineObject();

                } catch (Exception ee) {

                    JOptionPane.showMessageDialog(null, "Invalid input", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        mapEditing.add(o);

        l1.setBounds(139, 0, 32, 32);
        l1.setEnabled(false);
        l2.setBounds(176, 0, 32, 32);
        l3.setBounds(213, 0, 32, 32);

        l3.setToolTipText("Sets the current layer to edit, meaning object's position can be edited");

        ll = new JLabel((layer == 0 ? "Layer: Tiles" : "Layer: Objects"));
        ll.setBounds(211 + 37, 0, 100, 32);

        importB.setBounds(639 + 37, 0, 32, 32);
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

        mapEditing.add(l1);
        mapEditing.add(l2);
        mapEditing.add(ll);
        mapEditing.add(importB);

        export.setBounds(602 + 37, 0, 32, 32);
        export.setToolTipText("Export map");

        export.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fc = new JFileChooser();

                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int o = fc.showOpenDialog(null);

                if (o == JFileChooser.APPROVE_OPTION) {
                    String savePath = fc.getSelectedFile().getAbsolutePath();
                    saveDir = fc.getSelectedFile();

                    String outTiles = "";
                    String outObjects = "";

                    for (int i = 0; i < mapOutput[0].length; i++) {
                        for (int j = 0; j < mapOutput[0].length; j++) {

                            try {
                                outTiles += mapOutput[j][i] + (j < map.w - 1 ? " " : "");
                            } catch (Exception ee) {
                                // ee.printStackTrace();
                            }
                        }

                        outTiles += "\n";
                    }

                    for (Object oo : objects) {

                        outObjects += oo.name + " " + getRealCoords(oo.x) + "-" + getRealCoords(oo.y) + " "
                                + oo.angle + " " + oo.w + "-" + oo.h + " " + (oo.parent == null ? "" : oo.parent)
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

                            // Save the tileset file to the folder
                            String name = "tiles-" + MapEditor.map.tileSet.size;
                            File out = new File(dir.getAbsolutePath() + "\\" + name + ".png");
                            ImageIO.write(MapEditor.map.tileSet.tileSet, "png", out);

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

        mapEditing.add(export);

        JButton settings = new JButton(new ImageIcon("src/res/settings.png")),
                position = new JButton(new ImageIcon("src/res/position.png"));
        settings.setBounds(345 + 37, 0, 32, 32);
        position.setBounds(383 + 37, 0, 32, 32);

        position.setToolTipText("Change display position");
        settings.setToolTipText("Change window settings");

        pos = new JLabel("Root point: 0,0");
        pos.setBounds(420 + 37, 0, 100, 32);

        paintIndicator.setBounds(getVisibleRect());

        mapEditing.add(paintIndicator);
        mapEditing.add(pos);
        mapEditing.add(position);
        mapEditing.add(settings);

        l1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                layer = 0;
                l1.setEnabled(false);
                l2.setEnabled(true);
                l3.setEnabled(true);

                ll.setText("Layer: Tiles");
            }

        });

        l2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                layer = 1;
                l2.setEnabled(false);
                l1.setEnabled(true);
                l3.setEnabled(true);

                ll.setText("Layer: Objects");
            }

        });

        l3.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                layer = 2;
                l3.setEnabled(false);
                l1.setEnabled(true);
                l2.setEnabled(true);

                ll.setText("Layer: Editable");
            }

        });

        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame main = new JFrame("Tile selector");

                TilesetPreview preview = new TilesetPreview(self);
                JScrollPane scroll = new JScrollPane(preview, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

                main.add(scroll);

                main.setSize(240, 500);
                main.setResizable(false);
                main.setLocationRelativeTo(null);
                main.setVisible(true);
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
                        getComponentAt(0, 62).repaint();
                        requestFocus();
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

                        getComponentAt(0, 62).repaint();
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

        JToggleButton toggleGrid = new JToggleButton(new ImageIcon("src/res/swap.png"));
        toggleGrid.setToolTipText("Toogle grid on off");
        toggleGrid.setBounds(562 + 37, 0, 32, 32);

        toggleGrid.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {

                showGrid = !toggleGrid.isSelected();
                // Repaint
                getComponentAt(0, 62).repaint();
            }

        });

        JButton reset = new JButton(new ImageIcon("src/res/reset.png"));
        reset.setBounds(530 + 37, 0, 32, 32);
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

                getComponentAt(0, 62).repaint();
            }

        });

        JLabel label0 = new JLabel("Object: "),
                label2 = new JLabel("Scale X, Y: "), label3 = new JLabel("Rotation: ");

        label0.setBounds(5, 5, 300, 30);
        label2.setBounds(500, 10, 300, 20);
        label3.setBounds(675, 10, 300, 20);

        box.setBounds(60, 10, 200, 20);
        nextObject.setBounds(265, 10, 50, 20);

        box.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        try {

            box.setText(objectModels[current]);
        } catch (Exception e) {

            box.setText("----");
        }
        box.setEditable(false);

        nextObject.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (objectModels.length == 0)
                    return;

                current++;
                try {

                    box.setText(objectModels[current]);
                } catch (Exception ee) {

                    current = 0;
                    box.setText(objectModels[current]);
                }
                updateObject();
            }

        });

        parent.setBounds(718 + 37, 0, 32, 32);
        parent.setToolTipText("Add parent to selected object");
        parent.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (selected == null) {

                    JOptionPane.showMessageDialog(null, "No object selected", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String[] names = new String[objects.size()];

                names[0] = "NO PARENT";
                int index = 1;
                for (Object obj : objects) {

                    if (obj == selected)
                        continue;

                    names[index] = obj.name;
                    index++;
                }

                JComboBox<String> comboBox = new JComboBox<String>(names);
                comboBox.setSelectedIndex(-1);
                JOptionPane.showMessageDialog(null, comboBox, "Parent?", JOptionPane.QUESTION_MESSAGE);

                if (comboBox.getSelectedIndex() == -1)
                    return;

                selected.parent = comboBox.getSelectedIndex() == 0 ? null : comboBox.getSelectedItem().toString();
                JOptionPane.showMessageDialog(null, "Parent set", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        mapEditing.add(parent);

        objectList.setBounds(680 + 37, 0, 32, 32);
        objectList.setToolTipText("Lists all the objects on the map");
        objectList.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame frame = new JFrame();

                JTextArea text = new JTextArea();
                text.setEditable(false);

                String objs = "";
                int index = 0;
                for (Object o : objects) {

                    objs += (index + 1) + ": " + o.infoDump() + "\n\n";
                    index++;
                }

                text.setText(objs);
                JScrollPane scroll = new JScrollPane(text,
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

                frame.add(scroll);

                frame.setTitle("Output");
                frame.setSize(400, 400);
                frame.setVisible(true);
            }

        });

        mapEditing.add(objectList);

        scaleX.setBounds(550, 5, 60, 28);
        scaleY.setBounds(615, 5, 60, 28);

        rotation.setBounds(730, 5, 60, 28);

        scaleX.addChangeListener(this);
        scaleY.addChangeListener(this);
        rotation.addChangeListener(this);

        dataEditor.add(label3);
        dataEditor.add(rotation);
        dataEditor.add(scaleX);
        dataEditor.add(scaleY);
        dataEditor.add(nextObject);
        dataEditor.add(label0);
        dataEditor.add(box);
        dataEditor.add(label2);

        dataEditor.setLayout(null);
        dataEditor.setBounds(0, 0, getWidth(), 32);

        JPanel tilesetPanel = new JPanel();
        tilesetPanel.setBounds(0, 0, getWidth(), 32);

        importTileset.setBounds(5, 5, 200, 20);
        importObjectList.setBounds(210, 5, 200, 20);

        shotcuts.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame frame = new JFrame("Shortcuts");

                frame.setSize(510, 650);
                JLabel img = new JLabel(new ImageIcon("src/res/quick shortcuts.png"));
                img.setBounds(0, -20, 500, 650);

                frame.setLocationRelativeTo(null);
                frame.setResizable(false);
                frame.setLayout(null);
                frame.add(img);

                frame.setVisible(true);
            }

        });
        tilesetPanel.add(shotcuts);
        tilesetPanel.add(importTileset);
        tilesetPanel.add(importObjectList);

        importTileset.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                if (spriteFolder != "") {

                    fc.setCurrentDirectory(new File(spriteFolder));
                }

                int o = fc.showOpenDialog(null);
                if (o == JFileChooser.APPROVE_OPTION) {

                    try {

                        String s = JOptionPane.showInputDialog(null, "Tile size");
                        TileSet set = new TileSet(fc.getSelectedFile().getAbsolutePath(),
                                Integer.parseInt(s));

                        MapEditor.map = new Map(set, map.w, map.h);

                        // System.out.println(MapEditor.map.tileSet.tileSet);

                        getComponentAt(0, 62).repaint();
                        prev.setIcon(getPreview());

                        Project.tilesetPanel.removeAll();
                        ((TilesetPreview)Project.tilesetPanel).reload();
 

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }

        });

        importObjectList.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int o = fc.showDialog(null, "Object folder");
                if (o == JFileChooser.APPROVE_OPTION) {

                    setObjectModelsFromFolder(fc.getSelectedFile().getAbsolutePath());
                }
            }

        });

        setVisible(true);

        mapEditing.setLayout(null);
        mapEditing.setBounds(0, 0, getWidth(), 32);

        mapEditing.add(toggleGrid);
        mapEditing.add(reset);
        mapEditing.add(b);
        mapEditing.add(prev);
        mapEditing.add(tilePreview);
        mapEditing.add(l3);

        panels.setBounds(0, 0, getWidth(), 62);
        panels.add("Tools", mapEditing);
        panels.add("Object Parameters", dataEditor);
        panels.add("Tileset and Project", tilesetPanel);

        JButton saveButton = new JButton("Save project");
        tilesetPanel.add(saveButton);
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
             
                saveMap();
            }

        });

        add(panels);
        add(panel);

        panel.setFocusable(true);
        panel.requestFocus();
        prev.setIcon(getPreview());

        b.setToolTipText("Change the current tile");
        l1.setToolTipText("Change to tile placing mode");
        l2.setToolTipText("Change to object placing mode");

        panel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

                if (e.getKeyChar() == 'h') {
                    x--;
                } else if (e.getKeyChar() == 'j') {
                    y--;
                } else if (e.getKeyChar() == 'k') {
                    y++;
                } else if (e.getKeyChar() == 'l') {
                    x++;
                }

                if(e.getKeyChar() == 'o') defineObject();
                if(e.getKeyChar() == 's') saveMap();

                if(e.getKeyChar() == 'z') fastScroll = true; 

                if(e.getKeyChar() == 'f') changePaintMode();

                getComponentAt(0, 62).repaint();
            }

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == 37) {
                    x--;
                } else if (e.getKeyCode() == 40) {
                    y++;
                } else if (e.getKeyCode() == 38) {
                    y--;
                } else if (e.getKeyCode() == 39) {
                    x++;
                }
                
                getComponentAt(0, 62).repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {

                if(e.getKeyChar() == 'p') fastScroll = false; 
            }

        });

        panel.addMouseWheelListener(new MouseWheelListener() {
            
            private int sens = 3;

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                if (layer == 2 && selected != null) {

                    selected.angle += e.getWheelRotation() * sensibility;

                    if (selected.angle > 360)
                        selected.angle -= 360;
                    if (selected.angle < 0)
                        selected.angle = 360 + selected.angle;

                    getComponentAt(0, 62).repaint();
                }


                if (selected == null) {

                    int size = displayX + (e.getWheelRotation()) * (fastScroll ? sens : 1);

                    if (size <= 0 || size > maxSize * 2) {
        
                        JOptionPane.showMessageDialog(null, "Width out of bounds " + size + " for 1-" + maxSize*2,
                                "ERROR", JOptionPane.ERROR_MESSAGE);
                    } else {
        
                        displayX = size;
                        displayY = size;
                        getComponentAt(0, 62).repaint();
                        requestFocus();
                    }
                }
            }

        });

        MouseInputListener inputListener = new MouseInputListener() {

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

                int xx = _x / (800 / (displayX));
                int yy = _y / (800 / (displayY));

                if (e.getButton() == 1) {

                    // Get relative position position

                    if (layer == 0) {

                        if(paintMode == 0){

                            try {
    
                                mapOutput[x + xx][y + yy] = curTile;
                                changeConfirm();
    
                            } catch (Exception ee) {
    
                                JOptionPane.showMessageDialog(null, "Can't place tiles outside of map bounds", "ERROR",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        } else if (paintMode == 2) {

                            drag = new Point();
                            drag.setLocation(x + xx, y + yy);
                        } else if (paintMode == 3) {

                            //Fill from this square outwards
                            int fillTolerance = mapOutput[x + xx][y + yy];

                            try {
                
                                floodFill(x + xx, y + yy, fillTolerance);
                            } catch (Exception ee) {
                                
                                JOptionPane.showMessageDialog(null, "Can't place tiles outside of map bounds", "ERROR",
                                JOptionPane.ERROR_MESSAGE);
                            }
                
                            getComponentAt(0, 62).repaint();
                            changeConfirm();
                        }

                    } else if (layer == 1) {

                        if (currentObject != null) {

                            currentObject.x = x + (_x / (800 / displayX));
                            currentObject.y = y + (_y / (800 / displayY));

                            objects.add(currentObject);

                            Object o = new Object();
                            o.setName(currentObject.realName);
                            o.h = currentObject.h;
                            o.w = currentObject.w;
                            o.angle = currentObject.angle;

                            currentObject = o;

                            changeConfirm();
                        } else {

                            JOptionPane.showMessageDialog(null, "No object specified", "ERROR",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (layer == 2) {

                        move: {

                            for (Iterator<Object> iterator = objects.iterator(); iterator.hasNext();) {

                                Object o = iterator.next();

                                int realTileX = o.x - x;
                                int realTileY = o.y - y;

                                if ((_x / (800 / displayX)) == realTileX && (_y / (800 / displayY)) == realTileY) {

                                    selected = o;
                                    break move;
                                }
                            }

                            if (!(selected == null)) {

                                // Move object

                                selected.x = xx;
                                selected.y = yy;
                                changeConfirm();
                            }

                        }

                    }

                    getComponentAt(0, 62).repaint();
                } else if (e.getButton() == 3) {

                    if (layer == 1) {

                        // Delete objects

                        for (Iterator<Object> iterator = objects.iterator(); iterator.hasNext();) {

                            Object o = iterator.next();

                            int realTileX = o.x - x;
                            int realTileY = o.y - y;

                            if ((_x / (800 / displayX)) == realTileX && (_y / (800 / displayY)) == realTileY) {
                                iterator.remove();
                                break;
                            }
                        }
                    } else if (layer == 2) {

                        selected = null;
                    }

                    getComponentAt(0, 62).repaint();
                } else if (e.getButton() == 2) {

                    // Display info
                    for (Iterator<Object> i = objects.iterator(); i.hasNext();) {
                        Object o = i.next();

                        int realTileX = o.x - x;
                        int realTileY = o.y - y;

                        // System.out.println(tarX + " | " + tarY);

                        if ((_x / (800 / displayX)) == realTileX && (_y / (800 / displayY)) == realTileY) {

                            String info = o.infoDump();
                            JOptionPane.showMessageDialog(null, info, "OBJECT INFO", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                if(drag != null && layer == 0 && paintMode == 2) {

                    int _x = e.getPoint().x;
                    int _y = e.getPoint().y;

                    int xx = _x / (800 / (displayX));
                    int yy = _y / (800 / (displayY));

                    Point newPoint = new Point();
                    newPoint.setLocation(x + xx, y + yy);

                    if(drag.getX() >= mapOutput[0].length || drag.getY() >= mapOutput[0].length){

                        JOptionPane.showMessageDialog(null, "Can't place tiles outside of map bounds", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
                        
                        drag = null;
                        getComponentAt(0, 62).repaint();
                        return;
                    }

                    //Fill rectangle
                    try {
                        
                        for (int i = (int) Math.min(drag.getX(), newPoint.getX()); i <= (int) Math.max(drag.getX(), newPoint.getX()); i++) {
                            for (int j = (int) Math.min(drag.getY(), newPoint.getY()); j <= (int) Math.max(drag.getY(), newPoint.getY()); j++) {
                                
                                try {mapOutput[i][j] = curTile;}catch(Exception eee){}
                            }
                        }

                    } catch (Exception ee) {
                        
                        ee.printStackTrace();
                    }

                    drag = null;
                    changeConfirm();
                    getComponentAt(0, 62).repaint();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }

            @Override
            public void mouseDragged(MouseEvent e) {

                if((paintMode == 1) && layer == 0) {

                    int _x = e.getPoint().x;
                    int _y = e.getPoint().y;

                    int xx = _x / (800 / (displayX));
                    int yy = _y / (800 / (displayY));

                    try {
                        mapOutput[x + xx][y + yy] = curTile;
                    } catch (Exception ee) {
    
                        JOptionPane.showMessageDialog(null, "Can't place tiles outside of map bounds", "ERROR",
                                JOptionPane.ERROR_MESSAGE);
                    }

                    changeConfirm();
                    getComponentAt(0, 62).repaint();
                } else if (paintMode == 2 && layer == 0) {
                    
                    int _x = e.getPoint().x;
                    int _y = e.getPoint().y;

                    int xx = _x / (800 / (displayX));
                    int yy = _y / (800 / (displayY));
                    mousePos.setLocation(x + xx - (drag.getX() - 1), y + yy - (drag.getY() - 1));
                    getComponentAt(0, 62).repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }

        };

        panel.addMouseMotionListener(inputListener);
        panel.addMouseListener(inputListener);
    }

    public ImageIcon getPreview() {

        BufferedImage i = map.tileSet.getTileOf(curTile);

        return new ImageIcon(i.getScaledInstance(32, 32, BufferedImage.SCALE_SMOOTH));
    }

    class DrawPanel extends JPanel {

        @Override
        public void paintComponent(Graphics g) {

            g.clearRect(0, 0, this.getWidth(), this.getHeight());

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

            if (showGrid) {

                for (int i = 0; i < 800; i += xx) {
                    g.drawLine(0, i, 800, i);
                }

                for (int j = 0; j < 800; j += yy) {
                    g.drawLine(j, 0, j, 800);
                }
            }

            for (Object o : objects) {

                double rotationRequired = Math.toRadians(o.angle);
                double locationX = oImg.getWidth() / 2;
                double locationY = oImg.getHeight() / 2;
                AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

                BufferedImage drawImage = oImg;
                // Find id there are sprite references to object
                ObjectData od = null;
                for (ObjectData objectInformation : Project.objectInformation) {
                    
                    if(objectInformation.name != null && objectInformation.name.equals(o.realName)){

                        try{
                            drawImage = ImageIO.read(new File(objectInformation.image));
                            od = objectInformation;
                        }catch(Exception ignored){

                            ignored.printStackTrace();
                        }
                    }
                }
                
                int w = xx * o.w;
                int h = yy * o.h;
                
                if(od != null){ 
                    
                    int relativesize = ((int) od.spriteDimensions.x * xx)/(map.tileSet.size);
                    int relativesizeY = ((int) od.spriteDimensions.y * yy)/(map.tileSet.size);
                    
                    w = o.w * relativesize;
                    h = o.h * relativesizeY;
                }

                g.drawImage(op.filter(drawImage, null), (o.x - x) * xx, (o.y - y) * yy,
                        w,
                        h,
                        null);

                if (selected == o) {

                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(Color.RED);

                    Stroke stroke = new BasicStroke(5);
                    g2d.setStroke(stroke);
                    g.drawRect((o.x - x) * xx, (o.y - y) * yy, w, h);
                }
            }

            if(drag != null) {

                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.BLUE);

                Stroke stroke = new BasicStroke(5);
                g2d.setStroke(stroke);

                Point myPoint = new Point();
                myPoint.setLocation((mousePos.x - x) * xx, (mousePos.y - y) * yy);

                g.drawRoundRect((int) (Math.max(drag.getX(), x) - Math.min(drag.getX(), x)) * xx, (int) (Math.max(drag.getY(), y) - Math.min(drag.getY(), y)) * yy, myPoint.x, myPoint.y, 10, 10);
            }

            if(pathing) {

                Vec2 myPoint = new Vec2();
                myPoint.x = (mousePos.x - x) * xx;
                myPoint.y = (mousePos.y - y) * yy;

                for (Vec2 v : points) {
                
                    g.fillOval((int)v.x, (int)v.y, xx, yy);
                }
                g.fillOval((int)myPoint.x, (int)myPoint.y, xx, yy);
            }

            pos.setText("Root point: " + x + "," + y);
            requestFocus();
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

            Scanner s = null;

            if (tiles.exists()) {
                s = new Scanner(tiles);

                while (s.hasNextLine())
                    inTiles += s.nextLine() + "\n";

                s.close();
            }

            if (obj.exists()) {
                s = new Scanner(obj);

                while (s.hasNextLine())
                    inObj += s.nextLine() + "\n";

                s.close();
            }

            // Get the info from the objects
            objects.clear();

            for (String line : inObj.split("\n")) {

                String[] info = line.split(" ");

                // System.out.println(info.length);
                if (info.length < 3)
                    continue;

                Object oo = new Object();
                oo.setName(info[0]);
                oo.x = (Integer.parseInt(info[1].split("-")[0])) / map.tileSet.size;
                oo.y = (Integer.parseInt(info[1].split("-")[1])) / map.tileSet.size;

                oo.w = Integer.parseInt(info[3].split("-")[0]);
                oo.h = Integer.parseInt(info[3].split("-")[1]);

                try {

                    oo.parent = info[4];

                } catch (Exception e) {
                }

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

            // Import tileset
            try {

                TileSet tileset = new TileSet("src/res/defaultTile.png", 16);
                findtiles: {
                    // Search for tile file, if it doesn't exist then create with default tile set
                    File file = null;
                    Integer size = 0;

                    File[] f = new File(dirPath).listFiles();
                    for (File file2 : f) {

                        if (file2.getName().contains("tiles-"))
                            file = file2;
                    }

                    if (file == null)
                        break findtiles;

                    if (file.getName().contains("tiles-")) {

                        try {

                            String[] split = file.getName().split("tiles-");
                            size = Integer.valueOf(
                                    split[split.length - 1].substring(0, split[split.length - 1].lastIndexOf(".")));

                        } catch (Exception e) {

                            e.printStackTrace();
                            break findtiles;
                        }

                    } else {
                        break findtiles;
                    }

                    tileset = new TileSet(file.getAbsolutePath(), size);
                }

                map.tileSet = tileset;
                map.w = mapOutput[0].length;
                map.h = mapOutput[0].length;
                
                saved.setText("Up to date");
                saved.setBackground(Color.WHITE);

                saveDir = new File(dirPath);
                prev.setIcon(getPreview());

            } catch (Exception e) {

                e.printStackTrace();
                return;
            }

            getComponentAt(0, 62).repaint();

        } catch (Exception ee) {

            JOptionPane.showMessageDialog(null, "Unable to open files", "ERROR", JOptionPane.ERROR_MESSAGE);
            ee.printStackTrace();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {

        updateObject();
    }

    private void updateObject() {

        if (currentObject == null) {
            currentObject = new Object();
        }

        currentObject.angle = (int) rotation.getValue();
        currentObject.w = (int) scaleX.getValue();
        currentObject.h = (int) scaleY.getValue();

        currentObject.setName(box.getText().substring(0, box.getText().length() - 5));
    }

    private void setEditorValues() {

        scaleX.setValue(currentObject.w);
        scaleY.setValue(currentObject.h);
        rotation.setValue(currentObject.angle);

        box.setText(currentObject.name);
    }

    public void setObjectModelsFromFolder(String folder) {

        File[] files = new File(folder).listFiles();

        if (files == null)
            return;

        List<String> results = new ArrayList<String>();

        for (File file : files) {
            if (file.getName().substring(file.getName().lastIndexOf(".")).contains(".java")) {
                results.add(file.getName());
            }
        }

        objectModels = results.toArray(new String[results.size()]);
    } 
    
    public void saveMap(){

        if (saveDir == null) {JOptionPane.showMessageDialog(null, "No saving directory specified try exporting your map", "ERROR", JOptionPane.ERROR_MESSAGE);}
        File tileFile = new File(saveDir.getAbsolutePath() + "/room-tiles.txt");
        if (!tileFile.exists()) {JOptionPane.showMessageDialog(null, "The saving directory is not a map", "ERROR", JOptionPane.ERROR_MESSAGE);}

        //Save information    

        String outTiles = "";
        String outObjects = "";
        
        for (int i = 0; i < mapOutput[0].length; i++) {
            for (int j = 0; j < mapOutput[0].length; j++) {
                
                try {
                    outTiles += mapOutput[j][i] + (j < map.w - 1 ? " " : "");
                } catch (Exception ee) {
                    
                    ee.printStackTrace();
                }
            }

            outTiles += "\n";
        }

        for (Object oo : objects) {

            outObjects += oo.realName + " " + getRealCoords(oo.x) + "-" + getRealCoords(oo.y) + " "
                    + oo.angle + " " + oo.w + "-" + oo.h + " " + (oo.name == null ? "" : oo.name) + (oo.parent == null ? "" : oo.parent)
                    + "\n";
        }

        //System.out.println(outTiles);
        // Output the files
        File dir = saveDir;
        //System.out.println(saveDir);
        if (dir.exists()) {

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

                // Save the tileset file to the folder
                //Delete current tileset
                for (File f : new File(dir.getAbsolutePath()).listFiles()) {
                    
                    if(f.getName().contains("tiles-")) f.delete();
                }
                String name = "tiles-" + MapEditor.map.tileSet.size;
                File out = new File(dir.getAbsolutePath() + "\\" + name + ".png");
                ImageIO.write(MapEditor.map.tileSet.tileSet, "png", out);

                System.out.println(name);
                saved.setText("Up to date");
                saved.setBackground(Color.WHITE);
                
                JOptionPane.showMessageDialog(null, "Save complete", "SUCCESS",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e1) {
                JOptionPane.showMessageDialog(null, "Export canceled", "ERROR", JOptionPane.ERROR_MESSAGE);
                e1.printStackTrace();
            }
        }    
    }

    private void defineObject() {

        String name = JOptionPane.showInputDialog(null, "Object name: ");
        if(name.trim() == "" || name == null) return;
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

            o.setName(name);
            o.angle = angle;
            o.w = scaleX;
            o.h = scaleY;

            currentObject = o;

            getComponentAt(0, 62).repaint();

            setEditorValues();
        }
    }

    public void changeConfirm(){

        saved.setText("There are unsaved changes!");
        saved.setBackground(Color.RED);
    }

    public void changePaintMode(){

        JComboBox<String> comboBox = new JComboBox<String>(modes);
        JOptionPane.showMessageDialog(null, comboBox, "Paint mode", JOptionPane.QUESTION_MESSAGE);

        paintMode = comboBox.getSelectedIndex();
    }

    private void floodFill(int xx, int yy, int checkTile) {

        int prevC = checkTile;
        if(prevC==curTile) return;
        floodFillUtil(xx, yy, prevC);
    }

    private void floodFillUtil(int xx, int yy, int prevC)
    {
        // Base cases
        if (xx < 0 || xx >= mapOutput[0].length || yy < 0 || yy >= mapOutput[0].length)
            return;
        if (mapOutput[xx][yy] != prevC)
            return;
    
        // Replace the color at (x, y)
        mapOutput[xx][yy] = curTile;
    
        // Recur for north, east, south and west
        floodFillUtil(xx+1, yy, prevC);
        floodFillUtil(xx-1, yy, prevC);
        floodFillUtil(xx, yy+1, prevC);
        floodFillUtil(xx, yy-1, prevC);
    }
}
