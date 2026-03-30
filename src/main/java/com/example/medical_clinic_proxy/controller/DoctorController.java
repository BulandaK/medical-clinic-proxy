package com.example.medical_clinic_proxy.controller;

import com.example.medical_clinic_proxy.dto.PageResponse;
import com.example.medical_clinic_proxy.dto.VisitDto;
import com.example.medical_clinic_proxy.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping("/{doctorId}/visits")
    public PageResponse<VisitDto> getDoctorAvailableVisits(Pageable pageable, @PathVariable Long doctorId) {
        log.info("Get available visits for doctor with ID: {}",doctorId);
        return doctorService.getAvailableVisitsByDoctorId(pageable, doctorId);
    }

    @GetMapping("/visits")
    public PageResponse<VisitDto> getVisitsBySpecializationAndDate(
            Pageable pageable,
            @RequestParam String specialization,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate date) {
        log.info("Get available visits for doctors with specialization: {} with on date: {}",specialization,date);
        return doctorService.getAvailableVisitsBySpecializationAndDate(pageable, date, specialization);
    }
}
