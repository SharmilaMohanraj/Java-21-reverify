package com.enterprise.hr.repository;

import com.enterprise.hr.domain.AttendanceRecord;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    Optional<AttendanceRecord> findFirstByEmployeeIdAndCheckOutAtIsNullOrderByCheckInAtDesc(Long employeeId);

    List<AttendanceRecord> findByEmployeeIdOrderByCheckInAtDesc(Long employeeId);
}
