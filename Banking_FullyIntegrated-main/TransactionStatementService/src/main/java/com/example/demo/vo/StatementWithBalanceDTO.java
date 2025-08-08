package com.example.demo.vo;

import java.math.BigDecimal;
import java.util.List;

public class StatementWithBalanceDTO {

    private String accountNumber;
    private String customerId;
    private String holderName;
    private BigDecimal balance;
    private List<TransactionStatementDTO> transactions;

    // Getters and setters
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getHolderName() {
        return holderName;
    }
    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<TransactionStatementDTO> getTransactions() {
        return transactions;
    }
    public void setTransactions(List<TransactionStatementDTO> transactions) {
        this.transactions = transactions;
    }
}

