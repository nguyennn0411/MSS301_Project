package com.fucar.car.dto.response;

import java.math.BigDecimal;

public record CarResponse(
        Long carId,
        String carName,
        String carDescription,
        String imageUrl,
        Integer numberOfDoors,
        Integer seatingCapacity,
        String fuelType,
        Integer year,
        Long mileage,
        Long manufacturerId,
        String manufacturerName,
        Long supplierId,
        String supplierName,
        Boolean carStatus,
        String operationalStatus,
        BigDecimal carRentingPricePerDay
) {
}
