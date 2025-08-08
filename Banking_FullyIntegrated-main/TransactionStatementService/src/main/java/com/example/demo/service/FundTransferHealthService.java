package com.example.demo.service;

import com.example.demo.clients.FundTransferClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FundTransferHealthService {

    @Autowired
    private FundTransferClient fundTransferClient;

    public boolean isFundTransferServiceAvailable() {
        try {
            // Try to call a simple endpoint to check if service is available
            fundTransferClient.getRecentFundTransfers(1);
            return true;
        } catch (Exception e) {
            System.err.println("Fund Transfer Service health check failed: " + e.getMessage());
            return false;
        }
    }

    public String getFundTransferServiceStatus() {
        if (isFundTransferServiceAvailable()) {
            return "Fund Transfer Service: ✅ Available";
        } else {
            return "Fund Transfer Service: ❌ Unavailable";
        }
    }
}
