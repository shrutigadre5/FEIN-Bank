// File: com.example.demo.repo.PayeeRepository.java
package com.example.demo.repo;

import com.example.demo.entities.Payee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayeeRepository extends JpaRepository<Payee, Long> {

    // Fetch all payees added by a customer (irrespective of account used)
    List<Payee> findByCustomerId(Long customerId);

    // Fetch payees added for a specific payee account number
    List<Payee> findByPayeeAccountNumber(Long payeeAccountNumber);

    // Fetch payee added by a customer to a specific payee account number
    List<Payee> findByCustomerIdAndPayeeAccountNumber(Long customerId, Long payeeAccountNumber);
}
