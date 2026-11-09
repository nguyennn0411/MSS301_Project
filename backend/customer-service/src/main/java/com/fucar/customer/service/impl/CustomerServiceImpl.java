package com.fucar.customer.service.impl;

import com.fucar.customer.dto.request.CustomerLoginRequest;
import com.fucar.customer.dto.request.RegisterCustomerRequest;
import com.fucar.customer.dto.request.UpdateCustomerRequest;
import com.fucar.customer.dto.response.CustomerAuthResponse;
import com.fucar.customer.dto.response.CustomerResponse;
import com.fucar.customer.entity.Customer;
import com.fucar.customer.exception.ApiException;
import com.fucar.customer.mapper.CustomerMapper;
import com.fucar.customer.repository.CustomerRepository;
import com.fucar.customer.service.CustomerService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               CustomerMapper customerMapper,
                               PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CustomerResponse register(RegisterCustomerRequest request) {
        if (customerRepository.existsByEmail(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (customerRepository.existsByIdentityCard(request.identityCard())) {
            throw new ApiException(HttpStatus.CONFLICT, "Identity card already exists");
        }
        Customer customer = Customer.builder()
                .customerName(request.customerName())
                .telephone(request.telephone())
                .email(request.email())
                .customerBirthday(request.customerBirthday())
                .address(request.address())
                .identityCard(request.identityCard())
                .customerStatus(true)
                .password(passwordEncoder.encode(request.password()))
                .build();
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerAuthResponse login(CustomerLoginRequest request) {
        Customer customer = customerRepository.findByEmail(request.email())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!Boolean.TRUE.equals(customer.getCustomerStatus())
                || !passwordEncoder.matches(request.password(), customer.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        return new CustomerAuthResponse(customer.getCustomerId(), customer.getEmail(), customer.getCustomerName(), "CUSTOMER");
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAll() {
        return customerRepository.findAll().stream().map(customerMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {
        return customerMapper.toResponse(findCustomer(id));
    }

    @Override
    public CustomerResponse update(Long id, UpdateCustomerRequest request) {
        Customer customer = findCustomer(id);
        if (request.email() != null && !request.email().equalsIgnoreCase(customer.getEmail())
                && customerRepository.existsByEmail(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (request.customerName() != null) {
            customer.setCustomerName(request.customerName());
        }
        if (request.telephone() != null) {
            customer.setTelephone(request.telephone());
        }
        if (request.email() != null) {
            customer.setEmail(request.email());
        }
        if (request.customerBirthday() != null) {
            customer.setCustomerBirthday(request.customerBirthday());
        }
        if (request.address() != null) {
            customer.setAddress(request.address());
        }
        if (request.identityCard() != null && !request.identityCard().equals(customer.getIdentityCard())) {
            if (customerRepository.existsByIdentityCard(request.identityCard())) {
                throw new ApiException(HttpStatus.CONFLICT, "Identity card already exists");
            }
            customer.setIdentityCard(request.identityCard());
        }
        if (request.customerStatus() != null) {
            customer.setCustomerStatus(request.customerStatus());
        }
        if (request.password() != null && !request.password().isBlank()) {
            customer.setPassword(passwordEncoder.encode(request.password()));
        }
        return customerMapper.toResponse(customer);
    }

    @Override
    public void disable(Long id) {
        Customer customer = findCustomer(id);
        customer.setCustomerStatus(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsActiveById(Long id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        return customer != null && Boolean.TRUE.equals(customer.getCustomerStatus());
    }

    private Customer findCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found"));
    }
}
