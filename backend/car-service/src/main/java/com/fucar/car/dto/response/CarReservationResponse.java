package com.fucar.car.dto.response;

import java.math.BigDecimal;

public record CarReservationResponse(String sagaId, Long carId, BigDecimal pricePerDay, String status) {}
