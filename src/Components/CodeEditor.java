package Components;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CodeEditor extends JFrame implements ActionListener {

    File file;

    JMenuBar mb = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    JMenuItem close = new JMenuItem("Close File"), save = new JMenuItem("Save File");
    JTextArea textArea = new JTextArea();

    public CodeEditor(File file) {

        this.file = file;
        textArea.setBounds(0, 0, getWidth(), getHeight());

        add(textArea);
        JScrollPane scroll = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scroll);

        fileMenu.add(save);
        fileMenu.add(close);

        save.addActionListener(this);
        close.addActionListener(this);

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

        s.close();
        return text;
    }

    private void saveFile() throws IOException {

        String text = textArea.getText();

        FileWriter fw = new FileWriter(file);
        fw.write(text);
        fw.close();

        JOptionPane.showMessageDialog(null, "FILE SAVED", "Status", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == close) {

            setVisible(false);
            dispose();
        } else if (e.getSource() == save) {

            try {
                saveFile();
            } catch (Exception e1) {

                JOptionPane.showMessageDialog(null, "No file was found", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
