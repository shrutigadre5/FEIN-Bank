package com.example.demo.service;

import com.example.demo.clients.AccountClient;
import com.example.demo.clients.FundTransferClient;
import com.example.demo.entities.TransactionStatement;
import com.example.demo.repos.TransactionStatementRepo;
import com.example.demo.vo.AccountDTO;
import com.example.demo.vo.FundTransferDTO;
import com.example.demo.vo.TransactionStatementDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionStatementServiceImpl implements TransactionStatementService {

    @Autowired private TransactionStatementRepo repo;
    @Autowired private AccountClient accountClient; // optional enrichment
    @Autowired private FundTransferClient fundTransferClient; // fund transfer service client

    // ➕ Create
    @Override
    public TransactionStatement addTransaction(TransactionStatement transaction) {
        return repo.save(transaction);
    }

    // 🔍 Read
    @Override
    public TransactionStatement getTransactionById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public List<TransactionStatement> getAllTransactions(String accountNumber) {
        try {
            AccountDTO a = accountClient.getAccountByNumber(accountNumber);
            System.out.println("Account: " + (a != null ? a.getHolderName() : "N/A"));
        } catch (Exception ignored) {}
        
        List<TransactionStatement> transactions = new ArrayList<>();
        
        // First try to search using the ACCOUNTNO field for exact account match
        try {
            Long accountNo = Long.parseLong(accountNumber);
            transactions = repo.findByAccountNoOrderByTransactionDateDesc(accountNo);
        } catch (NumberFormatException e) {
            // If accountNumber is not a valid Long, fall back to string search
            transactions = repo.findBySenderAccountNoOrReceiverAccountNoOrderByTransactionDateDesc(accountNumber, accountNumber);
        }
        
        return transactions;
    }

    @Override
    public List<TransactionStatement> getLatestTransactions(String accountNumber, int limit) {
        Pageable pageable = PageRequest.of(0, Math.max(1, limit), Sort.by(Sort.Direction.DESC, "transactionDate"));
        List<TransactionStatement> transactions = new ArrayList<>();
        
        // First try to search using the ACCOUNTNO field for exact account match
        try {
            Long accountNo = Long.parseLong(accountNumber);
            transactions = repo.findLatestByAccountNo(accountNo, pageable);
        } catch (NumberFormatException e) {
            // If accountNumber is not a valid Long, fall back to string search
            transactions = repo.findLatestForAccount(accountNumber, pageable);
        }
        
        return transactions;
    }

    @Override
    public List<TransactionStatement> getLastSixMonthsTransactions(String accountNumber) {
        LocalDate today = LocalDate.now();
        LocalDate from  = today.minusMonths(6);

        // IMPORTANT: convert to java.util.Date for repo method
        Date fromTs = toDate(from.atStartOfDay());
        Date toTs   = toDate(today.plusDays(1).atStartOfDay()); // exclusive upper bound

        return repo.findBetweenForAccount(accountNumber, fromTs, toTs);
    }

    @Override
    public TransactionStatement updateTransaction(Long id, TransactionStatement incoming) {
        return repo.findById(id).map(ex -> {
            ex.setSenderAccountNo(incoming.getSenderAccountNo());
            ex.setReceiverAccountNo(incoming.getReceiverAccountNo());
            ex.setPaymentMethod(incoming.getPaymentMethod());
            ex.setAmount(incoming.getAmount());
            ex.setStatus(incoming.getStatus());
            ex.setRemarks(incoming.getRemarks());
            ex.setTransactionDate(incoming.getTransactionDate());
            ex.setCreatedAt(incoming.getCreatedAt());
            return repo.save(ex);
        }).orElse(null);
    }

    @Override
    public String deleteTransaction(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return "Deleted transaction id: " + id;
        }
        return "Transaction id not found: " + id;
    }

    // =========================
    // Statement (DTO) search
    // =========================
    @Override
    public List<TransactionStatementDTO> searchStatement(
            String accountNumber,
            String type,
            String from,
            String to,
            String sortBy,
            String direction,
            Integer page,
            Integer size
    ) {
        String effectiveType = normalizeType(type); // ALL/DEBIT/CREDIT
        Date fromTs = parseFrom(from); // -> Date or null
        Date toTs   = parseTo(to);     // -> Date or null (exclusive if date-only)

        Sort sort = buildSort(sortBy, direction);
        Pageable pageable = PageRequest.of(
                page != null && page >= 0 ? page : 0,
                size != null && size > 0 ? size : 50,
                sort
        );

        List<TransactionStatement> items = new ArrayList<>();
        
        // First try to search using the ACCOUNTNO field for exact account match
        try {
            Long accountNo = Long.parseLong(accountNumber);
            items = repo.searchByAccountNo(accountNo, effectiveType, fromTs, toTs, pageable);
        } catch (NumberFormatException e) {
            // If accountNumber is not a valid Long, fall back to string search
            items = repo.searchForAccount(accountNumber, effectiveType, fromTs, toTs, pageable);
        }

        return items.stream()
                .map(t -> toDto(t, accountNumber))
                .filter(dto -> filterByType(dto, effectiveType))
                .collect(Collectors.toList());
    }

    // ---- helpers ----
    private String normalizeType(String type) {
        if (type == null) return "ALL";
        String t = type.trim().toUpperCase(Locale.ROOT);
        return (t.equals("DEBIT") || t.equals("CREDIT")) ? t : "ALL";
    }

    private Date parseFrom(String from) {
        if (from == null || from.isBlank()) return null;
        try {
            if (from.length() <= 10) {
                LocalDate d = LocalDate.parse(from); // yyyy-MM-dd
                return toDate(d.atStartOfDay());
            }
            LocalDateTime dt = LocalDateTime.parse(from); // yyyy-MM-dd'T'HH:mm:ss
            return toDate(dt);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid 'from' format. Use yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss");
        }
    }

    private Date parseTo(String to) {
        if (to == null || to.isBlank()) return null;
        try {
            if (to.length() <= 10) {
                LocalDate d = LocalDate.parse(to);
                return toDate(d.plusDays(1).atStartOfDay()); // exclusive next day 00:00
            }
            LocalDateTime dt = LocalDateTime.parse(to);
            return toDate(dt); // treat provided datetime as exclusive upper bound instant
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid 'to' format. Use yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss");
        }
    }

    private Sort buildSort(String sortBy, String direction) {
        String field = (sortBy == null || sortBy.isBlank()) ? "transactionDate" : sortBy;
        Sort.Direction dir = "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Set<String> allowed = Set.of("transactionDate", "amount", "paymentMethod", "status", "transactionId");
        if (!allowed.contains(field)) field = "transactionDate";
        return Sort.by(dir, field);
    }

    // Convert LocalDateTime/LocalDate -> java.util.Date
    private Date toDate(LocalDateTime ldt) {
        return java.sql.Timestamp.valueOf(ldt);
    }
    private Date toDate(LocalDate ld) {
        return toDate(ld.atStartOfDay());
    }

    private TransactionStatementDTO toDto(TransactionStatement t, String accountNumber) {
        TransactionStatementDTO dto = new TransactionStatementDTO();
        dto.setTransactionId(t.getTransactionId());
        dto.setAmount(t.getAmount());

        if (t.getTransactionDate() != null) {
            dto.setTransactionDate(
                t.getTransactionDate().toInstant()
                  .atZone(ZoneId.systemDefault()).toLocalDateTime()
            );
        }

        dto.setPaymentMethod(t.getPaymentMethod());
        dto.setStatus(t.getStatus());
        dto.setRemarks(t.getRemarks());

        // Determine transaction type and counterparty based on available data
        boolean outgoing = false;
        String counterpartyAccount = "";
        
        if (t.getSenderAccountNo() != null && t.getReceiverAccountNo() != null) {
            // Old data model: use sender/receiver account logic
            outgoing = accountNumber != null && accountNumber.equals(t.getSenderAccountNo());
            counterpartyAccount = outgoing ? t.getReceiverAccountNo() : t.getSenderAccountNo();
        } else {
            // New data model: Need to determine DEBIT/CREDIT based on business logic
            
            // For now, let's use a simple approach:
            // If database already has signed amounts, use that
            boolean isNegativeAmount = t.getAmount() != null && t.getAmount().compareTo(new java.math.BigDecimal("0")) < 0;
            
            if (isNegativeAmount) {
                outgoing = true; // Already negative = DEBIT
            } else {
                // If amount is positive, we need additional logic to determine if it's DEBIT or CREDIT
                // For account-specific transactions, we'll need to determine based on context
                
                // Check remarks for keywords
                String remarks = t.getRemarks() != null ? t.getRemarks().toLowerCase() : "";
                boolean hasDebitKeywords = remarks.contains("withdraw") || remarks.contains("debit") || 
                                         remarks.contains("payment") || remarks.contains("transfer out") ||
                                         remarks.contains("sent") || remarks.contains("paid");
                boolean hasCreditKeywords = remarks.contains("deposit") || remarks.contains("credit") || 
                                          remarks.contains("received") || remarks.contains("transfer in") ||
                                          remarks.contains("incoming");
                
                // Check payment method
                String paymentMethod = t.getPaymentMethod() != null ? t.getPaymentMethod().toLowerCase() : "";
                boolean isWithdrawalMethod = paymentMethod.contains("withdraw") || paymentMethod.contains("atm") ||
                                           paymentMethod.contains("transfer");
                
                if (hasDebitKeywords || isWithdrawalMethod) {
                    outgoing = true; // DEBIT
                } else if (hasCreditKeywords) {
                    outgoing = false; // CREDIT  
                } else {
                    // Default assumption: if no clear indicators and amount is positive, assume CREDIT
                    outgoing = false;
                }
            }
            
            counterpartyAccount = "Account: " + (t.getAccountNo() != null ? t.getAccountNo().toString() : "N/A");
        }
        
        dto.setTxnType(outgoing ? "DEBIT" : "CREDIT");
        dto.setCounterpartyAccount(counterpartyAccount);
        
        // Set the amount with proper sign: negative for DEBIT, positive for CREDIT
        if (t.getAmount() != null) {
            BigDecimal signedAmount = outgoing ? t.getAmount().negate() : t.getAmount();
            dto.setAmount(signedAmount);
        }
        
        dto.setBalanceAfterTxn(t.getBalanceAfterTxn());
        return dto;
    }

    private boolean filterByType(TransactionStatementDTO dto, String type) {
        if ("ALL".equals(type)) {
            return true;
        }
        if ("DEBIT".equals(type) && "DEBIT".equals(dto.getTxnType())) {
            return true;
        }
        if ("CREDIT".equals(type) && "CREDIT".equals(dto.getTxnType())) {
            return true;
        }
        return false;
    }
    
    @Override
    public List<TransactionStatementDTO> getLatestStatementDTO(String accountNumber, int count) {
        return searchStatement(accountNumber, "ALL", null, null, "transactionDate", "DESC", 0, count);
    }

    // =========================
    // Customer ID based operations
    // =========================
    @Override
    public List<TransactionStatement> getAllTransactionsByCustomerId(String customerId) {
        try {
            AccountDTO account = accountClient.getAccountByCustomerId(customerId);
            if (account != null && account.getAccountNumber() != null) {
                return getAllTransactions(account.getAccountNumber());
            }
        } catch (Exception e) {
            System.err.println("Error fetching account for customerId " + customerId + ": " + e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public List<TransactionStatement> getLatestTransactionsByCustomerId(String customerId, int limit) {
        try {
            AccountDTO account = accountClient.getAccountByCustomerId(customerId);
            if (account != null && account.getAccountNumber() != null) {
                return getLatestTransactions(account.getAccountNumber(), limit);
            }
        } catch (Exception e) {
            System.err.println("Error fetching account for customerId " + customerId + ": " + e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public List<TransactionStatement> getLastSixMonthsTransactionsByCustomerId(String customerId) {
        try {
            AccountDTO account = accountClient.getAccountByCustomerId(customerId);
            if (account != null && account.getAccountNumber() != null) {
                return getLastSixMonthsTransactions(account.getAccountNumber());
            }
        } catch (Exception e) {
            System.err.println("Error fetching account for customerId " + customerId + ": " + e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public List<TransactionStatementDTO> getLatestStatementDTOByCustomerId(String customerId, int count) {
        try {
            AccountDTO account = accountClient.getAccountByCustomerId(customerId);
            if (account != null && account.getAccountNumber() != null) {
                return getLatestStatementDTO(account.getAccountNumber(), count);
            }
        } catch (Exception e) {
            System.err.println("Error fetching account for customerId " + customerId + ": " + e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public List<TransactionStatementDTO> searchStatementByCustomerId(
            String customerId,
            String type,
            String from,
            String to,
            String sortBy,
            String direction,
            Integer page,
            Integer size
    ) {
        try {
            AccountDTO account = accountClient.getAccountByCustomerId(customerId);
            if (account != null && account.getAccountNumber() != null) {
                return searchStatement(account.getAccountNumber(), type, from, to, sortBy, direction, page, size);
            }
        } catch (Exception e) {
            System.err.println("Error fetching account for customerId " + customerId + ": " + e.getMessage());
        }
        return new ArrayList<>();
    }

    // =========================
    // Fund Transfer Synchronization
    // =========================
    @Override
    public String syncAllFundTransfers() {
        try {
            List<FundTransferDTO> fundTransfers = fundTransferClient.getAllFundTransfers();
            
            if (fundTransfers.isEmpty()) {
                return "No fund transfers available to sync (Fund Transfer Service may be unavailable)";
            }
            
            int syncedCount = 0;
            
            for (FundTransferDTO ft : fundTransfers) {
                if (convertAndSaveFundTransfer(ft)) {
                    syncedCount++;
                }
            }
            
            return "Successfully synced " + syncedCount + " out of " + fundTransfers.size() + " fund transfers";
        } catch (Exception e) {
            System.err.println("Error syncing all fund transfers: " + e.getMessage());
            return "Failed to sync fund transfers: Fund Transfer Service is unavailable - " + e.getMessage();
        }
    }

    @Override
    public String syncRecentFundTransfers(int limit) {
        try {
            List<FundTransferDTO> fundTransfers = fundTransferClient.getRecentFundTransfers(limit);
            
            if (fundTransfers.isEmpty()) {
                return "No recent fund transfers available to sync (Fund Transfer Service may be unavailable)";
            }
            
            int syncedCount = 0;
            
            for (FundTransferDTO ft : fundTransfers) {
                if (convertAndSaveFundTransfer(ft)) {
                    syncedCount++;
                }
            }
            
            return "Successfully synced " + syncedCount + " out of " + fundTransfers.size() + " recent fund transfers";
        } catch (Exception e) {
            System.err.println("Error syncing recent fund transfers: " + e.getMessage());
            return "Failed to sync recent fund transfers: Fund Transfer Service is unavailable - " + e.getMessage();
        }
    }

    @Override
    public String syncFundTransfersByAccount(String accountNumber) {
        try {
            List<FundTransferDTO> fundTransfers = fundTransferClient.getFundTransfersByAccount(accountNumber);
            int syncedCount = 0;
            
            for (FundTransferDTO ft : fundTransfers) {
                if (convertAndSaveFundTransfer(ft)) {
                    syncedCount++;
                }
            }
            
            return "Successfully synced " + syncedCount + " out of " + fundTransfers.size() + " fund transfers for account " + accountNumber;
        } catch (Exception e) {
            System.err.println("Error syncing fund transfers for account " + accountNumber + ": " + e.getMessage());
            return "Failed to sync fund transfers for account " + accountNumber + ": " + e.getMessage();
        }
    }

    @Override
    public String syncFundTransfersByCustomer(String customerId) {
        try {
            List<FundTransferDTO> fundTransfers = fundTransferClient.getFundTransfersByCustomer(customerId);
            int syncedCount = 0;
            
            for (FundTransferDTO ft : fundTransfers) {
                if (convertAndSaveFundTransfer(ft)) {
                    syncedCount++;
                }
            }
            
            return "Successfully synced " + syncedCount + " out of " + fundTransfers.size() + " fund transfers for customer " + customerId;
        } catch (Exception e) {
            System.err.println("Error syncing fund transfers for customer " + customerId + ": " + e.getMessage());
            return "Failed to sync fund transfers for customer " + customerId + ": " + e.getMessage();
        }
    }

    @Override
    public String syncCompletedFundTransfers() {
        try {
            List<FundTransferDTO> fundTransfers = fundTransferClient.getCompletedFundTransfers();
            
            if (fundTransfers.isEmpty()) {
                return "No completed fund transfers available to sync (Fund Transfer Service may be unavailable)";
            }
            
            int syncedCount = 0;
            
            for (FundTransferDTO ft : fundTransfers) {
                if (convertAndSaveFundTransfer(ft)) {
                    syncedCount++;
                }
            }
            
            return "Successfully synced " + syncedCount + " out of " + fundTransfers.size() + " completed fund transfers";
        } catch (Exception e) {
            System.err.println("Error syncing completed fund transfers: " + e.getMessage());
            return "Failed to sync completed fund transfers: Fund Transfer Service is unavailable - " + e.getMessage();
        }
    }

    // =========================
    // Helper Methods for Fund Transfer Conversion
    // =========================
    private boolean convertAndSaveFundTransfer(FundTransferDTO fundTransfer) {
        try {
            // Check if this fund transfer already exists to avoid duplicates
            if (fundTransferAlreadyExists(fundTransfer)) {
                System.out.println("Fund transfer with reference " + fundTransfer.getReferenceNumber() + " already exists, skipping");
                return false;
            }

            // Create transaction for sender (DEBIT)
            TransactionStatement senderTransaction = createTransactionFromFundTransfer(fundTransfer, true);
            repo.save(senderTransaction);

            // Create transaction for receiver (CREDIT) - only if it's not the same account
            if (!fundTransfer.getFromAccountNumber().equals(fundTransfer.getToAccountNumber())) {
                TransactionStatement receiverTransaction = createTransactionFromFundTransfer(fundTransfer, false);
                repo.save(receiverTransaction);
            }

            return true;
        } catch (Exception e) {
            System.err.println("Error converting fund transfer " + fundTransfer.getReferenceNumber() + ": " + e.getMessage());
            return false;
        }
    }

    private boolean fundTransferAlreadyExists(FundTransferDTO fundTransfer) {
        // Check if a transaction with this reference number already exists
        try {
            List<TransactionStatement> existing = repo.findByFundTransferReference(fundTransfer.getReferenceNumber());
            return !existing.isEmpty();
        } catch (Exception e) {
            System.err.println("Error checking for existing fund transfer: " + e.getMessage());
            return false;
        }
    }

    private TransactionStatement createTransactionFromFundTransfer(FundTransferDTO fundTransfer, boolean isSender) {
        TransactionStatement transaction = new TransactionStatement();
        
        // Set basic transaction details
        transaction.setSenderAccountNo(fundTransfer.getFromAccountNumber());
        transaction.setReceiverAccountNo(fundTransfer.getToAccountNumber());
        transaction.setAmount(fundTransfer.getAmount());
        transaction.setPaymentMethod(fundTransfer.getTransferType());
        transaction.setStatus(mapFundTransferStatus(fundTransfer.getStatus()));
        
        // Set remarks with fund transfer reference
        String baseRemarks = fundTransfer.getRemarks() != null ? fundTransfer.getRemarks() : "Fund Transfer";
        transaction.setRemarks(baseRemarks + " - FT Ref: " + fundTransfer.getReferenceNumber());
        
        // Set transaction date
        if (fundTransfer.getCompletedDate() != null) {
            transaction.setTransactionDate(toDate(fundTransfer.getCompletedDate()));
        } else if (fundTransfer.getTransferDate() != null) {
            transaction.setTransactionDate(toDate(fundTransfer.getTransferDate()));
        } else {
            transaction.setTransactionDate(new Date());
        }
        
        transaction.setCreatedAt(LocalDateTime.now());
        
        return transaction;
    }

    private String mapFundTransferStatus(String fundTransferStatus) {
        if (fundTransferStatus == null) return "PENDING";
        
        switch (fundTransferStatus.toUpperCase()) {
            case "COMPLETED":
                return "SUCCESS";
            case "FAILED":
                return "FAILED";
            case "CANCELLED":
                return "CANCELLED";
            case "PENDING":
            default:
                return "PENDING";
        }
    }

}
