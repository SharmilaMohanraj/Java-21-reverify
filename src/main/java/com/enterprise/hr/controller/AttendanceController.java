package com.enterprise.hr.controller;

import com.enterprise.hr.dto.attendance.AttendanceResponse;
import com.enterprise.hr.dto.attendance.CheckInRequest;
import com.enterprise.hr.dto.attendance.CheckOutRequest;
import com.enterprise.hr.service.AttendanceService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/check-in")
    public ResponseEntity<AttendanceResponse> checkIn(@RequestBody CheckInRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.checkIn());
    }

    @PostMapping("/check-out")
    public AttendanceResponse checkOut(@RequestBody CheckOutRequest request) {
        return attendanceService.checkOut();
    }

    @GetMapping("/me")
    public List<AttendanceResponse> myAttendance() {
        return attendanceService.getMyAttendance();
    }
}
