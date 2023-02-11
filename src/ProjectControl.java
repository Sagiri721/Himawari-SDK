import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ProjectControl extends JPanel implements ActionListener {

    private JButton newObject = new JButton("New Object"), newAlert = new JButton("Create Alarm"),
            newPlugin = new JButton("Import plugin"), importComponent = new JButton("Import component"),
            deleteCluster = new JButton("Delete cluster"), createCluster = new JButton("Create cluster"),
            clearLogs = new JButton("Clear logs"), checkLogs = new JButton("Check storage logs");
    private JTextArea area = null;
    private JComboBox<String> clusterBox = null;

    private List<String> clusters = new ArrayList<String>();
    private static Connection conn = null;
    private static File dbFile = null;
    private JLabel label0 = new JLabel("Control Panel"), databaseStatus = new JLabel(""), dbData = new JLabel(""), clusterInfo = new JLabel("");

    public ProjectControl() {

        label0.setFont(Style.HEADER_FONT);
        label0.setForeground(Style.MAIN_TEXT_COLOR);
        label0.setBounds(5, 5, 200, 30);
        add(label0);

        dbFile = new File(Project.engineFiles.getParent() + "/Engine/Assets/storage.db");
        System.out.println(dbFile.getAbsolutePath());

        databaseStatus.setText(dbFile.exists() ? "Storage status: OPEN" : "Storage status: MISSING");
        databaseStatus.setFont(Style.HEADER_FONT);
        databaseStatus.setBounds(5, 510, 410, 30);

        JLabel status = new JLabel(new ImageIcon("src/res/status/"+(dbFile.exists()?"open" : "closed")+".png"));
        status.setBounds(270, 510, 32, 32);
        add(status);
        add(databaseStatus);

        dbData.setText(dbFile.length() + " bytes(" + (dbFile.length() / 1000)
                + " KB)");
        dbData.setBounds(5, 540, 410, 20);
        add(dbData);
        
        
        // Fetch database clusters
        connect();
        
        try {

            ResultSet rs = conn.getMetaData().getTables(null, null, null, null);
            while (rs.next()) {
                String name = (rs.getString("TABLE_NAME"));
                // filter table
                if (!name.startsWith("sqlite_")) clusters.add(name + " (" + getClusterStatus(name) + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        close();

        long active = clusters.stream().filter(string -> string.contains("(custom cluster)")).count();

        clusterInfo.setText(active+" active clusters out of "+clusters.size());
        clusterInfo.setBounds(5, 520, 410, 100);
        add(clusterInfo);
        
        clusterBox = new JComboBox<String>(clusters.toArray(new String[clusters.size()]));
        clusterBox.setBounds(5, 595, 200, 30);

        deleteCluster.setBounds(210, 595, 200, 30);
        deleteCluster.addActionListener(this);
        
        createCluster.setBounds(5, 625, 200, 30);
        createCluster.addActionListener(this);
        
        clearLogs.setBounds(210, 625, 200, 30);
        clearLogs.addActionListener(this);
        
        checkLogs.setBounds(5, 660, 405, 30);
        checkLogs.addActionListener(this);

        add(checkLogs);
        add(clearLogs);
        add(deleteCluster);
        add(createCluster);
        add(clusterBox);

        JLabel create = new JLabel("Creation", SwingConstants.CENTER);
        create.setBounds(5, 35, 410, 20);

        newObject.setBounds(5, 70, 410, 40);
        newObject.addActionListener(this);

        newAlert.setBounds(5, 115, 410, 40);
        newAlert.addActionListener(this);

        newPlugin.setBounds(5, 160, 410, 40);
        newPlugin.addActionListener(this);

        importComponent.setBounds(5, 205, 410, 40);
        importComponent.addActionListener(this);

        
        area = new JTextArea();
        JScrollPane pane = new JScrollPane(area);
        pane.setBounds(5, 270, 410, 200);
        
        add(pane);
        
        add(create);
        add(newObject);
        add(newAlert);
        add(newPlugin);
        add(importComponent);

        setBackground(Style.SECONDARY_BACKGROUND);

        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        setLayout(null);
    }

    private String getClusterStatus(String name) {

        switch (name) {
            case "localStorage":  return "main cluster";
            case "logs": return "logs cluster";
            default: return "custom cluster";
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == newObject) {

            try {

                Scanner s = new Scanner(new File("src/templates/Object.txt"));

                String text = "";
                while (s.hasNextLine())
                    text += s.nextLine() + "\n";

                s.close();

                String object_name = JOptionPane.showInputDialog(null, "Object name?", "Create Object",
                        JOptionPane.INFORMATION_MESSAGE);

                if (object_name == null || object_name.trim() == "") {

                    JOptionPane.showMessageDialog(null, "Object name can't be empty", "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                text = text.replace("[package]", (Project.projectName.replace("/", ".") + "."));
                text = text.replace("[Object]", object_name);
                text = text.replace("[Start]", "");

                Integer option = JOptionPane.showConfirmDialog(null, "Go to advanced configuration panel?",
                        "Advanced Configuration", JOptionPane.YES_NO_OPTION);
                // No: 1 Yes: 0

                if (option == 0) {

                    Functions.OpenPanelAsFrame(400, 600, "Advanced Object Creation", new ObjectWizard(object_name),
                            false);

                    return;
                } else {

                    File newFile = new File(Project.path + "/src/main/java/" + Project.projectName + "/Assets/Objects/"
                            + object_name + ".java");
                    if (newFile.exists()) {

                        JOptionPane.showMessageDialog(null, "There already exists an object with this name", "ERROR",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    FileWriter fw = new FileWriter(newFile);

                    fw.write(text);
                    fw.close();

                    JOptionPane.showMessageDialog(null, "Object Created");

                }

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource() == newAlert) {

            Scanner s;
            try {

                s = new Scanner(new File("src/templates/Alert.txt"));

                String text = "";
                while (s.hasNextLine())
                    text += s.nextLine() + "\n";

                s.close();

                String object_name = JOptionPane.showInputDialog(null, "Alarm name?", "Create Alarm",
                        JOptionPane.INFORMATION_MESSAGE);

                if (object_name == null || object_name.trim() == "") {

                    JOptionPane.showMessageDialog(null, "Alarm name can't be empty", "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                text = text.replace("[Object]", object_name);

                File newFile = new File(Project.path + "/src/main/java/" + Project.projectName + "/Assets/Objects/"
                        + object_name + ".java");
                if (newFile.exists()) {

                    JOptionPane.showMessageDialog(null, "There already exists an alarm with this name", "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                FileWriter fw = new FileWriter(newFile);

                fw.write(text);
                fw.close();

                JOptionPane.showMessageDialog(null, "Alarm Created");

                area.setText("AlarmPack pack = new AlarmPack(new Alarm1(), my_delay);\nAlarm.runAlarm(pack);");

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource() == createCluster) {

            String name = JOptionPane.showInputDialog(null, "Cluster name?");
            name = name.replace(" ", "-");
            if(accordsToNamingConvention(name)){
                
                int ans = 0;
                try{
                    ans = Integer.parseInt( (String) JOptionPane.showInputDialog(null,
                    "Text",
                    name, JOptionPane.INFORMATION_MESSAGE,
                    null,
                    null,
                    "Capacity?"));
                } catch(Exception ee) {

                    JOptionPane.showMessageDialog(null, "Input is NaN", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if(ans <= 0 || ans >= 5) {JOptionPane.showMessageDialog(null, "Capacity out of bounds 1 - 5", "ERROR", JOptionPane.ERROR_MESSAGE);}
                // Create
                connect();

                String sql = "CREATE TABLE IF NOT EXISTS "+name+" (key VARCHAR(200) PRIMARY KEY, ";
                for(int i = 0; i < ans; i++) sql += (" value" + String.valueOf(i+1) + " VARCHAR(200)" + (i==(ans-1) ? ");" : ","));

                try(Statement stmt = conn.createStatement()){

                    stmt.execute(sql);
                    JOptionPane.showMessageDialog(null, "Cluster created succefully", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);

                    updateComponent();

                }catch(SQLException ex) {

                    JOptionPane.showMessageDialog(null, "Cluster was not created", "ERROR", JOptionPane.ERROR_MESSAGE);
                    JOptionPane.showMessageDialog(null, "reason: " + ex.getStackTrace(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }

                close();

            }else JOptionPane.showMessageDialog(null, "Your name is not valid", "ERROR", JOptionPane.ERROR_MESSAGE);

        }else if (e.getSource() == deleteCluster) {

            //Delete
            connect();

            String clusterName = clusterBox.getSelectedItem().toString().split(" ")[0];
            if(clusterName.equals("localStorage".trim()) || clusterName.equals("logs".trim())) {

                JOptionPane.showMessageDialog(null, "Can't delete system tables", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "DROP TABLE " + clusterName;
            try(Statement stmt = conn.createStatement()) {

                stmt.execute(sql);
                
            } catch(SQLException ex) {

                JOptionPane.showMessageDialog(null, "Cluster was not deleted", "ERROR", JOptionPane.ERROR_MESSAGE);
                JOptionPane.showMessageDialog(null, "reason: " + ex.getStackTrace(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }

            close();
            updateComponent();
            
        } else if (e.getSource() == newPlugin) {

            new PluginManager();
        } else if (e.getSource() == checkLogs) {

            long milis = System.currentTimeMillis();
            connect();

            String sql = "SELECT * FROM logs";
            try(Statement stmt = conn.createStatement()){

                ResultSet rs = stmt.executeQuery(sql);
                
                rs.last();
                String output = "Found " + rs.getRow() + " logs in " + ((System.currentTimeMillis() - milis)/1000) + " seconds ...\n";

                rs.first();
                while(rs.next()) {

                    output += "description: " + rs.getString("description") + " | date: " + rs.getString("log_date") + "\n";
                }
                area.setText(output);
                
            }catch(Exception ee) {ee.printStackTrace();}

            close();
        }

    }

    private boolean accordsToNamingConvention(String name)  {

        List<String> comparableClusterNames = clusters.stream().map(string -> (string.substring(0, string.lastIndexOf("(") - 1))).collect(Collectors.toList());

        if(name.toLowerCase().startsWith("sqlite")) return false;
        if(comparableClusterNames.contains(name)) return false;
        if(name.trim().length() == 0) return false;

        return true;
    }

    public static void connect() {
        try {
            // db parameters
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close() {

        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void updateComponent(){

         // Fetch database clusters
         connect();

         try {
 
             ResultSet rs = conn.getMetaData().getTables(null, null, null, null);
             while (rs.next()) {
                 String name = (rs.getString("TABLE_NAME"));
                 // filter table
                 if (!name.startsWith("sqlite_")) clusters.add(name + " (" + getClusterStatus(name) + ")");
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
 
         close();
 
         long active = clusters.stream().filter(string -> string.contains("(custom cluster)")).count();
 
         clusterInfo.setText(active+" active clusters out of "+clusters.size());
         
         DefaultComboBoxModel<String> tempModel = new DefaultComboBoxModel<String>(clusters.toArray(new String[clusters.size()]));
         clusterBox.setModel(tempModel);
    }
}
