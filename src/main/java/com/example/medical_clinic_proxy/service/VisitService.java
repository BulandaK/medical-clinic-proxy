package com.example.medical_clinic_proxy.service;

import com.example.medical_clinic_proxy.client.MedicalClinicClient;
import com.example.medical_clinic_proxy.dto.VisitDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VisitService {
    private final MedicalClinicClient medicalClinicClient;

    public VisitDto bookVisit(Long visitId,Long patientId) {
        return medicalClinicClient.bookVisit(visitId, patientId);
    }
}
