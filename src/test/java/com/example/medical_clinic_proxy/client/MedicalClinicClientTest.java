package com.example.medical_clinic_proxy.client;

import com.example.medical_clinic_proxy.dto.DoctorDto;
import com.example.medical_clinic_proxy.dto.PageResponse;
import com.example.medical_clinic_proxy.dto.VisitDto;
import com.example.medical_clinic_proxy.exception.ConflictException;
import com.example.medical_clinic_proxy.exception.NotFoundException;
import com.example.medical_clinic_proxy.exception.ServiceUnavailableException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class MedicalClinicClientTest {
    @Autowired
    private MedicalClinicClient medicalClinicClient;
    @Autowired
    WireMockServer wireMockServer;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getVisits_PatientExists_ReturnsVisitsList() throws JsonProcessingException {
        Long patientId = 1L;
        List<VisitDto> mockVisits = List.of(
                new VisitDto(10L, null, null, 100L, "Dr House", patientId, "Jan Kowalski"),
                new VisitDto(11L, null, null, 101L, "Dr Strange", patientId, "Jan Kowalski")
        );

        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/visits/" + patientId))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(mockVisits))
                        .withStatus(200)));

        List<VisitDto> result = medicalClinicClient.getVisits(patientId);

        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("Dr House", result.get(0).doctorFullName()),
                () -> assertEquals("Dr Strange", result.get(1).doctorFullName())
        );
    }

    @Test
    void getVisits_NotFound_ThrowsNotFoundException() {
        Long patientId = 99L;

        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/visits/99"))
                .willReturn(WireMock.aResponse()
                        .withStatus(404)));

        assertThrows(NotFoundException.class,
                () -> medicalClinicClient.getVisits(patientId));
    }

    @Test
    void bookVisit_ValidData_ReturnsBookedVisit() throws JsonProcessingException {
        Long visitId = 50L;
        Long patientId = 5L;
        VisitDto response = new VisitDto(visitId, null, null, 1L, "Dr House", patientId, "Jan Kowalski");

        wireMockServer.stubFor(WireMock.patch(WireMock.urlEqualTo("/visits/" + visitId + "/patient/" + patientId))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(response))
                        .withStatus(200)));

        VisitDto result = medicalClinicClient.bookVisit(visitId, patientId);

        assertAll(
                () -> assertEquals(visitId, result.id()),
                () -> assertEquals(patientId, result.patientId()),
                () -> assertNotNull(result.patientFullName())
        );
    }

    @Test
    void bookVisit_Conflict_ThrowsConflictException() {
        Long visitId = 1L;
        Long patientId = 1L;

        wireMockServer.stubFor(WireMock.patch(WireMock.urlEqualTo("/visits/1/patient/1"))
                .willReturn(WireMock.aResponse()
                        .withStatus(409)));

        assertThrows(ConflictException.class,
                () -> medicalClinicClient.bookVisit(visitId, patientId));
    }

    @Test
    void getAllVisits_ByDoctorId_ReturnsPage() throws JsonProcessingException {
        Long doctorId = 7L;
        PageRequest pageable = PageRequest.of(0, 5);
        VisitDto response = new VisitDto(1L, null, null, doctorId, "Dr House", 1L, "Jan Kowalski");
        PageResponse<VisitDto> mockResponse = new PageResponse<>(List.of(response), 0, 5, 0L, 0);

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/visits"))
                .withQueryParam("doctorId", WireMock.equalTo("7"))
                .withQueryParam("page", WireMock.equalTo("0"))
                .withQueryParam("size", WireMock.equalTo("5"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(mockResponse))
                        .withStatus(200)));

        PageResponse<VisitDto> result = medicalClinicClient.getAllVisits(pageable, doctorId, null, null, null, true);

        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(0, result.totalElements()),
                () -> assertEquals(1, result.content().size()),
                () -> assertEquals(7L, result.content().get(0).doctorId()),
                () -> assertEquals("Dr House", result.content().get(0).doctorFullName())
        );
    }

    @Test
    void getAllVisits_ByDateAndSpec_ReturnsPage() throws JsonProcessingException {
        String spec = "Cardiology";
        LocalDateTime start = LocalDateTime.of(2026, 5, 1, 20, 30, 0);
        PageRequest pageable = PageRequest.of(0, 10);
        String formattedDate = "2026-05-01T20:30:00";

        PageResponse<VisitDto> mockResponse = new PageResponse<>(List.of(), 0, 10, 0L, 0);

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/visits"))
                .withQueryParam("specialization", WireMock.equalTo(spec))
                .withQueryParam("startRange", WireMock.equalTo(formattedDate))
                .withQueryParam("page", WireMock.equalTo("0"))
                .withQueryParam("size", WireMock.equalTo("10"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(mockResponse))
                        .withStatus(200)));

        PageResponse<VisitDto> result = medicalClinicClient.getAllVisits(pageable, null, start, null, spec, true);

        assertNotNull(result);
    }

    @Test
    void getAllVisits_ServiceUnavailable_ThrowsServiceUnavailableException() {
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/visits"))
                .withQueryParam("doctorId", WireMock.equalTo("1"))
                .withQueryParam("available", WireMock.equalTo("true"))
                .willReturn(WireMock.aResponse()
                        .withStatus(503)));

        assertThrows(ServiceUnavailableException.class,
                () -> medicalClinicClient.getAllVisits(PageRequest.of(0, 10), 1L, null, null, null, true));
    }

    @Test
    void getDoctorsBySpecialization_DataCorrect_ReturnsListDoctorDto() throws JsonProcessingException {
        String specialization = "Cardiology";
        DoctorDto doctor = new DoctorDto(1L, "Cardiology", "drhaouse@gmail.com", "Luka", "House");
        List<DoctorDto> mockResponse = List.of(doctor);

        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/doctors/specialization/Cardiology"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(mockResponse))
                        .withStatus(200)));

        List<DoctorDto> result = medicalClinicClient.getDoctorsBySpecialization(specialization);

        assertNotNull(result);
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.get(0).id()),
                () -> assertEquals("House", result.get(0).lastName())
        );
    }

    @Test
    void getDoctorsBySpecialization_NotFound_ThrowsNotFoundException() {
        String specialization = "Cardiology";

        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/doctors/specialization/Cardiology"))
                .willReturn(WireMock.aResponse()
                        .withStatus(404)));

        assertThrows(NotFoundException.class,
                () -> medicalClinicClient.getDoctorsBySpecialization(specialization));
    }

    @Test
    void deleteVisit_DataCorrect_DeletesSuccessfully() {
        Long visitId = 1L;

        wireMockServer.stubFor(WireMock.delete(WireMock.urlEqualTo("/visits/1"))
                .willReturn(WireMock.aResponse()
                        .withStatus(204)));

        assertDoesNotThrow(() -> medicalClinicClient.deleteVisit(visitId));
    }

    @Test
    void deleteVisit_NotFound_ThrowsNotFoundException() {
        Long visitId = 99L;

        wireMockServer.stubFor(WireMock.delete(WireMock.urlEqualTo("/visits/99"))
                .willReturn(WireMock.aResponse()
                        .withStatus(404)));

        assertThrows(NotFoundException.class,
                () -> medicalClinicClient.deleteVisit(visitId));
    }
}
