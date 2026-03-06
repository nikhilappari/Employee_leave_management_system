import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * ApplyLeaveFrame.java
 * Dialog for an Employee to submit a leave application.
 * Uses BorderLayout: NORTH=title, CENTER=form, SOUTH=buttons
 * so buttons are ALWAYS fully visible regardless of content height.
 */
public class ApplyLeaveFrame extends JDialog {

    private final Employee applicant;

    // ── Form fields ───────────────────────────────────────────────────────────
    private JTextField fromDateField;
    private JTextField toDateField;

    // Shared fonts
    private final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD,  13);
    private final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    // ── Constructor ───────────────────────────────────────────────────────────
    public ApplyLeaveFrame(JFrame parent, Employee applicant) {
        super(parent, "Apply for Leave", true);
        this.applicant = applicant;
        setSize(480, 360);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    // ── Build UI ──────────────────────────────────────────────────────────────
    private void initComponents() {

        // ── Root panel ────────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(new Color(245, 247, 250));
        root.setBorder(new EmptyBorder(22, 40, 22, 40));

        // ── NORTH: Title ──────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 18, 0));

        JLabel title = new JLabel("Apply for Leave", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(25, 70, 150));

        JLabel subtitle = new JLabel("Enter your leave dates below", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subtitle.setForeground(new Color(120, 130, 150));

        header.add(title,    BorderLayout.CENTER);
        header.add(subtitle, BorderLayout.SOUTH);

        // ── CENTER: Form ──────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(10, 4, 10, 8);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.anchor  = GridBagConstraints.WEST;

        // Row 0 — Employee info (read-only)
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.weightx = 0.35;
        JLabel empLbl = new JLabel("Employee:");
        empLbl.setFont(LABEL_FONT);
        form.add(empLbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        JLabel empVal = new JLabel(applicant.getName() + "  (ID: " + applicant.getEmpId() + ")");
        empVal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        empVal.setForeground(new Color(50, 80, 130));
        form.add(empVal, gbc);

        // Row 1 — From Date
        fromDateField = styledField("e.g. 2026-04-01");
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        JLabel fromLbl = new JLabel("From Date:");
        fromLbl.setFont(LABEL_FONT);
        form.add(fromLbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        form.add(fromDateField, gbc);

        // Row 2 — To Date
        toDateField = styledField("e.g. 2026-04-05");
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.35;
        JLabel toLbl = new JLabel("To Date:");
        toLbl.setFont(LABEL_FONT);
        form.add(toLbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        form.add(toDateField, gbc);

        // Row 3 — hint
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JLabel hint = new JLabel("Accepted: YYYY-MM-DD · DD/MM/YYYY · MM/DD/YYYY · DD-MM-YYYY · DD.MM.YYYY · DD MMM YYYY");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(new Color(140, 150, 165));
        form.add(hint, gbc);

        // ── SOUTH: Buttons ────────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 14, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(18, 20, 0, 20));

        JButton submitBtn = makeBtn("Submit Leave",  new Color(25, 70, 150),  Color.WHITE);
        JButton cancelBtn = makeBtn("Cancel",         new Color(220, 50,  50), Color.WHITE);

        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);

        // ── Assemble ──────────────────────────────────────────────────────────
        root.add(header,   BorderLayout.NORTH);
        root.add(form,     BorderLayout.CENTER);
        root.add(btnPanel, BorderLayout.SOUTH);
        add(root);

        // ── ActionListeners ───────────────────────────────────────────────────
        submitBtn.addActionListener(e -> handleSubmit());
        cancelBtn.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(submitBtn);
    }

    // ── Styled text field with placeholder foreground ─────────────────────────
    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(FIELD_FONT);
        f.setPreferredSize(new Dimension(0, 36));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(190, 205, 225), 1),
            new EmptyBorder(4, 8, 4, 8)));
        f.setForeground(new Color(40, 40, 40));
        f.setToolTipText(placeholder);
        return f;
    }

    // ── Styled button ─────────────────────────────────────────────────────────
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
        // Hover effect
        Color hover = bg.brighter();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited (MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    // ── Accepted date patterns (tried in order) ──────────────────────────────
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),   // 2026-04-01
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),   // 01/04/2026
        DateTimeFormatter.ofPattern("MM/dd/yyyy"),   // 04/01/2026
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),   // 01-04-2026
        DateTimeFormatter.ofPattern("dd.MM.yyyy"),   // 01.04.2026
        DateTimeFormatter.ofPattern("d/M/yyyy"),     // 1/4/2026
        DateTimeFormatter.ofPattern("d-M-yyyy"),     // 1-4-2026
        DateTimeFormatter.ofPattern("dd MMM yyyy"),  // 01 Apr 2026
        DateTimeFormatter.ofPattern("d MMM yyyy"),   // 1 Apr 2026
        DateTimeFormatter.ofPattern("dd MMMM yyyy"), // 01 April 2026
        DateTimeFormatter.ofPattern("yyyy/MM/dd")    // 2026/04/01
    );

    /**
     * Tries to parse a date string with all accepted formats.
     * Returns the date normalised as YYYY-MM-DD, or null if no format matched.
     */
    private String parseDate(String input) {
        String trimmed = input.trim();
        for (DateTimeFormatter fmt : DATE_FORMATS) {
            try {
                LocalDate date = LocalDate.parse(trimmed, fmt);
                return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException ignored) {
                // try next format
            }
        }
        return null;  // no format matched
    }

    // ── Submit logic ──────────────────────────────────────────────────────────
    private void handleSubmit() {
        String fromRaw = fromDateField.getText().trim();
        String toRaw   = toDateField.getText().trim();

        if (fromRaw.isEmpty() || toRaw.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Both From Date and To Date are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Try to parse each date with the accepted formats list
        String from = parseDate(fromRaw);
        String to   = parseDate(toRaw);

        if (from == null || to == null) {
            JOptionPane.showMessageDialog(this,
                "Could not recognise the date format.\n\n" +
                "Accepted formats:\n" +
                "  • YYYY-MM-DD      e.g. 2026-04-01\n" +
                "  • DD/MM/YYYY      e.g. 01/04/2026\n" +
                "  • MM/DD/YYYY      e.g. 04/01/2026\n" +
                "  • DD-MM-YYYY      e.g. 01-04-2026\n" +
                "  • DD.MM.YYYY      e.g. 01.04.2026\n" +
                "  • DD MMM YYYY     e.g. 01 Apr 2026\n" +
                "  • DD MMMM YYYY    e.g. 01 April 2026\n" +
                "  • YYYY/MM/DD      e.g. 2026/04/01",
                "Invalid Date Format", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate that 'from' is not after 'to'
        if (LocalDate.parse(from).isAfter(LocalDate.parse(to))) {
            JOptionPane.showMessageDialog(this,
                "'From Date' cannot be later than 'To Date'.",
                "Date Range Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Store as normalised YYYY-MM-DD
        DataStore ds = DataStore.getInstance();
        ds.addLeaveRequest(new LeaveRequest(
            ds.nextLeaveId(), applicant.getEmpId(), from, to, "Pending"));

        JOptionPane.showMessageDialog(this,
            "Leave application submitted successfully!\n" +
            "From: " + from + "  →  To: " + to + "\nStatus: Pending",
            "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
