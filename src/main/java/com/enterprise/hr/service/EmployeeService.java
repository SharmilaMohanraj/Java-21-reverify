package com.enterprise.hr.service;

import com.enterprise.hr.domain.Department;
import com.enterprise.hr.domain.Designation;
import com.enterprise.hr.domain.Employee;
import com.enterprise.hr.dto.EmployeeDtos.CreateEmployeeRequest;
import com.enterprise.hr.dto.EmployeeDtos.EmployeeResponse;
import com.enterprise.hr.exception.BadRequestException;
import com.enterprise.hr.exception.BusinessRuleException;
import com.enterprise.hr.exception.NotFoundException;
import com.enterprise.hr.repository.DepartmentRepository;
import com.enterprise.hr.repository.DesignationRepository;
import com.enterprise.hr.repository.EmployeeRepository;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmployeeService {
    private final EmployeeRepository employees;
    private final DepartmentRepository departments;
    private final DesignationRepository designations;
    private final PasswordEncoder encoder;

    public EmployeeService(EmployeeRepository employees, DepartmentRepository departments,
                           DesignationRepository designations, PasswordEncoder encoder) {
        this.employees = employees;
        this.departments = departments;
        this.designations = designations;
        this.encoder = encoder;
    }

    public EmployeeResponse create(CreateEmployeeRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (employees.findByEmailIgnoreCase(email).isPresent() || employees.findByEmployeeCode(request.employeeCode().trim()).isPresent()) {
            throw new BusinessRuleException("DUPLICATE_EMPLOYEE", "Employee email or employee code already exists");
        }
        Employee employee = new Employee();
        employee.setEmployeeCode(request.employeeCode().trim());
        employee.setFullName(request.fullName().trim());
        employee.setEmail(email);
        employee.setPasswordHash(encoder.encode(request.password()));
        employee.setRole(request.role());
        employee.setAnnualLeaveEntitlementDays(request.annualLeaveEntitlementDays());
        if (request.departmentId() != null) employee.setDepartment(department(request.departmentId()));
        if (request.designationId() != null) employee.setDesignation(designation(request.designationId()));
        return response(employees.save(employee));
    }

    public EmployeeResponse assignManager(Long employeeId, Long managerId) {
        Employee employee = get(employeeId);
        employee.setManager(managerId == null ? null : manager(managerId, employeeId));
        return response(employee);
    }

    public Employee getByEmail(String email) {
        return employees.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("Employee not found"));
    }

    private Employee get(Long id) {
        return employees.findById(id).orElseThrow(() -> new NotFoundException("Employee not found"));
    }

    private Department department(Long id) { return departments.findById(id).orElseThrow(() -> new NotFoundException("Department not found")); }
    private Designation designation(Long id) { return designations.findById(id).orElseThrow(() -> new NotFoundException("Designation not found")); }

    private Employee manager(Long id, Long employeeId) {
        if (id.equals(employeeId)) throw new BadRequestException("An employee cannot manage themselves");
        return get(id);
    }

    public EmployeeResponse response(Employee employee) {
        return new EmployeeResponse(employee.getId(), employee.getEmployeeCode(), employee.getFullName(), employee.getEmail(),
                employee.getRole(), employee.getAnnualLeaveEntitlementDays(),
                employee.getDepartment() == null ? null : employee.getDepartment().getId(),
                employee.getDesignation() == null ? null : employee.getDesignation().getId(),
                employee.getManager() == null ? null : employee.getManager().getId());
    }
}
