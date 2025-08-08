package com.example.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "Customer-Service", url = "http://localhost:8081")
public interface CustomerClient {

    @GetMapping("/api/customers/{customerId}")
    Map<String, Object> getCustomerById(@PathVariable("customerId") Long customerId);

    @GetMapping("/api/customers/{customerId}/accounts")
    List<Map<String, Object>> getCustomerAccounts(@PathVariable("customerId") Long customerId);

    @GetMapping("/api/customers")
    List<Map<String, Object>> getAllCustomers();
}
