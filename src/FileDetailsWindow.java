import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class FileDetailsWindow extends JFrame {

    File file;

    public FileDetailsWindow(File file) {

        this.file = file;

        Path path = Paths.get(file.getAbsolutePath());
        BasicFileAttributes attr = null;

        try {
            attr = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (Exception e) {

        }

        JLabel name = new JLabel("Inspecting " + file.getName()),
                size = new JLabel("<html><body>File size: " + file.length() + " bytes(" + (file.length() / 1000)
                        + " KB)</body></html>");

        JLabel time = null;

        if (attr != null) {

            Date date = new Date(attr.creationTime().toMillis());
            Date date2 = new Date(attr.lastModifiedTime().toMillis());

            String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat format = new SimpleDateFormat(pattern);

            time = new JLabel(
                    "<html><body>File created at: " + format.format(date)
                            + "<br>File last edited at: "
                            + format.format(date2) + "</body></html>");
            time.setBounds(5, 60, 300, 30);
            add(time);
        }

        String extension = getExtension();
        if (extension != "") {

            JLabel icon = new JLabel(new ImageIcon("src/res/file-icons/icon-" + extension + ".png"));
            icon.setBounds(5, 130, 108, 108);

            add(icon);
        }

        name.setFont(Style.HEADER_FONT);
        name.setBounds(5, 5, 600, 30);
        size.setBounds(5, 50, 300, 30);

        add(name);
        add(size);

        setLayout(null);
        setTitle("File Inspector");
        setLocationRelativeTo(null);
        setSize(300, 400);
        setResizable(false);
        setVisible(true);
    }

    private String getExtension() {

        int i = file.getName().lastIndexOf('.');

        if (i == -1) {
            return "room";
        }
        String ext = file.getName().substring(i + 1);

        switch (ext) {

            case "png":
            case "jpg":
            case "jpeg":
            case "tiff":
                return "sprite";
            case "wav":
            case "ogg":
            case "mp3":
                return "sound";
            case "java":
            case "kt":

                Scanner s = null;
                try {
                    s = new Scanner(file);
                    while (s.hasNext()) {

                        if (s.nextLine().contains("extends Object")) {
                            s.close();
                            return "object";
                        }

                        if (s.nextLine().contains("extends Component")) {
                            s.close();
                            return "component";
                        }
                    }

                } catch (Exception e) {

                    if (s != null)
                        s.close();
                    e.printStackTrace();
                    return "unknown";
                }

                if (s != null)
                    s.close();
                return "unknown";
            case "ttf":
            case "otf":
            case "woff":
                return "font";
            case "db":
                return "db";
        }

        return "";
    }
}
