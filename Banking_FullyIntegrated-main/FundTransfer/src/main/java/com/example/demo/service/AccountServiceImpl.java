package com.example.demo.service;

import com.example.demo.entities.Account;
import com.example.demo.repo.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Optional<Account> findByAccountNo(Long accountNo) {
        return accountRepository.findByAccountNo(accountNo);
    }

    @Override
    public List<Account> findByCustomerId(Long customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Double getBalanceByAccountNo(Long accountNo) {
        return accountRepository.getBalanceByAccountNo(accountNo);
    }

    @Override
    public boolean validateAccount(Long accountNo) {
        return accountRepository.existsByAccountNoAndStatusActive(accountNo);
    }

    @Override
    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }
}
