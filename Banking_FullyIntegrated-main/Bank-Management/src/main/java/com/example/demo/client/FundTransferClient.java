package com.example.demo.client;

import com.example.demo.dto.TransferRequest;
import com.example.demo.entities.Transactions;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "Fund-Transfer-Service", url = "http://localhost:8083")
public interface FundTransferClient {

        String BASE_URL = "http://localhost:8083";

        @PostMapping("/api/{customerId}/{accountNumber}/self-deposit")
        ResponseEntity<?> selfDeposit(
                        @PathVariable Long customerId,
                        @PathVariable Long accountNumber,
                        @RequestBody TransferRequest request);

        @PostMapping("/api/{customerId}/{accountNumber}/payee-transfer/{payee_id}")
        ResponseEntity<?> payeeTransfer(
                        @PathVariable Long customerId,
                        @PathVariable Long accountNumber,
                        @PathVariable("payee_id") Long payeeId,
                        @RequestBody TransferRequest request);

        @PostMapping("/api/{customerId}/{accountNumber}/manual-transfer")
        ResponseEntity<?> manualTransfer(
                        @PathVariable Long customerId,
                        @PathVariable Long accountNumber,
                        @RequestBody TransferRequest request);
}
