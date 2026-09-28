package com.enterprise.hr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PerformanceReviewCycleRequest(
        @NotBlank(message = "Cycle name is required")
        @Size(max = 150, message = "Cycle name must not exceed 150 characters")
        String name,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate,

        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description
) {
}
