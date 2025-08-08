package com.example.demo.dto;

import java.math.BigDecimal;

import com.example.demo.entities.PaymentMethod;
import com.example.demo.entities.TransactionType;

import jakarta.persistence.Column;

public class TransactionRequest {

    private Long customerId;
    private Long accountNumber;
    private BigDecimal amount;
    private PaymentMethod paymentMethod; // Example: SELF_DEPOSIT
    private String remarks;

    // Add this for payee transfers
    private Long payeeId;
    @Column(name = "transaction_password", nullable = false, length = 100)
    private String transactionPassword;

    // Getters and setters

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountno) {
        this.accountNumber = accountno;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(Long payeeId) {
        this.payeeId = payeeId;
    }

    public String getTransactionPassword() {
        return transactionPassword;
    }

    public void setTransactionPassword(String transactionPassword) {
        this.transactionPassword = transactionPassword;
    }
}
