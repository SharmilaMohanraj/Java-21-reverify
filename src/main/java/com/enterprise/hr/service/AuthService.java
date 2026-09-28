package com.enterprise.hr.service;
import com.enterprise.hr.domain.Employee;
import com.enterprise.hr.dto.AuthDtos.*;
import com.enterprise.hr.exception.NotFoundException;
import com.enterprise.hr.exception.UnauthorizedException;
import com.enterprise.hr.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class AuthService {
 private final EmployeeService employees; private final PasswordEncoder encoder; private final JwtService jwt;
 public AuthService(EmployeeService e, PasswordEncoder p, JwtService j) { employees=e; encoder=p; jwt=j; }
 public LoginResponse login(LoginRequest request) { Employee e; try { e=employees.getByEmail(request.email()); } catch(NotFoundException ex) { throw new UnauthorizedException("Invalid email or password"); } if(!encoder.matches(request.password(),e.getPasswordHash())) throw new UnauthorizedException("Invalid email or password"); return new LoginResponse(jwt.issue(e),"Bearer",e.getId(),e.getEmail(),e.getRole()); }
}
