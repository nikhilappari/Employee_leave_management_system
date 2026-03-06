import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * ViewLeaveFrame.java
 * Shared screen used by both Admin and Employee to view leave requests.
 *
 * Admin mode   : empId == -1  → Shows ALL leave requests with Approve/Reject buttons
 *                               + per-request leave balance info (5 leaves/month policy).
 * Employee mode: empId >= 0   → Shows only that employee's own leave requests (read-only).
 */
public class ViewLeaveFrame extends JDialog {

    private final int     filterEmpId;
    private final boolean isAdminView;

    private DefaultTableModel tableModel;
    private JTable            table;

    // ── Column indices (admin view) ────────────────────────────────────────────
    private static final int COL_LEAVE_ID    = 0;
    private static final int COL_EMP_ID      = 1;
    private static final int COL_FROM        = 3;
    private static final int COL_TO          = 4;
    private static final int COL_DAYS        = 5;
    private static final int COL_USED        = 6;
    private static final int COL_LEFT        = 7;
    private static final int COL_STATUS      = 8;
    // ── Constructor ───────────────────────────────────────────────────────────
    public ViewLeaveFrame(JFrame parent, int empId, String windowTitle) {
        super(parent, windowTitle, true);
        this.filterEmpId = empId;
        this.isAdminView = (empId == -1);
        setSize(isAdminView ? 1050 : 680, 520);
        setLocationRelativeTo(parent);
        setResizable(true);
        initComponents();
    }

    // ── Build UI ──────────────────────────────────────────────────────────────
    private void initComponents() {
        JPanel outer = new JPanel(new BorderLayout(8, 8));
        outer.setBorder(new EmptyBorder(14, 14, 14, 14));
        outer.setBackground(new Color(245, 247, 250));

        // ── NORTH: Policy info banner (admin only) ────────────────────────────
        if (isAdminView) {
            JPanel banner = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            banner.setBackground(new Color(25, 70, 150));
            banner.setBorder(new EmptyBorder(6, 10, 6, 10));

            JLabel policyIcon = new JLabel("📋");
            policyIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));

            JLabel policyLbl = new JLabel(
                "Leave Policy:  Each employee is entitled to  " +
                DataStore.MAX_LEAVES_PER_MONTH +
                " leaves per calendar month.  " +
                "Columns show days applied for, leaves used & remaining for the leave's month.");
            policyLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            policyLbl.setForeground(Color.WHITE);

            banner.add(policyIcon);
            banner.add(policyLbl);
            outer.add(banner, BorderLayout.NORTH);
        }

        // ── Table ─────────────────────────────────────────────────────────────
        String[] cols = isAdminView
            ? new String[]{"Leave ID", "Emp ID", "Employee Name",
                           "From Date", "To Date",
                           "Days Applied", "Used (Month)", "Left (Month)", "Status"}
            : new String[]{"Leave ID", "From Date", "To Date", "Days Applied", "Status"};

        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));

        // Centre-align numeric/small columns
        DefaultTableCellRenderer centreRenderer = new DefaultTableCellRenderer();
        centreRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        if (isAdminView) {
            for (int col : new int[]{COL_LEAVE_ID, COL_EMP_ID, COL_DAYS, COL_USED, COL_LEFT}) {
                table.getColumnModel().getColumn(col).setCellRenderer(centreRenderer);
            }
            // Preferred column widths
            int[] widths = {70, 60, 160, 100, 100, 95, 100, 95, 90};
            for (int i = 0; i < widths.length; i++) {
                table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            }
        }

        // Colour rows by status + highlight low leave balance in red
        int statusCol = isAdminView ? COL_STATUS : 4;  // employee view: status is column 4
        int leftCol   = isAdminView ? COL_LEFT   : -1;

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                if (!sel) {
                    String status = (String) tableModel.getValueAt(row, statusCol);
                    if ("Approved".equals(status))      c.setBackground(new Color(198, 239, 206));
                    else if ("Rejected".equals(status)) c.setBackground(new Color(255, 199, 206));
                    else                                c.setBackground(Color.WHITE);

                    // Highlight "Left (Month)" cell in orange/red when ≤ 1
                    if (isAdminView && col == leftCol) {
                        Object leftVal = tableModel.getValueAt(row, leftCol);
                        if (leftVal instanceof Integer) {
                            int left = (Integer) leftVal;
                            if (left == 0)      c.setBackground(new Color(255, 100, 100));  // red
                            else if (left <= 1) c.setBackground(new Color(255, 200, 100));  // amber
                        }
                        setHorizontalAlignment(SwingConstants.CENTER);
                    } else {
                        setHorizontalAlignment(SwingConstants.LEFT);
                    }
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        outer.add(scroll, BorderLayout.CENTER);

        // ── Button panel ──────────────────────────────────────────────────────
        if (isAdminView) {
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 6));
            btnPanel.setOpaque(false);

            JButton approveBtn = new JButton("✅  Approve Selected");
            JButton rejectBtn  = new JButton("❌  Reject Selected");
            JButton refreshBtn = new JButton("🔄  Refresh");
            JButton closeBtn   = new JButton("Close");

            styleBtn(approveBtn, new Color(34, 139, 34));
            styleBtn(rejectBtn,  new Color(180, 40, 40));
            styleBtn(refreshBtn, new Color(30, 80, 160));
            styleBtn(closeBtn,   new Color(120, 120, 120));

            btnPanel.add(approveBtn);
            btnPanel.add(rejectBtn);
            btnPanel.add(refreshBtn);
            btnPanel.add(closeBtn);

            outer.add(btnPanel, BorderLayout.SOUTH);

            approveBtn.addActionListener(e -> updateSelectedLeave("Approved"));
            rejectBtn .addActionListener(e -> updateSelectedLeave("Rejected"));
            refreshBtn.addActionListener(e -> loadData());
            closeBtn  .addActionListener(e -> dispose());

        } else {
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            btnPanel.setOpaque(false);
            JButton closeBtn = new JButton("Close");
            styleBtn(closeBtn, new Color(120, 120, 120));
            btnPanel.add(closeBtn);
            outer.add(btnPanel, BorderLayout.SOUTH);
            closeBtn.addActionListener(e -> dispose());
        }

        add(outer);
        loadData();
    }

    // ── Load / refresh table data ─────────────────────────────────────────────
    private void loadData() {
        tableModel.setRowCount(0);

        List<LeaveRequest> source = isAdminView
            ? DataStore.getInstance().getLeaveRequests()
            : DataStore.getInstance().getLeavesByEmpId(filterEmpId);

        if (source.isEmpty()) {
            if (isAdminView) {
                tableModel.addRow(new Object[]{"—", "—", "No records found", "—", "—", "—", "—", "—", "—"});
            } else {
                tableModel.addRow(new Object[]{"—", "No records found", "—", "—", "—"});
            }
            return;
        }

        DataStore ds = DataStore.getInstance();

        for (LeaveRequest lr : source) {
            // Determine which month to compute balance for: use the from-date month
            int year  = LocalDate.now().getYear();
            int month = LocalDate.now().getMonthValue();
            try {
                LocalDate from = LocalDate.parse(lr.getFromDate());
                year  = from.getYear();
                month = from.getMonthValue();
            } catch (Exception ignored) { /* use today if parsing fails */ }

            if (isAdminView) {
                Employee emp     = ds.findById(lr.getEmpId());
                String empName   = (emp != null) ? emp.getName() : "Unknown";
                int daysApplied  = lr.getDaysApplied();
                int leavesUsed   = ds.getLeavesUsedInMonth(lr.getEmpId(), year, month);
                int leavesLeft   = ds.getLeavesLeftInMonth(lr.getEmpId(), year, month);

                tableModel.addRow(new Object[]{
                    lr.getLeaveId(),
                    lr.getEmpId(),
                    empName,
                    lr.getFromDate(),
                    lr.getToDate(),
                    daysApplied,
                    leavesUsed + " / " + DataStore.MAX_LEAVES_PER_MONTH,
                    leavesLeft,
                    lr.getStatus()
                });
            } else {
                tableModel.addRow(new Object[]{
                    lr.getLeaveId(),
                    lr.getFromDate(),
                    lr.getToDate(),
                    lr.getDaysApplied(),
                    lr.getStatus()
                });
            }
        }
    }

    // ── Approve or Reject selected leave ─────────────────────────────────────
    private void updateSelectedLeave(String newStatus) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a leave request from the table first.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object leaveIdObj = tableModel.getValueAt(selectedRow, COL_LEAVE_ID);
        if ("—".equals(leaveIdObj)) return;

        int leaveId = (int) leaveIdObj;
        LeaveRequest lr = DataStore.getInstance().findLeaveById(leaveId);

        if (lr == null) {
            JOptionPane.showMessageDialog(this,
                "Leave request not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Employee emp     = DataStore.getInstance().findById(lr.getEmpId());
        String   empName = (emp != null) ? emp.getName() : "Employee #" + lr.getEmpId();

        // Extra info for admin confirmation dialog
        int daysApplied = lr.getDaysApplied();
        int year = LocalDate.now().getYear(), month = LocalDate.now().getMonthValue();
        try {
            LocalDate from = LocalDate.parse(lr.getFromDate());
            year  = from.getYear();
            month = from.getMonthValue();
        } catch (Exception ignored) {}
        int leavesLeft = DataStore.getInstance().getLeavesLeftInMonth(lr.getEmpId(), year, month);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Set Leave ID " + leaveId + " to '" + newStatus + "'?\n\n" +
            "Employee  : " + empName  + "  (ID: " + lr.getEmpId() + ")\n" +
            "Period    : " + lr.getFromDate() + "  →  " + lr.getToDate() + "\n" +
            "Days applied for : " + daysApplied + " day(s)\n" +
            "Leaves left this month (after this action): " +
                (newStatus.equals("Approved") ? Math.max(0, leavesLeft - daysApplied) : leavesLeft),
            "Confirm " + newStatus,
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            lr.setStatus(newStatus);
            loadData();
            JOptionPane.showMessageDialog(this,
                "Leave request " + newStatus.toLowerCase() + " successfully.",
                "Updated", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ── Button style helper ───────────────────────────────────────────────────
    private void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(170, 46));
        btn.setMargin(new Insets(0, 10, 0, 10));
    }
}
