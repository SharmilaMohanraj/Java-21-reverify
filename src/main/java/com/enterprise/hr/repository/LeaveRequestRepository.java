package com.enterprise.hr.repository;

import com.enterprise.hr.domain.LeaveRequest;
import com.enterprise.hr.domain.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployeeEmailOrderByRequestedAtDesc(String email);

    List<LeaveRequest> findByEmployeeManagerEmailAndStatusOrderByRequestedAtAsc(String managerEmail, LeaveStatus status);

    List<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);

    boolean existsByEmployeeIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long employeeId, LeaveStatus status, LocalDate endDate, LocalDate startDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from LeaveRequest l join fetch l.employee where l.id = :id")
    Optional<LeaveRequest> findForDecisionById(@Param("id") Long id);
}
