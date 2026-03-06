import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

/**
 * DataStore.java
 * Singleton data manager holding all in-memory lists.
 * Acts as the single source of truth for employees and leave requests.
 */
public class DataStore {

    // ── Singleton instance ────────────────────────────────────────────────────
    private static DataStore instance;

    // ── In-memory storage ─────────────────────────────────────────────────────
    private ArrayList<Employee>    employees    = new ArrayList<>();
    private ArrayList<LeaveRequest> leaveRequests = new ArrayList<>();

    // Auto-increment counters
    private int nextEmpId   = 3;   // two sample employees pre-seeded (IDs 1,2)
    private int nextLeaveId = 1;

    // ── Private constructor — seeds sample data ────────────────────────────────
    private DataStore() {
        // Pre-seeded employees for demonstration
        employees.add(new Employee(1, "Alice Johnson", "Engineering",
                                   "9000000001", "alice@company.com", "alice123"));
        employees.add(new Employee(2, "Bob Smith",    "Marketing",
                                   "9000000002", "bob@company.com",   "bob123"));
    }

    // ── Singleton accessor ────────────────────────────────────────────────────
    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    // ── Employee operations ───────────────────────────────────────────────────

    /** Returns the full employee list. */
    public ArrayList<Employee> getEmployees() { return employees; }

    /** Generates the next unique employee ID. */
    public int nextEmpId() { return nextEmpId++; }

    /** Adds a new employee to the store. */
    public void addEmployee(Employee e) {
        employees.add(e);
    }

    /**
     * Deletes an employee by ID.
     * @return true if found and removed, false otherwise.
     */
    public boolean deleteEmployee(int empId) {
        return employees.removeIf(e -> e.getEmpId() == empId);
    }

    /**
     * Finds an employee by numeric ID.
     * @return Employee object, or null if not found.
     */
    public Employee findById(int empId) {
        for (Employee e : employees) {
            if (e.getEmpId() == empId) return e;
        }
        return null;
    }

    /**
     * Validates employee login credentials.
     * @return matching Employee or null.
     */
    public Employee validateEmployee(String name, String password) {
        for (Employee e : employees) {
            if (e.getName().equalsIgnoreCase(name.trim()) &&
                e.getPassword().equals(password)) {
                return e;
            }
        }
        return null;
    }

    // ── Leave-request operations ──────────────────────────────────────────────

    /** Returns all leave requests. */
    public ArrayList<LeaveRequest> getLeaveRequests() { return leaveRequests; }

    /** Generates the next unique leave ID. */
    public int nextLeaveId() { return nextLeaveId++; }

    /** Adds a new leave request to the store. */
    public void addLeaveRequest(LeaveRequest lr) {
        leaveRequests.add(lr);
    }

    /**
     * Returns leave requests belonging to a specific employee.
     */
    public List<LeaveRequest> getLeavesByEmpId(int empId) {
        List<LeaveRequest> result = new ArrayList<>();
        for (LeaveRequest lr : leaveRequests) {
            if (lr.getEmpId() == empId) result.add(lr);
        }
        return result;
    }

    /**
     * Finds a leave request by its ID.
     * @return LeaveRequest or null.
     */
    public LeaveRequest findLeaveById(int leaveId) {
        for (LeaveRequest lr : leaveRequests) {
            if (lr.getLeaveId() == leaveId) return lr;
        }
        return null;
    }

    // ── Leave balance helpers (5 leaves per month policy) ──────────────────────

    /** Maximum leaves allowed per employee per month. */
    public static final int MAX_LEAVES_PER_MONTH = 5;

    /**
     * Returns the total number of leave DAYS used by an employee in the given
     * calendar month, counting only Approved and Pending requests.
     *
     * @param empId  the employee's ID
     * @param year   e.g. 2026
     * @param month  1-based month (1 = Jan, 12 = Dec)
     */
    public int getLeavesUsedInMonth(int empId, int year, int month) {
        int total = 0;
        for (LeaveRequest lr : leaveRequests) {
            if (lr.getEmpId() != empId) continue;
            if ("Rejected".equals(lr.getStatus())) continue;  // don't count rejected leaves
            try {
                LocalDate from = LocalDate.parse(lr.getFromDate());
                LocalDate to   = LocalDate.parse(lr.getToDate());
                // Walk each day in the leave range, count those in the target month
                LocalDate cursor = from;
                while (!cursor.isAfter(to)) {
                    if (cursor.getYear() == year && cursor.getMonthValue() == month) {
                        total++;
                    }
                    cursor = cursor.plusDays(1);
                }
            } catch (Exception ignored) { /* skip malformed dates */ }
        }
        return total;
    }

    /**
     * Returns the number of leaves REMAINING for an employee in the given month.
     * Minimum value is 0.
     */
    public int getLeavesLeftInMonth(int empId, int year, int month) {
        return Math.max(0, MAX_LEAVES_PER_MONTH - getLeavesUsedInMonth(empId, year, month));
    }
}
