package com.example.demo.controller;

import com.example.demo.dto.TransferRequest;
import com.example.demo.entities.Payee;
import com.example.demo.entities.Transactions;
import com.example.demo.service.PayeeService;
import com.example.demo.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final PayeeService payeeService;
    private final TransactionService transactionService;

    public TransactionController(PayeeService payeeService, TransactionService transactionService) {
        this.payeeService = payeeService;
        this.transactionService = transactionService;
    }

    // ✅ Fetch saved payees for a customer and account
    @GetMapping("/{customerId}/{senderaccountNumber}/payees")
    public ResponseEntity<?> getPayeesByCustomerAndAccount(
            @PathVariable Long customerId,
            @PathVariable Long senderaccountNumber) {
        try {
            List<Payee> payees = payeeService.getPayeesByCustomerAndAccount(customerId, senderaccountNumber);
            return ResponseEntity.ok(payees);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Error fetching payees: " + e.getMessage());
        }
    }

    // ✅ Perform a transfer from sender to receiver
    @PostMapping("/transfer")
    public ResponseEntity<?> performTransfer(@RequestBody TransferRequest request) {
        try {
            Transactions result = transactionService.performPayeeTransfer(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Transfer failed: " + e.getMessage());
        }
    }
}
