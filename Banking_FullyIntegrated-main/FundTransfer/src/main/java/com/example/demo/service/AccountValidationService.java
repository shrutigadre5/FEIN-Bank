package com.example.demo.service;

import java.util.Map;

public interface AccountValidationService {

    /**
     * Validates IFSC code format and checks if it exists
     * 
     * @param ifscCode the IFSC code to validate
     * @return validation result with bank details
     */
    Map<String, Object> validateIfscCode(String ifscCode);

    /**
     * Validates account number and returns account holder information
     * 
     * @param accountNumber the account number to validate
     * @param ifscCode      the IFSC code of the bank
     * @return account holder information
     */
    Map<String, Object> validateAccountAndGetHolderInfo(Long accountNumber, String ifscCode);

    /**
     * Validates both IFSC and account number together
     * 
     * @param accountNumber the account number
     * @param ifscCode      the IFSC code
     * @return combined validation result with account holder info
     */
    Map<String, Object> validateAccountWithIfsc(Long accountNumber, String ifscCode);
}
