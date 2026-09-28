package com.enterprise.hr.report.dto;

/** Leave entitlement and usage totals for one employee. */
public record LeaveBalanceReportResponse(
        Long employeeId,
        String employeeCode,
        String employeeName,
        long annualLeaveEntitlementDays,
        long approvedLeaveDays,
        long remainingLeaveDays
) {
}
