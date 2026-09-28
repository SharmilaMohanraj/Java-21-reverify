package com.enterprise.hr.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class LeaveWorkflowExceptionHandler {
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Map<String, Object>> businessRule(BusinessRuleException exception,
                                                             HttpServletRequest request) {
        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "status", exception.getStatus().value(),
                "error", exception.getStatus().getReasonPhrase(),
                "code", exception.getCode(),
                "message", exception.getMessage(),
                "path", request.getRequestURI());
        return ResponseEntity.status(exception.getStatus()).body(body);
    }
}
