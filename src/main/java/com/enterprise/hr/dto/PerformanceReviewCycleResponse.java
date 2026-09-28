package com.enterprise.hr.dto;

import java.time.LocalDate;

public record PerformanceReviewCycleResponse(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        String description
) {
}
