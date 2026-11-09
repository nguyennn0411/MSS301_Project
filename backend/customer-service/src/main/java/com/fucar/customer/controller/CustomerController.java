package com.fucar.customer.controller;

import com.fucar.customer.dto.request.RegisterCustomerRequest;
import com.fucar.customer.dto.request.UpdateCustomerRequest;
import com.fucar.customer.dto.response.CustomerResponse;
import com.fucar.customer.exception.ApiException;
import com.fucar.customer.service.CustomerService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> register(@Valid @RequestBody RegisterCustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.register(request));
    }

    @GetMapping
    public List<CustomerResponse> getAll() {
        return customerService.getAll();
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable Long id) {
        return customerService.getById(id);
    }

    @GetMapping({"/profile", "/me"})
    public CustomerResponse getProfile(@RequestHeader("X-User-Id") Long userId) {
        return customerService.getById(userId);
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody UpdateCustomerRequest request) {
        return customerService.update(id, request);
    }

    @PutMapping({"/profile", "/me"})
    public CustomerResponse updateProfile(@RequestHeader("X-User-Id") Long userId,
                                          @Valid @RequestBody UpdateCustomerRequest request) {
        return customerService.update(userId, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> disable(@PathVariable Long id) {
        customerService.disable(id);
        return ResponseEntity.noContent().build();
    }
}
