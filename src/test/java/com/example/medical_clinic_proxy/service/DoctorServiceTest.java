package com.example.medical_clinic_proxy.service;

import com.example.medical_clinic_proxy.client.MedicalClinicClient;
import com.example.medical_clinic_proxy.dto.PageResponse;
import com.example.medical_clinic_proxy.dto.VisitDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {
    @Mock
    private MedicalClinicClient medicalClinicClient;
    @InjectMocks
    private DoctorService doctorService;

    @Test
    void getAvailableVisitsByDoctorId_DataCorrect_ReturnsVisit() {
        PageRequest page = PageRequest.of(1, 1);
        Long doctorId = 1L;
        VisitDto visit = new VisitDto(1L, null, null, doctorId, "Jan Kowalski", 1L, "Adam Nowak");
        List<VisitDto> visitDtoList = List.of(visit);
        PageResponse<VisitDto> content = new PageResponse<>(visitDtoList, 1, 1, 1L, 1);

        when(medicalClinicClient.getAllVisits(page, doctorId, null, null)).thenReturn(content);

        PageResponse<VisitDto> response = doctorService.getAvailableVisitsByDoctorId(page, doctorId);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, response.content().size()),
                () -> Assertions.assertEquals(1L, response.content().getFirst().doctorId()),
                () -> Assertions.assertEquals("Jan Kowalski", response.content().getFirst().doctorFullName()),
                () -> Assertions.assertEquals("Adam Nowak", response.content().getFirst().patientFullName())
        );

        verify(medicalClinicClient, times(1)).getAllVisits(page, doctorId, null, null);
    }

    @Test
    void getAvailableVisitsBySpecializationAndDate_DataCorrect_ReturnsVisits() {
        PageRequest page = PageRequest.of(1, 1);
        LocalDate date = LocalDate.of(2026, 12, 12);
        String specialization = "Cardiology";
        VisitDto visit = new VisitDto(1L, null, null, 1L, "Jan Kowalski", 1L, "Adam Nowak");
        List<VisitDto> visitDtoList = List.of(visit);
        PageResponse<VisitDto> content = new PageResponse<>(visitDtoList, 1, 1, 1L, 1);

        when(medicalClinicClient.getAllVisits(page, null, date, specialization)).thenReturn(content);

        PageResponse<VisitDto> response = doctorService.getAvailableVisitsBySpecializationAndDate(page, date, specialization);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, response.content().size()),
                () -> Assertions.assertEquals(1L, response.content().getFirst().doctorId()),
                () -> Assertions.assertEquals("Jan Kowalski", response.content().getFirst().doctorFullName()),
                () -> Assertions.assertEquals("Adam Nowak", response.content().getFirst().patientFullName())
        );
        verify(medicalClinicClient, times(1)).getAllVisits(page, null,date,specialization);

    }
}
