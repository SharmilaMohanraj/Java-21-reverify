package com.enterprise.hr.controller;

import com.enterprise.hr.dto.CreateLeaveRequest;
import com.enterprise.hr.dto.LeaveDecisionRequest;
import com.enterprise.hr.dto.LeaveRequestResponse;
import com.enterprise.hr.service.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {
    private final LeaveRequestService leaveRequests;

    public LeaveRequestController(LeaveRequestService leaveRequests) {
        this.leaveRequests = leaveRequests;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveRequestResponse create(Authentication authentication, @Valid @RequestBody CreateLeaveRequest request) {
        return leaveRequests.create(callerEmail(authentication), request);
    }

    @GetMapping("/me")
    public List<LeaveRequestResponse> mine(Authentication authentication) {
        return leaveRequests.mine(callerEmail(authentication));
    }

    @GetMapping("/pending-subordinates")
    public List<LeaveRequestResponse> pendingSubordinates(Authentication authentication) {
        return leaveRequests.pendingSubordinates(callerEmail(authentication));
    }

    @PatchMapping("/{id}/decision")
    public LeaveRequestResponse decide(Authentication authentication, @PathVariable Long id,
                                       @Valid @RequestBody LeaveDecisionRequest request) {
        return leaveRequests.decide(callerEmail(authentication), id, request);
    }

    private String callerEmail(Authentication authentication) {
        return authentication == null ? null : authentication.getName();
    }
}
