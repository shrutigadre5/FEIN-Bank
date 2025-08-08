package com.example.demo.service;

import com.example.demo.client.PayeeClient;
import com.example.demo.entities.Payee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayeeServiceCaller {

    private final PayeeClient payeeClient;

    @Autowired
    public PayeeServiceCaller(PayeeClient payeeClient) {
        this.payeeClient = payeeClient;
    }

    public List<Payee> getPayeesByCustomerId(long customerId) {
        return payeeClient.getPayeesByCustomer_Id(customerId);
    }

    public Payee addPayee(Payee payee) {
        return payeeClient.addPayee(payee);
    }

    

    public void deletePayee(Long payeeId) {
        payeeClient.deletePayee(payeeId);
    }
}
