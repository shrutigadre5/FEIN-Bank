package com.example.demo.repos;

import com.example.demo.entities.TransactionStatement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TransactionStatementRepo extends JpaRepository<TransactionStatement, Long> {

    List<TransactionStatement> findBySenderAccountNoOrReceiverAccountNoOrderByTransactionDateDesc(
            String senderAccountNo,
            String receiverAccountNo
    );

    @Query("SELECT t FROM TransactionStatement t " +
           "WHERE t.senderAccountNo = :accountNo OR t.receiverAccountNo = :accountNo " +
           "ORDER BY t.transactionDate DESC")
    List<TransactionStatement> findLatestForAccount(
            @Param("accountNo") String accountNo,
            Pageable pageable
    );

    @Query("SELECT t FROM TransactionStatement t " +
           "WHERE (t.senderAccountNo = :accountNo OR t.receiverAccountNo = :accountNo) " +
           "AND t.transactionDate >= :fromTs " +
           "AND t.transactionDate <  :toTs " +
           "ORDER BY t.transactionDate DESC")
    List<TransactionStatement> findBetweenForAccount(
            @Param("accountNo") String accountNo,
            @Param("fromTs") Date fromTs,
            @Param("toTs") Date toTs
    );

    @Query("SELECT t FROM TransactionStatement t " +
           "WHERE (" +
           "   (:type = 'ALL' AND (t.senderAccountNo = :accountNo OR t.receiverAccountNo = :accountNo)) " +
           "   OR (:type = 'DEBIT'  AND t.senderAccountNo  = :accountNo) " +
           "   OR (:type = 'CREDIT' AND t.receiverAccountNo = :accountNo)" +
           ") " +
           "AND (:fromTs IS NULL OR t.transactionDate >= :fromTs) " +
           "AND (:toTs   IS NULL OR t.transactionDate <  :toTs)")
    List<TransactionStatement> searchForAccount(
            @Param("accountNo") String accountNo,
            @Param("type") String type,     
            @Param("fromTs") Date fromTs,   
            @Param("toTs") Date toTs,       
            Pageable pageable
    );

    // Find transactions by fund transfer reference number to avoid duplicates
    @Query("SELECT t FROM TransactionStatement t WHERE t.remarks LIKE %:referenceNumber%")
    List<TransactionStatement> findByFundTransferReference(@Param("referenceNumber") String referenceNumber);

    // Search by the new ACCOUNTNO field
    @Query("SELECT t FROM TransactionStatement t " +
           "WHERE t.accountNo = :accountNo " +
           "AND (:fromTs IS NULL OR t.transactionDate >= :fromTs) " +
           "AND (:toTs IS NULL OR t.transactionDate < :toTs) " +
           "ORDER BY t.transactionDate DESC")
    List<TransactionStatement> searchByAccountNo(
            @Param("accountNo") Long accountNo,
            @Param("type") String type,
            @Param("fromTs") Date fromTs,
            @Param("toTs") Date toTs,
            Pageable pageable
    );

    // Find all transactions by accountNo field
    @Query("SELECT t FROM TransactionStatement t " +
           "WHERE t.accountNo = :accountNo " +
           "ORDER BY t.transactionDate DESC")
    List<TransactionStatement> findByAccountNoOrderByTransactionDateDesc(@Param("accountNo") Long accountNo);

    // Find latest transactions by accountNo field
    @Query("SELECT t FROM TransactionStatement t " +
           "WHERE t.accountNo = :accountNo " +
           "ORDER BY t.transactionDate DESC")
    List<TransactionStatement> findLatestByAccountNo(@Param("accountNo") Long accountNo, Pageable pageable);
}
