package com.example.demo.service;

import com.example.demo.dto.TransferRequest;
import com.example.demo.entities.Transactions;

public interface TransactionService {
    Transactions performSelfDeposit(TransferRequest request);

    Transactions performPayeeTransfer(TransferRequest request);

    Transactions performManualTransfer(TransferRequest request);
}
