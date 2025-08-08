package com.example.demo.entities;

import jakarta.persistence.*;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Entity
@Table(name = "Account_Request")
public class AccountRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acc_req_seq")
    @SequenceGenerator(name = "acc_req_seq", sequenceName = "ACCOUNT_REQUEST_SEQ", allocationSize = 1)
    private Long accountRequestID;

    @Column(nullable = false, length = 5)
    @Pattern(regexp = "Mr|Mrs|Miss|Ms", message = "Invalid title")
    private String title;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(length = 50)
    private String middleName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 10)
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Invalid mobile number")
    private String mobileNo;

    @Column(nullable = false, unique = true, length = 100)
    @Email(message = "Invalid email format")
    private String email;

    @Column(nullable = false, unique = true, length = 12)
    @Pattern(regexp = "\\d{12}", message = "Aadhar number must be 12 digits")
    private String aadharNo;

    @Column(nullable = false, unique = true, length = 10)
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]$", message = "Invalid PAN format")
    private String panNo;

    @Column(nullable = false)
    private LocalDate dob;

    @Column(nullable = false, length = 255)
    private String residentialAddress;

    @Column(nullable = false, length = 255)
    private String permanentAddress;

    @Column(nullable = false, length = 10)
    @Pattern(regexp = "Business|Employee|Unemployed", message = "Invalid occupation")
    private String occupation;

    @DecimalMin(value = "0.0", inclusive = true, message = "Income must be non-negative")
    private Double annualIncome;

    @Pattern(regexp = "SAVINGS|CURRENT|SALARIED", message = "Invalid account type")
    @NotBlank(message = "Account type is required")
    private String accountType;


    @Column(nullable = false)
    private LocalDate applicationDate = LocalDate.now();

    // Getters and Setters

    public Long getAccountRequestID() {
        return accountRequestID;
    }

    public void setAccountRequestID(Long accountRequestID) {
        this.accountRequestID = accountRequestID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(String aadharNo) {
        this.aadharNo = aadharNo;
    }

    public String getPanNo() {
        return panNo;
    }

    public void setPanNo(String panNo) {
        this.panNo = panNo;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(String residentialAddress) {
        this.residentialAddress = residentialAddress;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public Double getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(Double annualIncome) {
        this.annualIncome = annualIncome;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }
}
