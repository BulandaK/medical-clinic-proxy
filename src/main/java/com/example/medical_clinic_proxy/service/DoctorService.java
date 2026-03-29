package com.example.medical_clinic_proxy.service;

import com.example.medical_clinic_proxy.client.MedicalClinicClient;
import com.example.medical_clinic_proxy.dto.PageResponse;
import com.example.medical_clinic_proxy.dto.VisitDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DoctorService {
    private final MedicalClinicClient medicalClinicClient;

    public PageResponse<VisitDto> getAvailableVisitsByDoctorId(Pageable pageable, Long doctorId) {
        return medicalClinicClient.getAllVisits(pageable, doctorId, null, null);
    }

    public PageResponse<VisitDto> getAvailableVisitsBySpecializationAndDate(Pageable pageable, LocalDate date, String specialization) {
        return medicalClinicClient.getAllVisits(pageable, null, date, specialization);
    }
}
