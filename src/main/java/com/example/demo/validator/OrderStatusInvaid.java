package com.example.demo.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class OrderStatusInvaid implements ConstraintValidator<OrderStatus, String> {
    @Override
    public void initialize(OrderStatus constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(s))
            return true;
        return s.equals("Chờ") || s.equals("Đang giao hàng") || s.equals("Đã nhận hàng") || s.equals("Đã giao hàng") || s.equals("Hủy");
    }
}
