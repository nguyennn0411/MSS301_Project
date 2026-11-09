package com.fucar.customer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

public record UpdateCustomerRequest(
        String customerName,
        String telephone,
        @Email String email,
        @Past LocalDate customerBirthday,
        String address,
        String identityCard,
        Boolean customerStatus,
        String password
) {
}
