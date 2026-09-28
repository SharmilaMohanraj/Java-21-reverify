package com.enterprise.hr.controller;
import com.enterprise.hr.dto.OrganizationDtos.*; import com.enterprise.hr.service.OrganizationService; import jakarta.validation.Valid; import java.util.List; import org.springframework.http.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/designations")
public class DesignationController { private final OrganizationService service; public DesignationController(OrganizationService s){service=s;}
 @GetMapping public List<OrganizationResponse> all(){return service.designations();} @GetMapping("/{id}") public OrganizationResponse one(@PathVariable Long id){return service.designation(id);}
 @PostMapping @PreAuthorize("hasAuthority('ROLE_HR')") public ResponseEntity<OrganizationResponse> create(@Valid @RequestBody OrganizationRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.createDesignation(r));}
 @PutMapping("/{id}") @PreAuthorize("hasAuthority('ROLE_HR')") public OrganizationResponse update(@PathVariable Long id,@Valid @RequestBody OrganizationRequest r){return service.updateDesignation(id,r);}
 @DeleteMapping("/{id}") @PreAuthorize("hasAuthority('ROLE_HR')") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.deleteDesignation(id);}
}
