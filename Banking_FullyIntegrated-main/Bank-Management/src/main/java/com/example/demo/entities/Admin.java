package com.example.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//POJO
@Entity
@Table


public class Admin {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment (if supported)
    private Long adminId;
	@Column(unique = true, nullable = false)
    private String username;
    private String name;
    private String password;
    
	public Admin(Long adminId, String username, String name, String password) {
		super();
		this.adminId = adminId;
		this.username = username;
		this.name = name;
		this.password = password;
	}

	public Admin() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
    
}
