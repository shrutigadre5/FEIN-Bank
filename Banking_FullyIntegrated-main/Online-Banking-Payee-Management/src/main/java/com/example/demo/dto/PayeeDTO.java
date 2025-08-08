package com.example.demo.dto;

public class PayeeDTO {
    private Long id;
    private long customerId;
    private String payeeName;
    private String nickname;
    private String bankName;
    private String ifscCode;
    private Long payeeAccountNumber;

    // Constructors
    public PayeeDTO() {}

    public PayeeDTO(Long id, long customerId, String payeeName, String nickname, String bankName, String ifscCode, Long payeeAccountNumber) {
        this.id = id;
        this.customerId = customerId;
        this.payeeName = payeeName;
        this.nickname = nickname;
        this.bankName = bankName;
        this.ifscCode = ifscCode;
        this.payeeAccountNumber = payeeAccountNumber;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
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

    public Long getPayeeAccountNumber() {
        return payeeAccountNumber;
    }

    public void setPayeeAccountNumber(Long payeeAccountNumber) {
        this.payeeAccountNumber = payeeAccountNumber;
    }
}
