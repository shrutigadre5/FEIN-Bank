package com.oracle.banking.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oracle.banking.entities.AccountRequest;

public interface AccountRequestRepository 
	extends JpaRepository<AccountRequest, Long>{

}
