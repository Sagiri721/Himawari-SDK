import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.event.*;

public class Login extends JPanel {

    JTextField username = new JTextField();
    JPasswordField password = new JPasswordField();

    public Login() {

        JLabel header = new JLabel("Login");
        header.setFont(Style.HEADER_FONT);
        
        JLabel label = new JLabel("Username: "), label1 = new JLabel("Password: ");
        
        JButton login = Style.GetStyledButton("Login");
        login.setAlignmentX(SwingConstants.CENTER);
        password.setEchoChar('#');

        login.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                 
                    Object[] valid = WebsiteConnector.isLoginValid(username.getText().trim(), new String(password.getPassword()));
                    if((boolean) valid[0]) {

                        JOptionPane.showMessageDialog(null, "Login success", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                        //Save login
                        Settings.username = username.getText().trim();
                        Settings.uid = (String) valid[1];

                        Settings.updateFile();
                    }else {

                        JOptionPane.showMessageDialog(null, "Login credentials do not match", "ERROR", JOptionPane.ERROR_MESSAGE);
                        password.setText("");
                    }
                    
                } catch (Exception ee) {
                
                    JOptionPane.showMessageDialog(null, "No user found", "ERROR", JOptionPane.ERROR_MESSAGE);
                    username.setText("");
                }
            }

        });

        label.setBounds(5, 55, 400, 20);
        label1.setBounds(5, 80, 400, 20);

        username.setBounds(100, 50, 300, 20);
        password.setBounds(100, 80, 300, 20);

        login.setBounds(5, 115, 200, 30);

        header.setBounds(5, 5, 200, 30);

        add(header);
        add(label);
        add(label1);
        add(login);
        add(password);
        add(username);

        setLayout(null);
    }
}
