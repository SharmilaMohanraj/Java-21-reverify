package com.enterprise.hr.dto;

import com.enterprise.hr.domain.Role;
import jakarta.validation.constraints.*;

public final class AuthDtos {
    private AuthDtos() { }
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) { }
    public record LoginResponse(String token, String tokenType, Long employeeId, String email, Role role) { }
}
