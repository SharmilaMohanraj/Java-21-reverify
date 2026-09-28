package com.enterprise.hr.dto;

import com.enterprise.hr.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class EmployeeDtos {
    private EmployeeDtos() { }

    public record CreateEmployeeRequest(
            @NotBlank @Size(max = 64) String employeeCode,
            @NotBlank @Size(max = 160) String fullName,
            @NotBlank @Email @Size(max = 180) String email,
            @NotBlank @Size(min = 8, max = 100) String password,
            @NotNull Role role,
            @NotNull @Min(0) Integer annualLeaveEntitlementDays,
            Long departmentId,
            Long designationId) { }

    public record ManagerRequest(Long managerId) { }

    public record EmployeeResponse(
            Long id,
            String employeeCode,
            String fullName,
            String email,
            Role role,
            Integer annualLeaveEntitlementDays,
            Long departmentId,
            Long designationId,
            Long managerId) { }
}
