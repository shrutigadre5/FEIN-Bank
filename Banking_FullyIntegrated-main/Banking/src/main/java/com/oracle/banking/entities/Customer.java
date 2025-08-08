package com.oracle.banking.entities;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "Customer")
public class Customer {
	
	@Id
	@Column(name="CustomerID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
	@SequenceGenerator(name = "customer_seq", sequenceName = "CUSTOMER_SEQ", allocationSize = 1)
    private Long customerID;

	
    @Column(name="Title")
    private String title;

    @Column(name="FirstName")
    private String firstName;

    @Column(name="MiddleName")
    private String middleName;

    @Column(name="LastName")
    private String lastName;

    @Column(name="MobileNo")
    private String mobileNo;

    @Column(name="Email")
    private String email;

    @Column(name="AadharNo")
    private String aadharNo;

    @Column(name="PanNo")
    private String panNo;

    @Column(name="DOB")
    //@Temporal(TemporalType.DATE)
    private LocalDate dob;

    @Column(name="ResidentialAddress")
    private String residentialAddress;

    @Column(name="PermanentAddress")
    private String permanentAddress;

    @Column(name="Occupation")
    private String occupation;

    @Column(name="AnnualIncome")
    private Double annualIncome;

    @Column(name="LoginPassword")
    private String loginPassword;

	public Customer(Long customerID, String title, String firstName, String middleName, String lastName,
			String mobileNo, String email, String aadharNo, String panNo, LocalDate dob, String residentialAddress,
			String permanentAddress, String occupation, Double annualIncome, String loginPassword) {
		super();
		this.customerID = customerID;
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
		this.loginPassword = loginPassword;
	}

	public Customer() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getCustomerID() {
		return customerID;
	}

	public void setCustomerID(Long customerID) {
		this.customerID = customerID;
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

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

}
