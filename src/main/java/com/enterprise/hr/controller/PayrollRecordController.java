package com.enterprise.hr.controller;

import com.enterprise.hr.dto.PayrollRecordRequest;
import com.enterprise.hr.dto.PayrollRecordResponse;
import com.enterprise.hr.service.PayrollRecordService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/payroll-records")
@PreAuthorize("hasRole('HR')")
public class PayrollRecordController {

    private final PayrollRecordService payrollRecordService;

    public PayrollRecordController(PayrollRecordService payrollRecordService) {
        this.payrollRecordService = payrollRecordService;
    }

    @PostMapping
    public ResponseEntity<PayrollRecordResponse> create(@Valid @RequestBody PayrollRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payrollRecordService.create(request));
    }

    @GetMapping
    public List<PayrollRecordResponse> findAll() {
        return payrollRecordService.findAll();
    }

    @GetMapping("/{id}")
    public PayrollRecordResponse findById(@PathVariable @Positive Long id) {
        return payrollRecordService.findById(id);
    }

    @PutMapping("/{id}")
    public PayrollRecordResponse update(@PathVariable @Positive Long id,
                                        @Valid @RequestBody PayrollRecordRequest request) {
        return payrollRecordService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        payrollRecordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
