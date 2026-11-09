package com.fucar.car.dto.response;

import java.math.BigDecimal;

public record CarInternalResponse(Long carId, Boolean carStatus, BigDecimal carRentingPricePerDay) {
}
