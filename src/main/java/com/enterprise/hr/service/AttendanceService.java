package com.enterprise.hr.service;

import com.enterprise.hr.domain.AttendanceRecord;
import com.enterprise.hr.domain.Employee;
import com.enterprise.hr.dto.attendance.AttendanceResponse;
import com.enterprise.hr.exception.AttendanceBusinessRuleException;
import com.enterprise.hr.repository.AttendanceRecordRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EntityManager entityManager;

    public AttendanceService(AttendanceRecordRepository attendanceRecordRepository, EntityManager entityManager) {
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.entityManager = entityManager;
    }

    /** Creates an open attendance interval for the authenticated employee. */
    @Transactional
    public AttendanceResponse checkIn() {
        Employee employee = lockCurrentEmployee();
        attendanceRecordRepository.findFirstByEmployeeIdAndCheckOutAtIsNullOrderByCheckInAtDesc(employee.getId())
                .ifPresent(record -> {
                    throw new AttendanceBusinessRuleException("An open attendance record already exists");
                });

        AttendanceRecord record = attendanceRecordRepository.save(new AttendanceRecord(employee, Instant.now()));
        return toResponse(record);
    }

    /** Closes the authenticated employee's only open attendance interval. */
    @Transactional
    public AttendanceResponse checkOut() {
        Employee employee = lockCurrentEmployee();
        AttendanceRecord record = attendanceRecordRepository
                .findFirstByEmployeeIdAndCheckOutAtIsNullOrderByCheckInAtDesc(employee.getId())
                .orElseThrow(() -> new AttendanceBusinessRuleException("No open attendance record exists"));

        record.checkOut(Instant.now());
        return toResponse(record);
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getMyAttendance() {
        Employee employee = findCurrentEmployee();
        return attendanceRecordRepository.findByEmployeeIdOrderByCheckInAtDesc(employee.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    /*
     * Locking the employee row serializes check-in/check-out transitions for one
     * caller, including the otherwise racy case where no open record exists yet.
     */
    private Employee lockCurrentEmployee() {
        String email = currentCallerEmail();
        return entityManager.createQuery("select e from Employee e where e.email = :email", Employee.class)
                .setParameter("email", email)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> employeeNotFound(email));
    }

    private Employee findCurrentEmployee() {
        String email = currentCallerEmail();
        return entityManager.createQuery("select e from Employee e where e.email = :email", Employee.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> employeeNotFound(email));
    }

    private String currentCallerEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
                || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }
        return authentication.getName();
    }

    private ResponseStatusException employeeNotFound(String email) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "No employee exists for authenticated email: " + email);
    }

    private AttendanceResponse toResponse(AttendanceRecord record) {
        return new AttendanceResponse(
                record.getId(),
                record.getEmployee().getId(),
                record.getCheckInAt(),
                record.getCheckOutAt()
        );
    }
}
