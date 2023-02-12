import java.awt.Image;
import java.awt.event.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.io.BufferedInputStream;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PluginManager extends JFrame implements ActionListener {

    JButton importButton = Style.GetStyledButton("Import from local file system");
    int files_moved = 0, files_failed = 0, files_merged = 0;
    String output_string = "";
    JTextArea output = new JTextArea();

    public PluginManager() {

        setTitle("Himawari plugin manager");

        setSize(620, 600);

        JLabel title = new JLabel("Himawari plugin manager");
        JLabel icon = new JLabel(
                new ImageIcon(new ImageIcon("src\\res\\pluginmanager.png").getImage().getScaledInstance(300, 150,
                        Image.SCALE_SMOOTH)));

        output.setEditable(false);
        JScrollPane scroll = new JScrollPane(output,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setBounds(6, 100, 300, 200);

        title.setBounds(5, 5, 400, 30);
        title.setFont(Style.HEADER_FONT);

        importButton.setBounds(5, 50, 260, 30);
        icon.setBounds(300, 100, 300, 150);
        importButton.addActionListener(this);

        add(scroll);
        add(title);
        add(icon);
        add(importButton);

        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private long getFolderSize(File folder) {
        long length = 0;

        // listFiles() is used to list the
        // contents of the given folder
        File[] files = folder.listFiles();

        int count = files.length;

        // loop for traversing the directory
        for (int i = 0; i < count; i++) {
            if (files[i].isFile()) {
                length += files[i].length();
            } else {
                length += getFolderSize(files[i]);
            }
        }
        return length;
    }

    private void moveFilesFromOutside(File file) throws IOException {

        Path source = file.toPath();
        File newFile = new File(
                Project.path + "\\src\\main\\java\\" + Project.projectName + "\\Engine\\"
                        + file.getParentFile().getName() + "\\" + file.getName());

        if (newFile.exists()) {

            // Check if it is an asset or a code file
            int i = file.getName().lastIndexOf('.');
            if (i == -1) {
                return;
            }
            String ext = file.getName().substring(i + 1);

            if (ext == "java") {

            } else {

                updateLogs("Failed moving " + file.getName() + " to " + newFile.getCanonicalPath()
                        + " - can't move a file that already exists in the target directory");
                files_failed++;
            }

            return;
        }

        Files.copy(source, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        updateLogs("Success moving " + file.getName() + " to " + newFile.getCanonicalPath());
        files_moved++;
    }

    private void copyFromFolder(File folder) {

        File[] files = folder.listFiles();
        for (File f : files) {

            if (f.isFile()) {

                try {
                    moveFilesFromOutside(f);
                } catch (IOException e) {
                    files_failed++;
                }
            } else if (f.isDirectory()) {

                copyFromFolder(f);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == importButton) {

            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int opt = fc.showOpenDialog(null);

            if (opt == JFileChooser.APPROVE_OPTION) {

                // Get confirmation
                long size = getFolderSize(fc.getSelectedFile());
                int ans = JOptionPane.showConfirmDialog(null,
                        "You are about to add a " + ((double) size / (1024 * 1024))
                                + "MB folder system to your project\nProceed?",
                        "Confirmation",
                        JOptionPane.OK_CANCEL_OPTION);

                if (ans == JOptionPane.CANCEL_OPTION)
                    return;

                files_moved = 0;
                files_failed = 0;
                files_merged = 0;
                output_string = "###################################################\n";

                updateLogs("Started importing " + ((double) size / (1024 * 1024)) + "MB from "
                        + fc.getSelectedFile().getAbsolutePath());

                File[] folderSystem = fc.getSelectedFile().listFiles();
                for (File f : folderSystem) {

                    if (f.isFile())
                        continue;
                    copyFromFolder(f);
                }

                //Import dependencies
                File depenFile = new File(fc.getSelectedFile().getAbsolutePath() + "/plugin.json");
                Gson g = new Gson();

                if(depenFile.exists()){
                 
                    try(Reader reader = Files.newBufferedReader(Paths.get(depenFile.getAbsolutePath()))) {
                 
                        JsonObject obj = new JsonParser().parse(reader).getAsJsonObject();
                        String text = Functions.getPOM();

                        for(JsonElement d : obj.get("dependencies").getAsJsonArray()) {

                            String elem = d.getAsString();
                            if(!text.contains(elem)){
                                
                                text = text.replace("</dependencies>", elem + "</dependencies>");
                                updateLogs(elem + " added as a dependency");
                            }else updateLogs("Dependency " + elem + " already added. Skipping 1 line");
                        }

                        Functions.writePOM(text);
                        updateLogs("Dependencies added");

                    } catch (Exception ee) {
                        
                        updateLogs("Failed to open plugin.json");
                        ee.printStackTrace();
                    }
                }else {

                    updateLogs("Skipping dependency imports");
                }

                updateLogs("Plugin import finished\n" + files_moved + " files moved successfuly\n" + files_failed
                        + " files that could not be moved\n" + files_merged + " files merged");

                updateLogs("###################################################\n");
            }
        }

    }

    private void updateLogs(String text) {

        output_string += text + "\n";
        output.setText(output_string);
    }

    public static void joinFiles(File destination, File[] sources)
            throws IOException {
        OutputStream output = null;
        try {
            output = createAppendableStream(destination);
            for (File source : sources) {
                appendFile(output, source);
            }
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    private static BufferedOutputStream createAppendableStream(File destination)
            throws FileNotFoundException {
        return new BufferedOutputStream(new FileOutputStream(destination, true));
    }

    private static void appendFile(OutputStream output, File source)
            throws IOException {
        InputStream input = null;
        try {
            input = new BufferedInputStream(new FileInputStream(source));
            IOUtils.copy(input, output);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }
}
