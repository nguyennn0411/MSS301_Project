package com.fucar.renting.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RentingDetailResponse(Long carId, LocalDate startDate, LocalDate endDate, BigDecimal price) {
}
