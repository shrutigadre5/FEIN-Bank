package com.example.demo.service;

import com.example.demo.entities.Account;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    Optional<Account> findByAccountNo(Long accountNo);

    List<Account> findByCustomerId(Long customerId);

    List<Account> findAll();

    Double getBalanceByAccountNo(Long accountNo);

    boolean validateAccount(Long accountNo);

    Account updateAccount(Account account);
}
