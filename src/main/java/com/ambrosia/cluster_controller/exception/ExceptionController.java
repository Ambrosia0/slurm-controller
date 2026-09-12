package com.ambrosia.cluster_controller.exception;

import java.util.stream.Collectors;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(ApiException.class)
    public ErrorResponse apiException(ApiException ex){
        var problemDetail = ProblemDetail.forStatusAndDetail(ex.getHttpStatus(), ex.getMessage());
        if(ex.getProperties() != null)
            problemDetail.setProperties(ex.getProperties());
        return ErrorResponse.builder(ex, problemDetail).build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleValidationException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations()
            .stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest().body(message);
    }
}
