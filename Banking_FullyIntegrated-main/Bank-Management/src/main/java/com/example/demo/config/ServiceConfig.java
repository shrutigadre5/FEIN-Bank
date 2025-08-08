package com.example.demo.config;

import com.example.demo.client.FundTransferClient;
import com.example.demo.client.PayeeClient;
import com.example.demo.repos.TransactionsRepository;
import com.example.demo.service.PayeeService;
import com.example.demo.service.TransactionService;
import com.example.demo.service.impl.FundTransferServiceImpl;
import com.example.demo.service.impl.PayeeServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public PayeeService payeeService(PayeeClient payeeClient) {
        return new PayeeServiceImpl(payeeClient);
    }

    @Bean
    public TransactionService transactionService(FundTransferClient fundTransferClient,
            TransactionsRepository transactionsRepository) {
        return new FundTransferServiceImpl(fundTransferClient, transactionsRepository);
    }
}
