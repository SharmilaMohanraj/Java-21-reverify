package com.enterprise.hr.report.service;

import com.enterprise.hr.domain.AttendanceRecord;
import com.enterprise.hr.domain.Employee;
import com.enterprise.hr.exception.BusinessRuleException;
import com.enterprise.hr.domain.LeaveRequest;
import com.enterprise.hr.domain.LeaveStatus;
import com.enterprise.hr.report.dto.AttendanceReportResponse;
import com.enterprise.hr.report.dto.LeaveBalanceReportResponse;
import com.enterprise.hr.report.repository.HrReportRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class HrReportService {

    private final HrReportRepository reportRepository;

    public HrReportService(HrReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * Dates are interpreted as UTC calendar dates.  The end bound is converted
     * to the beginning of the following day so an endDate is inclusive without
     * relying on database-specific date functions.
     */
    public List<AttendanceReportResponse> attendanceReport(LocalDate startDate, LocalDate endDate) {
        validateRange(startDate, endDate);
        Instant startAt = startDate == null ? null : startDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endAtExclusive = endDate == null ? null
                : endDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        return reportRepository.findAttendanceForReport(startAt, endAtExclusive).stream()
                .map(this::toAttendanceResponse)
                .toList();
    }

    public List<LeaveBalanceReportResponse> leaveBalanceReport() {
        Map<Long, Long> approvedDaysByEmployee = new HashMap<>();
        for (LeaveRequest request : reportRepository.findLeaveRequestsByStatusForReport(LeaveStatus.APPROVED)) {
            long inclusiveDays = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
            approvedDaysByEmployee.merge(request.getEmployee().getId(), inclusiveDays, Long::sum);
        }

        return reportRepository.findAllEmployeesForLeaveBalanceReport().stream()
                .map(employee -> toLeaveBalanceResponse(employee,
                        approvedDaysByEmployee.getOrDefault(employee.getId(), 0L)))
                .toList();
    }

    private void validateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BusinessRuleException(
                    "INVALID_ATTENDANCE_REPORT_RANGE",
                    "startDate must be on or before endDate",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private AttendanceReportResponse toAttendanceResponse(AttendanceRecord record) {
        Employee employee = record.getEmployee();
        return new AttendanceReportResponse(
                record.getId(), employee.getId(), employee.getEmployeeCode(), employee.getFullName(),
                record.getCheckInAt(), record.getCheckOutAt());
    }

    private LeaveBalanceReportResponse toLeaveBalanceResponse(Employee employee, long approvedDays) {
        long entitlement = employee.getAnnualLeaveEntitlementDays();
        return new LeaveBalanceReportResponse(
                employee.getId(), employee.getEmployeeCode(), employee.getFullName(), entitlement,
                approvedDays, entitlement - approvedDays);
    }
}
