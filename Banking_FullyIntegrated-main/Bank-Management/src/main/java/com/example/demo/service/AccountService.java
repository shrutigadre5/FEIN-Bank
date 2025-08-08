package com.example.demo.service;

import com.example.demo.dto.AccountDTO;
import com.example.demo.dto.TransferRequest;
import com.example.demo.entities.Account;
import java.util.*;
public interface AccountService {
    List<AccountDTO> getAccountsByCustomerId(Long customerId);
    Account updateAccount(Long accountNo, Account updated);
    Account patchAccount(Long accountNo, Account patchData);
    void deleteAccount(Long accountNo);
    Account transferAmount(TransferRequest request);
    Account getByAccountId(Long accountNo);
}
