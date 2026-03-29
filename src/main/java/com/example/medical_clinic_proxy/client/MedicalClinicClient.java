package com.example.medical_clinic_proxy.client;

import com.example.medical_clinic_proxy.dto.PageResponse;
import com.example.medical_clinic_proxy.dto.VisitDto;
import org.springframework.data.domain.Pageable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@FeignClient(value = "medical-clinic", url = "${medical.clinic.api.url}", configuration = MedicalClinicConfig.class)
public interface MedicalClinicClient {
    @GetMapping("/visits/{patientId}")
    List<VisitDto> getVisits(@PathVariable Long patientId);

    @PatchMapping("/visits/{id}/patient/{patientId}")
    VisitDto bookVisit(@PathVariable Long id, @PathVariable Long patientId);

    @GetMapping("/visits")
    PageResponse<VisitDto> getAllVisits(
            @SpringQueryMap Pageable pageable,
            @RequestParam("doctorId") Long doctorId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("specialization") String specialization
    );
}
