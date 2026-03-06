import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * EmployeeDashboard.java
 * Main panel for a logged-in Employee. Shows their name and provides
 * buttons to apply for leave and check leave status.
 */
public class EmployeeDashboard extends JFrame {

    private final Employee currentEmployee;

    // ── Constructor ───────────────────────────────────────────────────────────
    public EmployeeDashboard(Employee employee) {
        this.currentEmployee = employee;
        setTitle("ELMS – Employee Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);   // full screen
        setMinimumSize(new Dimension(560, 440));
        setResizable(true);
        initComponents();
    }

    // ── Build UI ──────────────────────────────────────────────────────────────
    private void initComponents() {
        // Full-screen background
        JPanel screen = new JPanel(new GridBagLayout());
        screen.setBackground(new Color(220, 230, 245));

        // Card centred on screen
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(190, 205, 230), 1),
            new EmptyBorder(36, 55, 36, 55)));
        outer.setBackground(new Color(245, 247, 252));
        outer.setPreferredSize(new Dimension(520, 420));

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(false);

        JLabel title = new JLabel("Employee Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(25, 70, 150));

        JLabel welcome = new JLabel("Welcome, " + currentEmployee.getName() +
                                    "  (ID: " + currentEmployee.getEmpId() + ")",
                                    SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        welcome.setForeground(new Color(80, 100, 130));
        welcome.setBorder(new EmptyBorder(4, 0, 20, 0));

        header.add(title);
        header.add(welcome);

        // Button panel
        JPanel btnPanel = new JPanel(new GridLayout(3, 1, 0, 16));
        btnPanel.setOpaque(false);

        JButton applyLeaveBtn  = createBtn("📝  Apply for Leave",    new Color(30, 80, 160));
        JButton checkStatusBtn = createBtn("📊  Check Leave Status", new Color(100, 60, 160));
        JButton logoutBtn      = createBtn("🔒  Logout",             new Color(90, 90, 90));

        btnPanel.add(applyLeaveBtn);
        btnPanel.add(checkStatusBtn);
        btnPanel.add(logoutBtn);

        outer.add(header,   BorderLayout.NORTH);
        outer.add(btnPanel, BorderLayout.CENTER);
        screen.add(outer);
        add(screen);

        // ── ActionListeners ───────────────────────────────────────────────────

        applyLeaveBtn.addActionListener(e ->
            new ApplyLeaveFrame(this, currentEmployee).setVisible(true));

        checkStatusBtn.addActionListener(e ->
            new ViewLeaveFrame(this, currentEmployee.getEmpId(),
                               "My Leave Requests").setVisible(true));

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }

    // ── Button factory ────────────────────────────────────────────────────────
    private JButton createBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 55));
        btn.setMargin(new Insets(0, 14, 0, 14));
        return btn;
    }
}
