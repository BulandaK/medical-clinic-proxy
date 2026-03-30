package com.example.medical_clinic_proxy.client;

import com.example.medical_clinic_proxy.dto.PageResponse;
import com.example.medical_clinic_proxy.dto.VisitDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class MedicalClinicClientFallbackFactory implements FallbackFactory<MedicalClinicClient> {
    @Override
    public MedicalClinicClient create(Throwable cause) {
        log.error("An exception occurred while calling the UserSession", cause);
        return new MedicalClinicClient() {
            @Override
            public List<VisitDto> getVisits(Long patientId) {
                log.info("[Fallback] get visits for patient");
                return List.of();
            }

            @Override
            public VisitDto bookVisit(Long id, Long patientId) {
                log.info("[Fallback] book visit");
                return new VisitDto(null,null,null,null,null,null,null);
            }

            @Override
            public PageResponse<VisitDto> getAllVisits(Pageable pageable, Long doctorId, LocalDate date, String specialization) {
                log.info("[Fallback] get visits");
                return null;
            }
        };
    }

}
