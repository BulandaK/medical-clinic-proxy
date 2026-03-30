package com.example.medical_clinic_proxy.controller;

import com.example.medical_clinic_proxy.dto.VisitDto;
import com.example.medical_clinic_proxy.service.VisitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/visits")
public class VisitController {
    private final VisitService visitService;

    @PostMapping("/{visitId}/patient/{patientId}")
    public VisitDto book(@PathVariable Long visitId,@PathVariable Long patientId) {
        return visitService.bookVisit(visitId,patientId);
    }
}
