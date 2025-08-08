package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
// (Optional) RestTemplate bean if you still use it elsewhere
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.demo.clients")
@EnableScheduling
public class TransactionStatementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionStatementServiceApplication.class, args);
        System.out.println("Transaction Statement started...");
    }

    @Bean
    public RestTemplate restTemplate() { // only if you still need it
        return new RestTemplate();
    }
}
