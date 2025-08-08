package com.example.demo.controller;

import com.example.demo.clients.AccountClient;
import com.example.demo.entities.TransactionStatement;
import com.example.demo.service.FundTransferHealthService;
import com.example.demo.service.TransactionStatementService;
import com.example.demo.vo.AccountDTO;
import com.example.demo.vo.StatementWithBalanceDTO;
import com.example.demo.vo.TransactionStatementDTO;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/transactions")
public class TransactionStatementController {

    @Autowired
    private TransactionStatementService service;
    
    @Autowired
	private AccountClient accountClient;  // FeignClient or RestTemplate

    @Autowired
    private FundTransferHealthService fundTransferHealthService;

    // =========================
    // CRUD & basic entity reads
    // =========================

    // 🔄 Create a transaction (POST)
    @PostMapping("/add")
    public TransactionStatement addTransaction(@RequestBody TransactionStatement transaction) {
        return service.addTransaction(transaction);
    }

    // 🔍 Read a transaction by ID (GET)
    @GetMapping("/get/{id}")
    public TransactionStatement getTransactionById(@PathVariable Long id) {
        return service.getTransactionById(id);
    }

    // 📄 Read all transactions by account number (entity list)
    @GetMapping("/{accountNumber}")
    public List<TransactionStatement> getAllTransactions(@PathVariable String accountNumber) {
        return service.getAllTransactions(accountNumber);
    }

    // 📊 Get last N transactions (entity list)
    @GetMapping("/{accountNumber}/latest")
    public List<TransactionStatement> getLatestTransactions(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "5") @Min(1) int limit) {
        return service.getLatestTransactions(accountNumber, limit);
    }

    // 📆 Get transactions from the last 6 months (entity list)
    @GetMapping("/{accountNumber}/last6months")
    public List<TransactionStatement> getLastSixMonthsTransactions(@PathVariable String accountNumber) {
        return service.getLastSixMonthsTransactions(accountNumber);
    }

    // ✏️ Update a transaction (PUT)
    @PutMapping("/update/{id}")
    public TransactionStatement updateTransaction(@PathVariable Long id,
                                                  @RequestBody TransactionStatement transaction) {
        return service.updateTransaction(id, transaction);
    }

    // 🗑️ Delete a transaction (DELETE)
    @DeleteMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        return service.deleteTransaction(id);
    }

    // =========================
    // Statement (DTO) endpoints
    // =========================

    // 📄 Statement (DTO) – all rows for an account, minimal fields + derived txnType/counterparty
    @GetMapping("/{accountNumber}/statement")
    public List<TransactionStatementDTO> getStatement(@PathVariable String accountNumber) {
        // If you created a simple pass-through method, else call searchStatement with defaults
        return service.searchStatement(
                accountNumber,
                "ALL",      // type
                null,       // from
                null,       // to
                "transactionDate",
                "DESC",
                0,          // page
                100         // size (adjust default as you wish)
        );
    }

    // 📊 Statement (DTO) – latest N
    @GetMapping("/{accountNumber}/statement/latest")
    public List<TransactionStatementDTO> getLatestStatement(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "5") @Min(1) int limit) {
        // If you added service.getLatestStatement(account, limit) use that; otherwise route via search with size=limit
        return service.searchStatement(
                accountNumber,
                "ALL",
                null,
                null,
                "transactionDate",
                "DESC",
                0,
                limit
        );
    }

    // 🔎 Statement (DTO) – powerful search with type/date range/sort/pagination
    // type: ALL | DEBIT | CREDIT
    // from/to: yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss (to is exclusive if date-only)
    // sortBy: transactionDate | amount | paymentMethod | status | transactionId
    // direction: ASC | DESC
    @GetMapping("/{accountNumber}/statement/search")
    public List<TransactionStatementDTO> searchStatement(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "ALL") String type,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "50") @Min(1) Integer size
    ) {
        return service.searchStatement(accountNumber, type, from, to, sortBy, direction, page, size);
    }
    
    @GetMapping("/{accountNo}/statementWithBalance")
    public ResponseEntity<StatementWithBalanceDTO> getStatementWithBalance(
            @PathVariable String accountNo,
            @RequestParam(defaultValue = "5") int count) {

        // 1. Fetch latest N transactions (DTOs)
        List<TransactionStatementDTO> transactions = service.getLatestStatementDTO(accountNo, count);

        // 2. Fetch account info from Feign Client
        AccountDTO account = accountClient.getAccountByNumber(accountNo);

        // 3. Build final DTO
        StatementWithBalanceDTO dto = new StatementWithBalanceDTO();
        dto.setAccountNumber(accountNo);
        dto.setBalance(account != null ? account.getBalance() : null);
        dto.setCustomerId(account != null ? account.getCustomerId() : null);
        dto.setHolderName(account != null ? account.getHolderName() : null);
        dto.setTransactions(transactions);

        return ResponseEntity.ok(dto);
    }

    // =========================
    // Customer ID based endpoints
    // =========================

    // 📄 Read all transactions by customer ID (entity list)
    @GetMapping("/customer/{customerId}")
    public List<TransactionStatement> getAllTransactionsByCustomerId(@PathVariable String customerId) {
        return service.getAllTransactionsByCustomerId(customerId);
    }

    // 📊 Get last N transactions by customer ID (entity list)
    @GetMapping("/customer/{customerId}/latest")
    public List<TransactionStatement> getLatestTransactionsByCustomerId(
            @PathVariable String customerId,
            @RequestParam(defaultValue = "5") @Min(1) int limit) {
        return service.getLatestTransactionsByCustomerId(customerId, limit);
    }

    // 📆 Get transactions from the last 6 months by customer ID (entity list)
    @GetMapping("/customer/{customerId}/last6months")
    public List<TransactionStatement> getLastSixMonthsTransactionsByCustomerId(@PathVariable String customerId) {
        return service.getLastSixMonthsTransactionsByCustomerId(customerId);
    }

    // 📄 Statement (DTO) by customer ID – all rows for a customer, minimal fields + derived txnType/counterparty
    @GetMapping("/customer/{customerId}/statement")
    public List<TransactionStatementDTO> getStatementByCustomerId(@PathVariable String customerId) {
        return service.searchStatementByCustomerId(
                customerId,
                "ALL",      // type
                null,       // from
                null,       // to
                "transactionDate",
                "DESC",
                0,          // page
                100         // size (adjust default as you wish)
        );
    }

    // 📊 Statement (DTO) by customer ID – latest N
    @GetMapping("/customer/{customerId}/statement/latest")
    public List<TransactionStatementDTO> getLatestStatementByCustomerId(
            @PathVariable String customerId,
            @RequestParam(defaultValue = "5") @Min(1) int limit) {
        return service.searchStatementByCustomerId(
                customerId,
                "ALL",
                null,
                null,
                "transactionDate",
                "DESC",
                0,
                limit
        );
    }

    // 🔎 Statement (DTO) by customer ID – powerful search with type/date range/sort/pagination
    @GetMapping("/customer/{customerId}/statement/search")
    public List<TransactionStatementDTO> searchStatementByCustomerId(
            @PathVariable String customerId,
            @RequestParam(defaultValue = "ALL") String type,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "50") @Min(1) Integer size
    ) {
        return service.searchStatementByCustomerId(customerId, type, from, to, sortBy, direction, page, size);
    }

    // 📊 Statement with balance by customer ID
    @GetMapping("/customer/{customerId}/statementWithBalance")
    public ResponseEntity<StatementWithBalanceDTO> getStatementWithBalanceByCustomerId(
            @PathVariable String customerId,
            @RequestParam(defaultValue = "5") int count) {

        // 1. Fetch latest N transactions (DTOs) by customer ID
        List<TransactionStatementDTO> transactions = service.getLatestStatementDTOByCustomerId(customerId, count);

        // 2. Fetch account info from Feign Client using customer ID
        AccountDTO account = accountClient.getAccountByCustomerId(customerId);

        // 3. Build final DTO
        StatementWithBalanceDTO dto = new StatementWithBalanceDTO();
        dto.setAccountNumber(account != null ? account.getAccountNumber() : null);
        dto.setCustomerId(customerId);
        dto.setHolderName(account != null ? account.getHolderName() : null);
        dto.setBalance(account != null ? account.getBalance() : null);
        dto.setTransactions(transactions);

        return ResponseEntity.ok(dto);
    }

    // =========================
    // Fund Transfer Synchronization endpoints
    // =========================

    // 🔄 Sync all fund transfers
    @PostMapping("/sync/fundtransfers/all")
    public ResponseEntity<String> syncAllFundTransfers() {
        // Check service health first
        if (!fundTransferHealthService.isFundTransferServiceAvailable()) {
            return ResponseEntity.ok("❌ Fund Transfer Service is currently unavailable. Please try again later.");
        }
        
        String result = service.syncAllFundTransfers();
        return ResponseEntity.ok(result);
    }

    // 🔄 Sync recent fund transfers
    @PostMapping("/sync/fundtransfers/recent")
    public ResponseEntity<String> syncRecentFundTransfers(@RequestParam(defaultValue = "100") int limit) {
        // Check service health first
        if (!fundTransferHealthService.isFundTransferServiceAvailable()) {
            return ResponseEntity.ok("❌ Fund Transfer Service is currently unavailable. Please try again later.");
        }
        
        String result = service.syncRecentFundTransfers(limit);
        return ResponseEntity.ok(result);
    }

    // 🔄 Sync fund transfers by account number
    @PostMapping("/sync/fundtransfers/account/{accountNumber}")
    public ResponseEntity<String> syncFundTransfersByAccount(@PathVariable String accountNumber) {
        String result = service.syncFundTransfersByAccount(accountNumber);
        return ResponseEntity.ok(result);
    }

    // 🔄 Sync fund transfers by customer ID
    @PostMapping("/sync/fundtransfers/customer/{customerId}")
    public ResponseEntity<String> syncFundTransfersByCustomer(@PathVariable String customerId) {
        String result = service.syncFundTransfersByCustomer(customerId);
        return ResponseEntity.ok(result);
    }

    // 🔄 Sync completed fund transfers only
    @PostMapping("/sync/fundtransfers/completed")
    public ResponseEntity<String> syncCompletedFundTransfers() {
        String result = service.syncCompletedFundTransfers();
        return ResponseEntity.ok(result);
    }

    // =========================
    // Advanced filtering endpoints
    // =========================

    // 🔎 Advanced search with both customer ID and account number filtering
    @GetMapping("/advanced/statement/search")
    public List<TransactionStatementDTO> advancedSearchStatement(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(defaultValue = "ALL") String type,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "50") @Min(1) Integer size
    ) {
        // If both customerId and accountNumber are provided, verify they match
        if (customerId != null && !customerId.trim().isEmpty() && 
            accountNumber != null && !accountNumber.trim().isEmpty()) {
            
            try {
                // Get account info for the customer
                AccountDTO customerAccount = accountClient.getAccountByCustomerId(customerId);
                
                // Verify that the provided account number matches the customer's account
                if (customerAccount == null || !accountNumber.equals(customerAccount.getAccountNumber())) {
                    return List.of(); // Return empty list if account doesn't belong to customer
                }
                
                // Use account number for search since it's verified
                return service.searchStatement(accountNumber, type, from, to, sortBy, direction, page, size);
                
            } catch (Exception e) {
                System.err.println("Error verifying customer-account relationship: " + e.getMessage());
                return List.of();
            }
        }
        
        // If only customerId is provided, use customer endpoint
        if (customerId != null && !customerId.trim().isEmpty()) {
            return service.searchStatementByCustomerId(customerId, type, from, to, sortBy, direction, page, size);
        }
        
        // If only accountNumber is provided, use account endpoint
        if (accountNumber != null && !accountNumber.trim().isEmpty()) {
            return service.searchStatement(accountNumber, type, from, to, sortBy, direction, page, size);
        }
        
        // Neither provided
        return List.of();
    }

    // =========================
    // Demo & Test endpoints
    // =========================

    // 🎯 Demo endpoint - fetch transactions with formatted response
    @GetMapping("/demo/{accountNumber}")
    public ResponseEntity<String> demoFetchTransactions(@PathVariable String accountNumber) {
        try {
            // Fetch latest 10 transactions
            List<TransactionStatement> transactions = service.getLatestTransactions(accountNumber, 10);
            
            if (transactions.isEmpty()) {
                return ResponseEntity.ok("📭 No transactions found for account: " + accountNumber);
            }
            
            StringBuilder result = new StringBuilder();
            result.append("🔍 Found ").append(transactions.size()).append(" transactions for account: ").append(accountNumber).append("\n");
            result.append("=".repeat(60)).append("\n\n");
            
            for (int i = 0; i < transactions.size(); i++) {
                TransactionStatement txn = transactions.get(i);
                result.append(String.format("Transaction %d:\n", i + 1));
                result.append(String.format("  ID: %s\n", txn.getTransactionId()));
                result.append(String.format("  Amount: ₹%.2f\n", txn.getAmount()));
                result.append(String.format("  Date: %s\n", txn.getTransactionDate()));
                result.append(String.format("  Method: %s\n", txn.getPaymentMethod()));
                result.append(String.format("  Status: %s\n", txn.getStatus()));
                result.append(String.format("  Remarks: %s\n", txn.getRemarks()));
                result.append(String.format("  From: %s → To: %s\n", txn.getSenderAccountNo(), txn.getReceiverAccountNo()));
                result.append("-".repeat(40)).append("\n");
            }
            
            return ResponseEntity.ok(result.toString());
            
        } catch (Exception e) {
            return ResponseEntity.ok("❌ Error fetching transactions: " + e.getMessage());
        }
    }

    // =========================
    // Health Check endpoints
    // =========================

    // 🏥 Check Fund Transfer Service health
    @GetMapping("/health/fundtransfer")
    public ResponseEntity<String> checkFundTransferServiceHealth() {
        String status = fundTransferHealthService.getFundTransferServiceStatus();
        return ResponseEntity.ok(status);
    }

    // 🔍 System diagnostic endpoint
    @GetMapping("/diagnostic")
    public ResponseEntity<String> systemDiagnostic() {
        StringBuilder diagnostic = new StringBuilder();
        diagnostic.append("🔍 Transaction Statement Service Diagnostic\n");
        diagnostic.append("========================================\n\n");
        
        // Check Fund Transfer Service
        diagnostic.append("Fund Transfer Integration:\n");
        diagnostic.append("- ").append(fundTransferHealthService.getFundTransferServiceStatus()).append("\n");
        
        // Check Account Service (basic test)
        try {
            accountClient.getAccountByNumber("TEST");
            diagnostic.append("- Account Service: ✅ Available\n");
        } catch (Exception e) {
            diagnostic.append("- Account Service: ❌ Unavailable (").append(e.getMessage()).append(")\n");
        }
        
        diagnostic.append("\nConfiguration:\n");
        diagnostic.append("- Sync Scheduler: Enabled\n");
        diagnostic.append("- Circuit Breaker: Enabled\n");
        diagnostic.append("- Fallback Handling: Enabled\n");
        
        diagnostic.append("\nAvailable Endpoints:\n");
        diagnostic.append("- Sync Recent: POST /api/transactions/sync/fundtransfers/recent\n");
        diagnostic.append("- Health Check: GET /api/transactions/health/fundtransfer\n");
        diagnostic.append("- Customer Search: GET /api/transactions/customer/{id}/statement\n");
        diagnostic.append("- Advanced Search: GET /api/transactions/advanced/statement/search\n");
        
        return ResponseEntity.ok(diagnostic.toString());
    }

}

