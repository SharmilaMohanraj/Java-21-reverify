package com.enterprise.hr.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PayrollRecordRequest(
        @NotNull @Positive Long employeeId,
        @NotNull LocalDate periodStart,
        @NotNull LocalDate periodEnd,
        @NotNull @DecimalMin(value = "0.00") @Digits(integer = 17, fraction = 2) BigDecimal grossPay,
        @NotNull @DecimalMin(value = "0.00") @Digits(integer = 17, fraction = 2) BigDecimal deductions,
        @NotNull @DecimalMin(value = "0.00") @Digits(integer = 17, fraction = 2) BigDecimal netPay
) {
    @AssertTrue(message = "period end must not be before period start")
    public boolean isValidPeriod() {
        return periodStart == null || periodEnd == null || !periodEnd.isBefore(periodStart);
    }

    @AssertTrue(message = "net pay must equal gross pay minus deductions")
    public boolean isNetPayConsistent() {
        return grossPay == null || deductions == null || netPay == null
                || grossPay.subtract(deductions).compareTo(netPay) == 0;
    }
}
