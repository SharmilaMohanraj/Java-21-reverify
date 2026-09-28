package com.enterprise.hr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.enterprise.hr.exception.BusinessRuleException;
import com.enterprise.hr.exception.NotFoundException;
import com.enterprise.hr.repository.DepartmentRepository;
import com.enterprise.hr.repository.DesignationRepository;
import com.enterprise.hr.repository.EmployeeRepository;
import com.enterprise.hr.service.OrganizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceDeletionTest {
    @Mock private DepartmentRepository departments;
    @Mock private DesignationRepository designations;
    @Mock private EmployeeRepository employees;

    private OrganizationService service;

    @BeforeEach
    void setUp() {
        service = new OrganizationService(departments, designations, employees);
    }

    @Test
    void rejectsDeletingDepartmentWithEmployeeAssignments() {
        when(departments.existsById(1L)).thenReturn(true);
        when(employees.existsByDepartmentId(1L)).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> service.deleteDepartment(1L));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("DEPARTMENT_ASSIGNED", exception.getCode());
        assertTrue(exception.getMessage().contains("employee assignments"));
        verify(departments, never()).deleteById(1L);
    }

    @Test
    void rejectsDeletingDesignationWithEmployeeAssignments() {
        when(designations.existsById(2L)).thenReturn(true);
        when(employees.existsByDesignationId(2L)).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> service.deleteDesignation(2L));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("DESIGNATION_ASSIGNED", exception.getCode());
        assertTrue(exception.getMessage().contains("employee assignments"));
        verify(designations, never()).deleteById(2L);
    }

    @Test
    void deletesUnassignedOrganizationResources() {
        when(departments.existsById(1L)).thenReturn(true);
        when(designations.existsById(2L)).thenReturn(true);

        service.deleteDepartment(1L);
        service.deleteDesignation(2L);

        verify(departments).deleteById(1L);
        verify(designations).deleteById(2L);
    }

    @Test
    void retainsNotFoundBehaviorBeforeAssignmentChecks() {
        assertThrows(NotFoundException.class, () -> service.deleteDepartment(1L));
        assertThrows(NotFoundException.class, () -> service.deleteDesignation(2L));

        verify(employees, never()).existsByDepartmentId(1L);
        verify(employees, never()).existsByDesignationId(2L);
    }
}
