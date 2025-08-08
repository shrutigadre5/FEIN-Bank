package com.example.demo.service;

import com.example.demo.feign.AccountClient;
import com.example.demo.entities.Account;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class AccountValidationServiceImpl implements AccountValidationService {

    private final AccountClient accountClient;

    public AccountValidationServiceImpl(AccountClient accountClient) {
        this.accountClient = accountClient;
    }

    @Override
    public Map<String, Object> validateIfscCode(String ifscCode) {
        Map<String, Object> result = new HashMap<>();

        if (ifscCode == null || ifscCode.trim().isEmpty()) {
            result.put("valid", false);
            result.put("error", "IFSC code cannot be empty");
            return result;
        }

        // Normalize
        ifscCode = ifscCode.trim().toUpperCase();

        // IFSC pattern
        Pattern ifscPattern = Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");

        if (!ifscPattern.matcher(ifscCode).matches()) {
            result.put("valid", false);
            result.put("error", "Invalid IFSC code format. Expected format: ABCD0123456 (4 letters + 0 + 6 alphanumeric)");
            return result;
        }

        // Extract bank code
        String bankCode = ifscCode.substring(0, 4);

        // Lookup bank info
        Map<String, String> bankInfo = getBankInfo(bankCode);
        String bankName = bankInfo != null ? bankInfo.getOrDefault("bankName", "Unknown") : "Unknown";
        String bankFullName = bankInfo != null ? bankInfo.getOrDefault("bankFullName", "Unknown Bank") : "Unknown Bank";

        result.put("valid", true);
        result.put("ifscCode", ifscCode);
        result.put("bankCode", bankCode);
        result.put("bankName", bankName);
        result.put("bankFullName", bankFullName);

        return result;
    }


    @Override
    public Map<String, Object> validateAccountAndGetHolderInfo(Long accountNumber, String ifscCode) {
        Map<String, Object> result = new HashMap<>();

        if (accountNumber == null) {
            result.put("valid", false);
            result.put("error", "Account number cannot be null");
            return result;
        }

        try {
            // First validate IFSC code
            Map<String, Object> ifscValidation = validateIfscCode(ifscCode);
            if (!(Boolean) ifscValidation.get("valid")) {
                return ifscValidation; // Return IFSC validation error
            }

            // Try to get account information from Bank Management system
            Account account = accountClient.getAccountById(accountNumber);

            if (account == null) {
                result.put("valid", false);
                result.put("error", "Account not found");
                return result;
            }

            // For demonstration, we'll create a mock account holder name
            // In a real system, this would come from the account data
            String accountHolderName = generateAccountHolderName(account);

            result.put("valid", true);
            result.put("accountNumber", accountNumber);
            result.put("accountHolderName", accountHolderName);
            result.put("accountType",
                    account.getAccountType() != null ? account.getAccountType().toString() : "SAVINGS");
            result.put("balance", account.getBalance());
            result.put("status", account.getStatus() != null ? account.getStatus().toString() : "ACTIVE");
            result.put("ifscCode", ifscCode);
            result.put("bankName", ifscValidation.get("bankName"));

            return result;

        } catch (Exception e) {
            result.put("valid", false);
            result.put("error", "Failed to validate account: " + e.getMessage());
            return result;
        }
    }

    @Override
    public Map<String, Object> validateAccountWithIfsc(Long accountNumber, String ifscCode) {
        Map<String, Object> result = new HashMap<>();

        // Validate both IFSC and account
        Map<String, Object> accountValidation = validateAccountAndGetHolderInfo(accountNumber, ifscCode);

        if (!(Boolean) accountValidation.get("valid")) {
            return accountValidation;
        }

        // Additional validation: Check if account belongs to the bank indicated by IFSC
        // This is a business logic check that can be customized
        String bankCode = ifscCode.substring(0, 4);
        boolean accountBelongsToBank = validateAccountBelongsToBank(accountNumber, bankCode);

        if (!accountBelongsToBank) {
            result.put("valid", false);
            result.put("error", "Account number does not belong to the bank indicated by IFSC code");
            return result;
        }

        return accountValidation;
    }

    /**
     * Get bank information based on bank code
     */
    private Map<String, String> getBankInfo(String bankCode) {
        Map<String, String> bankInfo = new HashMap<>();

        // Common Indian bank codes - in a real system, this would be from a database
        switch (bankCode) {
            case "HDFC":
                bankInfo.put("bankName", "HDFC Bank");
                bankInfo.put("bankFullName", "Housing Development Finance Corporation Bank");
                break;
            case "ICIC":
                bankInfo.put("bankName", "ICICI Bank");
                bankInfo.put("bankFullName", "Industrial Credit and Investment Corporation of India Bank");
                break;
            case "SBIN":
                bankInfo.put("bankName", "State Bank of India");
                bankInfo.put("bankFullName", "State Bank of India");
                break;
            case "AXIS":
                bankInfo.put("bankName", "Axis Bank");
                bankInfo.put("bankFullName", "Axis Bank Limited");
                break;
            case "PUNB":
                bankInfo.put("bankName", "Punjab National Bank");
                bankInfo.put("bankFullName", "Punjab National Bank");
                break;
            case "BARB":
                bankInfo.put("bankName", "Bank of Baroda");
                bankInfo.put("bankFullName", "Bank of Baroda");
                break;
            case "CNRB":
                bankInfo.put("bankName", "Canara Bank");
                bankInfo.put("bankFullName", "Canara Bank");
                break;
            case "UBIN":
                bankInfo.put("bankName", "Union Bank of India");
                bankInfo.put("bankFullName", "Union Bank of India");
                break;
            default:
                bankInfo.put("bankName", "Unknown Bank");
                bankInfo.put("bankFullName", "Bank with code: " + bankCode);
                break;
        }

        return bankInfo;
    }

    /**
     * Generate account holder name (mock implementation)
     * In a real system, this would fetch from customer data
     */
    private String generateAccountHolderName(Account account) {
        // This is a placeholder implementation
        // In a real system, you would fetch this from customer service or database
        String[] firstNames = { "Rajesh", "Priya", "Amit", "Sneha", "Vikram", "Anita", "Suresh", "Meera" };
        String[] lastNames = { "Sharma", "Patel", "Kumar", "Singh", "Gupta", "Verma", "Jain", "Agarwal" };

        // Use account number to generate consistent name
        int firstNameIndex = (int) (account.getAccountNo() % firstNames.length);
        int lastNameIndex = (int) ((account.getAccountNo() / 10) % lastNames.length);

        return firstNames[firstNameIndex] + " " + lastNames[lastNameIndex];
    }

    /**
     * Validate if account belongs to the specified bank
     * This is a mock implementation
     */
    private boolean validateAccountBelongsToBank(Long accountNumber, String bankCode) {
        // In a real implementation, this would check against bank databases
        // For now, we'll assume all accounts are valid for demonstration
        return true;
    }
}
