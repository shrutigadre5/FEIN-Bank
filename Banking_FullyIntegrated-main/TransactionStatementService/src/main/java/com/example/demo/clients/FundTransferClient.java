package com.example.demo.clients;

import com.example.demo.clients.fallback.FundTransferClientFallback;
import com.example.demo.vo.FundTransferDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
    name = "FundTransfer",  // Service name as registered in Eureka
    fallback = FundTransferClientFallback.class
)
public interface FundTransferClient {

    @GetMapping("/api/fundtransfer/all")
    List<FundTransferDTO> getAllFundTransfers();

    @GetMapping("/api/fundtransfer/account/{accountNumber}")
    List<FundTransferDTO> getFundTransfersByAccount(@PathVariable("accountNumber") String accountNumber);

    @GetMapping("/api/fundtransfer/customer/{customerId}")
    List<FundTransferDTO> getFundTransfersByCustomer(@PathVariable("customerId") String customerId);

    @GetMapping("/api/fundtransfer/recent")
    List<FundTransferDTO> getRecentFundTransfers(@RequestParam(defaultValue = "100") int limit);

    @GetMapping("/api/fundtransfer/pending")
    List<FundTransferDTO> getPendingFundTransfers();

    @GetMapping("/api/fundtransfer/completed")
    List<FundTransferDTO> getCompletedFundTransfers();
}
