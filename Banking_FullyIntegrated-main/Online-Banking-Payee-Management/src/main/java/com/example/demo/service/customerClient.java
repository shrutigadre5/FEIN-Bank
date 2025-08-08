package com.example.demo.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.dto.CustomerDTO;

@FeignClient(name = "customerService", url = "http://localhost:8081")
public interface customerClient {
    @GetMapping("/api/customers/{customerId}")
    CustomerDTO getCustomerById(@PathVariable("customerId") String customerId);
}