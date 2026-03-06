import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * AdminDashboard.java
 * Main control panel for the Admin role.
 * Contains navigation buttons to all admin sub-modules.
 */
public class AdminDashboard extends JFrame {

    // ── Constructor ───────────────────────────────────────────────────────────
    public AdminDashboard() {
        setTitle("ELMS – Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);   // full screen
        setMinimumSize(new Dimension(640, 500));
        setResizable(true);
        initComponents();
    }

    // ── Build UI ──────────────────────────────────────────────────────────────
    private void initComponents() {
        // Full-screen background — deep slate blue
        JPanel screen = new JPanel(new GridBagLayout());
        screen.setBackground(new Color(15, 23, 42));   // near-black slate

        // Card — dark navy with subtle border
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 75, 120), 1),
            new EmptyBorder(40, 60, 40, 60)));
        outer.setBackground(new Color(28, 37, 65));    // deep navy card
        outer.setPreferredSize(new Dimension(600, 590));

        // Title + subtitle header block
        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBlock.setOpaque(false);
        titleBlock.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel title = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Employee Leave Management System", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(160, 180, 220));

        titleBlock.add(title);
        titleBlock.add(sub);

        // Button panel — 6 rows with even spacing
        JPanel btnPanel = new JPanel(new GridLayout(5, 1, 0, 12));
        btnPanel.setOpaque(false);

        // Professional colour palette: each button has a distinct, harmonious hue
        JButton addEmpBtn     = createBtn("  Add Employee",          new Color(16, 185, 129));  // emerald
        JButton deleteEmpBtn  = createBtn("  Delete Employee",       new Color(239, 68,  68));  // coral-red
        JButton viewEmpBtn    = createBtn("  View All Employees",    new Color(59, 130, 246));  // bright blue
        JButton pendingMgrBtn = createBtn("  Approve / Reject Leave",new Color(245, 158, 11));  // amber
        JButton logoutBtn     = createBtn("  Logout",                new Color(100, 116, 139)); // steel grey

        btnPanel.add(addEmpBtn);
        btnPanel.add(deleteEmpBtn);
        btnPanel.add(viewEmpBtn);
        btnPanel.add(pendingMgrBtn);
        btnPanel.add(logoutBtn);

        outer.add(titleBlock, BorderLayout.NORTH);
        outer.add(btnPanel,   BorderLayout.CENTER);
        screen.add(outer);
        add(screen);

        // ── ActionListeners ───────────────────────────────────────────────────

        addEmpBtn.addActionListener(e ->
            new AddEmployeeFrame(this).setVisible(true));

        deleteEmpBtn.addActionListener(e -> handleDeleteEmployee());

        viewEmpBtn.addActionListener(e -> showEmployeeTable());

        pendingMgrBtn.addActionListener(e ->
            new ViewLeaveFrame(this, -1, "Approve / Reject Leaves").setVisible(true));

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }

    // ── Button factory — dark-theme style ────────────────────────────────────
    private JButton createBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 56));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMargin(new Insets(0, 22, 0, 14));
        // Subtle hover: lighten on mouse-enter, restore on exit
        Color hoverBg = bg.brighter();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hoverBg); }
            public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    // ── Delete Employee by ID ─────────────────────────────────────────────────
    private void handleDeleteEmployee() {
        String input = JOptionPane.showInputDialog(this,
            "Enter Employee ID to delete:", "Delete Employee",
            JOptionPane.PLAIN_MESSAGE);

        if (input == null || input.trim().isEmpty()) return;

        try {
            int empId = Integer.parseInt(input.trim());
            boolean removed = DataStore.getInstance().deleteEmployee(empId);
            if (removed) {
                JOptionPane.showMessageDialog(this,
                    "Employee (ID: " + empId + ") deleted successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "No employee found with ID: " + empId,
                    "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid numeric Employee ID.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Show Employee Table using JTable ──────────────────────────────────────
    private void showEmployeeTable() {
        String[] cols = {"ID", "Name", "Department", "Phone", "Email"};
        java.util.ArrayList<Employee> emps = DataStore.getInstance().getEmployees();

        Object[][] data = new Object[emps.size()][5];
        for (int i = 0; i < emps.size(); i++) {
            Employee e = emps.get(i);
            data[i][0] = e.getEmpId();
            data[i][1] = e.getName();
            data[i][2] = e.getDepartment();
            data[i][3] = e.getPhone();
            data[i][4] = e.getEmail();
        }

        JTable table = new JTable(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; } // read-only
        };
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(620, 300));

        JDialog dialog = new JDialog(this, "All Employees", true);
        dialog.add(scroll);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
