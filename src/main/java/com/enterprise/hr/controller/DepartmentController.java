package com.enterprise.hr.controller;
import com.enterprise.hr.dto.OrganizationDtos.*; import com.enterprise.hr.service.OrganizationService; import jakarta.validation.Valid; import java.util.List; import org.springframework.http.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/departments")
public class DepartmentController { private final OrganizationService service; public DepartmentController(OrganizationService s){service=s;}
 @GetMapping public List<OrganizationResponse> all(){return service.departments();} @GetMapping("/{id}") public OrganizationResponse one(@PathVariable Long id){return service.department(id);}
 @PostMapping @PreAuthorize("hasAuthority('ROLE_HR')") public ResponseEntity<OrganizationResponse> create(@Valid @RequestBody OrganizationRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.createDepartment(r));}
 @PutMapping("/{id}") @PreAuthorize("hasAuthority('ROLE_HR')") public OrganizationResponse update(@PathVariable Long id,@Valid @RequestBody OrganizationRequest r){return service.updateDepartment(id,r);}
 @DeleteMapping("/{id}") @PreAuthorize("hasAuthority('ROLE_HR')") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.deleteDepartment(id);}
}
