package com.fucar.gateway.service;

import com.fucar.gateway.config.AdminProperties;
import com.fucar.gateway.dto.request.LoginRequest;
import com.fucar.gateway.dto.response.AuthResponse;
import com.fucar.gateway.dto.response.CustomerLoginResponse;
import com.fucar.gateway.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuthService {
    private final AdminProperties adminProperties;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final WebClient customerWebClient;

    public AuthService(AdminProperties adminProperties,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       WebClient customerWebClient) {
        this.adminProperties = adminProperties;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.customerWebClient = customerWebClient;
    }

    public Mono<AuthResponse> login(LoginRequest request) {
        if (adminProperties.getEmail().equalsIgnoreCase(request.email())
                && adminPasswordMatches(request.password())) {
            String token = jwtService.generateToken(request.email(), "ADMIN", 0L);
            return Mono.just(new AuthResponse(token, "ADMIN"));
        }

        return customerWebClient.post()
                .uri("/internal/auth/customer-login")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CustomerLoginResponse.class)
                .map(customer -> new AuthResponse(
                        jwtService.generateToken(customer.email(), "CUSTOMER", customer.customerId()),
                        "CUSTOMER"
                ))
                .onErrorMap(ex -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
    }

    private boolean adminPasswordMatches(String rawPassword) {
        String configuredPassword = adminProperties.getPassword();
        if (configuredPassword != null && configuredPassword.startsWith("$2")) {
            return passwordEncoder.matches(rawPassword, configuredPassword);
        }
        return rawPassword.equals(configuredPassword);
    }
}
