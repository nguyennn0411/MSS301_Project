package com.fucar.customer.mapper;

import com.fucar.customer.dto.response.CustomerResponse;
import com.fucar.customer.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getTelephone(),
                customer.getEmail(),
                customer.getCustomerBirthday(),
                customer.getAddress(),
                customer.getIdentityCard(),
                customer.getCustomerStatus()
        );
    }
}
