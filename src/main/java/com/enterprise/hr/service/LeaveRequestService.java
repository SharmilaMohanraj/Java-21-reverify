package com.enterprise.hr.service;

import com.enterprise.hr.domain.Employee;
import com.enterprise.hr.domain.LeaveRequest;
import com.enterprise.hr.domain.LeaveStatus;
import com.enterprise.hr.dto.CreateLeaveRequest;
import com.enterprise.hr.dto.LeaveDecisionRequest;
import com.enterprise.hr.dto.LeaveRequestResponse;
import com.enterprise.hr.exception.BusinessRuleException;
import com.enterprise.hr.repository.LeaveEmployeeLookup;
import com.enterprise.hr.repository.LeaveRequestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LeaveRequestService {
    private final LeaveRequestRepository leaveRequests;
    private final LeaveEmployeeLookup employees;

    public LeaveRequestService(LeaveRequestRepository leaveRequests, LeaveEmployeeLookup employees) {
        this.leaveRequests = leaveRequests;
        this.employees = employees;
    }

    @Transactional
    public LeaveRequestResponse create(String callerEmail, CreateLeaveRequest command) {
        validateRange(command.startDate(), command.endDate());
        Employee employee = employeeFor(callerEmail);
        LeaveRequest request = new LeaveRequest(employee, command.startDate(), command.endDate(), command.reason().trim());
        return toResponse(leaveRequests.save(request));
    }

    public List<LeaveRequestResponse> mine(String callerEmail) {
        // Resolving the caller first prevents an unknown principal from receiving an empty success response.
        employeeFor(callerEmail);
        return leaveRequests.findByEmployeeEmailOrderByRequestedAtDesc(callerEmail).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<LeaveRequestResponse> pendingSubordinates(String callerEmail) {
        employeeFor(callerEmail);
        return leaveRequests.findByEmployeeManagerEmailAndStatusOrderByRequestedAtAsc(callerEmail, LeaveStatus.PENDING)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public LeaveRequestResponse decide(String callerEmail, Long requestId, LeaveDecisionRequest command) {
        if (command.status() != LeaveStatus.APPROVED && command.status() != LeaveStatus.REJECTED) {
            throw new BusinessRuleException("INVALID_LEAVE_DECISION", "A leave request can only be approved or rejected");
        }

        Employee manager = employeeFor(callerEmail);
        LeaveRequest request = leaveRequests.findForDecisionById(requestId)
                .orElseThrow(() -> new BusinessRuleException("LEAVE_REQUEST_NOT_FOUND", "Leave request was not found", HttpStatus.NOT_FOUND));

        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new BusinessRuleException("LEAVE_REQUEST_ALREADY_DECIDED", "Only pending leave requests can be decided");
        }
        Employee requestManager = request.getEmployee().getManager();
        if (requestManager == null || !callerEmail.equalsIgnoreCase(requestManager.getEmail())) {
            throw new BusinessRuleException("NOT_DIRECT_MANAGER", "Only the employee's direct manager may decide this leave request", HttpStatus.FORBIDDEN);
        }

        if (command.status() == LeaveStatus.APPROVED) {
            ensureApprovalAllowed(request);
        }
        request.decide(command.status(), manager, normalize(command.comment()));
        return toResponse(request);
    }

    private void ensureApprovalAllowed(LeaveRequest request) {
        Long employeeId = request.getEmployee().getId();
        if (leaveRequests.existsByEmployeeIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                employeeId, LeaveStatus.APPROVED, request.getEndDate(), request.getStartDate())) {
            throw new BusinessRuleException("OVERLAPPING_APPROVED_LEAVE", "The requested dates overlap approved leave");
        }

        long alreadyApprovedDays = leaveRequests.findByEmployeeIdAndStatus(employeeId, LeaveStatus.APPROVED).stream()
                .mapToLong(approved -> inclusiveDays(approved.getStartDate(), approved.getEndDate()))
                .sum();
        long requestedDays = inclusiveDays(request.getStartDate(), request.getEndDate());
        int entitlement = request.getEmployee().getAnnualLeaveEntitlementDays();
        if (alreadyApprovedDays + requestedDays > entitlement) {
            throw new BusinessRuleException("ANNUAL_LEAVE_ENTITLEMENT_EXCEEDED",
                    "Approval would exceed the employee's annual leave entitlement");
        }
    }

    private Employee employeeFor(String email) {
        if (email == null || email.isBlank()) {
            throw new BusinessRuleException("UNAUTHENTICATED_CALLER", "An authenticated caller email is required", HttpStatus.UNAUTHORIZED);
        }
        return employees.findByEmail(email)
                .orElseThrow(() -> new BusinessRuleException("EMPLOYEE_NOT_FOUND", "No employee exists for the authenticated email", HttpStatus.NOT_FOUND));
    }

    private void validateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new BusinessRuleException("INVALID_LEAVE_DATE_RANGE", "endDate must be on or after startDate");
        }
    }

    private long inclusiveDays(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private LeaveRequestResponse toResponse(LeaveRequest request) {
        Employee employee = request.getEmployee();
        Employee decidedBy = request.getDecidedBy();
        return new LeaveRequestResponse(request.getId(), employee.getId(), employee.getEmail(),
                request.getStartDate(), request.getEndDate(), request.getReason(), request.getStatus(),
                request.getRequestedAt(), decidedBy == null ? null : decidedBy.getId(), request.getDecidedAt(),
                request.getDecisionComment());
    }
}
