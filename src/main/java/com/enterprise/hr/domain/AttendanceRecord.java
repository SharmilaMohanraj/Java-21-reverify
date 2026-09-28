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
import java.time.Instant;

/** A single employee attendance interval. */
@Entity
@Table(name = "attendance")
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "check_in_at", nullable = false, updatable = false)
    private Instant checkInAt;

    @Column(name = "check_out_at")
    private Instant checkOutAt;

    protected AttendanceRecord() {
        // Required by JPA.
    }

    public AttendanceRecord(Employee employee, Instant checkInAt) {
        this.employee = employee;
        this.checkInAt = checkInAt;
    }

    public Long getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Instant getCheckInAt() {
        return checkInAt;
    }

    public Instant getCheckOutAt() {
        return checkOutAt;
    }

    public void checkOut(Instant checkedOutAt) {
        if (checkedOutAt.isBefore(checkInAt)) {
            throw new IllegalArgumentException("Check-out time cannot be before check-in time");
        }
        this.checkOutAt = checkedOutAt;
    }
}
