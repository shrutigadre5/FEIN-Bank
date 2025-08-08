package com.example.demo.service;

import com.example.demo.entities.Payee;
import com.example.demo.repo.PayeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayeeServiceImpl implements PayeeService {

    private final PayeeRepository payeeRepository;

    public PayeeServiceImpl(PayeeRepository payeeRepository) {
        this.payeeRepository = payeeRepository;
    }

    @Override
    public List<Payee> getPayeesByCustomerAndAccount(Long customerId, Long accountNumber) {
        // Since we removed sender account number from payee, just return all payees for
        // customer
        return payeeRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Payee> getPayeesByCustomer(Long customerId) {
        return payeeRepository.findByCustomerId(customerId);
    }
}
