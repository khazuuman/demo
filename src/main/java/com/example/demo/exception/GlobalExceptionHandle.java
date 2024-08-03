package com.example.demo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandle {

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<Errors> handlingAppException(AppException exception) {
        ErrorCode code = exception.getErrorCode();

        Errors errors = Errors.builder()
                .message(code.getMessage())
                .build();
        return ResponseEntity.status(code.getHttpStatusCode()).body(errors);
    }

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<?> handlingException(Exception exception) {
        Errors errors = Errors.builder()
                .message(exception.getMessage())
                .build();
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<Errors> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> errorsMap = new HashMap<>();
        BindingResult bindingResult = exception.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            String enumKey = fieldError.getDefaultMessage();
            ErrorCode errorCode;

            try {
                errorCode = ErrorCode.valueOf(enumKey);
            } catch (IllegalArgumentException e) {
                errorCode = ErrorCode.DEFAULT_ERROR;
            }
            errorsMap.put(fieldError.getField(), errorCode.getMessage());
        }
        log.warn(exception.getMessage());
        Errors errors = Errors.builder()
                .message(ErrorCode.NOT_FULL_INFO.getMessage())
                .errors(errorsMap)
                .build();
        return ResponseEntity.badRequest().body(errors);
    }

}
