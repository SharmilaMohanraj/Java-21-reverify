package com.enterprise.hr.repository;

import com.enterprise.hr.domain.PerformanceReviewCycle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceReviewCycleRepository extends JpaRepository<PerformanceReviewCycle, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
