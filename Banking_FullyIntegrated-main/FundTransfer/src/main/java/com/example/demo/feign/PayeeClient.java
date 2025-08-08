package com.example.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.entities.Payee;

@FeignClient(name = "Online-Banking-Payee-Management", url = "http://localhost:8082")
public interface PayeeClient {
    
    @GetMapping("/api/payees/{payeeId}")
    Payee getPayeeById(@PathVariable("payeeId") Long payeeId);
}