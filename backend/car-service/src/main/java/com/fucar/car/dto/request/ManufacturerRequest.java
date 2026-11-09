package com.fucar.car.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ManufacturerRequest(
        @NotBlank String manufacturerName,
        String description,
        String manufacturerCountry
) {
}
