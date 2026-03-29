package com.example.medical_clinic_proxy.controller;

import com.example.medical_clinic_proxy.dto.PageResponse;
import com.example.medical_clinic_proxy.dto.VisitDto;
import com.example.medical_clinic_proxy.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping("/{doctorId}/visits")
    public PageResponse<VisitDto> getDoctorAvailableVisits(Pageable pageable, @PathVariable Long doctorId) {
        return doctorService.getAvailableVisitsByDoctorId(pageable, doctorId);
    }

    @GetMapping("/{specialization}/{date}")
    public PageResponse<VisitDto> getVisitsBySpecializationAndDate(
            Pageable pageable,
            @PathVariable String specialization,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate date) {
        return doctorService.getAvailableVisitsBySpecializationAndDate(pageable, date, specialization);
    }
}
