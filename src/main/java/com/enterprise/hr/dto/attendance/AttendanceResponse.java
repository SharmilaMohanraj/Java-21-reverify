package com.enterprise.hr.dto.attendance;

import java.time.Instant;

public record AttendanceResponse(
        Long id,
        Long employeeId,
        Instant checkInAt,
        Instant checkOutAt
) {
}
