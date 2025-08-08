// File: com.example.demo.service.PayeeService.java
package com.example.demo.service;

import com.example.demo.entities.Payee;
import java.util.List;

public interface PayeeService {
    List<Payee> getPayeesByCustomerAndAccount(Long customerId, Long payeeAccountNumber);

    List<Payee> getPayeesByCustomer(Long customerId);
}
