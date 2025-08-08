package com.oracle.banking.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oracle.banking.entities.Account;

public interface AccountRepository 
	extends JpaRepository<Account, Long>{

	long countByStatus(String string);
	
	Long countByAccountTypeIgnoreCase( String accountType);


}
