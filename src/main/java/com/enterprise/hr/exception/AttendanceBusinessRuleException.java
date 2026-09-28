package com.enterprise.hr.exception;

/** Raised when an attendance state transition violates a business invariant. */
public class AttendanceBusinessRuleException extends BusinessRuleException {

    public AttendanceBusinessRuleException(String message) {
        super("ATTENDANCE_BUSINESS_RULE_VIOLATION", message);
    }
}
