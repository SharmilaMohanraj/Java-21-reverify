package com.enterprise.hr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Raised when an attendance state transition violates a business invariant. */
@ResponseStatus(HttpStatus.CONFLICT)
public class AttendanceBusinessRuleException extends RuntimeException {

    public AttendanceBusinessRuleException(String message) {
        super(message);
    }
}
