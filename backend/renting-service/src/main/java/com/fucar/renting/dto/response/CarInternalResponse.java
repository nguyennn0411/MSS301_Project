package com.fucar.renting.dto.response;

import java.math.BigDecimal;

public record CarInternalResponse(Long carId, Boolean carStatus, BigDecimal carRentingPricePerDay) {
}
