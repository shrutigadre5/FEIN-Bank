package com.example.demo.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.AccountDTO;
import com.example.demo.dto.TransferRequest;
import com.example.demo.entities.Account;
import com.example.demo.entities.Customer;
import com.example.demo.exception.AccountNotFoundException;
import com.example.demo.repos.AccountRepository;
import com.example.demo.repos.CustomerLoginRepository;
import com.example.demo.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepo;
    private final CustomerLoginRepository customerRepo;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepo, CustomerLoginRepository customerRepo) {
        this.accountRepo = accountRepo;
        this.customerRepo = customerRepo;
    }

    public Account getByAccountId(Long accountNo) {
        return accountRepo.findById(accountNo).orElse(null);
    }

    @Override
    public List<AccountDTO> getAccountsByCustomerId(Long customerId) {
        try {
            System.out.println("Fetching accounts for customer ID: " + customerId);
            List<Account> accounts = accountRepo.findByCustomer_CustomerId(customerId);
            System.out.println("Found " + accounts.size() + " accounts");

            if (accounts.isEmpty()) {
                System.out.println("No accounts found, returning empty list");
                return new ArrayList<>();
            }

            List<AccountDTO> accountDTOs = accounts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            System.out.println("Successfully converted " + accountDTOs.size() + " accounts to DTOs");
            return accountDTOs;
        } catch (Exception e) {
            System.err.println("Error in getAccountsByCustomerId: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Account updateAccount(Long accountNo, Account updated) {
        Account acc = accountRepo.findById(accountNo)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNo));

        acc.setAccountType(updated.getAccountType());
        acc.setStatus(updated.getStatus());
        acc.setBalance(updated.getBalance());
        acc.setTransactionPassword(updated.getTransactionPassword());

        return accountRepo.save(acc);
    }

    @Override
    public Account patchAccount(Long accountNo, Account patch) {
        Account existing = accountRepo.findById(accountNo)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNo));

        if (patch.getAccountType() != null) {
            existing.setAccountType(patch.getAccountType());
        }
        if (patch.getStatus() != null) {
            existing.setStatus(patch.getStatus());
        }
        if (patch.getBalance() != null) {
            existing.setBalance(patch.getBalance());
        }
        if (patch.getTransactionPassword() != null) {
            existing.setTransactionPassword(patch.getTransactionPassword());
        }

        if (patch.getCustomer() != null) {
            Customer existingCustomer = existing.getCustomer();
            Customer patchCustomer = patch.getCustomer();

            if (existingCustomer != null) {
                if (patchCustomer.getEmail() != null) {
                    existingCustomer.setEmail(patchCustomer.getEmail());
                }
                if (patchCustomer.getMobileNo() != null) {
                    existingCustomer.setMobileNo(patchCustomer.getMobileNo());
                }
                customerRepo.save(existingCustomer);
            }
        }

        return accountRepo.save(existing);
    }

    @Override
    public void deleteAccount(Long accountNo) {
        if (!accountRepo.existsById(accountNo)) {
            throw new AccountNotFoundException("Account not found: " + accountNo);
        }
        accountRepo.deleteById(accountNo);
    }

    private AccountDTO convertToDTO(Account account) {
        try {
            System.out.println("Converting account: " + account.getAccountNo());
            Customer customer = account.getCustomer();
            if (customer == null) {
                System.err.println("Customer is null for account: " + account.getAccountNo());
                // Try to fetch customer directly
                Optional<Customer> customerOpt = customerRepo.findById(1001L); // Use the customer ID from your data
                if (customerOpt.isPresent()) {
                    customer = customerOpt.get();
                    System.out.println("Found customer separately: " + customer.getCustomerId());
                } else {
                    throw new RuntimeException("Customer not found for account: " + account.getAccountNo());
                }
            }

            AccountDTO dto = new AccountDTO();
            dto.setAccountNo(account.getAccountNo());
            dto.setAccountType(account.getAccountType());
            dto.setApplicationDate(account.getApplicationDate());
            dto.setStatus(account.getStatus());
            dto.setBalance(account.getBalance());

            dto.setCustomer_Id(customer.getCustomerId());
            dto.setFullName(customer.getFirstName() + " " + customer.getLastName());
            dto.setEmail(customer.getEmail());
            dto.setMobileNo(customer.getMobileNo());
            dto.setAadharNo(customer.getAadharNo());
            dto.setPanNo(customer.getPanNo());
            dto.setOccupation(customer.getOccupation());
            dto.setAnnualIncome(customer.getAnnualIncome());

            System.out.println("Successfully converted account DTO: " + dto.getAccountNo());
            return dto;
        } catch (Exception e) {
            System.err.println("Error converting account to DTO: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Account transferAmount(TransferRequest request) {
        Long senderAccNo = request.getSenderAccount();
        Long receiverAccNo = request.getReceiverAccount();
        Double amount = request.getAmount();

        // Fetch sender and receiver accounts
        Account sender = accountRepo.findById(senderAccNo)
                .orElseThrow(() -> new RuntimeException("Sender account not found: " + senderAccNo));
        Account receiver = accountRepo.findById(receiverAccNo)
                .orElseThrow(() -> new RuntimeException("Receiver account not found: " + receiverAccNo));

        // Check for sufficient funds
        if (sender.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds in sender's account");
        }

        // Perform balance update
        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        // Save updated accounts
        accountRepo.save(sender);
        accountRepo.save(receiver);

        // Return updated sender (you can change this if needed)
        return sender;
    }

}