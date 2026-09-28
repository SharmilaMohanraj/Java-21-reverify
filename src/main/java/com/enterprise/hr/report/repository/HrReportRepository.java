package com.enterprise.hr.report.repository;

import com.enterprise.hr.domain.AttendanceRecord;
import com.enterprise.hr.domain.Employee;
import com.enterprise.hr.domain.LeaveRequest;
import com.enterprise.hr.domain.LeaveStatus;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/**
 * Read-only queries owned by the reporting feature.  Keeping these queries here
 * prevents reporting concerns from expanding the operational repositories.
 */
@org.springframework.stereotype.Repository
public interface HrReportRepository extends Repository<AttendanceRecord, Long> {

    @Query("""
            select a from AttendanceRecord a
            join fetch a.employee e
            where (:startAt is null or a.checkInAt >= :startAt)
              and (:endAtExclusive is null or a.checkInAt < :endAtExclusive)
            order by a.checkInAt desc, a.id desc
            """)
    List<AttendanceRecord> findAttendanceForReport(
            @Param("startAt") Instant startAt,
            @Param("endAtExclusive") Instant endAtExclusive);

    @Query("""
            select e from Employee e
            order by e.id asc
            """)
    List<Employee> findAllEmployeesForLeaveBalanceReport();

    @Query("""
            select l from LeaveRequest l
            join fetch l.employee e
            where l.status = :status
            """)
    List<LeaveRequest> findLeaveRequestsByStatusForReport(@Param("status") LeaveStatus status);
}
