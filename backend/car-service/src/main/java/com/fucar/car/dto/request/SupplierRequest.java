package com.fucar.car.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SupplierRequest(
        @NotBlank String supplierName,
        String supplierDescription,
        String supplierAddress
) {
}
