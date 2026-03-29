package com.example.medical_clinic_proxy.controller;

import com.example.medical_clinic_proxy.dto.VisitCreateRequest;
import com.example.medical_clinic_proxy.dto.VisitDto;
import com.example.medical_clinic_proxy.service.VisitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/visits")
public class VisitController {
    private final VisitService visitService;

    @PostMapping("/book")
    public VisitDto book(@RequestBody VisitCreateRequest visitCreateRequest) {
        return visitService.bookVisit(visitCreateRequest);
    }
}
