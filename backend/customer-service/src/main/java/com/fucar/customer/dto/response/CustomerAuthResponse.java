package com.fucar.customer.dto.response;

public record CustomerAuthResponse(Long customerId, String email, String customerName, String role) {
}
