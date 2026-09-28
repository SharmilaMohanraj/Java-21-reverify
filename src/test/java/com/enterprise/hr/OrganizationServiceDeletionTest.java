package com.enterprise.hr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.enterprise.hr.domain.Department;
import com.enterprise.hr.domain.Designation;
import com.enterprise.hr.dto.OrganizationDtos.OrganizationRequest;
import com.enterprise.hr.exception.BadRequestException;
import com.enterprise.hr.exception.BusinessRuleException;
import com.enterprise.hr.exception.NotFoundException;
import com.enterprise.hr.repository.DepartmentRepository;
import com.enterprise.hr.repository.DesignationRepository;
import com.enterprise.hr.repository.EmployeeRepository;
import com.enterprise.hr.service.OrganizationService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceDeletionTest {
    @Mock
    private DepartmentRepository departments;

    @Mock
    private DesignationRepository designations;

    @Mock
    private EmployeeRepository employees;

    private OrganizationService service;

    @BeforeEach
    void setUp() {
        service = new OrganizationService(departments, designations, employees);
    }

    @Test
    void rejectsDeletingDepartmentWithEmployeeAssignments() {
        when(departments.existsById(1L)).thenReturn(true);
        when(employees.existsByDepartmentId(1L)).thenReturn(true);

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
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

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
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

    @Test
    void rejectsDepartmentUpdateWhenAnotherDepartmentHasTheRequestedName() {
        Department department = spy(new Department());
        OrganizationRequest request = new OrganizationRequest(" Engineering ", "Updated description");
        when(departments.findById(1L)).thenReturn(Optional.of(department));
        when(departments.existsByNameIgnoreCaseAndIdNot("Engineering", 1L)).thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> service.updateDepartment(1L, request));

        assertEquals("Department name already exists", exception.getMessage());
        verify(departments).existsByNameIgnoreCaseAndIdNot("Engineering", 1L);
        verify(department, never()).setName(anyString());
        verify(department, never()).setDescription("Updated description");
    }

    @Test
    void rejectsDesignationUpdateWhenAnotherDesignationHasTheRequestedName() {
        Designation designation = spy(new Designation());
        OrganizationRequest request = new OrganizationRequest(" Manager ", "Updated description");
        when(designations.findById(2L)).thenReturn(Optional.of(designation));
        when(designations.existsByNameIgnoreCaseAndIdNot("Manager", 2L)).thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> service.updateDesignation(2L, request));

        assertEquals("Designation name already exists", exception.getMessage());
        verify(designations).existsByNameIgnoreCaseAndIdNot("Manager", 2L);
        verify(designation, never()).setName(anyString());
        verify(designation, never()).setDescription("Updated description");
    }
}
