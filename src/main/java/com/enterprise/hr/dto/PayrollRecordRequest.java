package com.enterprise.hr.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record PayrollRecordRequest(
        @NotNull @Positive Long employeeId,
        @NotNull LocalDate periodStart,
        @NotNull LocalDate periodEnd,
        @NotNull @DecimalMin(value = "0.00") @Digits(integer = 17, fraction = 2) BigDecimal grossAmount,
        @NotNull @DecimalMin(value = "0.00") @Digits(integer = 17, fraction = 2) BigDecimal deductionAmount,
        @NotNull @DecimalMin(value = "0.00") @Digits(integer = 17, fraction = 2) BigDecimal netAmount,
        Instant paidAt
) {
    @AssertTrue(message = "period end must not be before period start")
    public boolean isValidPeriod() {
        return periodStart == null || periodEnd == null || !periodEnd.isBefore(periodStart);
    }

    @AssertTrue(message = "net amount must equal gross amount minus deduction amount")
    public boolean isNetAmountConsistent() {
        return grossAmount == null || deductionAmount == null || netAmount == null
                || grossAmount.subtract(deductionAmount).compareTo(netAmount) == 0;
    }
}
