package com.oracle.banking.entities;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name="Account_Request")
public class AccountRequest {
	@Id
	@Column(name="ACCOUNT_REQUEST_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accountreq_seq")
	@SequenceGenerator(name = "accountreq_seq", sequenceName = "ACCOUNTREQ_SEQ", allocationSize = 1)
    private Long accountRequestID;

	@Column(name = "TITLE")
    private String title;
	
	@Column(name = "FIRST_NAME")
    private String firstName;
	
	@Column(name = "MIDDLE_NAME")
    private String middleName;
	
	@Column(name = "LAST_NAME")
    private String lastName;
	
	@Column(name = "MOBILE_NO")
    private String mobileNo;
	
	@Column(name = "EMAIL")
    private String email;
	
	@Column(name = "AADHAR_NO")
    private String aadharNo;
	
	@Column(name = "PAN_NO")
    private String panNo;

    //@Temporal(TemporalType.DATE)
    private LocalDate dob;

    @Column(name = "RESIDENTIAL_ADDRESS")
    private String residentialAddress;
    
    @Column(name = "PERMANENT_ADDRESS")
    private String permanentAddress;
    
    @Column(name = "OCCUPATION")
    private String occupation;
    
    @Column(name = "ANNUAL_INCOME")
    private Double annualIncome;
    
    @Column(name = "ACCOUNT_TYPE")
    private String accountType;

    //@Temporal(TemporalType.DATE)
    @Column(name = "APPLICATION_DATE")
    private LocalDate applicationDate;

	public AccountRequest(Long accountRequestID, String title, String firstName, String middleName, String lastName,
			String mobileNo, String email, String aadharNo, String panNo, LocalDate dob, String residentialAddress,
			String permanentAddress, String occupation, Double annualIncome, String accountType,
			LocalDate applicationDate) {
		super();
		this.accountRequestID = accountRequestID;
		this.title = title;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.mobileNo = mobileNo;
		this.email = email;
		this.aadharNo = aadharNo;
		this.panNo = panNo;
		this.dob = dob;
		this.residentialAddress = residentialAddress;
		this.permanentAddress = permanentAddress;
		this.occupation = occupation;
		this.annualIncome = annualIncome;
		this.accountType = accountType;
		this.applicationDate = applicationDate;
	}

	public AccountRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

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
