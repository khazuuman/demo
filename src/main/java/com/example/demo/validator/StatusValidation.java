package com.example.demo.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class StatusValidation implements ConstraintValidator<StatusValidate, String> {


    @Override
    public void initialize(StatusValidate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(s))
            return true;
        return "Hiển thị".equals(s) || "Ẩn".equals(s);
    }
}
