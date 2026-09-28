package com.enterprise.hr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "payroll_record")
@Check(constraints = "period_end >= period_start AND gross_amount >= 0 AND deduction_amount >= 0 AND net_amount = gross_amount - deduction_amount")
public class PayrollRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull
    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @NotNull
    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(name = "gross_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal grossAmount;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(name = "deduction_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal deductionAmount;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(name = "net_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal netAmount;

    @Column(name = "paid_at")
    private Instant paidAt;

    protected PayrollRecord() { }

    public PayrollRecord(Employee employee, LocalDate periodStart, LocalDate periodEnd,
                         BigDecimal grossAmount, BigDecimal deductionAmount, BigDecimal netAmount) {
        this(employee, periodStart, periodEnd, grossAmount, deductionAmount, netAmount, null);
    }

    public PayrollRecord(Employee employee, LocalDate periodStart, LocalDate periodEnd,
                         BigDecimal grossAmount, BigDecimal deductionAmount, BigDecimal netAmount, Instant paidAt) {
        this.employee = employee;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.grossAmount = grossAmount;
        this.deductionAmount = deductionAmount;
        this.netAmount = netAmount;
        this.paidAt = paidAt;
    }

    public Long getId() { return id; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public LocalDate getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }
    public BigDecimal getGrossAmount() { return grossAmount; }
    public void setGrossAmount(BigDecimal grossAmount) { this.grossAmount = grossAmount; }
    public BigDecimal getDeductionAmount() { return deductionAmount; }
    public void setDeductionAmount(BigDecimal deductionAmount) { this.deductionAmount = deductionAmount; }
    public BigDecimal getNetAmount() { return netAmount; }
    public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
    public Instant getPaidAt() { return paidAt; }
    public void setPaidAt(Instant paidAt) { this.paidAt = paidAt; }
}
