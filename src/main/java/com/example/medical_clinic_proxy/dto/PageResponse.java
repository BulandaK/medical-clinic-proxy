package com.example.medical_clinic_proxy.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        Long totalElements,
        int totalPages
) {
}
