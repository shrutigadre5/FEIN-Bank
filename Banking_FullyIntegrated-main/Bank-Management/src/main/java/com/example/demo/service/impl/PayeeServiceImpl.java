package com.example.demo.service.impl;

import com.example.demo.client.PayeeClient;
import com.example.demo.entities.Payee;
import com.example.demo.service.PayeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayeeServiceImpl implements PayeeService {

    private final PayeeClient payeeClient;

    @Autowired
    public PayeeServiceImpl(PayeeClient payeeClient) {
        this.payeeClient = payeeClient;
    }

    @Override
    public List<Payee> getPayeesByCustomerAndAccount(Long customerId, Long accountNumber) {
        // For now, we'll just fetch payees by customer ID
        // In a real implementation, you might want to filter by account number as well
        try {
            return payeeClient.getPayeesByCustomer_Id(customerId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch payees: " + e.getMessage());
        }
    }
}
