package com.enterprise.hr.service;

import com.enterprise.hr.domain.Department;
import com.enterprise.hr.domain.Designation;
import com.enterprise.hr.dto.OrganizationDtos.OrganizationRequest;
import com.enterprise.hr.dto.OrganizationDtos.OrganizationResponse;
import com.enterprise.hr.exception.BadRequestException;
import com.enterprise.hr.exception.BusinessRuleException;
import com.enterprise.hr.exception.NotFoundException;
import com.enterprise.hr.repository.DepartmentRepository;
import com.enterprise.hr.repository.DesignationRepository;
import com.enterprise.hr.repository.EmployeeRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrganizationService {
    private final DepartmentRepository departments;
    private final DesignationRepository designations;
    private final EmployeeRepository employees;

    public OrganizationService(
            DepartmentRepository departments,
            DesignationRepository designations,
            EmployeeRepository employees) {
        this.departments = departments;
        this.designations = designations;
        this.employees = employees;
    }

    public OrganizationResponse createDepartment(OrganizationRequest request) {
        if (departments.existsByNameIgnoreCase(request.name())) {
            throw new BadRequestException("Department name already exists");
        }

        Department department = new Department();
        set(department, request);
        return department(departments.save(department));
    }

    public List<OrganizationResponse> departments() {
        return departments.findAll().stream().map(this::department).toList();
    }

    public OrganizationResponse department(Long id) {
        return department(departments.findById(id)
                .orElseThrow(() -> new NotFoundException("Department not found")));
    }

    public OrganizationResponse updateDepartment(Long id, OrganizationRequest request) {
        Department department = departments.findById(id)
                .orElseThrow(() -> new NotFoundException("Department not found"));
        String name = normalizedName(request);
        if (departments.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new BadRequestException("Department name already exists");
        }

        set(department, request);
        return department(department);
    }

    public void deleteDepartment(Long id) {
        if (!departments.existsById(id)) {
            throw new NotFoundException("Department not found");
        }
        if (employees.existsByDepartmentId(id)) {
            throw new BusinessRuleException(
                    "DEPARTMENT_ASSIGNED",
                    "Department has employee assignments and cannot be deleted");
        }
        departments.deleteById(id);
    }

    public OrganizationResponse createDesignation(OrganizationRequest request) {
        if (designations.existsByNameIgnoreCase(request.name())) {
            throw new BadRequestException("Designation name already exists");
        }

        Designation designation = new Designation();
        set(designation, request);
        return designation(designations.save(designation));
    }

    public List<OrganizationResponse> designations() {
        return designations.findAll().stream().map(this::designation).toList();
    }

    public OrganizationResponse designation(Long id) {
        return designation(designations.findById(id)
                .orElseThrow(() -> new NotFoundException("Designation not found")));
    }

    public OrganizationResponse updateDesignation(Long id, OrganizationRequest request) {
        Designation designation = designations.findById(id)
                .orElseThrow(() -> new NotFoundException("Designation not found"));
        String name = normalizedName(request);
        if (designations.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new BadRequestException("Designation name already exists");
        }

        set(designation, request);
        return designation(designation);
    }

    public void deleteDesignation(Long id) {
        if (!designations.existsById(id)) {
            throw new NotFoundException("Designation not found");
        }
        if (employees.existsByDesignationId(id)) {
            throw new BusinessRuleException(
                    "DESIGNATION_ASSIGNED",
                    "Designation has employee assignments and cannot be deleted");
        }
        designations.deleteById(id);
    }

    private String normalizedName(OrganizationRequest request) {
        return request.name().trim();
    }

    private void set(Department department, OrganizationRequest request) {
        department.setName(normalizedName(request));
        department.setDescription(request.description());
    }

    private void set(Designation designation, OrganizationRequest request) {
        designation.setName(normalizedName(request));
        designation.setDescription(request.description());
    }

    private OrganizationResponse department(Department department) {
        return new OrganizationResponse(
                department.getId(), department.getName(), department.getDescription());
    }

    private OrganizationResponse designation(Designation designation) {
        return new OrganizationResponse(
                designation.getId(), designation.getName(), designation.getDescription());
    }
}
