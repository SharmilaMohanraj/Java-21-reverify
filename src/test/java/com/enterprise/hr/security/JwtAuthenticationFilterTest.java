package com.enterprise.hr.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.enterprise.hr.domain.Employee;
import com.enterprise.hr.domain.Role;
import com.enterprise.hr.repository.EmployeeRepository;
import io.jsonwebtoken.JwtException;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.security.core.context.SecurityContextHolder;

class JwtAuthenticationFilterTest {
    private final JwtService jwtService = mock(JwtService.class);
    private final EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, employeeRepository);

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticatesWithTheCurrentEmployeeRoleInsteadOfTheTokenRole() throws Exception {
        Employee employee = new Employee();
        employee.setEmail("employee@example.com");
        employee.setRole(Role.MANAGER);
        when(jwtService.subject("token")).thenReturn("EMPLOYEE@example.com");
        when(employeeRepository.findByEmailIgnoreCase("EMPLOYEE@example.com")).thenReturn(Optional.of(employee));

        filter.doFilter(bearerRequest("token"), new MockHttpServletResponse(), new MockFilterChain());

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertEquals("employee@example.com", authentication.getPrincipal());
        assertEquals("ROLE_MANAGER", authentication.getAuthorities().iterator().next().getAuthority());
        verify(jwtService, never()).role(anyString());
    }

    @Test
    void clearsAuthenticationWhenTheTokenSubjectDoesNotIdentifyAnEmployee() throws Exception {
        when(jwtService.subject("token")).thenReturn("missing@example.com");
        when(employeeRepository.findByEmailIgnoreCase("missing@example.com")).thenReturn(Optional.empty());

        filter.doFilter(bearerRequest("token"), new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void clearsAuthenticationForAnInvalidToken() throws Exception {
        when(jwtService.subject("invalid")).thenThrow(new JwtException("Invalid token"));

        filter.doFilter(bearerRequest("invalid"), new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private MockHttpServletRequest bearerRequest(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        return request;
    }
}
