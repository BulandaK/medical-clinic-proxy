package com.example.medical_clinic_proxy.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends MedicalClinicException{
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
