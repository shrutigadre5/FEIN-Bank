package com.example.demo.controller;

import com.example.demo.dto.TransactionRequest;
import com.example.demo.entities.Account;
import com.example.demo.entities.Customer;
import com.example.demo.entities.Payee;
import com.example.demo.entities.PaymentMethod;
import com.example.demo.entities.Transactions;
import com.example.demo.service.AccountService;
import com.example.demo.service.AccountValidationService;
import com.example.demo.service.CustomerService;
import com.example.demo.service.PayeeService;
import com.example.demo.service.TransactionService;
import com.example.exception.AccountNotFoundException;
import com.example.exception.TransactionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;
    private final CustomerService customerService;
    private final AccountService accountService;
    private final PayeeService payeeService;
    private final AccountValidationService accountValidationService;

    public TransactionController(TransactionService transactionService,
            CustomerService customerService,
            AccountService accountService,
            PayeeService payeeService,
            AccountValidationService accountValidationService) {
        this.transactionService = transactionService;
        this.customerService = customerService;
        this.accountService = accountService;
        this.payeeService = payeeService;
        this.accountValidationService = accountValidationService;
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<?> getAccountByNumber(@PathVariable String accountNumber) {
        try {
            System.out.println("DEBUG: Looking for account: " + accountNumber);
            Long accountNo = Long.valueOf(accountNumber);
            System.out.println("DEBUG: Converted to Long: " + accountNo);

            Optional<Account> accountOpt = accountService.findByAccountNo(accountNo);
            System.out.println("DEBUG: Account found: " + accountOpt.isPresent());

            if (!accountOpt.isPresent()) {
                // Let's also check what accounts exist
                List<Account> allAccounts = accountService.findAll();
                System.out.println("DEBUG: Total accounts in DB: " + allAccounts.size());
                for (Account acc : allAccounts) {
                    System.out.println("DEBUG: Account in DB: " + acc.getAccountNo());
                }

                Map<String, String> error = new HashMap<>();
                error.put("error", "Account not found: " + accountNumber);
                return ResponseEntity.badRequest().body(error);
            }

            Account account = accountOpt.get();
            Optional<Customer> customerOpt = customerService.findById(account.getCustomerId());

            if (!customerOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Customer not found for account: " + accountNumber);
                return ResponseEntity.badRequest().body(error);
            }

            Customer customer = customerOpt.get();

            // Build account response
            Map<String, Object> result = new HashMap<>();
            result.put("accountNumber", account.getAccountNo().toString());
            result.put("accountNo", account.getAccountNo().toString());
            result.put("accountType", account.getAccountType().toString());
            result.put("balance", account.getBalance());
            result.put("status", account.getStatus().toString());

            // Add customer information
            Map<String, Object> customerInfo = new HashMap<>();
            customerInfo.put("customerId", customer.getCustomerId());
            customerInfo.put("firstName", customer.getFirstName());
            customerInfo.put("lastName", customer.getLastName());
            customerInfo.put("email", customer.getEmail());

            result.put("customerInfo", customerInfo);

            return ResponseEntity.ok(result);
        } catch (NumberFormatException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid account number format: " + accountNumber);
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            System.out.println("DEBUG: Exception occurred: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load account: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Debug endpoint to list all accounts in the database
    @GetMapping("/debug/accounts")
    public ResponseEntity<?> getAllAccounts() {
        try {
            List<Account> accounts = accountService.findAll(); // We'll need to add this method
            List<Map<String, Object>> accountList = new ArrayList<>();

            for (Account account : accounts) {
                Map<String, Object> accountData = new HashMap<>();
                accountData.put("accountNo", account.getAccountNo());
                accountData.put("accountType", account.getAccountType().toString());
                accountData.put("balance", account.getBalance());
                accountData.put("customerId", account.getCustomerId());
                accountData.put("status", account.getStatus().toString());
                accountList.add(accountData);
            }

            return ResponseEntity.ok(accountList);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load all accounts: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Payee endpoints
    @GetMapping("/payees/customer/{customerId}")
    public ResponseEntity<?> getPayeesByCustomer(@PathVariable Long customerId) {
        try {
            System.out.println("DEBUG: Fetching payees for customer ID: " + customerId);
            List<Payee> payees = payeeService.getPayeesByCustomer(customerId);
            System.out.println("DEBUG: Found " + payees.size() + " payees for customer " + customerId);
            return ResponseEntity.ok(payees);
        } catch (Exception e) {
            System.err.println("ERROR: Failed to load payees for customer " + customerId + ": " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load payees: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{customerId}/{accountNumber}/payees")
    public ResponseEntity<?> getPayeesByCustomerAndAccount(@PathVariable Long customerId,
            @PathVariable Long accountNumber) {
        try {
            List<Payee> payees = payeeService.getPayeesByCustomerAndAccount(customerId, accountNumber);
            return ResponseEntity.ok(payees);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load payees: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Validation endpoints
    @GetMapping("/validate-ifsc/{ifscCode}")
    public ResponseEntity<?> validateIfsc(@PathVariable String ifscCode) {
        try {
            Map<String, Object> result = accountValidationService.validateIfscCode(ifscCode);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "IFSC validation failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/validate-account/{accountNumber}/ifsc/{ifscCode}")
    public ResponseEntity<?> validateAccount(@PathVariable Long accountNumber, @PathVariable String ifscCode) {
        try {
            Map<String, Object> result = accountValidationService.validateAccountWithIfsc(accountNumber, ifscCode);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Account validation failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/account-info/{accountNumber}")
    public ResponseEntity<?> getAccountInfo(@PathVariable Long accountNumber) {
        try {
            Map<String, Object> result = transactionService.getAccountHolderInfo(accountNumber);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get account info: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Transaction endpoints
    @PostMapping("/{customerId}/{accountNumber}/self-deposit")
    public ResponseEntity<?> performSelfDeposit(@PathVariable Long customerId,
            @PathVariable Long accountNumber,
            @RequestBody Map<String, Object> requestBody) {
        try {
            TransactionRequest request = new TransactionRequest();
            request.setCustomerId(customerId);
            request.setAccountNumber(accountNumber);

            // Handle amount conversion safely
            Object amountObj = requestBody.get("amount");
            Double amount;
            if (amountObj instanceof Number) {
                amount = ((Number) amountObj).doubleValue();
            } else if (amountObj instanceof String) {
                amount = Double.parseDouble((String) amountObj);
            } else {
                throw new IllegalArgumentException("Invalid amount format");
            }
            request.setAmount(BigDecimal.valueOf(amount));

            request.setPaymentMethod(PaymentMethod.valueOf((String) requestBody.get("paymentMethod")));
            request.setRemarks((String) requestBody.get("remarks"));
            request.setTransactionPassword((String) requestBody.get("transactionPassword"));

            Transactions transaction = transactionService.performSelfDeposit(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("transactionId", transaction.getTransactionId());
            response.put("message", "Self deposit completed successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Self deposit failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{customerId}/{accountNumber}/payee-transfer/{payeeId}")
    public ResponseEntity<?> performPayeeTransfer(@PathVariable Long customerId,
            @PathVariable Long accountNumber,
            @PathVariable Long payeeId,
            @RequestBody Map<String, Object> requestBody) {
        try {
            System.out.println("DEBUG: Payee transfer request received");
            System.out.println("DEBUG: Customer ID: " + customerId);
            System.out.println("DEBUG: Account Number: " + accountNumber);
            System.out.println("DEBUG: Payee ID: " + payeeId);
            System.out.println("DEBUG: Request body: " + requestBody);

            TransactionRequest request = new TransactionRequest();
            request.setCustomerId(customerId);
            request.setAccountNumber(accountNumber);
            request.setPayeeId(payeeId);

            // Handle amount conversion safely
            Object amountObj = requestBody.get("amount");
            if (amountObj == null) {
                throw new IllegalArgumentException("Amount is required");
            }
            Double amount;
            if (amountObj instanceof Number) {
                amount = ((Number) amountObj).doubleValue();
            } else if (amountObj instanceof String) {
                amount = Double.parseDouble((String) amountObj);
            } else {
                throw new IllegalArgumentException("Invalid amount format");
            }
            request.setAmount(BigDecimal.valueOf(amount));

            String paymentMethodStr = (String) requestBody.get("paymentMethod");
            if (paymentMethodStr == null) {
                throw new IllegalArgumentException("Payment method is required");
            }
            request.setPaymentMethod(PaymentMethod.valueOf(paymentMethodStr));

            request.setRemarks((String) requestBody.get("remarks"));

            String transactionPassword = (String) requestBody.get("transactionPassword");
            if (transactionPassword == null || transactionPassword.trim().isEmpty()) {
                throw new IllegalArgumentException("Transaction password is required");
            }
            request.setTransactionPassword(transactionPassword);

            System.out.println("DEBUG: All validations passed, calling service");

            Transactions transaction = transactionService.performPayeeTransfer(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("transactionId", transaction.getTransactionId());
            response.put("message", "Payee transfer completed successfully");

            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Number format error - " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid number format: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: Invalid argument - " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid input: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (AccountNotFoundException e) {
            System.out.println("ERROR: Account not found - " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Account not found: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (TransactionException e) {
            System.out.println("ERROR: Transaction error - " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Transaction error: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            System.out.println("ERROR: Unexpected error - " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/manual-transfer")
    public ResponseEntity<?> manualTransferGet() {
        Map<String, String> error = new HashMap<>();
        error.put("error",
                "Manual transfer endpoint only supports POST requests. Use POST method with proper request body.");
        return ResponseEntity.badRequest().body(error);
    }

    @PostMapping("/manual-transfer")
    public ResponseEntity<?> performManualTransfer(@RequestBody Map<String, Object> requestBody) {
        try {
            String fromAccountNumber = (String) requestBody.get("fromAccountNumber");
            String toAccountNumber = (String) requestBody.get("toAccountNumber");
            String recipientName = (String) requestBody.get("recipientName");
            String ifscCode = (String) requestBody.get("ifscCode");

            // Handle amount conversion safely
            Object amountObj = requestBody.get("amount");
            Double amount;
            if (amountObj instanceof Number) {
                amount = ((Number) amountObj).doubleValue();
            } else if (amountObj instanceof String) {
                amount = Double.parseDouble((String) amountObj);
            } else {
                throw new IllegalArgumentException("Invalid amount format");
            }

            String paymentMethod = (String) requestBody.get("paymentMethod");
            String remarks = (String) requestBody.get("remarks");
            String transactionPassword = (String) requestBody.get("transactionPassword");

            // Use customerId consistently
            Long customerId = null;
            Object customerIdObj = requestBody.get("customerId");
            if (customerIdObj instanceof Number) {
                customerId = ((Number) customerIdObj).longValue();
            } else if (customerIdObj instanceof String) {
                customerId = Long.parseLong((String) customerIdObj);
            } else {
                throw new IllegalArgumentException("customerId is required for transaction password validation");
            }

            // Call service to perform transfer and update balances
            Transactions transaction = transactionService.performManualTransfer(
                    fromAccountNumber, toAccountNumber, recipientName, ifscCode,
                    amount, paymentMethod, remarks, transactionPassword, customerId);

            // transactionService.performManualTransfer will:
            // - Validate transaction password
            // - Update sender/receiver balances
            // - Insert transaction record into transactions table

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("transactionId", transaction.getTransactionId());
            response.put("message", "Manual transfer completed successfully");
            response.put("customerId", customerId); // Use customerId consistently

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Manual transfer failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

}
