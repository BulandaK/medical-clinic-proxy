package com.example.medical_clinic_proxy.service;

import com.example.medical_clinic_proxy.client.MedicalClinicClient;
import com.example.medical_clinic_proxy.dto.PageResponse;
import com.example.medical_clinic_proxy.dto.VisitDto;
import com.example.medical_clinic_proxy.exception.NotFoundException;
import com.example.medical_clinic_proxy.exception.ServiceUnavailableException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {
    @Mock
    private MedicalClinicClient medicalClinicClient;
    @InjectMocks
    private DoctorService doctorService;

    @Test
    void getAllVisits_DataCorrect_ReturnsPageResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        Long doctorId = 1L;
        VisitDto visit = new VisitDto(1L, null, null, doctorId, "Adam Nowak", null, null);
        PageResponse<VisitDto> pageResponse = new PageResponse<>(List.of(visit), 1, 1, 1L, 1);

        when(medicalClinicClient.getAllVisits(pageable, doctorId, null, null, null, false))
                .thenReturn(pageResponse);

        PageResponse<VisitDto> response = doctorService.getAllVisits(pageable, doctorId);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(1, response.content().size()),
                () -> Assertions.assertEquals(doctorId, response.content().get(0).doctorId()),
                () -> Assertions.assertEquals("Adam Nowak", response.content().get(0).doctorFullName())
        );
    }

    @Test
    void getAllVisits_DoctorNotFound_ThrowsNotFoundException() {
        Pageable pageable = PageRequest.of(0, 10);
        Long doctorId = 99L;

        when(medicalClinicClient.getAllVisits(pageable, doctorId, null, null, null, false))
                .thenThrow(new NotFoundException("Resource not found (Doctor/Visit)"));

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> doctorService.getAllVisits(pageable, doctorId));

        Assertions.assertEquals("Resource not found (Doctor/Visit)", exception.getMessage());
    }

    @Test
    void getAllVisits_ServiceUnavailable_ThrowsServiceUnavailableException() {
        Pageable pageable = PageRequest.of(0, 10);
        Long doctorId = 1L;

        when(medicalClinicClient.getAllVisits(pageable, doctorId, null, null, null, false))
                .thenThrow(new ServiceUnavailableException("Service not available right now"));

        ServiceUnavailableException exception = Assertions.assertThrows(ServiceUnavailableException.class,
                () -> doctorService.getAllVisits(pageable, doctorId));

        Assertions.assertEquals("Service not available right now", exception.getMessage());
    }

    @Test
    void deleteVisit_DataCorrect_DeletesSuccessfully() {
        Long visitId = 1L;

        doNothing().when(medicalClinicClient).deleteVisit(visitId);

        Assertions.assertDoesNotThrow(() -> doctorService.deleteVisit(visitId));

        verify(medicalClinicClient, times(1)).deleteVisit(visitId);
    }

    @Test
    void deleteVisit_VisitNotFound_ThrowsNotFoundException() {
        Long visitId = 99L;

        doThrow(new NotFoundException("Resource not found (Doctor/Visit)"))
                .when(medicalClinicClient).deleteVisit(visitId);

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> doctorService.deleteVisit(visitId));

        Assertions.assertEquals("Resource not found (Doctor/Visit)", exception.getMessage());
    }

    @Test
    void deleteVisit_ServiceUnavailable_ThrowsServiceUnavailableException() {
        Long visitId = 1L;

        doThrow(new ServiceUnavailableException("Service not available right now"))
                .when(medicalClinicClient).deleteVisit(visitId);

        ServiceUnavailableException exception = Assertions.assertThrows(ServiceUnavailableException.class,
                () -> doctorService.deleteVisit(visitId));

        Assertions.assertEquals("Service not available right now", exception.getMessage());
    }
}
