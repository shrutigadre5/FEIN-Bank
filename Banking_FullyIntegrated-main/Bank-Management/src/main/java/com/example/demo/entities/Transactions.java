package com.example.demo.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity(name = "Transactions")
@Table(name = "TRANSACTIONS")
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_seq")
    @SequenceGenerator(name = "transaction_id_seq", sequenceName = "TRANSACTION_ID_SEQ", allocationSize = 1)
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "sender_account_no")
    private Long senderAccountNo;

    @Column(name = "receiver_account_no")
    private Long receiverAccountNo;

    @Column(name = "accountno")
    private Long accountNo;

    @Column(name = "customerid")
    private Long customerId;
    
    @Column(name = "transaction_type", nullable = false, length = 20)
    private String transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Column(name = "amount", nullable = false)
    private double amount;

   
    @Column(name = "status", nullable = false, length = 15)
    private String status;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "transaction_date", nullable = true)
    private LocalDateTime transactionDate;

    @Column(name = "entry_timestamp")
    private LocalDateTime entryTimestamp;

    @Column(name = "balance_after_txn")
    private double balanceAfterTxn;

    @Column(name = "payee_id", nullable = true)
    private Long payeeId; // Link to Payee (manual FK if needed)

    // Getters and Setters

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getSenderAccountNo() {
        return senderAccountNo;
    }

    public void setSenderAccountNo(Long senderAccountNo) {
        this.senderAccountNo = senderAccountNo;
    }

    public Long getReceiverAccountNo() {
        return receiverAccountNo;
    }

    public void setReceiverAccountNo(Long receiverAccountNo) {
        this.receiverAccountNo = receiverAccountNo;
    }

    public Long getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(Long accountNo) {
        this.accountNo = accountNo;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String string) {
        this.transactionType = string;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public LocalDateTime getEntryTimestamp() {
        return entryTimestamp;
    }

    public void setEntryTimestamp(LocalDateTime entryTimestamp) {
        this.entryTimestamp = entryTimestamp;
    }

    public double getBalanceAfterTxn() {
        return balanceAfterTxn;
    }

    public void setBalanceAfterTxn(double balanceAfterTxn) {
        this.balanceAfterTxn = balanceAfterTxn;
    }

    public Long getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(Long payeeId) {
        this.payeeId = payeeId;
    }
}
