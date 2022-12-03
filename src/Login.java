import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login extends JPanel {

    public Login() {

        JLabel header = new JLabel("Login");
        header.setFont(Style.HEADER_FONT);

        JLabel label = new JLabel("Username: "), label1 = new JLabel("Password: ");

        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();

        JButton login = Style.GetStyledButton("Login");

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
