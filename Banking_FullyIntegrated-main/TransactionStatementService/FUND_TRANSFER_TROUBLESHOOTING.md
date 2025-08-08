# Fund Transfer Service Integration Troubleshooting

## Common Issues and Solutions

### 1. Fund Transfer Service Unavailable Error

**Error Message:**
```
Error syncing recent fund transfers: [500] during [GET] to [http://FUNDTRANSFER/api/fundtransfer/recent?limit=50] [FundTransferClient#getRecentFundTransfers(int)]: [{"error":"An unexpected error occurred: No static resource api/fundtransfer/recent."}]
```

**Possible Causes:**
1. Fund Transfer Service is not running
2. Fund Transfer Service is not registered in Eureka
3. Fund Transfer Service endpoints don't exist
4. Network connectivity issues

**Solutions:**

#### Solution 1: Check Fund Transfer Service Status
```bash
# Check if service is running
GET http://localhost:8085/api/transactions/health/fundtransfer
```

#### Solution 2: Disable Scheduled Sync (Temporary)
Add to `application.properties`:
```properties
fundtransfer.sync.enable-scheduler=false
```

#### Solution 3: Use Manual Sync with Health Check
The system now includes health checks before attempting sync operations:
```bash
POST http://localhost:8085/api/transactions/sync/fundtransfers/recent
```

#### Solution 4: Check Eureka Registry
1. Open Eureka Dashboard: `http://localhost:8761`
2. Verify `FUNDTRANSFER` service is registered
3. Check service instances and health status

#### Solution 5: Use Fallback Configuration
The system now includes fallback handling - when the Fund Transfer Service is unavailable:
- Sync operations return friendly error messages
- Scheduled tasks continue running without failing
- Empty lists are returned instead of exceptions

### 2. Service Configuration

#### Current Fallback Behavior:
- **Scheduler**: Continues running, logs warnings when service unavailable
- **Manual Sync**: Returns clear status messages about service availability
- **Health Check**: Available at `/api/transactions/health/fundtransfer`

#### Circuit Breaker Configuration:
```properties
feign.hystrix.enabled=true
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=15000
hystrix.command.default.circuitBreaker.requestVolumeThreshold=5
hystrix.command.default.circuitBreaker.sleepWindowInMilliseconds=30000
```

### 3. Monitoring and Logging

#### Check Application Logs:
Look for these log messages:
- `FundTransferClient fallback: ... - Fund Transfer Service unavailable`
- `Fund Transfer Service health check failed`
- `⚠️ Fund Transfer Service appears to be unavailable`

#### Health Check Endpoint:
```bash
GET http://localhost:8085/api/transactions/health/fundtransfer
```

**Response Examples:**
- ✅ `Fund Transfer Service: ✅ Available`
- ❌ `Fund Transfer Service: ❌ Unavailable`

### 4. Development Mode Setup

#### Option 1: Mock Fund Transfer Service
Create a simple mock service or use Wiremock:
```bash
# Start Wiremock on port 8081
java -jar wiremock-standalone.jar --port 8081
```

#### Option 2: Disable Fund Transfer Integration
```properties
fundtransfer.sync.enable-scheduler=false
```

#### Option 3: Use Local Testing
Update service name in `FundTransferClient`:
```java
@FeignClient(name = "FUNDTRANSFER", url = "http://localhost:8081")
```

### 5. Production Deployment Checklist

- [ ] Fund Transfer Service is deployed and running
- [ ] Fund Transfer Service is registered in Eureka
- [ ] All required endpoints are implemented:
  - `/api/fundtransfer/all`
  - `/api/fundtransfer/recent`
  - `/api/fundtransfer/completed`
  - `/api/fundtransfer/account/{accountNumber}`
  - `/api/fundtransfer/customer/{customerId}`
- [ ] Network connectivity between services
- [ ] Circuit breaker thresholds are appropriate
- [ ] Monitoring and alerting are configured

### 6. API Endpoints for Testing

#### Manual Sync (with health check):
```bash
POST http://localhost:8085/api/transactions/sync/fundtransfers/recent?limit=10
POST http://localhost:8085/api/transactions/sync/fundtransfers/completed
POST http://localhost:8085/api/transactions/sync/fundtransfers/all
```

#### Health Check:
```bash
GET http://localhost:8085/api/transactions/health/fundtransfer
```

#### View Existing Transactions:
```bash
GET http://localhost:8085/api/transactions/customer/1001/statement/latest?limit=10
```

### 7. Error Messages Reference

| Error | Meaning | Action |
|-------|---------|--------|
| `Fund Transfer Service is currently unavailable` | Pre-sync health check failed | Check service status, try again later |
| `No recent fund transfers available to sync` | Service responded but no data | Normal behavior, no action needed |
| `Fund Transfer Service is unavailable` | Sync operation failed | Check service logs, verify endpoints |
| `FundTransferClient fallback` | Circuit breaker activated | Service will auto-recover, monitor status |

### 8. Configuration Reference

```properties
# Enable/disable scheduler
fundtransfer.sync.enable-scheduler=true

# Sync frequency (minutes)
fundtransfer.sync.sync-interval-minutes=5

# Number of recent records to sync
fundtransfer.sync.recent-limit=50

# Feign timeouts
feign.client.config.default.connectTimeout=5000
feign.client.config.default.readTimeout=10000

# Circuit breaker settings
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=15000
```
