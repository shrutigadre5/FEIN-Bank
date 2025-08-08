package com.example.demo.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionStatementDTO {

    private Long transactionId;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String paymentMethod;
    private String status;
    private String remarks;

   
    private String txnType;           
    private String counterpartyAccount;
    private Long balanceAfterTxn;

    public TransactionStatementDTO() {}

    public TransactionStatementDTO(
            Long transactionId,
            BigDecimal amount,
            LocalDateTime transactionDate,
            String paymentMethod,
            String status,
            String remarks,
            String txnType,
            String counterpartyAccount,
            Long balanceAfterTxn
    ) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.remarks = remarks;
        this.txnType = txnType;
        this.counterpartyAccount = counterpartyAccount;
        this.balanceAfterTxn = balanceAfterTxn;
    }

    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getTxnType() { return txnType; }
    public void setTxnType(String txnType) { this.txnType = txnType; }

    public String getCounterpartyAccount() { return counterpartyAccount; }
    public void setCounterpartyAccount(String counterpartyAccount) { this.counterpartyAccount = counterpartyAccount; }

    public Long getBalanceAfterTxn() { return balanceAfterTxn; }
    public void setBalanceAfterTxn(Long balanceAfterTxn) { this.balanceAfterTxn = balanceAfterTxn; }
}

