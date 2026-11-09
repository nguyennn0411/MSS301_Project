package com.fucar.renting.dto.request;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.List;

public record CompleteRentingRequest(
        @DecimalMin(value = "0.0", message = "Late fee cannot be negative") BigDecimal lateFee,
        @DecimalMin(value = "0.0", message = "Damage fee cannot be negative") BigDecimal damageFee,
        @DecimalMin(value = "0.0", message = "Other fee cannot be negative") BigDecimal otherFee,
        String adminNote,
        List<Long> maintenanceCarIds
) {
}
