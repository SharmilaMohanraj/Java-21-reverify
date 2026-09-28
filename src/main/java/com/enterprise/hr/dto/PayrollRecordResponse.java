package com.enterprise.hr.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PayrollRecordResponse(
        Long id,
        Long employeeId,
        LocalDate periodStart,
        LocalDate periodEnd,
        BigDecimal grossPay,
        BigDecimal deductions,
        BigDecimal netPay
) {
}
