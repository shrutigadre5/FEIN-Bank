package com.example.demo.controller;

import com.example.demo.dto.AccountDTO;
import com.example.demo.dto.CustomerLoginRequestDTO;
import com.example.demo.dto.CustomerLoginResponseDTO;
import com.example.demo.entities.Account;
import com.example.demo.entities.Customer;
import com.example.demo.repos.AccountRepository;
import com.example.demo.repos.CustomerLoginRepository;
import com.example.demo.service.CustomerLoginService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerLoginController {

    @Autowired
    private CustomerLoginService customerService;

    @Autowired
    private CustomerLoginRepository customerRepository;
    @Autowired
    private AccountRepository accountRepo;

    private AccountDTO convertToDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setAccountNo(account.getAccountNo());
        dto.setAccountType(account.getAccountType());
        dto.setBalance(account.getBalance());
        dto.setStatus(account.getStatus());
        return dto;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long customerId) {
        return customerRepository.findById(customerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public List<AccountDTO> getAccountsByCustomerId(Long customerId) {
        List<Account> accounts = accountRepo.findByCustomer_CustomerId(customerId);
        if (accounts.isEmpty()) {
            return new ArrayList<>(); // Instead of throwing exception
        }
        return accounts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/login")
    public CustomerLoginResponseDTO login(@RequestBody CustomerLoginRequestDTO loginRequest) {
        return customerService.login(loginRequest);
    }

    @GetMapping("/debug/customers")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

}
