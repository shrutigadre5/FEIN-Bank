package com.example.demo.feign;

import com.example.demo.dto.TransactionRequest;
import com.example.demo.entities.Account;
import com.example.demo.entities.Transactions;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "Banking-Management", url = "http://localhost:8081")
public interface AccountClient {

    @PostMapping("/api/accounts/{accountNo}/transfer")
    Transactions transferFunds(
            @PathVariable("accountNo") Long accountNo,
            @RequestBody TransactionRequest request);

    @GetMapping("/api/accounts/{accountNumber}")
    Account getAccountById(@PathVariable("accountNumber") Long accountNumber);

    @PutMapping("/api/accounts/update-balance")
    Account updateAccountBalance(@RequestBody Account account);

    @PostMapping("/api/accounts/{accountNumber}/deposit")
    Account depositToAccount(
            @PathVariable("accountNumber") Long accountNumber,
            @RequestBody TransactionRequest request);

    @PostMapping("/api/accounts/{fromAccountNumber}/transfer-to/{toAccountNumber}")
    Transactions transferBetweenAccounts(
            @PathVariable("fromAccountNumber") Long fromAccountNumber,
            @PathVariable("toAccountNumber") Long toAccountNumber,
            @RequestBody TransactionRequest request);
}
