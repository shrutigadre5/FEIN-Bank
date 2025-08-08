package com.example.demo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entities.Customer;

import java.util.Optional;

public interface CustomerLoginRepository extends JpaRepository<Customer, Long> {
	Optional<Customer> findByCustomerId(Long customerId);

	// Add a method to find by customerId directly since it's the primary key
	default Optional<Customer> findById(Long customerId) {
		return findByCustomerId(customerId);
	}
}
