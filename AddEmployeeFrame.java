import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * AddEmployeeFrame.java
 * Modal dialog for Admin to add a new employee.
 * Layout: BorderLayout with NORTH=title, CENTER=form fields, SOUTH=buttons.
 * This ensures buttons are always fully visible regardless of content height.
 */
public class AddEmployeeFrame extends JDialog {

    // ── Form fields ───────────────────────────────────────────────────────────
    private JTextField nameField;
    private JTextField deptField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField passwordField;

    // Shared fonts — class-level so both initComponents() and addRow() can use them
    private final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD,  13);
    private final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    // ── Constructor ───────────────────────────────────────────────────────────
    public AddEmployeeFrame(JFrame parent) {
        super(parent, "Add New Employee", true);
        setSize(520, 460);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    // ── Build UI ──────────────────────────────────────────────────────────────
    private void initComponents() {

        // ── Root panel: white/light background, padded ────────────────────────
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(new Color(245, 247, 250));
        root.setBorder(new EmptyBorder(20, 36, 20, 36));

        // ── NORTH: Header ─────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 18, 0));

        JLabel title = new JLabel("Add New Employee", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(22, 120, 55));

        JLabel subtitle = new JLabel("Fill in all fields and click Save", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subtitle.setForeground(new Color(120, 130, 150));

        header.add(title,    BorderLayout.CENTER);
        header.add(subtitle, BorderLayout.SOUTH);

        // ── CENTER: Form grid ─────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(8, 4, 8, 8);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.anchor  = GridBagConstraints.WEST;

        // Input fields
        nameField     = styledField();
        deptField     = styledField();
        phoneField    = styledField();
        emailField    = styledField();
        passwordField = styledField();

        // Employee ID row (read-only info)
        addRow(form, gbc, 0, "Employee ID:", null,          true);
        addRow(form, gbc, 1, "Name:",        nameField,     false);
        addRow(form, gbc, 2, "Department:",  deptField,     false);
        addRow(form, gbc, 3, "Phone:",       phoneField,    false);
        addRow(form, gbc, 4, "Email:",       emailField,    false);
        addRow(form, gbc, 5, "Password:",    passwordField, false);

        // ── SOUTH: Button bar ─────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 14, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(18, 40, 0, 40));

        JButton saveBtn   = makeBtn("Save Employee", new Color(22, 120, 55),  Color.WHITE);
        JButton cancelBtn = makeBtn("Cancel",         new Color(230, 50,  50), Color.WHITE);

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        // ── Assemble ──────────────────────────────────────────────────────────
        root.add(header,   BorderLayout.NORTH);
        root.add(form,     BorderLayout.CENTER);
        root.add(btnPanel, BorderLayout.SOUTH);
        add(root);

        // ── ActionListeners ───────────────────────────────────────────────────
        saveBtn  .addActionListener(e -> handleSave());
        cancelBtn.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(saveBtn);
    }

    // ── Add a row: label on left, field (or auto-assign hint) on right ────────
    private void addRow(JPanel form, GridBagConstraints gbc,
                        int row, String labelText, JTextField field, boolean isHint) {
        // Label column
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.3;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(LABEL_FONT);
        form.add(lbl, gbc);

        // Value column
        gbc.gridx = 1; gbc.weightx = 0.7;
        if (isHint) {
            JLabel hint = new JLabel("(auto-assigned)");
            hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            hint.setForeground(new Color(130, 150, 170));
            form.add(hint, gbc);
        } else {
            form.add(field, gbc);
        }
    }

    // ── Helper: creates a styled JTextField ───────────────────────────────────
    private JTextField styledField() {
        JTextField f = new JTextField(18);
        f.setFont(FIELD_FONT);
        f.setPreferredSize(new Dimension(0, 36));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(190, 205, 225), 1),
            new EmptyBorder(4, 8, 4, 8)));
        return f;
    }

    // ── Helper: creates a styled JButton ─────────────────────────────────────
    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 48));
        return btn;
    }

    // ── Save logic ────────────────────────────────────────────────────────────
    private void handleSave() {
        String name     = nameField.getText().trim();
        String dept     = deptField.getText().trim();
        String phone    = phoneField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (name.isEmpty() || dept.isEmpty() || phone.isEmpty() ||
            email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "All fields are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DataStore ds = DataStore.getInstance();
        int newId    = ds.nextEmpId();
        ds.addEmployee(new Employee(newId, name, dept, phone, email, password));

        JOptionPane.showMessageDialog(this,
            "Employee added successfully!\nEmployee ID assigned: " + newId,
            "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
