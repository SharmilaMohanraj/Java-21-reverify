package com.enterprise.hr.dto;

import jakarta.validation.constraints.*;

public final class OrganizationDtos {
    private OrganizationDtos() { }
    public record OrganizationRequest(@NotBlank @Size(max=120) String name, @Size(max=500) String description) { }
    public record OrganizationResponse(Long id, String name, String description) { }
}
