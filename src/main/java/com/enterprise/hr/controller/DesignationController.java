package com.enterprise.hr.controller;

import com.enterprise.hr.dto.OrganizationDtos.OrganizationRequest;
import com.enterprise.hr.dto.OrganizationDtos.OrganizationResponse;
import com.enterprise.hr.service.OrganizationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/designations")
public class DesignationController {
    private final OrganizationService service;

    public DesignationController(OrganizationService service) {
        this.service = service;
    }

    @GetMapping
    public List<OrganizationResponse> all() {
        return service.designations();
    }

    @GetMapping("/{id}")
    public OrganizationResponse one(@PathVariable Long id) {
        return service.designation(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<OrganizationResponse> create(
            @Valid @RequestBody OrganizationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createDesignation(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public OrganizationResponse update(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationRequest request) {
        return service.updateDesignation(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteDesignation(id);
    }
}
