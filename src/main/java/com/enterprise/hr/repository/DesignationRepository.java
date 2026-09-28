package com.enterprise.hr.repository;
import com.enterprise.hr.domain.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DesignationRepository extends JpaRepository<Designation, Long> { boolean existsByNameIgnoreCase(String name); }
