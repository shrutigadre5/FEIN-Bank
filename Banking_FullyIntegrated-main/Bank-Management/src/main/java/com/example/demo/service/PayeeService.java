package com.example.demo.service;

import com.example.demo.entities.Payee;

import java.util.List;

public interface PayeeService {
    List<Payee> getPayeesByCustomerAndAccount(Long customerId, Long accountNumber);
}
