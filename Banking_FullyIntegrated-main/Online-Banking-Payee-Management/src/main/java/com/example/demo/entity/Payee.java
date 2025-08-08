package com.example.demo.entity;

import java.sql.Timestamp;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "payee")
public class Payee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payee_seq")
    @SequenceGenerator(name = "payee_seq", sequenceName = "PAYEE_SEQ", allocationSize = 1)
    @Column(name = "ID") // maps to ID in Oracle table
    private Long payeeId;

    @NotNull(message = "Customer ID is required")
    @Column(name = "CUSTOMERID", nullable = false)
    private Long customerId;

    @NotNull(message = "Payee account number is required")
    @Column(name = "PAYEE_ACCOUNT_NUMBER", nullable = false)
    private Long payeeAccountNumber;

    @NotBlank(message = "Payee name is required")
    @Column(name = "PAYEE_NAME")
    private String payeeName;

    @Column(name = "NICKNAME")
    private String nickname;

    @Column(name = "BANK_NAME")
    private String bankName;

    @Column(name = "IFSC_CODE")
    private String ifscCode;

    @Column(name = "ADDED_AT")
    private Timestamp addedAt = new Timestamp(System.currentTimeMillis());

    // Getters and Setters

    public Long getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(Long payeeId) {
        this.payeeId = payeeId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getPayeeAccountNumber() {
        return payeeAccountNumber;
    }

    public void setPayeeAccountNumber(Long payeeAccountNumber) {
        this.payeeAccountNumber = payeeAccountNumber;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public Timestamp getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
    }
    
    @Override
    public String toString() {
        return "Payee{" +
                "payeeId=" + payeeId +
                ", customerId=" + customerId +
                ", payeeAccountNumber=" + payeeAccountNumber +
                ", payeeName='" + payeeName + '\'' +
                ", nickname='" + nickname + '\'' +
                ", bankName='" + bankName + '\'' +
                ", ifscCode='" + ifscCode + '\'' +
                ", addedAt=" + addedAt +
                '}';
    }
}
