package com.enterprise.hr.dto;

import com.enterprise.hr.domain.LeaveStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LeaveDecisionRequest(
        @NotNull LeaveStatus status,
        @Size(max = 1000) String comment) {
}
