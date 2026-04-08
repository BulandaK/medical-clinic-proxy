package com.example.medical_clinic_proxy.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends MedicalClinicException {
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
