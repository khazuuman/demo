package com.example.demo.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.PARAMETER, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = {StatusValidation.class})
public @interface OrderStatus {
    String message() default "status invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
