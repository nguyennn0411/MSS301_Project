package com.fucar.car.dto.response;

import java.time.LocalDate;

public record CarScheduleResponse(
        Long reservationId,
        String sagaId,
        Long carId,
        LocalDate startDate,
        LocalDate endDate,
        String status
) {}
