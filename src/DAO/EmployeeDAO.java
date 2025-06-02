package DAO;

import model.Employee;
import utils.DatabaseUtils;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM Employees";

        try (Connection conn = DatabaseUtils.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Employee emp = mapResultSetToEmployee(rs);
                list.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Employee getEmployeeById(int id) {
        Employee emp = null;
        String sql = "SELECT * FROM Employees WHERE Id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    emp = mapResultSetToEmployee(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emp;
    }

    public boolean insertEmployee(Employee employee) {
        // Kiểm tra mã nhân viên đã tồn tại chưa
        if (isEmployeeCodeExists(employee.getEmployeeCode())) {
            System.err.println("EmployeeCode đã tồn tại: " + employee.getEmployeeCode());
            return false;
        }
        String sql = "INSERT INTO Employees(EmployeeCode, FirstName, LastName, Email, Phone, Address, BirthDate, Gender, IdNumber, DepartmentId, PositionId, HireDate, Status, AvatarPath) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setEmployeeParams(ps, employee, false);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE Employees SET EmployeeCode=?, FirstName=?, LastName=?, Email=?, Phone=?, Address=?, BirthDate=?, Gender=?, IdNumber=?, DepartmentId=?, PositionId=?, HireDate=?, Status=?, AvatarPath=? WHERE Id=?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setEmployeeParams(ps, employee, true);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEmployee(int id) {
        String sql = "DELETE FROM Employees WHERE Id=?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isEmployeeCodeExists(String employeeCode) {
        String sql = "SELECT COUNT(*) FROM Employees WHERE EmployeeCode = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setId(rs.getInt("Id"));
        emp.setEmployeeCode(rs.getString("EmployeeCode"));
        emp.setFirstName(rs.getString("FirstName"));
        emp.setLastName(rs.getString("LastName"));
        emp.setEmail(rs.getString("Email"));
        emp.setPhone(rs.getString("Phone"));
        emp.setAddress(rs.getString("Address"));

        Date birthDate = rs.getDate("BirthDate");
        if (birthDate != null) {
            emp.setBirthDate(birthDate.toLocalDate());
        }

        emp.setGender(rs.getString("Gender"));
        emp.setIdNumber(rs.getString("IdNumber"));

        int deptId = rs.getInt("DepartmentId");
        if (!rs.wasNull()) emp.setDepartmentId(deptId);

        int posId = rs.getInt("PositionId");
        if (!rs.wasNull()) emp.setPositionId(posId);

        Date hireDate = rs.getDate("HireDate");
        if (hireDate != null) {
            emp.setHireDate(hireDate.toLocalDate());
        }

        emp.setStatus(rs.getString("Status"));
        emp.setAvatarPath(rs.getString("AvatarPath"));

        return emp;
    }

    private void setEmployeeParams(PreparedStatement ps, Employee emp, boolean includeId) throws SQLException {
        ps.setString(1, emp.getEmployeeCode());
        ps.setString(2, emp.getFirstName());
        ps.setString(3, emp.getLastName());
        ps.setString(4, emp.getEmail());
        ps.setString(5, emp.getPhone());
        ps.setString(6, emp.getAddress());

        if (emp.getBirthDate() != null) {
            ps.setDate(7, Date.valueOf(emp.getBirthDate()));
        } else {
            ps.setNull(7, Types.DATE);
        }

        ps.setString(8, emp.getGender());
        ps.setString(9, emp.getIdNumber());

        if (emp.getDepartmentId() != null) {
            ps.setInt(10, emp.getDepartmentId());
        } else {
            ps.setNull(10, Types.INTEGER);
        }

        if (emp.getPositionId() != null) {
            ps.setInt(11, emp.getPositionId());
        } else {
            ps.setNull(11, Types.INTEGER);
        }

        if (emp.getHireDate() != null) {
            ps.setDate(12, Date.valueOf(emp.getHireDate()));
        } else {
            ps.setNull(12, Types.DATE);
        }

        ps.setString(13, emp.getStatus());
        ps.setString(14, emp.getAvatarPath());

        if (includeId) {
            ps.setInt(15, emp.getId());
        }
    }
}
