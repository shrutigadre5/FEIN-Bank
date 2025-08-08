package com.example.demo.entities;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "Account")
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accountno_seq")
	@SequenceGenerator(name = "accountno_seq", sequenceName = "ACCOUNTNO_SEQ", allocationSize = 1)
	@Column(name = "account_no") // Keep this consistent with your existing database column
	private Long accountNo;
	@Column(name = "AccountType")
	private String accountType;
	@Column(name = "ApplicationDate")
	// @Temporal(TemporalType.DATE)
	private LocalDate applicationDate;
	@Column(name = "Status")
	private String status;
	@Column(name = "Balance")
	private Double balance = 0.0;
	@Column(name = "TransactionPassword")
	private String transactionPassword;
	@ManyToOne
	@JoinColumn(name = "CUSTOMERID", nullable = false)
	private Customer customer;

	public Account(Long accountNo, String accountType, LocalDate applicationDate, String status, Double balance,
			String transactionPassword, Customer customer) {
		super();
		this.accountNo = accountNo;
		this.accountType = accountType;
		this.applicationDate = applicationDate;
		this.status = status;
		this.balance = balance;
		this.transactionPassword = transactionPassword;
		this.customer = customer;
	}

	public Account() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(Long accountNo) {
		this.accountNo = accountNo;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public String getTransactionPassword() {
		return transactionPassword;
	}

	public void setTransactionPassword(String transactionPassword) {
		this.transactionPassword = transactionPassword;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}
