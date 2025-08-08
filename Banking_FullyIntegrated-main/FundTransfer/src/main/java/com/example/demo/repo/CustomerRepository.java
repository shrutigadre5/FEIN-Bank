package com.example.demo.repo;

import com.example.demo.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Find customer by customer ID
    Optional<Customer> findByCustomerId(Long customerId);

    // Find customer by email
    Optional<Customer> findByEmail(String email);

    // Find customer by phone
    Optional<Customer> findByPhone(String phone);

    // Check if customer exists
    boolean existsByCustomerId(Long customerId);
}
