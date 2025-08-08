package com.example.demo.service.impl;

import com.example.demo.client.FundTransferClient;
import com.example.demo.dto.TransferRequest;
import com.example.demo.entities.Transactions;
import com.example.demo.repos.TransactionsRepository;
import com.example.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FundTransferServiceImpl implements TransactionService {

    // Make sure FundTransferClient is configured to use http://localhost:8083
    private final FundTransferClient fundTransferClient;
    private final TransactionsRepository transactionsRepository;

    @Autowired
    public FundTransferServiceImpl(FundTransferClient fundTransferClient,
            TransactionsRepository transactionsRepository) {
        this.fundTransferClient = fundTransferClient;
        this.transactionsRepository = transactionsRepository;
    }

    // Insert fields required for fund transfer:
    // - accountNumber (sender)
    // - amount
    // - customerId
    // - payeeId (for payee transfer)
    // These fields should be provided in TransferRequest

    @Override
    public Transactions performSelfDeposit(TransferRequest request) {
        try {
            // This will send accountNumber and amount to service running on 8083
            ResponseEntity<?> response = fundTransferClient.selfDeposit(
                    request.getCustomerId(),
                    request.getSenderAccount(),
                    request);

            // Handle the response and return a Transactions object
            if (response.getStatusCode().is2xxSuccessful()) {
                Object responseBody = response.getBody();
                if (responseBody instanceof Transactions transaction) {
                    // Save the transaction to our database
                    transaction.setTransactionType("DEPOSIT");
                    transaction.setTransactionDate(LocalDateTime.now());
                    transaction.setStatus("SUCCESS");
                    return transactionsRepository.save(transaction);
                } else {
                    throw new RuntimeException("Unexpected response type from fund transfer service.");
                }
            } else {
                // Create and save a failed transaction record
                Transactions failedTransaction = new Transactions();
                failedTransaction.setCustomerId(request.getCustomerId());
                failedTransaction.setAccountNo(request.getSenderAccount());
                failedTransaction.setAmount(request.getAmount());
                failedTransaction.setTransactionType("DEPOSIT");
                failedTransaction.setTransactionDate(LocalDateTime.now());
                failedTransaction.setRemarks(request.getRemarks()); // fixed getter name
                failedTransaction.setStatus("FAILED");
                transactionsRepository.save(failedTransaction);

                throw new RuntimeException("Self deposit failed: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Self deposit failed: " + e.getMessage());
        }
    }

    @Override
    public Transactions performPayeeTransfer(TransferRequest request) {
        try {
            // This will send accountNumber, amount, payeeId to service running on 8083
            ResponseEntity<?> response = fundTransferClient.payeeTransfer(
                    request.getCustomerId(),
                    request.getSenderAccount(),
                    request.getPayeeId(),
                    request);

            // Handle the response and return a Transactions object
            if (response.getStatusCode().is2xxSuccessful()) {
                Object responseBody = response.getBody();
                if (responseBody instanceof Transactions transaction) {
                    // Save the transaction to our database
                    transaction.setTransactionType("TRANSFER");
                    transaction.setTransactionDate(LocalDateTime.now());
                    transaction.setStatus("SUCCESS");
                    return transactionsRepository.save(transaction);
                } else {
                    throw new RuntimeException("Unexpected response type from fund transfer service.");
                }
            } else {
                // Create and save a failed transaction record
                Transactions failedTransaction = new Transactions();
                failedTransaction.setCustomerId(request.getCustomerId());
                failedTransaction.setAccountNo(request.getSenderAccount());
                failedTransaction.setAmount(request.getAmount());
                failedTransaction.setTransactionType("TRANSFER");
                failedTransaction.setTransactionDate(LocalDateTime.now());
                failedTransaction.setRemarks(request.getRemarks()); // fixed getter name
                failedTransaction.setStatus("FAILED");
                transactionsRepository.save(failedTransaction);

                throw new RuntimeException("Payee transfer failed: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Payee transfer failed: " + e.getMessage());
        }
    }

    @Override
    public Transactions performManualTransfer(TransferRequest request) {
        try {
            // This will send accountNumber and amount to service running on 8083
            ResponseEntity<?> response = fundTransferClient.manualTransfer(
                    request.getCustomerId(),
                    request.getSenderAccount(),
                    request);

            // Handle the response and return a Transactions object
            if (response.getStatusCode().is2xxSuccessful()) {
                Object responseBody = response.getBody();
                if (responseBody instanceof Transactions transaction) {
                    // Save the transaction to our database
                    transaction.setTransactionType("TRANSFER");
                    transaction.setTransactionDate(LocalDateTime.now());
                    transaction.setStatus("SUCCESS");
                    return transactionsRepository.save(transaction);
                } else {
                    throw new RuntimeException("Unexpected response type from fund transfer service.");
                }
            } else {
                // Create and save a failed transaction record
                Transactions failedTransaction = new Transactions();
                failedTransaction.setCustomerId(request.getCustomerId());
                failedTransaction.setAccountNo(request.getSenderAccount());
                failedTransaction.setAmount(request.getAmount());
                failedTransaction.setTransactionType("TRANSFER");
                failedTransaction.setTransactionDate(LocalDateTime.now());
                failedTransaction.setRemarks(request.getRemarks()); // fixed getter name
                failedTransaction.setStatus("FAILED");
                transactionsRepository.save(failedTransaction);

                throw new RuntimeException("Manual transfer failed: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Manual transfer failed: " + e.getMessage());
        }
    }
}
