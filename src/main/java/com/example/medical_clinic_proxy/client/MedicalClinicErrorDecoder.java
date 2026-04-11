package com.example.medical_clinic_proxy.client;

import com.example.medical_clinic_proxy.exception.BadRequestException;
import com.example.medical_clinic_proxy.exception.ConflictException;
import com.example.medical_clinic_proxy.exception.NotFoundException;
import com.example.medical_clinic_proxy.exception.ServiceUnavailableException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MedicalClinicErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("[Error] Fegin error in method: {}. Status: {}, Reason: {}",
                methodKey, response.status(), response.reason());

        return switch (response.status()) {
            case 400 -> new BadRequestException("Incorrect request");
            case 404 -> new NotFoundException("Resource not found (Doctor/Visit)");
            case 409 -> new ConflictException("Visit already assigned");
            case 503 -> new ServiceUnavailableException("Service not available right now");

            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}
