package com.fucar.customer.dto.response;

import java.time.LocalDate;

public record CustomerResponse(
        Long customerId,
        String customerName,
        String telephone,
        String email,
        LocalDate customerBirthday,
        String address,
        String identityCard,
        Boolean customerStatus
) {
}
