package com.example.demo.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Entity
@Table(name = "transactions")
public class TransactionStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "txn_seq")
    @SequenceGenerator(
        name = "txn_seq",
        sequenceName = "TRANSACTION_SEQ", 
        allocationSize = 1
    )
    @Column(name = "TRANSACTION_ID")
    private Long transactionId;

    @Column(name = "SENDER_ACCOUNT_NO")
    private String senderAccountNo;

    @Column(name = "RECEIVER_ACCOUNT_NO")
    private String receiverAccountNo;

    @Column(name = "PAYMENT_METHOD")
    private String paymentMethod;

    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "REMARKS")
    private String remarks;
    @Column(name = "ACCOUNTNO")
    private Long accountNo; // newly added

    @Column(name = "CUSTOMERID")
    private Long customerId; // newly added
    @Column(name = "BALANCE_AFTER_TXN")
    private Long BALANCE_AFTER_TXN;
    
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

	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TRANSACTION_DATE")
    private Date transactionDate;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

 
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public String getSenderAccountNo() { return senderAccountNo; }
    public void setSenderAccountNo(String senderAccountNo) { this.senderAccountNo = senderAccountNo; }

    public String getReceiverAccountNo() { return receiverAccountNo; }
    public void setReceiverAccountNo(String receiverAccountNo) { this.receiverAccountNo = receiverAccountNo; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Date getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Date transactionDate) { this.transactionDate = transactionDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getBalanceAfterTxn() { return BALANCE_AFTER_TXN; }
    public void setBalanceAfterTxn(Long balanceAfterTxn) { this.BALANCE_AFTER_TXN = balanceAfterTxn; }

    // convenience helper if you prefer LocalDateTime for transactionDate
    @Transient
    public LocalDateTime getTransactionDateAsLocalDateTime() {
        if (transactionDate == null) return null;
        return LocalDateTime.ofInstant(transactionDate.toInstant(), ZoneId.systemDefault());
    }
}
