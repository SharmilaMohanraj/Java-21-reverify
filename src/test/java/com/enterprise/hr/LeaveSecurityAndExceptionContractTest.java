package com.enterprise.hr;

import static org.assertj.core.api.Assertions.assertThat;

import com.enterprise.hr.controller.LeaveRequestController;
import com.enterprise.hr.domain.LeaveStatus;
import com.enterprise.hr.dto.LeaveDecisionRequest;
import com.enterprise.hr.exception.ApiExceptionHandler;
import com.enterprise.hr.exception.AttendanceBusinessRuleException;
import com.enterprise.hr.exception.BusinessRuleException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.lang.reflect.Method;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;

class LeaveSecurityAndExceptionContractTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void managerLeaveEndpointsRequireManagerRole() throws NoSuchMethodException {
        Method pending = LeaveRequestController.class.getMethod(
                "pendingSubordinates", org.springframework.security.core.Authentication.class);
        Method decision = LeaveRequestController.class.getMethod(
                "decide",
                org.springframework.security.core.Authentication.class,
                Long.class,
                LeaveDecisionRequest.class);

        assertThat(pending.getAnnotation(PreAuthorize.class).value()).isEqualTo("hasRole('MANAGER')");
        assertThat(decision.getAnnotation(PreAuthorize.class).value()).isEqualTo("hasRole('MANAGER')");
    }

    @Test
    void pendingDecisionIsRejectedByRequestValidation() {
        var violations = validator.validate(new LeaveDecisionRequest(LeaveStatus.PENDING, "not a decision"));

        assertThat(violations).singleElement()
                .extracting(violation -> violation.getMessage())
                .isEqualTo("must be APPROVED or REJECTED");
        assertThat(validator.validate(new LeaveDecisionRequest(LeaveStatus.APPROVED, null))).isEmpty();
        assertThat(validator.validate(new LeaveDecisionRequest(LeaveStatus.REJECTED, null))).isEmpty();
    }

    @Test
    void attendanceBusinessRulesUseTheSharedStructuredConflictResponse() {
        ApiExceptionHandler handler = new ApiExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/attendance/check-in");

        ResponseEntity<Map<String, Object>> response = handler.conflict(
                new AttendanceBusinessRuleException("An open attendance record already exists"), request);

        assertThat(new AttendanceBusinessRuleException("conflict"))
                .isInstanceOf(BusinessRuleException.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody())
                .containsEntry("status", 409)
                .containsEntry("error", "Conflict")
                .containsEntry("message", "An open attendance record already exists")
                .containsEntry("path", "/api/attendance/check-in");
        assertThat(response.getBody()).containsOnlyKeys("timestamp", "status", "error", "message", "path");
    }
}
