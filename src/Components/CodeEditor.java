package Components;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CodeEditor extends JFrame implements ActionListener {

    File file;

    JMenuBar mb = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    JMenuItem close = new JMenuItem("Close File");

    public CodeEditor(File file) {

        this.file = file;
        JTextArea textArea = new JTextArea();
        textArea.setBounds(0, 0, getWidth(), getHeight());

        add(textArea);
        JScrollPane scroll = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scroll);

        fileMenu.add(close);

        mb.add(fileMenu);
        setJMenuBar(mb);
        setTitle("Code Editor");
        setSize(700, 700);
        setLocationRelativeTo(null);
        setVisible(true);

        try {
            textArea.setText(getFileText());
        } catch (FileNotFoundException e) {

            JOptionPane.showMessageDialog(null, "No file was found", "ERROR", JOptionPane.ERROR_MESSAGE);
            setVisible(false);
            dispose();
        }
    }

    private String getFileText() throws FileNotFoundException {

        Scanner s = new Scanner(file);
        String text = "";

        while (s.hasNext()) {

            text += s.nextLine() + "\n";
        }

        return text;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
