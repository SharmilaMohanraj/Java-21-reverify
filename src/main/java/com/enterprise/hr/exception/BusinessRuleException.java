package com.enterprise.hr.exception;

import org.springframework.http.HttpStatus;

/** A client-visible conflict with a domain workflow rule. */
public class BusinessRuleException extends RuntimeException {
    private final String code;
    private final HttpStatus status;

    public BusinessRuleException(String message) {
        this("BUSINESS_RULE_VIOLATION", message, HttpStatus.CONFLICT);
    }

    public BusinessRuleException(String code, String message) {
        this(code, message, HttpStatus.CONFLICT);
    }

    public BusinessRuleException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String getCode() { return code; }
    public HttpStatus getStatus() { return status; }
}
