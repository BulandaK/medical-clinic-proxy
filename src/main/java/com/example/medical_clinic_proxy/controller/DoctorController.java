package com.example.medical_clinic_proxy.controller;

import com.example.medical_clinic_proxy.dto.PageResponse;
import com.example.medical_clinic_proxy.dto.VisitDto;
import com.example.medical_clinic_proxy.service.DoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/doctor")
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping("/{doctorId}/visits")
    public PageResponse<VisitDto> getDoctorAvailableVisits(Pageable pageable, @PathVariable Long doctorId) {
        log.info("Get all visits for doctor with ID: {}", doctorId);
        return doctorService.getAllVisits(pageable, doctorId);
    }

    @DeleteMapping("/visits/{visitId}")
    public void deleteVisit(@PathVariable Long visitId) {
        doctorService.deleteVisit(visitId);
    }
}
