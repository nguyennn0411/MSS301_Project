package com.fucar.gateway.dto.response;

public record CustomerLoginResponse(Long customerId, String email, String customerName, String role) {
}
