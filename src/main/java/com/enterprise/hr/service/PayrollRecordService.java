package com.enterprise.hr.service;

import com.enterprise.hr.domain.Employee;
import com.enterprise.hr.domain.PayrollRecord;
import com.enterprise.hr.dto.PayrollRecordRequest;
import com.enterprise.hr.dto.PayrollRecordResponse;
import com.enterprise.hr.exception.BusinessRuleException;
import com.enterprise.hr.exception.ResourceNotFoundException;
import com.enterprise.hr.repository.PayrollRecordRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PayrollRecordService {

    private final PayrollRecordRepository payrollRecordRepository;
    private final EntityManager entityManager;

    public PayrollRecordService(PayrollRecordRepository payrollRecordRepository, EntityManager entityManager) {
        this.payrollRecordRepository = payrollRecordRepository;
        this.entityManager = entityManager;
    }

    public PayrollRecordResponse create(PayrollRecordRequest request) {
        validateBusinessRules(request);
        PayrollRecord record = new PayrollRecord(
                findEmployee(request.employeeId()), request.periodStart(), request.periodEnd(),
                request.grossAmount(), request.deductionAmount(), request.netAmount(), request.paidAt());
        return toResponse(payrollRecordRepository.save(record));
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<PayrollRecordResponse> findAll() {
        return payrollRecordRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public PayrollRecordResponse findById(Long id) {
        return toResponse(findRecord(id));
    }

    public PayrollRecordResponse update(Long id, PayrollRecordRequest request) {
        validateBusinessRules(request);
        PayrollRecord record = findRecord(id);
        record.setEmployee(findEmployee(request.employeeId()));
        record.setPeriodStart(request.periodStart());
        record.setPeriodEnd(request.periodEnd());
        record.setGrossAmount(request.grossAmount());
        record.setDeductionAmount(request.deductionAmount());
        record.setNetAmount(request.netAmount());
        record.setPaidAt(request.paidAt());
        return toResponse(payrollRecordRepository.save(record));
    }

    public void delete(Long id) {
        payrollRecordRepository.delete(findRecord(id));
    }

    private PayrollRecord findRecord(Long id) {
        return payrollRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll record not found: " + id));
    }

    private Employee findEmployee(Long employeeId) {
        Employee employee = entityManager.find(Employee.class, employeeId);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found: " + employeeId);
        }
        return employee;
    }

    private void validateBusinessRules(PayrollRecordRequest request) {
        if (request.periodEnd().isBefore(request.periodStart())) {
            throw new BusinessRuleException("Period end must not be before period start");
        }
        if (request.grossAmount().signum() < 0 || request.deductionAmount().signum() < 0
                || request.netAmount().signum() < 0) {
            throw new BusinessRuleException("Payroll amounts must not be negative");
        }
        BigDecimal expectedNetAmount = request.grossAmount().subtract(request.deductionAmount());
        if (expectedNetAmount.compareTo(request.netAmount()) != 0) {
            throw new BusinessRuleException("Net amount must equal gross amount minus deduction amount");
        }
    }

    private PayrollRecordResponse toResponse(PayrollRecord record) {
        return new PayrollRecordResponse(
                record.getId(), record.getEmployee().getId(), record.getPeriodStart(), record.getPeriodEnd(),
                record.getGrossAmount(), record.getDeductionAmount(), record.getNetAmount(), record.getPaidAt());
    }
}
