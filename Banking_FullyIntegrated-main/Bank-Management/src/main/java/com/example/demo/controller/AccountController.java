package com.example.demo.controller;

import com.example.demo.dto.AccountDTO;
import com.example.demo.dto.TransferRequest;
//import com.example.demo.dto.TransactionRequest;
import com.example.demo.entities.Account;
import com.example.demo.entities.Payee;
import com.example.demo.entities.Transactions;
import com.example.demo.service.AccountService;
import com.example.demo.service.PayeeServiceCaller;
import com.example.demo.service.TransactionHistoryService;
import com.example.demo.service.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService service;

    @Autowired
    private PayeeServiceCaller payeeService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionHistoryService transactionHistoryService;

    @Autowired
    private RestTemplate restTemplate; // Add this bean in your config if not present

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountDTO>> getAccountsForCustomer(@PathVariable Long customerId) {
        try {
            System.out.println("Controller: Fetching accounts for customer: " + customerId);
            List<AccountDTO> accounts = service.getAccountsByCustomerId(customerId);
            System.out.println("Controller: Returning " + accounts.size() + " accounts");
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            System.err.println("Controller error fetching accounts for customer " + customerId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @PutMapping("/{accountNo}")
    public Account updateAccount(@PathVariable Long accountNo, @RequestBody Account updated) {
        return service.updateAccount(accountNo, updated);
    }

    @DeleteMapping("/{accountNo}")
    public void deleteAccount(@PathVariable Long accountNo) {
        service.deleteAccount(accountNo);
    }
    @GetMapping("/{accountNo}")
    public ResponseEntity<Account> getAccountByAccountNo(@PathVariable Long accountNo) {
        Account account = service.getByAccountId(accountNo);
        if (account != null) {
            return ResponseEntity.ok(account);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customer/{customerId}/payees")
    public ResponseEntity<List<Payee>> getPayeesForCustomer(@PathVariable Long customerId) {
        List<Payee> payees = payeeService.getPayeesByCustomerId(customerId);
        return ResponseEntity.ok(payees);
    }

    @PostMapping("/customer/{customerId}/payees")
    public ResponseEntity<Payee> addPayeeForCustomer(@PathVariable Long customerId, @RequestBody Payee payee) {
        // Set the customer ID on the payee object
        payee.setCustomerId(customerId);
        Payee createdPayee = payeeService.addPayee(payee);
        return ResponseEntity.ok(createdPayee);
    }

    @DeleteMapping("/customer/{customerId}/payees/{payeeId}")
    public ResponseEntity<Void> deletePayeeForCustomer(@PathVariable Long customerId, @PathVariable Long payeeId) {
        payeeService.deletePayee(payeeId);
        return ResponseEntity.ok().build();
    }

    // Clean transferFunds endpoint that will be called by Feign from 8083
    @PostMapping("/{senderAccount}/transfer")
    public ResponseEntity<?> transferAmount(
            @PathVariable Long senderAccount,
            @RequestBody TransferRequest request) {
        try {
            // Ensure path variable matches request body
            if (!senderAccount.equals(request.getSenderAccount())) {
                return ResponseEntity.badRequest().body("Mismatch in sender account numbers.");
            }

            // Call service method to handle balance update between sender and receiver
            Account updatedSender = service.transferAmount(request); // <-- use your AccountService
            return ResponseEntity.ok(updatedSender);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Transfer failed: " + e.getMessage());
        }
    }

    @PostMapping("/{accountNo}/deposit")
    public ResponseEntity<?> depositFunds(
            @PathVariable Long accountNo,
            @RequestBody TransferRequest request) {
        try {
            // Set account number in the request
            request.setSenderAccount(accountNo);
            Transactions transaction = transactionService.performSelfDeposit(request);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Deposit failed: " + e.getMessage());
        }
    }

    // Transaction history methods
    @GetMapping("/{accountNo}/transactions")
    public ResponseEntity<List<Transactions>> getTransactionHistory(@PathVariable Long accountNo) {
        List<Transactions> transactions = transactionHistoryService.getTransactionHistoryByAccountNo(accountNo);
        return ResponseEntity.ok(transactions);
    }

    // Remove or comment out the transferMoney endpoint that forwards to 8083
    // @PostMapping("/{accountNo}/transfer")
    // public ResponseEntity<?> transferMoney(
    // @PathVariable Long accountNo,
    // @RequestBody TransferRequest transferRequest) {
    // // Forwarding logic removed
    // return ResponseEntity.status(501).body("Transfer is handled by Fund Transfer
    // Service UI.");
    // }
}
