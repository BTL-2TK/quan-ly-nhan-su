package service;

import DAO.EmployeeDAO;
import model.Employee;

import java.util.List;

public class EmployeeService {
    private final EmployeeDAO employeeDAO= new EmployeeDAO();

    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }

    public Employee getEmployeeById(int id) {
        return employeeDAO.getEmployeeById(id);
    }

    public boolean addEmployee(Employee employee) {
        if (!isValidEmployee(employee)) {
            System.err.println("Employee data is invalid.");
            return false;
        }
        return employeeDAO.insertEmployee(employee);
    }

    public boolean updateEmployee(Employee employee) {
        if (employee == null || employee.getId() <= 0) {
            System.err.println("Employee ID is invalid.");
            return false;
        }
        if (!isValidEmployee(employee)) {
            System.err.println("Employee data is invalid.");
            return false;
        }
        return employeeDAO.updateEmployee(employee);
    }

    public boolean deleteEmployee(int id) {
        if (id <= 0) {
            System.err.println("Invalid employee ID.");
            return false;
        }
        return employeeDAO.deleteEmployee(id);
    }

    private boolean isValidEmployee(Employee employee) {
        if (employee == null) return false;
        if (employee.getEmployeeCode() == null || employee.getEmployeeCode().isBlank()) return false;
        if (employee.getFirstName() == null || employee.getFirstName().isBlank()) return false;
        if (employee.getLastName() == null || employee.getLastName().isBlank()) return false;

        return true;
    }
}
