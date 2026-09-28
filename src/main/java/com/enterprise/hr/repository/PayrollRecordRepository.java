package com.enterprise.hr.repository;

import com.enterprise.hr.domain.PayrollRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollRecordRepository extends JpaRepository<PayrollRecord, Long> {
}
