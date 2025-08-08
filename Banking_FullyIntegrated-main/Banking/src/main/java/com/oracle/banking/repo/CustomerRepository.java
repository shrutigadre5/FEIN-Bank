package com.oracle.banking.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oracle.banking.entities.Customer;

public interface CustomerRepository 
	extends JpaRepository<Customer, Long>{

	long count();

}
