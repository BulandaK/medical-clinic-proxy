package com.example.medical_clinic_proxy.client;

import com.example.medical_clinic_proxy.dto.PageResponse;
import com.example.medical_clinic_proxy.dto.VisitDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
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
    void bookVisit_ValidData_ReturnsBookedVisit() throws JsonProcessingException {
        // given
        Long visitId = 50L;
        Long patientId = 5L;
        VisitDto response = new VisitDto(visitId, null, null, 1L, "Dr House", patientId, "Jan Kowalski");

        wireMockServer.stubFor(WireMock.patch(WireMock.urlEqualTo("/visits/" + visitId + "/patient/" + patientId))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(response))
                        .withStatus(200)));

        // when
        VisitDto result = medicalClinicClient.bookVisit(visitId, patientId);

        // then
        assertAll(
                () -> assertEquals(visitId, result.id()),
                () -> assertEquals(patientId, result.patientId()),
                () -> assertNotNull(result.patientFullName())
        );
    }

    @Test
    void getAllVisits_ByDoctorId_ReturnsPage() throws JsonProcessingException {
        // given
        Long doctorId = 7L;
        PageRequest pageable = PageRequest.of(0, 5);
        PageResponse<VisitDto> mockResponse = new PageResponse<>(List.of(), 0, 5, 0L, 0);

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/visits"))
                .withQueryParam("doctorId", WireMock.equalTo("7"))
                .withQueryParam("page", WireMock.equalTo("0"))
                .withQueryParam("size", WireMock.equalTo("5"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(mockResponse))
                        .withStatus(200)));

        PageResponse<VisitDto> result = medicalClinicClient.getAllVisits(pageable, doctorId, null, null);

        assertNotNull(result);
        assertEquals(0, result.totalElements());
    }
    @Test
    void getAllVisits_ByDateAndSpec_ReturnsPage() throws JsonProcessingException {
        // given
        LocalDate date = LocalDate.of(2026, 5, 20);
        String spec = "Cardiology";
        PageRequest pageable = PageRequest.of(0, 10);
        PageResponse<VisitDto> mockResponse = new PageResponse<>(List.of(), 0, 10, 0L, 0);

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/visits"))
                .withQueryParam("date", WireMock.equalTo("2026-05-20"))
                .withQueryParam("specialization", WireMock.equalTo("Cardiology"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(mockResponse))
                        .withStatus(200)));

        PageResponse<VisitDto> result = medicalClinicClient.getAllVisits(pageable, null, date, spec);

        assertNotNull(result);
    }
}
