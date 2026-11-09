package com.fucar.renting.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreateRentingRequest(
        Long customerId,
        @Valid @NotEmpty(message = "At least one car is required")
        List<RentingDetailRequest> details
) {
}
