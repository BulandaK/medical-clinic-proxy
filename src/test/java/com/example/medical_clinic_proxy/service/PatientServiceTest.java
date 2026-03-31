package com.example.medical_clinic_proxy.service;

import com.example.medical_clinic_proxy.client.MedicalClinicClient;
import com.example.medical_clinic_proxy.dto.VisitDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {
    @Mock
    private MedicalClinicClient medicalClinicClient;
    @InjectMocks
    private PatientService patientService;

    @Test
    void getVisits_DataCorrect_ReturnsVisit() {
        Long patientId = 1L;
        VisitDto visit = new VisitDto(1L, null, null, 1L, "Jan Kowalski", 1L, "Adam Nowak");
        List<VisitDto> visitDtoList = List.of(visit);

        when(medicalClinicClient.getVisits(any())).thenReturn(visitDtoList);

        List<VisitDto> response = patientService.getVisits(patientId);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(1, response.size())
        );

    }
}
