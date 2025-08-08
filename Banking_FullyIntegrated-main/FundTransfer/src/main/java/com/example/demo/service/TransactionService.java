package com.example.demo.service;

import com.example.demo.dto.TransactionRequest;
import com.example.demo.entities.Transactions;
import java.util.Map;

public interface TransactionService {
    Transactions performSelfDeposit(TransactionRequest request);

    Transactions performPayeeTransfer(TransactionRequest request);

    /**
     * Validates IFSC code format and returns bank information
     */
    Map<String, Object> validateIfscCode(String ifscCode);

    /**
     * Validates account number with IFSC and returns account holder information
     */
    Map<String, Object> validateAccountWithIfsc(Long accountNumber, String ifscCode);

    /**
     * Gets account holder information for a given account number
     */
    Map<String, Object> getAccountHolderInfo(Long accountNumber);

    /**
     * Performs manual transfer to any account using IFSC and account number
     */
    Transactions performManualTransfer(String fromAccountNumber, String toAccountNumber,
            String recipientName, String ifscCode,
            Double amount, String paymentMethod, String remarks, String transactionPassword, Long customerId);
}
