package com.example.medical_clinic_proxy.service;

import com.example.medical_clinic_proxy.client.MedicalClinicClient;
import com.example.medical_clinic_proxy.dto.DoctorDto;
import com.example.medical_clinic_proxy.dto.PageResponse;
import com.example.medical_clinic_proxy.dto.VisitDto;
import com.example.medical_clinic_proxy.exception.BadRequestException;
import com.example.medical_clinic_proxy.exception.ConflictException;
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

import java.time.LocalDateTime;
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

    @Test
    void getVisits_PatientNotFound_ThrowsNotFoundException() {
        Long patientId = 99L;

        when(medicalClinicClient.getVisits(patientId))
                .thenThrow(new NotFoundException("Resource not found (Doctor/Visit)"));

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> patientService.getVisits(patientId));

        Assertions.assertEquals("Resource not found (Doctor/Visit)", exception.getMessage());
    }

    @Test
    void bookVisit_DataCorrect_ReturnsBookedVisit() {
        Long visitId = 1L;
        Long patientId = 2L;
        VisitDto bookedVisit = new VisitDto(visitId, null, null, 1L, "Adam Nowak", patientId, "Jan Kowalski");

        when(medicalClinicClient.bookVisit(visitId, patientId)).thenReturn(bookedVisit);

        VisitDto response = patientService.bookVisit(visitId, patientId);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(visitId, response.id()),
                () -> Assertions.assertEquals(patientId, response.patientId()),
                () -> Assertions.assertEquals("Jan Kowalski", response.patientFullName())
        );
    }

    @Test
    void bookVisit_VisitNotFound_ThrowsNotFoundException() {
        Long visitId = 99L;
        Long patientId = 1L;

        when(medicalClinicClient.bookVisit(visitId, patientId))
                .thenThrow(new NotFoundException("Resource not found (Doctor/Visit)"));

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> patientService.bookVisit(visitId, patientId));

        Assertions.assertEquals("Resource not found (Doctor/Visit)", exception.getMessage());
    }

    @Test
    void bookVisit_VisitAlreadyAssigned_ThrowsConflictException() {
        Long visitId = 1L;
        Long patientId = 1L;

        when(medicalClinicClient.bookVisit(visitId, patientId))
                .thenThrow(new ConflictException("Visit already assigned"));

        ConflictException exception = Assertions.assertThrows(ConflictException.class,
                () -> patientService.bookVisit(visitId, patientId));

        Assertions.assertEquals("Visit already assigned", exception.getMessage());
    }

    @Test
    void bookVisit_BadRequest_ThrowsBadRequestException() {
        Long visitId = 1L;
        Long patientId = 1L;

        when(medicalClinicClient.bookVisit(visitId, patientId))
                .thenThrow(new BadRequestException("Incorrect request"));

        BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                () -> patientService.bookVisit(visitId, patientId));

        Assertions.assertEquals("Incorrect request", exception.getMessage());
    }

    @Test
    void getAvailableVisitsByDoctorId_DataCorrect_ReturnsPageResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        Long doctorId = 1L;
        VisitDto visit = new VisitDto(1L, null, null, doctorId, "Adam Nowak", null, null);
        PageResponse<VisitDto> pageResponse = new PageResponse<>(List.of(visit), 1, 1, 1L, 1);

        when(medicalClinicClient.getAllVisits(pageable, doctorId, null, null, null, true))
                .thenReturn(pageResponse);

        PageResponse<VisitDto> response = patientService.getAvailableVisitsByDoctorId(pageable, doctorId);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(1, response.content().size()),
                () -> Assertions.assertEquals(doctorId, response.content().get(0).doctorId()),
                () -> Assertions.assertEquals("Adam Nowak", response.content().get(0).doctorFullName())
        );
    }

    @Test
    void getAvailableVisitsByDoctorId_ServiceUnavailable_ThrowsServiceUnavailableException() {
        Pageable pageable = PageRequest.of(0, 10);
        Long doctorId = 1L;

        when(medicalClinicClient.getAllVisits(pageable, doctorId, null, null, null, true))
                .thenThrow(new ServiceUnavailableException("Service not available right now"));

        ServiceUnavailableException exception = Assertions.assertThrows(ServiceUnavailableException.class,
                () -> patientService.getAvailableVisitsByDoctorId(pageable, doctorId));

        Assertions.assertEquals("Service not available right now", exception.getMessage());
    }

    @Test
    void getAllDoctorsWithSpecialization_DataCorrect_ReturnsDoctorList() {
        String specialization = "Kardiolog";
        DoctorDto doctor = new DoctorDto(1L, specialization, "adam.nowak@clinic.com", "Adam", "Nowak");
        List<DoctorDto> doctorList = List.of(doctor);

        when(medicalClinicClient.getDoctorsBySpecialization(specialization)).thenReturn(doctorList);

        List<DoctorDto> response = patientService.getAllDoctorsWithSpecialization(specialization);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(1, response.size()),
                () -> Assertions.assertEquals(specialization, response.get(0).specialization()),
                () -> Assertions.assertEquals("Adam", response.get(0).firstName()),
                () -> Assertions.assertEquals("Nowak", response.get(0).lastName())
        );
    }

    @Test
    void getAllDoctorsWithSpecialization_ServiceUnavailable_ThrowsServiceUnavailableException() {
        String specialization = "Kardiolog";

        when(medicalClinicClient.getDoctorsBySpecialization(specialization))
                .thenThrow(new ServiceUnavailableException("Service not available right now"));

        ServiceUnavailableException exception = Assertions.assertThrows(ServiceUnavailableException.class,
                () -> patientService.getAllDoctorsWithSpecialization(specialization));

        Assertions.assertEquals("Service not available right now", exception.getMessage());
    }
}
