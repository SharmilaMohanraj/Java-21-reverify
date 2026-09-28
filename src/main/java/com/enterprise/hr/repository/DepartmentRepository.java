package com.enterprise.hr.repository;
import com.enterprise.hr.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DepartmentRepository extends JpaRepository<Department, Long> { boolean existsByNameIgnoreCase(String name); }
