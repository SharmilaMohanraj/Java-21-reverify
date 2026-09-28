package com.enterprise.hr.service;

import com.enterprise.hr.domain.PerformanceReviewCycle;
import com.enterprise.hr.dto.PerformanceReviewCycleRequest;
import com.enterprise.hr.dto.PerformanceReviewCycleResponse;
import com.enterprise.hr.exception.BusinessRuleException;
import com.enterprise.hr.exception.ResourceNotFoundException;
import com.enterprise.hr.repository.PerformanceReviewCycleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PerformanceReviewCycleService {

    private final PerformanceReviewCycleRepository performanceReviewCycleRepository;

    public PerformanceReviewCycleService(PerformanceReviewCycleRepository performanceReviewCycleRepository) {
        this.performanceReviewCycleRepository = performanceReviewCycleRepository;
    }

    @Transactional
    public PerformanceReviewCycleResponse create(PerformanceReviewCycleRequest request) {
        validateRequest(request);
        String name = normalizeName(request.name());
        if (performanceReviewCycleRepository.existsByName(name)) {
            throw new BusinessRuleException("PERFORMANCE_REVIEW_CYCLE_DUPLICATE", "A performance review cycle with this name already exists");
        }

        PerformanceReviewCycle cycle = new PerformanceReviewCycle(
                name, request.startDate(), request.endDate(), request.description());
        return toResponse(performanceReviewCycleRepository.save(cycle));
    }

    public List<PerformanceReviewCycleResponse> findAll() {
        return performanceReviewCycleRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public PerformanceReviewCycleResponse findById(Long id) {
        return toResponse(getCycle(id));
    }

    @Transactional
    public PerformanceReviewCycleResponse update(Long id, PerformanceReviewCycleRequest request) {
        validateRequest(request);
        PerformanceReviewCycle cycle = getCycle(id);
        String name = normalizeName(request.name());
        if (performanceReviewCycleRepository.existsByNameAndIdNot(name, id)) {
            throw new BusinessRuleException("PERFORMANCE_REVIEW_CYCLE_DUPLICATE", "A performance review cycle with this name already exists");
        }

        cycle.update(name, request.startDate(), request.endDate(), request.description());
        return toResponse(performanceReviewCycleRepository.save(cycle));
    }

    @Transactional
    public void delete(Long id) {
        performanceReviewCycleRepository.delete(getCycle(id));
    }

    private PerformanceReviewCycle getCycle(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessRuleException("PERFORMANCE_REVIEW_CYCLE_INVALID_ID", "Performance review cycle id must be a positive number");
        }
        return performanceReviewCycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Performance review cycle not found: " + id));
    }

    private void validateRequest(PerformanceReviewCycleRequest request) {
        if (request == null || request.name() == null || normalizeName(request.name()).isEmpty()
                || request.startDate() == null || request.endDate() == null) {
            throw new BusinessRuleException("PERFORMANCE_REVIEW_CYCLE_INVALID", "Cycle name, start date, and end date are required");
        }
        if (request.endDate().isBefore(request.startDate())) {
            throw new BusinessRuleException("PERFORMANCE_REVIEW_CYCLE_INVALID_DATES", "End date must be on or after start date");
        }
    }

    private String normalizeName(String name) {
        return name.trim();
    }

    private PerformanceReviewCycleResponse toResponse(PerformanceReviewCycle cycle) {
        return new PerformanceReviewCycleResponse(
                cycle.getId(), cycle.getName(), cycle.getStartDate(), cycle.getEndDate(), cycle.getDescription());
    }
}
