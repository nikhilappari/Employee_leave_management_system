/**
 * Employee.java
 * Model class representing an Employee in the ELMS system.
 * Demonstrates OOP Encapsulation with private fields and public getters/setters.
 */
public class Employee {

    // ── Fields ────────────────────────────────────────────────────────────────
    private int    empId;
    private String name;
    private String department;
    private String phone;
    private String email;
    private String password;

    // ── Constructor ───────────────────────────────────────────────────────────
    public Employee(int empId, String name, String department,
                    String phone, String email, String password) {
        this.empId      = empId;
        this.name       = name;
        this.department = department;
        this.phone      = phone;
        this.email      = email;
        this.password   = password;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public int    getEmpId()      { return empId; }
    public String getName()       { return name; }
    public String getDepartment() { return department; }
    public String getPhone()      { return phone; }
    public String getEmail()      { return email; }
    public String getPassword()   { return password; }

    // ── Setters ───────────────────────────────────────────────────────────────
    public void setEmpId(int empId)           { this.empId      = empId; }
    public void setName(String name)          { this.name       = name; }
    public void setDepartment(String dept)    { this.department = dept; }
    public void setPhone(String phone)        { this.phone      = phone; }
    public void setEmail(String email)        { this.email      = email; }
    public void setPassword(String password)  { this.password   = password; }

    // ── toString ──────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Employee{" +
               "empId="      + empId      +
               ", name='"    + name       + '\'' +
               ", dept='"    + department + '\'' +
               ", phone='"   + phone      + '\'' +
               ", email='"   + email      + '\'' +
               '}';
    }
}
