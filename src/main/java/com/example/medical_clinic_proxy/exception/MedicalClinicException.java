package com.example.medical_clinic_proxy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MedicalClinicException extends RuntimeException {
    private final HttpStatus httpStatus;
    public MedicalClinicException(String message,HttpStatus status) {
        super(message);
        this.httpStatus = status;
    }
}
