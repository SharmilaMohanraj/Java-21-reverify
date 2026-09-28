package com.enterprise.hr.dto;

import com.enterprise.hr.domain.LeaveStatus;

import java.time.Instant;
import java.time.LocalDate;

public record LeaveRequestResponse(
        Long id,
        Long employeeId,
        String employeeEmail,
        LocalDate startDate,
        LocalDate endDate,
        String reason,
        LeaveStatus status,
        Instant requestedAt,
        Long decidedById,
        Instant decidedAt,
        String decisionComment) {
}
