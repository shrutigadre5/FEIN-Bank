package com.example.demo.repo;

import com.example.demo.entities.PaymentMethod;
import com.example.demo.entities.TransactionStatus;
import com.example.demo.entities.TransactionType;
import com.example.demo.entities.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, Long> {


    // ✅ Correct field names
    List<Transactions> findBySenderAccountNo(Long senderAccountNo);
    List<Transactions> findByReceiverAccountNo(Long receiverAccountNo);
    List<Transactions> findByTransactionType(TransactionType transactionType);
    
    List<Transactions> findByPaymentMethodAndStatus(PaymentMethod paymentMethod, TransactionStatus status);

}
