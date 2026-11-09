package com.fucar.customer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record RegisterCustomerRequest(
        @NotBlank String customerName,
        @NotBlank String telephone,
        @Email @NotBlank String email,
        @NotNull @Past LocalDate customerBirthday,
        @NotBlank String address,
        @NotBlank String identityCard,
        @Size(min = 6, message = "Password must be at least 6 characters")
        @NotBlank String password
) {
}
