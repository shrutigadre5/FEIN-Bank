package com.example.demo.service;

import com.example.demo.entities.Transactions;
import com.example.demo.repos.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionHistoryService {

    @Autowired
    private TransactionsRepository transactionsRepository;

    public List<Transactions> getTransactionHistoryByAccountNo(Long accountNo) {
        return transactionsRepository.findByAccountNo(accountNo);
    }

    public List<Transactions> getTransactionHistoryByCustomerId(Long customerId) {
        return transactionsRepository.findByCustomerId(customerId);
    }

    public List<Transactions> getTransactionHistoryByAccountAndType(Long accountNo, String transactionType) {
        return transactionsRepository.findByAccountNoAndTransactionType(accountNo, transactionType);
    }
}
