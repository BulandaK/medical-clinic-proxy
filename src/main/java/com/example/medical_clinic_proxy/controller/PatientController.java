package com.example.medical_clinic_proxy.controller;

import com.example.medical_clinic_proxy.dto.DoctorDto;
import com.example.medical_clinic_proxy.dto.PageResponse;
import com.example.medical_clinic_proxy.dto.VisitDto;
import com.example.medical_clinic_proxy.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/patient")
public class PatientController {
    private final PatientService patientService;

    @GetMapping("/{id}/my-visits")
    public List<VisitDto> getVisits(@PathVariable Long id) {
        log.info("Get all visits for patient with ID: {}", id);
        return patientService.getVisits(id);
    }

    @PostMapping("/visits/{visitId}/book/{patientId}")
    public VisitDto book(@PathVariable Long visitId, @PathVariable Long patientId) {
        log.info("Book visit with ID: {}, for patient: {}", visitId, patientId);
        return patientService.bookVisit(visitId, patientId);
    }

    @GetMapping("/doctors/{doctorId}/visits")
    public PageResponse<VisitDto> getDoctorAvailableVisits(Pageable pageable, @PathVariable Long doctorId) {
        log.info("Get available visits for doctor with ID: {}", doctorId);
        return patientService.getAvailableVisitsByDoctorId(pageable, doctorId);
    }

    @GetMapping("/doctors/specialization/{specialization}")
    public List<DoctorDto> getAllDoctorsWithSpecialization(@PathVariable String specialization) {
        log.info("Get all doctors with specialization: {}", specialization);
        return patientService.getAllDoctorsWithSpecialization(specialization);
    }

    @GetMapping("/available/visits")
    public PageResponse<VisitDto> getVisitsBySpecializationAndDate(
            Pageable pageable,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startRange,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endRange) {
        log.info("Get available visits for doctors with specialization: {} on date: {} -- {}", specialization, startRange, endRange);
        return patientService.getAvailableVisitsBySpecializationAndDate(pageable, startRange, endRange, specialization);
    }
}
