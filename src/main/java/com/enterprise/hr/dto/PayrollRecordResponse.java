package com.enterprise.hr.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record PayrollRecordResponse(
        Long id,
        Long employeeId,
        LocalDate periodStart,
        LocalDate periodEnd,
        BigDecimal grossAmount,
        BigDecimal deductionAmount,
        BigDecimal netAmount,
        Instant paidAt
) {
}
