package com.fucar.renting.dto.request;

import java.time.LocalDate;

public record ReserveCarRequest(Long carId, LocalDate startDate, LocalDate endDate) {}
