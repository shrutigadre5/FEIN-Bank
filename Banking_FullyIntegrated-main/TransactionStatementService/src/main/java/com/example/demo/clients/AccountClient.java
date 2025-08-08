package com.example.demo.clients;

import com.example.demo.vo.AccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
	    name = "Bank-Management"  
	)
	public interface AccountClient {

	    @GetMapping("/api/accounts/{accountNumber}")
	    AccountDTO getAccountByNumber(@PathVariable("accountNumber") String accountNumber);
	    
	    @GetMapping("/api/accounts/customer/{customerId}")
	    AccountDTO getAccountByCustomerId(@PathVariable("customerId") String customerId);
	}


