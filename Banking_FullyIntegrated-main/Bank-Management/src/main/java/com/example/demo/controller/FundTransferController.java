package com.example.demo.controller;

import com.example.demo.dto.TransferRequest;
import com.example.demo.entities.Transactions;
import com.example.demo.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/fund-transfer")
public class FundTransferController {

    private final TransactionService transactionService;

    public FundTransferController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Self-deposit endpoint
    @PostMapping("/{customerId}/{accountNumber}/self-deposit")
    public ResponseEntity<?> selfDeposit(
            @PathVariable Long customerId,
            @PathVariable String accountNumber,
            @RequestBody TransferRequest request) {
        try {
            request.setCustomerId(customerId);
            request.setSenderAccount(Long.parseLong(accountNumber));

            Transactions txn = transactionService.performSelfDeposit(request);
            return ResponseEntity.ok(txn);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Deposit failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Payee Transfer endpoint with payee_id path variable
    @PostMapping("/{customerId}/{accountNumber}/payee-transfer/{payee_id}")
    public ResponseEntity<?> payeeTransfer(
            @PathVariable Long customerId,
            @PathVariable String accountNumber,
            @PathVariable("payee_id") Long payeeId,
            @RequestBody TransferRequest request) {
        try {
            request.setCustomerId(customerId);
            request.setSenderAccount(Long.parseLong(accountNumber));
            request.setPayeeId(payeeId);

            Transactions txn = transactionService.performPayeeTransfer(request);
            return ResponseEntity.ok(txn);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Payee transfer failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Manual transfer endpoint
    @PostMapping("/{customerId}/{accountNumber}/manual-transfer")
    public ResponseEntity<?> manualTransfer(
            @PathVariable Long customerId,
            @PathVariable String accountNumber,
            @RequestBody TransferRequest request) {
        try {
            request.setCustomerId(customerId);
            request.setSenderAccount(Long.parseLong(accountNumber));

            Transactions txn = transactionService.performManualTransfer(request);
            return ResponseEntity.ok(txn);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Manual transfer failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
