package com.fucar.renting.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerClient {
    @GetMapping("/internal/customers/{id}/exists")
    Boolean existsActiveCustomer(@PathVariable("id") Long id);
}
