import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * LoginFrame.java
 * The first screen users see. Supports role-based login for Admin and Employee.
 *
 * Admin credentials  : admin / admin123  (hardcoded)
 * Employee credentials: name + password  (validated against DataStore)
 */
public class LoginFrame extends JFrame {

    // ── Swing components ──────────────────────────────────────────────────────
    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JRadioButton   adminRadio;
    private JRadioButton   employeeRadio;
    private JButton        loginButton;

    // ── Hardcoded admin credentials ───────────────────────────────────────────
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    // ── Constructor ───────────────────────────────────────────────────────────
    public LoginFrame() {
        setTitle("ELMS – Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);   // full screen
        setMinimumSize(new Dimension(600, 500));
        setResizable(true);

        initComponents();
    }

    // ── Build UI ──────────────────────────────────────────────────────────────
    private void initComponents() {

        // ── Full-screen wrapper: centres the card in the window ──────────────
        JPanel screen = new JPanel(new GridBagLayout());
        screen.setBackground(new Color(230, 236, 245));

        // ── Card panel (fixed width, centred) ─────────────────────────────────
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(245, 247, 250));
        outer.setPreferredSize(new Dimension(560, 500));

        // Drop shadow effect via LineBorder
        outer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(190, 205, 230), 1),
            new EmptyBorder(36, 55, 36, 55)));

        // ── Title label ───────────────────────────────────────────────────────
        JLabel titleLabel = new JLabel("Employee Leave Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(25, 70, 150));
        titleLabel.setBorder(new EmptyBorder(0, 0, 24, 0));

        // ── Form panel ────────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(9, 6, 9, 6);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.anchor  = GridBagConstraints.WEST;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);

        // Role selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(labelFont);
        form.add(roleLabel, gbc);

        adminRadio    = new JRadioButton("Admin",    true);
        employeeRadio = new JRadioButton("Employee", false);
        adminRadio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        employeeRadio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        adminRadio.setOpaque(false);
        employeeRadio.setOpaque(false);
        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(adminRadio);
        roleGroup.add(employeeRadio);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rolePanel.setOpaque(false);
        rolePanel.add(adminRadio);
        rolePanel.add(Box.createHorizontalStrut(24));
        rolePanel.add(employeeRadio);

        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        form.add(rolePanel, gbc);

        // Username
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel userLbl = new JLabel("Username:");
        userLbl.setFont(labelFont);
        form.add(userLbl, gbc);

        usernameField = new JTextField(18);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(0, 36));
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        form.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        JLabel passLbl = new JLabel("Password:");
        passLbl.setFont(labelFont);
        form.add(passLbl, gbc);

        passwordField = new JPasswordField(18);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(0, 36));
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;
        form.add(passwordField, gbc);

        // Role hint label
        JLabel hintLabel = new JLabel("Tip: Admin → admin / admin123   |   Employee → Name / password");
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hintLabel.setForeground(new Color(120, 130, 150));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        form.add(hintLabel, gbc);

        // Login button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(new Color(25, 70, 150));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(true);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(0, 52));
        loginButton.setMargin(new Insets(0, 0, 0, 0));

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 40, 6, 40);
        form.add(loginButton, gbc);

        // ── Assemble ──────────────────────────────────────────────────────────
        outer.add(titleLabel, BorderLayout.NORTH);
        outer.add(form,       BorderLayout.CENTER);
        screen.add(outer);   // card centred on full-screen background
        add(screen);

        // ── ActionListener: Login button ──────────────────────────────────────
        loginButton.addActionListener(e -> handleLogin());

        // Allow pressing Enter to login
        getRootPane().setDefaultButton(loginButton);
    }

    // ── Login logic ───────────────────────────────────────────────────────────
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password.",
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (adminRadio.isSelected()) {
            // ── Admin login ───────────────────────────────────────────────────
            if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                dispose();                          // close login window
                new AdminDashboard().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid admin credentials.\n(Username: admin | Password: admin123)",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // ── Employee login ────────────────────────────────────────────────
            Employee emp = DataStore.getInstance().validateEmployee(username, password);
            if (emp != null) {
                dispose();
                new EmployeeDashboard(emp).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid employee credentials.\nUse your full name and password.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
