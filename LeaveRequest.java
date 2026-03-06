import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * LeaveRequest.java
 * Model class representing a leave application submitted by an employee.
 */
public class LeaveRequest {

    // ── Fields ────────────────────────────────────────────────────────────────
    private int    leaveId;
    private int    empId;
    private String fromDate;
    private String toDate;
    private String status;   // "Pending" | "Approved" | "Rejected"

    // ── Constructor ───────────────────────────────────────────────────────────
    public LeaveRequest(int leaveId, int empId,
                        String fromDate, String toDate, String status) {
        this.leaveId  = leaveId;
        this.empId    = empId;
        this.fromDate = fromDate;
        this.toDate   = toDate;
        this.status   = status;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public int    getLeaveId()  { return leaveId; }
    public int    getEmpId()    { return empId; }
    public String getFromDate() { return fromDate; }
    public String getToDate()   { return toDate; }
    public String getStatus()   { return status; }

    /**
     * Returns the inclusive number of calendar days covered by this leave request.
     * Dates must be stored in YYYY-MM-DD format.
     */
    public int getDaysApplied() {
        try {
            LocalDate from = LocalDate.parse(fromDate);
            LocalDate to   = LocalDate.parse(toDate);
            return (int) ChronoUnit.DAYS.between(from, to) + 1;  // inclusive
        } catch (Exception e) {
            return 1;  // fallback if date format unexpected
        }
    }

    // ── Setters ───────────────────────────────────────────────────────────────
    public void setLeaveId(int leaveId)     { this.leaveId  = leaveId; }
    public void setEmpId(int empId)         { this.empId    = empId; }
    public void setFromDate(String from)    { this.fromDate = from; }
    public void setToDate(String to)        { this.toDate   = to; }
    public void setStatus(String status)    { this.status   = status; }

    // ── toString ──────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "LeaveRequest{" +
               "leaveId=" + leaveId +
               ", empId=" + empId   +
               ", from='" + fromDate + '\'' +
               ", to='"   + toDate   + '\'' +
               ", status='"+ status  + '\'' +
               '}';
    }
}
