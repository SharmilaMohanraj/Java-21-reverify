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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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
                request.grossPay(), request.deductions(), request.netPay());
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
        record.setGrossPay(request.grossPay());
        record.setDeductions(request.deductions());
        record.setNetPay(request.netPay());
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
        if (request.grossPay().signum() < 0 || request.deductions().signum() < 0 || request.netPay().signum() < 0) {
            throw new BusinessRuleException("Payroll amounts must not be negative");
        }
        BigDecimal expectedNetPay = request.grossPay().subtract(request.deductions());
        if (expectedNetPay.compareTo(request.netPay()) != 0) {
            throw new BusinessRuleException("Net pay must equal gross pay minus deductions");
        }
    }

    private PayrollRecordResponse toResponse(PayrollRecord record) {
        return new PayrollRecordResponse(
                record.getId(), record.getEmployee().getId(), record.getPeriodStart(), record.getPeriodEnd(),
                record.getGrossPay(), record.getDeductions(), record.getNetPay());
    }
}
