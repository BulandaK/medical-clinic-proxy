package com.example.medical_clinic_proxy.service;

import com.example.medical_clinic_proxy.client.MedicalClinicClient;
import com.example.medical_clinic_proxy.dto.VisitDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VisitServiceTest {
    @Mock
    private MedicalClinicClient medicalClinicClient;
    @InjectMocks
    private VisitService visitService;

    @Test
    void bookVisit_DataCorrect_ReturnsVisit() {
        Long visitId = 1L;
        Long patientId = 1L;
        VisitDto visit = new VisitDto(1L, null, null, 1L, "Jan Kowalski", 1L, "Adam Nowak");

        when(medicalClinicClient.bookVisit(any(),any())).thenReturn(visit);

        VisitDto response = visitService.bookVisit(visitId,patientId);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(1L,response.id()),
                () -> Assertions.assertEquals(1L,response.patientId())
        );

        verify(medicalClinicClient,times(1)).bookVisit(any(),any());
    }
}
