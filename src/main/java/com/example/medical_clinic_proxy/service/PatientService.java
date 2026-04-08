package com.example.medical_clinic_proxy.service;

import com.example.medical_clinic_proxy.client.MedicalClinicClient;
import com.example.medical_clinic_proxy.dto.DoctorDto;
import com.example.medical_clinic_proxy.dto.PageResponse;
import com.example.medical_clinic_proxy.dto.VisitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final MedicalClinicClient medicalClinicClient;

    public List<VisitDto> getVisits(Long id) {
        return medicalClinicClient.getVisits(id);
    }

    public VisitDto bookVisit(Long visitId, Long patientId) {
        return medicalClinicClient.bookVisit(visitId, patientId);
    }

    public PageResponse<VisitDto> getAvailableVisitsByDoctorId(Pageable pageable, Long doctorId) {
        return medicalClinicClient.getAllVisits(pageable, doctorId, null, null, null, true);
    }

    public PageResponse<VisitDto> getAvailableVisitsBySpecializationAndDate(Pageable pageable, LocalDateTime startRange, LocalDateTime endRange, String specialization) {
        return medicalClinicClient.getAllVisits(pageable, null, startRange, endRange, specialization, true);
    }

    public List<DoctorDto> getAllDoctorsWithSpecialization(String specialization) {
        return medicalClinicClient.getDoctorsBySpecialization(specialization);
    }
}
