package com.enterprise.hr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.enterprise.hr.domain.LeaveStatus;
import com.enterprise.hr.domain.Role;
import java.util.List;
import org.junit.jupiter.api.Test;

class DomainEnumTest {
    @Test
    void exposesOnlyTheSpecifiedRoles() {
        assertEquals(List.of(Role.HR, Role.MANAGER, Role.EMPLOYEE), List.of(Role.values()));
    }

    @Test
    void exposesOnlyTheSpecifiedLeaveStatuses() {
        assertEquals(List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED, LeaveStatus.REJECTED),
                List.of(LeaveStatus.values()));
    }
}
