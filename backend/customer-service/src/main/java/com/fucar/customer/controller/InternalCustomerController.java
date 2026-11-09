package com.fucar.customer.controller;

import com.fucar.customer.dto.request.CustomerLoginRequest;
import com.fucar.customer.dto.response.CustomerAuthResponse;
import com.fucar.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalCustomerController {
    private final CustomerService customerService;

    public InternalCustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/auth/customer-login")
    public CustomerAuthResponse login(@Valid @RequestBody CustomerLoginRequest request) {
        return customerService.login(request);
    }

    @GetMapping("/customers/{id}/exists")
    public boolean exists(@PathVariable Long id) {
        return customerService.existsActiveById(id);
    }
}
