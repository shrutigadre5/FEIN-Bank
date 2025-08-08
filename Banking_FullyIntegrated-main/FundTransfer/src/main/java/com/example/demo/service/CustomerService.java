package com.example.demo.service;

import com.example.demo.entities.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Optional<Customer> findById(Long customerId);

    List<Customer> findAll();

    boolean existsById(Long customerId);
}
