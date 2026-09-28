package com.enterprise.hr.dto;

import com.enterprise.hr.domain.LeaveStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LeaveDecisionStatusValidator implements ConstraintValidator<ValidLeaveDecision, LeaveStatus> {

    @Override
    public boolean isValid(LeaveStatus status, ConstraintValidatorContext context) {
        return status == null || status == LeaveStatus.APPROVED || status == LeaveStatus.REJECTED;
    }
}
