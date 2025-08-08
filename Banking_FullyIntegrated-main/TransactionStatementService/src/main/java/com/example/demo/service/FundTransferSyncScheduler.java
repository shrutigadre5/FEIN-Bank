package com.example.demo.service;

import com.example.demo.config.FundTransferSyncConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@ConditionalOnProperty(value = "fundtransfer.sync.enable-scheduler", havingValue = "true", matchIfMissing = true)
public class FundTransferSyncScheduler {

    @Autowired
    private TransactionStatementService transactionService;

    @Autowired
    private FundTransferSyncConfig.SyncProperties syncProperties;

    // Sync recent fund transfers every 5 minutes (configurable)
    @Scheduled(fixedRateString = "#{${fundtransfer.sync.sync-interval-minutes:5} * 60000}")
    public void syncRecentFundTransfers() {
        try {
            System.out.println("Starting scheduled sync of recent fund transfers at: " + LocalDateTime.now());
            String result = transactionService.syncRecentFundTransfers(syncProperties.getRecentLimit());
            System.out.println("Scheduled sync completed: " + result);
            
            // If the result indicates service unavailability, log a warning but don't fail
            if (result.contains("unavailable")) {
                System.out.println("⚠️  Fund Transfer Service appears to be unavailable - skipping this sync cycle");
            }
            
        } catch (Exception e) {
            System.err.println("Error in scheduled fund transfer sync: " + e.getMessage());
            System.out.println("⚠️  Continuing with next scheduled sync cycle...");
        }
    }

    // Sync all completed fund transfers once a day at 2 AM
    @Scheduled(cron = "0 0 2 * * *")
    public void syncCompletedFundTransfersDaily() {
        try {
            System.out.println("Starting daily sync of completed fund transfers at: " + LocalDateTime.now());
            String result = transactionService.syncCompletedFundTransfers();
            System.out.println("Daily sync completed: " + result);
            
            // If the result indicates service unavailability, log a warning but don't fail
            if (result.contains("unavailable")) {
                System.out.println("⚠️  Fund Transfer Service appears to be unavailable - skipping daily sync");
            }
            
        } catch (Exception e) {
            System.err.println("Error in daily fund transfer sync: " + e.getMessage());
            System.out.println("⚠️  Will retry in next scheduled daily sync...");
        }
    }
}
