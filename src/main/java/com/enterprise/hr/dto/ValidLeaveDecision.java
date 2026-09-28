package com.enterprise.hr.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Restricts manager decisions to terminal leave states. */
@Documented
@Constraint(validatedBy = LeaveDecisionStatusValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLeaveDecision {
    String message() default "must be APPROVED or REJECTED";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
