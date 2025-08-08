package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Configuration
public class FundTransferSyncConfig {

    @Component
    @ConfigurationProperties(prefix = "fundtransfer.sync")
    public static class SyncProperties {
        private int batchSize = 100;
        private int recentLimit = 50;
        private boolean enableScheduler = true;
        private long syncIntervalMinutes = 5;

        // Getters and Setters
        public int getBatchSize() { return batchSize; }
        public void setBatchSize(int batchSize) { this.batchSize = batchSize; }

        public int getRecentLimit() { return recentLimit; }
        public void setRecentLimit(int recentLimit) { this.recentLimit = recentLimit; }

        public boolean isEnableScheduler() { return enableScheduler; }
        public void setEnableScheduler(boolean enableScheduler) { this.enableScheduler = enableScheduler; }

        public long getSyncIntervalMinutes() { return syncIntervalMinutes; }
        public void setSyncIntervalMinutes(long syncIntervalMinutes) { this.syncIntervalMinutes = syncIntervalMinutes; }
    }
}
