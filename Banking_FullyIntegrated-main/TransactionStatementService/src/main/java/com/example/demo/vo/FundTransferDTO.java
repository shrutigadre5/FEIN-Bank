package com.example.demo.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FundTransferDTO {
    private Long transferId;
    private String fromAccountNumber;
    private String toAccountNumber;
    private String fromCustomerId;
    private String toCustomerId;
    private BigDecimal amount;
    private String transferType; // IMPS, NEFT, RTGS, UPI, etc.
    private String status; // PENDING, COMPLETED, FAILED, CANCELLED
    private String remarks;
    private String referenceNumber;
    private LocalDateTime transferDate;
    private LocalDateTime completedDate;
    private BigDecimal charges;
    private String failureReason;

    // Default constructor
    public FundTransferDTO() {}

    // Getters and Setters
    public Long getTransferId() { return transferId; }
    public void setTransferId(Long transferId) { this.transferId = transferId; }

    public String getFromAccountNumber() { return fromAccountNumber; }
    public void setFromAccountNumber(String fromAccountNumber) { this.fromAccountNumber = fromAccountNumber; }

    public String getToAccountNumber() { return toAccountNumber; }
    public void setToAccountNumber(String toAccountNumber) { this.toAccountNumber = toAccountNumber; }

    public String getFromCustomerId() { return fromCustomerId; }
    public void setFromCustomerId(String fromCustomerId) { this.fromCustomerId = fromCustomerId; }

    public String getToCustomerId() { return toCustomerId; }
    public void setToCustomerId(String toCustomerId) { this.toCustomerId = toCustomerId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getTransferType() { return transferType; }
    public void setTransferType(String transferType) { this.transferType = transferType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public LocalDateTime getTransferDate() { return transferDate; }
    public void setTransferDate(LocalDateTime transferDate) { this.transferDate = transferDate; }

    public LocalDateTime getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDateTime completedDate) { this.completedDate = completedDate; }

    public BigDecimal getCharges() { return charges; }
    public void setCharges(BigDecimal charges) { this.charges = charges; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
}
