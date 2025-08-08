package com.example.demo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.entity.Payee;
import com.example.demo.repository.PayeeRepository;

import java.util.Optional;
import java.sql.Timestamp;

@Service
public class PayeeService {
    private static final Logger logger = LoggerFactory.getLogger(PayeeService.class);
    
    @Autowired
    private PayeeRepository payeeRepo;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public boolean isCustomerValid(Long customerId) {
        String url = "http://localhost:8081/api/customers/" + customerId;
        try {
            logger.debug("Validating customer ID: {}", customerId);
            ResponseEntity<CustomerDTO> response = restTemplate.getForEntity(url, CustomerDTO.class);
            boolean isValid = response.getStatusCode().is2xxSuccessful();
            logger.debug("Customer validation result for {}: {}", customerId, isValid);
            return isValid;
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Customer not found: {}", customerId);
            return false;
        } catch (Exception e) {
            logger.error("Error validating customer {}: {}", customerId, e.getMessage());
            return false;
        }
    }
    
    public Payee getPayeeById(Long id) {
        return payeeRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Payee not found with id: " + id));
    }
    
    public Payee addPayee(Payee payee) {
        logger.info("Adding new payee: {}", payee);
        
        // Validate customer
        if (!isCustomerValid(payee.getCustomerId())) {
            logger.error("Invalid customer ID: {}", payee.getCustomerId());
            throw new RuntimeException("Invalid Customer ID: " + payee.getCustomerId());
        }
        
        // Set default timestamp if not provided
        if (payee.getAddedAt() == null) {
            payee.setAddedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        }
        
        Payee savedPayee = payeeRepo.save(payee);
        logger.info("Successfully added payee with ID: {}", savedPayee.getPayeeId());
        return savedPayee;
    }

    public List<Payee> getPayeesByCustomerId(Long customerId) {
        try {
            return payeeRepo.findByCustomerId(customerId); // ✅ CORRECT
        } catch (Exception e) {
            logger.error("Error fetching payees for customer ID {}: {}", customerId, e.getMessage(), e);
            return List.of(); // Return empty list in case of failure
        }
    }

    public void deletePayee(Long payeeId) {
        payeeRepo.deleteById(payeeId);
    }

    public boolean validatePayee(Long customerId, Long accountNumber) {
        return payeeRepo.findByCustomerId(customerId).stream()
                .anyMatch(p -> p.getPayeeAccountNumber().equals(accountNumber));
    }


    public Payee updatePayee(Long id, Payee updatedPayee) {
        Optional<Payee> optionalPayee = payeeRepo.findById(id);

        if (optionalPayee.isPresent()) {
            Payee existingPayee = optionalPayee.get();
            existingPayee.setPayeeName(updatedPayee.getPayeeName());
            existingPayee.setNickname(updatedPayee.getNickname());
            existingPayee.setBankName(updatedPayee.getBankName());
            existingPayee.setIfscCode(updatedPayee.getIfscCode());
            existingPayee.setPayeeAccountNumber(updatedPayee.getPayeeAccountNumber());
            return payeeRepo.save(existingPayee);
        } else {
            throw new RuntimeException("Payee not found with id: " + id);
        }
    }
}
