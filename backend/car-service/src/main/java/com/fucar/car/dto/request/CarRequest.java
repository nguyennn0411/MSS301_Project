package com.fucar.car.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CarRequest(
        @NotBlank String carName,
        String carDescription,
        String imageUrl,
        @NotNull @Min(2) Integer numberOfDoors,
        @NotNull @Min(1) Integer seatingCapacity,
        @NotBlank String fuelType,
        @NotNull @Min(1900) Integer year,
        @NotNull @Min(0) Long mileage,
        @NotNull Long manufacturerId,
        @NotNull Long supplierId,
        Boolean carStatus,
        String operationalStatus,
        @NotNull @DecimalMin("0.0") BigDecimal carRentingPricePerDay
) {
}
