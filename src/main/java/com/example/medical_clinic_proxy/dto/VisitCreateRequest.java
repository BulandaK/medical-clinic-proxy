package com.example.medical_clinic_proxy.dto;

public record VisitCreateRequest(
        Long visitId,
        Long patientId
) {
}
