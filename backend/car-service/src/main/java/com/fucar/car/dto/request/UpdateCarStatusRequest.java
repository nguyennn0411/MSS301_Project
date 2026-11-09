package com.fucar.car.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateCarStatusRequest(@NotBlank String operationalStatus) {
}
