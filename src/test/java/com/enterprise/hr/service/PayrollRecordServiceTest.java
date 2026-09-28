package com.enterprise.hr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.enterprise.hr.domain.Employee;
import com.enterprise.hr.domain.PayrollRecord;
import com.enterprise.hr.dto.PayrollRecordRequest;
import com.enterprise.hr.dto.PayrollRecordResponse;
import com.enterprise.hr.repository.PayrollRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PayrollRecordServiceTest {

    @Mock
    private PayrollRecordRepository payrollRecordRepository;

    @Mock
    private EntityManager entityManager;

    @Test
    void createMapsAmountContractFieldsFromRequestToResponse() {
        Employee employee = employeeWithId(42L);
        Instant paidAt = Instant.parse("2025-01-31T12:30:00Z");
        PayrollRecordRequest request = request(paidAt);
        when(entityManager.find(Employee.class, 42L)).thenReturn(employee);
        when(payrollRecordRepository.save(any(PayrollRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PayrollRecordResponse response = new PayrollRecordService(payrollRecordRepository, entityManager).create(request);

        assertEquals(new BigDecimal("100.00"), response.grossAmount());
        assertEquals(new BigDecimal("10.00"), response.deductionAmount());
        assertEquals(new BigDecimal("90.00"), response.netAmount());
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

        assertEquals(new BigDecimal("100.00"), record.getGrossAmount());
        assertEquals(new BigDecimal("10.00"), record.getDeductionAmount());
        assertEquals(new BigDecimal("90.00"), record.getNetAmount());
        assertNull(record.getPaidAt());
        assertNull(response.paidAt());
    }

    @Test
    void amountNamesAreTheJsonRequestAndResponseContract() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        String json = """
                {"employeeId":42,"periodStart":"2025-01-01","periodEnd":"2025-01-31",
                 "grossAmount":100.00,"deductionAmount":10.00,"netAmount":90.00}
                """;

        PayrollRecordRequest request = objectMapper.readValue(json, PayrollRecordRequest.class);
        String responseJson = objectMapper.writeValueAsString(new PayrollRecordResponse(
                7L, 42L, request.periodStart(), request.periodEnd(), request.grossAmount(),
                request.deductionAmount(), request.netAmount(), null));

        assertEquals(new BigDecimal("100.00"), request.grossAmount());
        assertEquals(new BigDecimal("10.00"), request.deductionAmount());
        assertEquals(new BigDecimal("90.00"), request.netAmount());
        assertFalse(responseJson.contains("grossPay"));
        assertFalse(responseJson.contains("deductions"));
        assertFalse(responseJson.contains("netPay"));
        assertEquals(true, responseJson.contains("grossAmount"));
        assertEquals(true, responseJson.contains("deductionAmount"));
        assertEquals(true, responseJson.contains("netAmount"));
    }

    @Test
    void amountFieldsUseTheDeclaredPersistenceColumns() throws NoSuchFieldException {
        assertEquals("gross_amount", PayrollRecord.class.getDeclaredField("grossAmount")
                .getAnnotation(Column.class).name());
        assertEquals("deduction_amount", PayrollRecord.class.getDeclaredField("deductionAmount")
                .getAnnotation(Column.class).name());
        assertEquals("net_amount", PayrollRecord.class.getDeclaredField("netAmount")
                .getAnnotation(Column.class).name());
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
