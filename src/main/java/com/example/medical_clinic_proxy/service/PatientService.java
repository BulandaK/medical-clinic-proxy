package com.example.medical_clinic_proxy.service;

import com.example.medical_clinic_proxy.client.MedicalClinicClient;
import com.example.medical_clinic_proxy.dto.VisitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final MedicalClinicClient medicalClinicClient;

    public List<VisitDto> getVisits(Long id) {
        return medicalClinicClient.getVisits(id);
    }
}
