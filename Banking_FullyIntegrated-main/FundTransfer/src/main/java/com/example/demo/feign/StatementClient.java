package com.example.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.entities.Transactions;

@FeignClient(name = "TransactionStatementService", url = "http://localhost:8085")
public interface StatementClient {

    @PostMapping("/api/statements/record")
    void recordStatement(@RequestBody Transactions transaction);
}