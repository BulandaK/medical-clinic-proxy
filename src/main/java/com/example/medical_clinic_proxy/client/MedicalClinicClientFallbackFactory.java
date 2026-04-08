package com.example.medical_clinic_proxy.client;

import com.example.medical_clinic_proxy.dto.DoctorDto;
import com.example.medical_clinic_proxy.dto.PageResponse;
import com.example.medical_clinic_proxy.dto.VisitDto;
import com.example.medical_clinic_proxy.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
                throw new ServiceUnavailableException("Service not available right now");
            }

            @Override
            public VisitDto bookVisit(Long id, Long patientId) {
                log.info("[Fallback] book visit");
                throw new ServiceUnavailableException("Service not available right now");
            }

            @Override
            public PageResponse<VisitDto> getAllVisits(Pageable pageable, Long doctorId, LocalDateTime startRange, LocalDateTime endRange, String specialization, boolean available) {
                log.info("[Fallback] get visits");
                throw new ServiceUnavailableException("Service not available right now");
            }

            @Override
            public List<DoctorDto> getDoctorsBySpecialization(String specialization) {
                throw new ServiceUnavailableException("Service not available right now");
            }

            @Override
            public void deleteVisit(Long id) {
                log.info("[Fallback] delete visit");
                throw new ServiceUnavailableException("Service not available right now");
            }
        };
    }

}
