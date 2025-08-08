package com.example.demo.repo;

import com.example.demo.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Find account by account number
    Optional<Account> findByAccountNo(Long accountNo);

    // Find all accounts for a specific customer
    List<Account> findByCustomerId(Long customerId);

    // Find accounts by customer ID and status
    List<Account> findByCustomerIdAndStatus(Long customerId, com.example.demo.entities.AccountStatus status);

    // Check if account exists and is active
    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.accountNo = :accountNo AND a.status = 'active'")
    boolean existsByAccountNoAndStatusActive(Long accountNo);

    // Get account balance
    @Query("SELECT a.balance FROM Account a WHERE a.accountNo = :accountNo")
    Double getBalanceByAccountNo(Long accountNo);
}
