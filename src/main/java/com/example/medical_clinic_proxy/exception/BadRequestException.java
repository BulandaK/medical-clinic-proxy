package com.example.medical_clinic_proxy.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends MedicalClinicException{
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
