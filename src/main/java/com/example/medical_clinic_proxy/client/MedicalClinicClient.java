package com.example.medical_clinic_proxy.client;

import com.example.medical_clinic_proxy.dto.DoctorDto;
import com.example.medical_clinic_proxy.dto.PageResponse;
import com.example.medical_clinic_proxy.dto.VisitDto;
import org.springframework.data.domain.Pageable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@FeignClient(value = "medical-clinic", url = "${medical.clinic.api.url}", configuration = MedicalClinicConfig.class, fallbackFactory = MedicalClinicClientFallbackFactory.class)
public interface MedicalClinicClient {
    @GetMapping("/visits/{patientId}")
    List<VisitDto> getVisits(@PathVariable Long patientId);

    @PatchMapping("/visits/{id}/patient/{patientId}")
    VisitDto bookVisit(@PathVariable Long id, @PathVariable Long patientId);

    @GetMapping("/visits")
    PageResponse<VisitDto> getAllVisits(
            @SpringQueryMap Pageable pageable,
            @RequestParam(value = "doctorId", required = false) Long doctorId,
            @RequestParam(value = "startRange", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startRange,
            @RequestParam(value = "endRange", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endRange,
            @RequestParam(value = "specialization", required = false) String specialization,
            @RequestParam(value = "available", required = false) boolean available
    );

    @GetMapping("/doctors/specialization/{specialization}")
    List<DoctorDto> getDoctorsBySpecialization(@PathVariable String specialization);

    @DeleteMapping("/visits/{id}")
    void deleteVisit(@PathVariable Long id);
}
