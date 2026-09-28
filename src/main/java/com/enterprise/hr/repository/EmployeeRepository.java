package com.enterprise.hr.repository;

import com.enterprise.hr.domain.Employee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmailIgnoreCase(String email);
    Optional<Employee> findByEmployeeCode(String employeeCode);
    boolean existsByDepartmentId(Long departmentId);
    boolean existsByDesignationId(Long designationId);
}
