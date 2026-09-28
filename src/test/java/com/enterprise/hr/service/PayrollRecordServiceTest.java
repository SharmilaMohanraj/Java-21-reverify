package com.enterprise.hr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.enterprise.hr.domain.Employee;
import com.enterprise.hr.domain.PayrollRecord;
import com.enterprise.hr.dto.PayrollRecordRequest;
import com.enterprise.hr.dto.PayrollRecordResponse;
import com.enterprise.hr.repository.PayrollRecordRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PayrollRecordServiceTest {

    @Mock
    private PayrollRecordRepository payrollRecordRepository;

    @Mock
    private EntityManager entityManager;

    @Test
    void createMapsPaidAtFromRequestToEntityAndResponse() {
        Employee employee = employeeWithId(42L);
        Instant paidAt = Instant.parse("2025-01-31T12:30:00Z");
        PayrollRecordRequest request = request(paidAt);
        when(entityManager.find(Employee.class, 42L)).thenReturn(employee);
        when(payrollRecordRepository.save(any(PayrollRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PayrollRecordResponse response = new PayrollRecordService(payrollRecordRepository, entityManager).create(request);

        assertEquals(paidAt, response.paidAt());
    }

    @Test
    void updateMapsNullablePaidAtFromRequestToEntityAndResponse() {
        Employee employee = employeeWithId(42L);
        PayrollRecord record = new PayrollRecord(employee, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31),
                new BigDecimal("100.00"), new BigDecimal("10.00"), new BigDecimal("90.00"), Instant.now());
        ReflectionTestUtils.setField(record, "id", 7L);
        PayrollRecordRequest request = request(null);
        when(payrollRecordRepository.findById(7L)).thenReturn(Optional.of(record));
        when(entityManager.find(Employee.class, 42L)).thenReturn(employee);
        when(payrollRecordRepository.save(record)).thenReturn(record);

        PayrollRecordResponse response = new PayrollRecordService(payrollRecordRepository, entityManager).update(7L, request);

        assertNull(record.getPaidAt());
        assertNull(response.paidAt());
    }

    private Employee employeeWithId(Long id) {
        Employee employee = new Employee();
        ReflectionTestUtils.setField(employee, "id", id);
        return employee;
    }

    private PayrollRecordRequest request(Instant paidAt) {
        return new PayrollRecordRequest(42L, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31),
                new BigDecimal("100.00"), new BigDecimal("10.00"), new BigDecimal("90.00"), paidAt);
    }
}
