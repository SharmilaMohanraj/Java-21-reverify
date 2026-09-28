package com.enterprise.hr.security;

import com.enterprise.hr.domain.Employee;
import com.enterprise.hr.repository.EmployeeRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final EmployeeRepository employeeRepository;

    public JwtAuthenticationFilter(JwtService jwt, EmployeeRepository employeeRepository) {
        this.jwt = jwt;
        this.employeeRepository = employeeRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            try {
                String subject = jwt.subject(header.substring(7));
                if (subject == null || subject.isBlank()) {
                    SecurityContextHolder.clearContext();
                } else {
                    employeeRepository.findByEmailIgnoreCase(subject)
                            .filter(employee -> employee.getRole() != null)
                            .ifPresentOrElse(this::authenticate, SecurityContextHolder::clearContext);
                }
            } catch (JwtException | IllegalArgumentException ignored) {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }

    private void authenticate(Employee employee) {
        String authority = "ROLE_" + employee.getRole().name();
        var authentication = new UsernamePasswordAuthenticationToken(
                employee.getEmail(), null, List.of(new SimpleGrantedAuthority(authority)));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
