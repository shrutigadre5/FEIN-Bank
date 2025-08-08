package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddPayeeRequest {
    
    @NotNull(message = "Payee account number is required")
    private Long payeeAccountNumber;

    @NotBlank(message = "Payee name is required")
    private String payeeName;

    private String nickname;

    private String bankName;

    private String ifscCode;

    // Getters and Setters
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

    @Override
    public String toString() {
        return "AddPayeeRequest{" +
                "payeeAccountNumber=" + payeeAccountNumber +
                ", payeeName='" + payeeName + '\'' +
                ", nickname='" + nickname + '\'' +
                ", bankName='" + bankName + '\'' +
                ", ifscCode='" + ifscCode + '\'' +
                '}';
    }
}
