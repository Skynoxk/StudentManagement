import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtUsernameOrEmail;
    private JPasswordField pwdPassword;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MainFrame() {
        setTitle("Login System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1095, 448);

        contentPane = new BackgroundPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // Transparent panel for login form
        TransparentPanel panel = new TransparentPanel(0.8f);
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(UIManager.getBorder("CheckBox.border"));
        panel.setBounds(374, 43, 357, 338);
        contentPane.add(panel);
        panel.setLayout(null);

        // ITC Logo
        ImageIcon originalLogo = new ImageIcon("C:\\Users\\User\\Pictures\\Screenshots\\Logo-ITC.png");
        Image resizedImage = originalLogo.getImage().getScaledInstance(138, 100, Image.SCALE_SMOOTH);
        LogoLabel logoLabel = new LogoLabel(new ImageIcon(resizedImage), 0.8f);
        logoLabel.setBounds(130, 10, 100, 70);
        panel.add(logoLabel);

        // Separator
        CustomSeparator separator = new CustomSeparator();
        separator.setBounds(10, 98, 337, 10);
        panel.add(separator);

        // Username or Email field with placeholder effect
        txtUsernameOrEmail = new JTextField("Username or Email");
        txtUsernameOrEmail.setHorizontalAlignment(SwingConstants.CENTER);
        txtUsernameOrEmail.setForeground(Color.GRAY);
        txtUsernameOrEmail.setBounds(27, 130, 301, 38);
        txtUsernameOrEmail.setColumns(10);
        panel.add(txtUsernameOrEmail);

        txtUsernameOrEmail.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtUsernameOrEmail.getText().equals("Username or Email")) {
                    txtUsernameOrEmail.setText("");
                    txtUsernameOrEmail.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtUsernameOrEmail.getText().isEmpty()) {
                    txtUsernameOrEmail.setText("Username or Email");
                    txtUsernameOrEmail.setForeground(Color.GRAY);
                }
            }
        });

        // Password field with placeholder effect
        pwdPassword = new JPasswordField();
        pwdPassword.setHorizontalAlignment(SwingConstants.CENTER);
        pwdPassword.setBounds(27, 182, 301, 43);
        pwdPassword.setEchoChar((char) 0);
        pwdPassword.setText("Password");
        pwdPassword.setForeground(Color.GRAY);
        panel.add(pwdPassword);

        pwdPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(pwdPassword.getPassword()).equals("Password")) {
                    pwdPassword.setText("");
                    pwdPassword.setEchoChar('•');
                    pwdPassword.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (pwdPassword.getPassword().length == 0) {
                    pwdPassword.setText("Password");
                    pwdPassword.setEchoChar((char) 0);
                    pwdPassword.setForeground(Color.GRAY);
                }
            }
        });

        // Login button
        JButton btnLogin = new JButton("Login");
        btnLogin.setForeground(Color.GRAY);
        btnLogin.setBounds(27, 246, 301, 38);
        panel.add(btnLogin);

        btnLogin.addActionListener(e -> {
            String username = txtUsernameOrEmail.getText();
            char[] password = pwdPassword.getPassword();

            if (username.isEmpty() || password.length == 0 || username.equals("Username or Email") || String.valueOf(password).equals("Password")) {
                JOptionPane.showMessageDialog(null, "Please fill in both fields.");
            } else {
                JOptionPane.showMessageDialog(null, "Logging in...");
                // Proceed with actual login logic
            }
        });
    }
}

// Background panel with image
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel() {
        ImageIcon icon = new ImageIcon("C:\\Users\\User\\Pictures\\Screenshots\\School_Background_Framework.jpg");
        backgroundImage = icon.getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

// Transparent panel
class TransparentPanel extends JPanel {
    private float opacity;

    public TransparentPanel(float opacity) {
        this.opacity = opacity;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
        super.paintComponent(g);
    }
}

// Logo label with transparency
class LogoLabel extends JLabel {
    private float opacity;
    private Image image;

    public LogoLabel(ImageIcon icon, float opacity) {
        this.image = icon.getImage();
        this.opacity = opacity;
        setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2d.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        g2d.dispose();
    }
}

// Custom separator
class CustomSeparator extends JSeparator {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), 3);
    }
}
