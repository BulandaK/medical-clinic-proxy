package com.example.medical_clinic_proxy.dto;

public record DoctorDto(
        Long id,
        String specialization,
        String email,
        String firstName,
        String lastName
) {
}

