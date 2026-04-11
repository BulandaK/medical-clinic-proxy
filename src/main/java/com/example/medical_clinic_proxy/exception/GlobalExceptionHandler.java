package com.example.medical_clinic_proxy.exception;

import com.example.medical_clinic_proxy.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MedicalClinicException.class)
    public ResponseEntity<ErrorResponse> handleMedicalClinicException(MedicalClinicException ex, HttpServletRequest request) {
        log.error("[Error] error occurred: {}", ex.getMessage());
        return createResponse(ex, request, ex.getHttpStatus());
    }

    private ResponseEntity<ErrorResponse> createResponse(Exception ex, HttpServletRequest request, HttpStatus status) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, status);
    }
}
