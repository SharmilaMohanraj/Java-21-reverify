package com.enterprise.hr.controller;

import com.enterprise.hr.dto.EmployeeDtos.CreateEmployeeRequest;
import com.enterprise.hr.dto.EmployeeDtos.EmployeeResponse;
import com.enterprise.hr.dto.EmployeeDtos.ManagerRequest;
import com.enterprise.hr.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<EmployeeResponse> create(
            @Valid @RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{employeeId}/manager")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public EmployeeResponse manager(
            @PathVariable Long employeeId,
            @Valid @RequestBody ManagerRequest request) {
        return service.assignManager(employeeId, request.managerId());
    }
}
