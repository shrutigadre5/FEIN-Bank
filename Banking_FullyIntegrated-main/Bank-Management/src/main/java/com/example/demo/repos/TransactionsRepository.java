package com.example.demo.repos;

import com.example.demo.entities.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Long> {
    List<Transactions> findByAccountNo(Long accountNo);

    List<Transactions> findByCustomerId(Long customerId);

    List<Transactions> findByAccountNoAndTransactionType(Long accountNo, String transactionType);
}
