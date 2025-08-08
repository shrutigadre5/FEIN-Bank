package com.example.demo.clients.fallback;

import com.example.demo.clients.FundTransferClient;
import com.example.demo.vo.FundTransferDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FundTransferClientFallback implements FundTransferClient {

    @Override
    public List<FundTransferDTO> getAllFundTransfers() {
        System.out.println("FundTransferClient fallback: getAllFundTransfers - Fund Transfer Service unavailable");
        return new ArrayList<>();
    }

    @Override
    public List<FundTransferDTO> getFundTransfersByAccount(String accountNumber) {
        System.out.println("FundTransferClient fallback: getFundTransfersByAccount - Fund Transfer Service unavailable");
        return new ArrayList<>();
    }

    @Override
    public List<FundTransferDTO> getFundTransfersByCustomer(String customerId) {
        System.out.println("FundTransferClient fallback: getFundTransfersByCustomer - Fund Transfer Service unavailable");
        return new ArrayList<>();
    }

    @Override
    public List<FundTransferDTO> getRecentFundTransfers(int limit) {
        System.out.println("FundTransferClient fallback: getRecentFundTransfers - Fund Transfer Service unavailable");
        return new ArrayList<>();
    }

    @Override
    public List<FundTransferDTO> getPendingFundTransfers() {
        System.out.println("FundTransferClient fallback: getPendingFundTransfers - Fund Transfer Service unavailable");
        return new ArrayList<>();
    }

    @Override
    public List<FundTransferDTO> getCompletedFundTransfers() {
        System.out.println("FundTransferClient fallback: getCompletedFundTransfers - Fund Transfer Service unavailable");
        return new ArrayList<>();
    }
}
