package com.enterprise.hr.report.controller;

import com.enterprise.hr.report.dto.AttendanceReportResponse;
import com.enterprise.hr.report.dto.LeaveBalanceReportResponse;
import com.enterprise.hr.report.service.HrReportService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('HR')")
public class HrReportController {

    private final HrReportService reportService;

    public HrReportController(HrReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/attendance")
    public List<AttendanceReportResponse> attendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return reportService.attendanceReport(startDate, endDate);
    }

    @GetMapping("/leave-balances")
    public List<LeaveBalanceReportResponse> leaveBalances() {
        return reportService.leaveBalanceReport();
    }
}
