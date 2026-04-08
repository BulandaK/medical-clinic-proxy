package com.example.medical_clinic_proxy.exception;

import org.springframework.http.HttpStatus;

public class ServiceUnavailableException extends MedicalClinicException {
    public ServiceUnavailableException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
