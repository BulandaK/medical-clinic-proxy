package com.example.medical_clinic_proxy.controller;

import com.example.medical_clinic_proxy.dto.VisitDto;
import com.example.medical_clinic_proxy.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/patient")
public class PatientController {
    private final PatientService patientService;

    @GetMapping("/{id}/visits")
    public List<VisitDto> getVisits(@PathVariable Long id) {
        log.info("Get all visits for patient with ID: {}",id);
        return patientService.getVisits(id);
    }
}
