package com.enterprise.hr.controller;
import com.enterprise.hr.dto.EmployeeDtos.*;
import com.enterprise.hr.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/employees")
public class EmployeeController { private final EmployeeService service; public EmployeeController(EmployeeService s) { service=s; }
 @PostMapping @PreAuthorize("hasAuthority('ROLE_HR')") public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody CreateEmployeeRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request)); }
 @PutMapping("/{employeeId}/manager") @PreAuthorize("hasAuthority('ROLE_HR')") public EmployeeResponse manager(@PathVariable Long employeeId, @Valid @RequestBody ManagerRequest request) { return service.assignManager(employeeId,request.managerId()); }
}
