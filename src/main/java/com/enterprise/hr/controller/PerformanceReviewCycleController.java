package com.enterprise.hr.controller;

import com.enterprise.hr.dto.PerformanceReviewCycleRequest;
import com.enterprise.hr.dto.PerformanceReviewCycleResponse;
import com.enterprise.hr.service.PerformanceReviewCycleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

import java.util.List;

@RestController
@RequestMapping("/api/performance-review-cycles")
public class PerformanceReviewCycleController {

    private final PerformanceReviewCycleService performanceReviewCycleService;

    public PerformanceReviewCycleController(PerformanceReviewCycleService performanceReviewCycleService) {
        this.performanceReviewCycleService = performanceReviewCycleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('HR')")
    public PerformanceReviewCycleResponse create(@Valid @RequestBody PerformanceReviewCycleRequest request) {
        return performanceReviewCycleService.create(request);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<PerformanceReviewCycleResponse> findAll() {
        return performanceReviewCycleService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public PerformanceReviewCycleResponse findById(@PathVariable Long id) {
        return performanceReviewCycleService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public PerformanceReviewCycleResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PerformanceReviewCycleRequest request) {
        return performanceReviewCycleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('HR')")
    public void delete(@PathVariable Long id) {
        performanceReviewCycleService.delete(id);
    }
}
