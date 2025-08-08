package com.example.demo.service;

import com.example.demo.entities.TransactionStatement;
import com.example.demo.vo.TransactionStatementDTO;

import java.util.List;

public interface TransactionStatementService {

    // ➕ Create
    TransactionStatement addTransaction(TransactionStatement transaction);

    // 🔍 Read
    TransactionStatement getTransactionById(Long id);
    List<TransactionStatement> getAllTransactions(String accountNumber);
    List<TransactionStatement> getLatestTransactions(String accountNumber, int limit);
    List<TransactionStatement> getLastSixMonthsTransactions(String accountNumber);

    // ✏️ Update
    TransactionStatement updateTransaction(Long id, TransactionStatement transaction);

    // 🗑️ Delete
    String deleteTransaction(Long id);

    // 📄 NEW: Statement (DTO) search with filtering/sorting/pagination
    List<TransactionStatementDTO> searchStatement(
            String accountNumber,
            String type,        // "ALL" | "DEBIT" | "CREDIT"
            String from,        // yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss (optional)
            String to,          // same; if date-only: exclusive next day 00:00
            String sortBy,      // transactionDate | amount | paymentMethod | status | transactionId
            String direction,   // ASC | DESC
            Integer page,       // default 0
            Integer size        // default 50
    );
    
    List<TransactionStatementDTO> getLatestStatementDTO(String accountNumber, int count);

    // 🆔 NEW: Customer ID based operations
    List<TransactionStatement> getAllTransactionsByCustomerId(String customerId);
    List<TransactionStatement> getLatestTransactionsByCustomerId(String customerId, int limit);
    List<TransactionStatement> getLastSixMonthsTransactionsByCustomerId(String customerId);
    List<TransactionStatementDTO> getLatestStatementDTOByCustomerId(String customerId, int count);
    List<TransactionStatementDTO> searchStatementByCustomerId(
            String customerId,
            String type,        // "ALL" | "DEBIT" | "CREDIT"
            String from,        // yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss (optional)
            String to,          // same; if date-only: exclusive next day 00:00
            String sortBy,      // transactionDate | amount | paymentMethod | status | transactionId
            String direction,   // ASC | DESC
            Integer page,       // default 0
            Integer size        // default 50
    );

    // 🔄 Fund Transfer Synchronization
    String syncAllFundTransfers();
    String syncRecentFundTransfers(int limit);
    String syncFundTransfersByAccount(String accountNumber);
    String syncFundTransfersByCustomer(String customerId);
    String syncCompletedFundTransfers();

}
