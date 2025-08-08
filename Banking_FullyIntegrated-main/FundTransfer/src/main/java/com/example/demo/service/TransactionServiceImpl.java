package com.example.demo.service;

import com.example.demo.dto.TransactionRequest;
import com.example.demo.entities.*;
import com.example.demo.repo.TransactionRepository;
import com.example.exception.TransactionException;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionsRepository;
    private final CustomerService customerService;
    private final AccountService accountService;
    private final PayeeService payeeService;
    private final AccountValidationService accountValidationService;

    // Constructor with local database services
    public TransactionServiceImpl(TransactionRepository transactionsRepository,
            CustomerService customerService,
            AccountService accountService,
            PayeeService payeeService,
            AccountValidationService accountValidationService) {
        this.transactionsRepository = transactionsRepository;
        this.customerService = customerService;
        this.accountService = accountService;
        this.payeeService = payeeService;
        this.accountValidationService = accountValidationService;
    }

    @Override
    @Transactional
    public Transactions performSelfDeposit(TransactionRequest request) {
        // Validate transaction password first
        if (request.getTransactionPassword() == null || request.getTransactionPassword().trim().isEmpty()) {
            throw new TransactionException("Transaction password is required");
        }

        if (!validateTransactionPassword(request.getAccountNumber(), request.getTransactionPassword())) {
            throw new TransactionException("Invalid transaction password");
        }

        // Validate customer exists in local database
        try {
            Optional<Customer> customer = customerService.findById(request.getCustomerId());
            if (!customer.isPresent()) {
                throw new TransactionException("Customer not found in database");
            }
        } catch (Exception e) {
            throw new TransactionException("Failed to validate customer: " + e.getMessage());
        }

        // Validate account belongs to customer and exists in local database
        try {
            List<Account> customerAccounts = accountService.findByCustomerId(request.getCustomerId());
            boolean accountExists = customerAccounts.stream()
                    .anyMatch(account -> account.getAccountNo().equals(request.getAccountNumber()));

            if (!accountExists) {
                throw new TransactionException("Account does not belong to the specified customer");
            }
        } catch (Exception e) {
            throw new TransactionException("Failed to validate customer account: " + e.getMessage());
        }

        // Get account details from local database
        Optional<Account> accountOpt = accountService.findByAccountNo(request.getAccountNumber());
        if (!accountOpt.isPresent()) {
            throw new TransactionException("Account not found");
        }

        Account account = accountOpt.get();
        double oldBalance = account.getBalance();
        double newBalance = oldBalance + request.getAmount().doubleValue();
        account.setBalance(newBalance);

        // Update account balance in database
        accountService.updateAccount(account);

        Transactions txn = new Transactions();
        txn.setCustomerId(request.getCustomerId());
        txn.setAccountNo(request.getAccountNumber());
        txn.setSenderAccountNo(request.getAccountNumber());
        txn.setReceiverAccountNo(request.getAccountNumber()); // Self deposit
        txn.setTransactionType(TransactionType.CREDIT);
        txn.setPaymentMethod(request.getPaymentMethod());
        txn.setAmount(request.getAmount().doubleValue());
        txn.setStatus(TransactionStatus.COMPLETED);
        txn.setRemarks(request.getRemarks());
        txn.setTransactionDate(LocalDateTime.now());
        txn.setEntryTimestamp(LocalDateTime.now());
        txn.setBalanceAfterTxn(newBalance);

        Transactions savedTxn = transactionsRepository.save(txn);
        // Note: Statement recording removed as StatementClient is external
        return savedTxn;
    }

    @Override
    @Transactional
    public Transactions performPayeeTransfer(TransactionRequest request) {
        // Validate transaction password first
        if (request.getTransactionPassword() == null || request.getTransactionPassword().trim().isEmpty()) {
            throw new TransactionException("Transaction password is required");
        }

        if (!validateTransactionPassword(request.getAccountNumber(), request.getTransactionPassword())) {
            throw new TransactionException("Invalid transaction password");
        }

        // Validate customer exists in local database
        try {
            Optional<Customer> customer = customerService.findById(request.getCustomerId());
            if (!customer.isPresent()) {
                throw new TransactionException("Customer not found in database");
            }
        } catch (Exception e) {
            throw new TransactionException("Failed to validate customer: " + e.getMessage());
        }

        // Validate account belongs to customer and exists in local database
        try {
            List<Account> customerAccounts = accountService.findByCustomerId(request.getCustomerId());
            boolean accountExists = customerAccounts.stream()
                    .anyMatch(account -> account.getAccountNo().equals(request.getAccountNumber()));

            if (!accountExists) {
                throw new TransactionException("Account does not belong to the specified customer");
            }
        } catch (Exception e) {
            throw new TransactionException("Failed to validate customer account: " + e.getMessage());
        }

        // Get sender account from local database
        Optional<Account> senderAccountOpt = accountService.findByAccountNo(request.getAccountNumber());
        if (!senderAccountOpt.isPresent()) {
            throw new TransactionException("Sender account not found");
        }
        Account senderAccount = senderAccountOpt.get();

        BigDecimal amount = request.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionException("Amount must be greater than zero.");
        }

        if (BigDecimal.valueOf(senderAccount.getBalance()).compareTo(amount) < 0) {
            throw new TransactionException("Insufficient funds in sender's account.");
        }

        PaymentMethod method = request.getPaymentMethod();

        if (method == PaymentMethod.RTGS && amount.compareTo(new BigDecimal("200000")) < 0) {
            throw new TransactionException("RTGS requires minimum ₹2,00,000 transaction amount.");
        }

        // Get payee from local database
        List<Payee> payees = payeeService.getPayeesByCustomer(request.getCustomerId());
        Optional<Payee> payeeOpt = payees.stream()
                .filter(p -> p.getPayeeId().equals(request.getPayeeId()))
                .findFirst();

        if (!payeeOpt.isPresent()) {
            throw new TransactionException("Payee not found for customer");
        }
        Payee payee = payeeOpt.get();

        Long payeeAccountNumber = payee.getPayeeAccountNumber();

        // Get payee account from local database
        Optional<Account> payeeAccountOpt = accountService.findByAccountNo(payeeAccountNumber);
        if (!payeeAccountOpt.isPresent()) {
            throw new TransactionException("Payee account not found");
        }
        Account payeeAccount = payeeAccountOpt.get();

        LocalDateTime now = LocalDateTime.now();

        // Prepare transaction objects
        Transactions debitTxn = new Transactions();
        Transactions creditTxn = new Transactions();

        // Common transaction info
        debitTxn.setCustomerId(request.getCustomerId());
        debitTxn.setAccountNo(senderAccount.getAccountNo());
        debitTxn.setSenderAccountNo(senderAccount.getAccountNo());
        debitTxn.setReceiverAccountNo(payeeAccount.getAccountNo());
        debitTxn.setAmount(amount.doubleValue());
        debitTxn.setTransactionType(TransactionType.DEBIT);
        debitTxn.setPaymentMethod(method);
        debitTxn.setTransactionDate(now);
        debitTxn.setRemarks(request.getRemarks());
        debitTxn.setEntryTimestamp(now);
        debitTxn.setPayeeId(payee.getPayeeId());
        creditTxn.setCustomerId(request.getCustomerId());
        creditTxn.setAccountNo(payeeAccount.getAccountNo());
        creditTxn.setSenderAccountNo(senderAccount.getAccountNo());
        creditTxn.setReceiverAccountNo(payeeAccount.getAccountNo());
        creditTxn.setAmount(amount.doubleValue());
        creditTxn.setTransactionType(TransactionType.CREDIT);
        creditTxn.setPaymentMethod(method);
        creditTxn.setEntryTimestamp(now);
        creditTxn.setRemarks(request.getRemarks());
        creditTxn.setEntryTimestamp(now);
        creditTxn.setPayeeId(payee.getPayeeId());

        switch (method) {
            case RTGS:
            case IMPS:
                // Update balances immediately
                BigDecimal senderBalance = BigDecimal.valueOf(senderAccount.getBalance());
                senderAccount.setBalance(senderBalance.subtract(amount).doubleValue());
                payeeAccount.setBalance(BigDecimal.valueOf(payeeAccount.getBalance()).add(amount).doubleValue());

                // Save updated balances to database
                accountService.updateAccount(senderAccount);
                accountService.updateAccount(payeeAccount);

                // Finalize debit
                debitTxn.setStatus(TransactionStatus.COMPLETED);
                debitTxn.setBalanceAfterTxn(senderAccount.getBalance());

                // Finalize credit
                creditTxn.setStatus(TransactionStatus.COMPLETED);
                creditTxn.setBalanceAfterTxn(payeeAccount.getBalance());

                break;

            case NEFT:
                // Deduct sender balance immediately
                BigDecimal senderBalanceNeft = BigDecimal.valueOf(senderAccount.getBalance());
                senderAccount.setBalance(senderBalanceNeft.subtract(amount).doubleValue());

                // Save updated sender balance to database
                accountService.updateAccount(senderAccount);

                // Set statuses accordingly
                debitTxn.setStatus(TransactionStatus.COMPLETED);
                debitTxn.setBalanceAfterTxn(senderAccount.getBalance());

                creditTxn.setStatus(TransactionStatus.PENDING); // will be updated later
                creditTxn.setBalanceAfterTxn(payeeAccount.getBalance()); // unchanged for now

                break;

            default:
                throw new TransactionException("Unsupported payment method.");
        }

        // Save both transactions
        transactionsRepository.save(creditTxn);
        Transactions savedDebitTxn = transactionsRepository.save(debitTxn);
        // Note: Statement recording removed as StatementClient is external
        return savedDebitTxn;
    }

    @Scheduled(fixedRate = 60000) // every 1 minute
    @Transactional
    public void processScheduledNEFTTransfers() {
        List<Transactions> pendingNEFTTxns = transactionsRepository
                .findByPaymentMethodAndStatus(PaymentMethod.NEFT, TransactionStatus.PENDING);

        LocalDateTime now = LocalDateTime.now();

        for (Transactions txn : pendingNEFTTxns) {
            if (txn.getEntryTimestamp().plusMinutes(2).isBefore(now)) {
                Long receiverAccNo = txn.getReceiverAccountNo();
                Optional<Account> receiverAccountOpt = accountService.findByAccountNo(receiverAccNo);

                if (receiverAccountOpt.isPresent()) {
                    Account receiverAccount = receiverAccountOpt.get();
                    // Update balance
                    BigDecimal newBalance = BigDecimal.valueOf(receiverAccount.getBalance())
                            .add(BigDecimal.valueOf(txn.getAmount()));
                    receiverAccount.setBalance(newBalance.doubleValue());

                    // Save updated balance to database
                    accountService.updateAccount(receiverAccount);

                    // Mark credit txn as COMPLETED
                    txn.setStatus(TransactionStatus.COMPLETED);
                    txn.setBalanceAfterTxn(newBalance.doubleValue());
                    txn.setTransactionDate(LocalDateTime.now()); // ✅ Set actual execution time here
                    transactionsRepository.save(txn); // ✅ save after all updates
                    // Note: Statement recording removed as StatementClient is external
                }
            }
        }
    }

    // Now fetch transaction password from Account entity using accountNo
    private boolean validateTransactionPassword(Long accountNo, String transactionPassword) {
        try {
            Optional<Account> accountOpt = accountService.findByAccountNo(accountNo);
            if (!accountOpt.isPresent()) {
                System.out.println("DEBUG: Account not found for accountNo " + accountNo);
                return false;
            }

            Account account = accountOpt.get();
            String storedPassword = account.getTransactionPassword(); // Ensure this field exists in Account entity
            System.out.println(
                    "DEBUG: Stored password='" + storedPassword + "', Received password='" + transactionPassword + "'");

            // Clean both sides before comparison
            boolean match = storedPassword != null
                    && transactionPassword != null
                    && storedPassword.strip().equals(transactionPassword.strip());

            System.out.println("DEBUG: Password match result: " + match);
            return match;
        } catch (Exception e) {
            System.out.println("DEBUG: Exception in validateTransactionPassword: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validates IFSC code format and returns bank information
     * 
     * @param ifscCode the IFSC code to validate
     * @return validation result with bank details
     */
    public Map<String, Object> validateIfscCode(String ifscCode) {
        return accountValidationService.validateIfscCode(ifscCode);
    }
    
    /**
     * Validates account number with IFSC and returns account holder information
     * 
     * @param accountNumber the account number to validate
     * @param ifscCode      the IFSC code of the bank
     * @return account holder information
     */
    public Map<String, Object> validateAccountWithIfsc(Long accountNumber, String ifscCode) {
        return accountValidationService.validateAccountWithIfsc(accountNumber, ifscCode);
    }

    /**
     * Gets account holder name for a given account number
     * 
     * @param accountNumber the account number
     * @return account holder information
     */
    public Map<String, Object> getAccountHolderInfo(Long accountNumber) {
        try {
            Optional<Account> accountOpt = accountService.findByAccountNo(accountNumber);
            if (!accountOpt.isPresent()) {
                Map<String, Object> error = new HashMap<>();
                error.put("valid", false);
                error.put("error", "Account not found");
                return error;
            }

            Account account = accountOpt.get();

            Map<String, Object> result = new HashMap<>();
            result.put("valid", true);
            result.put("accountNumber", accountNumber);
            result.put("accountHolderName", ""); // Let user fill the name manually
            result.put("accountType",
                    account.getAccountType() != null ? account.getAccountType().toString() : "savings");
            result.put("balance", account.getBalance());
            result.put("status", account.getStatus() != null ? account.getStatus().toString() : "active");

            return result;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("valid", false);
            error.put("error", "Failed to get account info: " + e.getMessage());
            return error;
        }
    }

    @Override
    @Transactional
    public Transactions performManualTransfer(String fromAccountNumber, String toAccountNumber,
            String recipientName, String ifscCode,
            Double amount, String paymentMethod, String remarks, String transactionPassword, Long customerId) {
        try {
            // Validate transaction password first
            if (transactionPassword == null || transactionPassword.trim().isEmpty()) {
                throw new TransactionException("Transaction password is required");
            }

            if (!validateTransactionPassword(Long.valueOf(fromAccountNumber), transactionPassword)) {
                throw new TransactionException("Invalid transaction password");
            }

            // Validate IFSC code
            Map<String, Object> ifscValidation = validateIfscCode(ifscCode);
            if (!(Boolean) ifscValidation.get("valid")) {
                throw new TransactionException("Invalid IFSC code: " + ifscValidation.get("error"));
            }

            // Validate recipient account
            Map<String, Object> accountValidation = validateAccountWithIfsc(Long.valueOf(toAccountNumber), ifscCode);
            if (!(Boolean) accountValidation.get("valid")) {
                throw new TransactionException("Invalid recipient account: " + accountValidation.get("error"));
            }

            // Get sender account from local database
            Optional<Account> senderAccountOpt = accountService.findByAccountNo(Long.valueOf(fromAccountNumber));
            if (!senderAccountOpt.isPresent()) {
                throw new TransactionException("Sender account not found");
            }
            Account senderAccount = senderAccountOpt.get();

            // Validate amount
            if (amount <= 0) {
                throw new TransactionException("Amount must be greater than zero");
            }

            if (BigDecimal.valueOf(senderAccount.getBalance()).compareTo(BigDecimal.valueOf(amount)) < 0) {
                throw new TransactionException("Insufficient funds in sender's account");
            }

            // Validate RTGS minimum amount
            PaymentMethod method = PaymentMethod.valueOf(paymentMethod);
            if (method == PaymentMethod.RTGS && amount < 200000) {
                throw new TransactionException("RTGS requires minimum ₹2,00,000 transaction amount");
            }

            LocalDateTime now = LocalDateTime.now();

            // Create debit transaction
            Transactions debitTxn = new Transactions();
            debitTxn.setCustomerId(customerId);
            debitTxn.setAccountNo(senderAccount.getAccountNo());
            debitTxn.setSenderAccountNo(Long.valueOf(fromAccountNumber));
            debitTxn.setReceiverAccountNo(Long.valueOf(toAccountNumber));
            debitTxn.setAmount(amount);
            debitTxn.setTransactionType(TransactionType.DEBIT);
            debitTxn.setPaymentMethod(method);
            debitTxn.setTransactionDate(now);
            debitTxn.setRemarks(remarks != null ? remarks : "Manual transfer");
            debitTxn.setEntryTimestamp(now);

            // Update sender balance
            BigDecimal senderBalance = new BigDecimal(senderAccount.getBalance().toString());
            BigDecimal newSenderBalance = senderBalance.subtract(BigDecimal.valueOf(amount));
            senderAccount.setBalance(newSenderBalance.doubleValue());

            // Save updated sender balance to database
            accountService.updateAccount(senderAccount);

            debitTxn.setStatus(TransactionStatus.COMPLETED);
            debitTxn.setBalanceAfterTxn(newSenderBalance.doubleValue());

            // Check if recipient account exists in our database (intra-bank transfer)
            Optional<Account> recipientAccountOpt = accountService.findByAccountNo(Long.valueOf(toAccountNumber));
            if (recipientAccountOpt.isPresent()) {
                // Intra-bank transfer - credit recipient immediately
                Account recipientAccount = recipientAccountOpt.get();
                BigDecimal recipientBalance = BigDecimal.valueOf(recipientAccount.getBalance());
                BigDecimal newRecipientBalance = recipientBalance.add(BigDecimal.valueOf(amount));
                recipientAccount.setBalance(newRecipientBalance.doubleValue());

                // Save updated recipient balance to database
                accountService.updateAccount(recipientAccount);

                // Create credit transaction for recipient
                Transactions creditTxn = new Transactions();
                creditTxn.setCustomerId(customerId);
                creditTxn.setAccountNo(recipientAccount.getAccountNo());
                creditTxn.setSenderAccountNo(Long.valueOf(fromAccountNumber));
                creditTxn.setReceiverAccountNo(Long.valueOf(toAccountNumber));
                creditTxn.setAmount(amount);
                creditTxn.setTransactionType(TransactionType.CREDIT);
                creditTxn.setPaymentMethod(method);
                creditTxn.setTransactionDate(now);
                creditTxn.setRemarks(remarks != null ? remarks : "Manual transfer (credit)");
                creditTxn.setEntryTimestamp(now);
                creditTxn.setStatus(TransactionStatus.COMPLETED);
                creditTxn.setBalanceAfterTxn(newRecipientBalance.doubleValue());

                // Save credit transaction
                transactionsRepository.save(creditTxn);
            }
            // If recipient account doesn't exist, it's an external transfer (no credit
            // transaction needed)

            // Save transaction
            Transactions savedTxn = transactionsRepository.save(debitTxn);
            // Note: Statement recording removed as StatementClient is external

            return savedTxn;

        } catch (Exception e) {
            throw new TransactionException("Manual transfer failed: " + e.getMessage());
        }
    }
}
