package com.enterprise.hr.report.dto;

import java.time.Instant;

/** A single attendance interval in the HR attendance report. */
public record AttendanceReportResponse(
        Long attendanceRecordId,
        Long employeeId,
        String employeeCode,
        String employeeName,
        Instant checkInAt,
        Instant checkOutAt
) {
}
