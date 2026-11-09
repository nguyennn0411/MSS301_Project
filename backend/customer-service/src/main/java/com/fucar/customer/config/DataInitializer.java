package com.fucar.customer.config;

import com.fucar.customer.entity.Customer;
import com.fucar.customer.repository.CustomerRepository;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        saveCustomer("Nguyen Van An", "0901000001", "customer1@gmail.com",
                LocalDate.of(2000, 1, 10), "Ho Chi Minh City", "079200000001", true);
        saveCustomer("Tran Thi Bich", "0901000002", "customer2@gmail.com",
                LocalDate.of(1999, 5, 20), "Ha Noi", "001199000002", true);
        saveCustomer("Le Minh Chau", "0901000003", "customer3@gmail.com",
                LocalDate.of(2001, 3, 15), "Da Nang", "048201000003", true);
        saveCustomer("Pham Quoc Duy", "0901000004", "customer4@gmail.com",
                LocalDate.of(1998, 11, 2), "Can Tho", "092198000004", true);
        saveCustomer("Hoang Gia Huy", "0901000005", "customer5@gmail.com",
                LocalDate.of(1997, 8, 25), "Hai Phong", "031197000005", false);
    }

    private void saveCustomer(String name, String telephone, String email, LocalDate birthday,
                              String address, String identityCard, boolean status) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseGet(Customer::new);
        customer.setCustomerName(name);
        customer.setTelephone(telephone);
        customer.setEmail(email);
        customer.setCustomerBirthday(birthday);
        customer.setAddress(address);
        customer.setIdentityCard(identityCard);
        customer.setCustomerStatus(status);
        customer.setPassword(passwordEncoder.encode("123456"));
        customerRepository.save(customer);
    }
}
