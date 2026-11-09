package com.fucar.customer.service;

import com.fucar.customer.dto.request.CustomerLoginRequest;
import com.fucar.customer.dto.request.RegisterCustomerRequest;
import com.fucar.customer.dto.request.UpdateCustomerRequest;
import com.fucar.customer.dto.response.CustomerAuthResponse;
import com.fucar.customer.dto.response.CustomerResponse;
import java.util.List;

public interface CustomerService {
    CustomerResponse register(RegisterCustomerRequest request);

    CustomerAuthResponse login(CustomerLoginRequest request);

    List<CustomerResponse> getAll();

    CustomerResponse getById(Long id);

    CustomerResponse update(Long id, UpdateCustomerRequest request);

    void disable(Long id);

    boolean existsActiveById(Long id);
}
