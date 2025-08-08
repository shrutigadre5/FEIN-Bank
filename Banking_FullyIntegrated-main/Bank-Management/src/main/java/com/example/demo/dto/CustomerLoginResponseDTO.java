package com.example.demo.dto;

public class CustomerLoginResponseDTO {
    private Long customerId;
    private String fullName;
    private String message;

    public CustomerLoginResponseDTO(Long customerId, String fullName, String message) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.message = message;
    }

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

    
}
