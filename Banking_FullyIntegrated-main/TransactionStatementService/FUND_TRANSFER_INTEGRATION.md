# Fund Transfer Integration

This document describes the fund transfer integration functionality added to the Transaction Statement Service.

## Overview

The service now automatically fetches fund transfer data from the Fund Transfer Service and converts them into transaction statements that are stored in the database. This ensures that all fund transfers are properly recorded as transactions for historical and reporting purposes.

## New Endpoints

### Fund Transfer Synchronization

All fund transfer sync endpoints use POST method and return a status message.

#### 1. Sync All Fund Transfers
```
POST /api/transactions/sync/fundtransfers/all
```
- Fetches all fund transfers from the Fund Transfer Service and adds them to the transaction database
- Returns: Status message with count of synced transactions

#### 2. Sync Recent Fund Transfers
```
POST /api/transactions/sync/fundtransfers/recent?limit=100
```
- Parameters:
  - `limit` (optional): Number of recent fund transfers to sync (default: 100)
- Returns: Status message with count of synced transactions

#### 3. Sync Fund Transfers by Account
```
POST /api/transactions/sync/fundtransfers/account/{accountNumber}
```
- Parameters:
  - `accountNumber`: The account number to sync fund transfers for
- Returns: Status message with count of synced transactions

#### 4. Sync Fund Transfers by Customer ID
```
POST /api/transactions/sync/fundtransfers/customer/{customerId}
```
- Parameters:
  - `customerId`: The customer ID to sync fund transfers for
- Returns: Status message with count of synced transactions

#### 5. Sync Completed Fund Transfers Only
```
POST /api/transactions/sync/fundtransfers/completed
```
- Syncs only fund transfers with "COMPLETED" status
- Returns: Status message with count of synced transactions

## Customer ID Based Transaction Endpoints

### 1. Get All Transactions by Customer ID
```
GET /api/transactions/customer/{customerId}
```

### 2. Get Latest Transactions by Customer ID
```
GET /api/transactions/customer/{customerId}/latest?limit=5
```

### 3. Get Last 6 Months Transactions by Customer ID
```
GET /api/transactions/customer/{customerId}/last6months
```

### 4. Get Statement by Customer ID
```
GET /api/transactions/customer/{customerId}/statement
```

### 5. Get Latest Statement by Customer ID
```
GET /api/transactions/customer/{customerId}/statement/latest?limit=5
```

### 6. Search Statement by Customer ID
```
GET /api/transactions/customer/{customerId}/statement/search?type=ALL&from=2024-01-01&to=2024-12-31&sortBy=transactionDate&direction=DESC&page=0&size=50
```

### 7. Get Statement with Balance by Customer ID
```
GET /api/transactions/customer/{customerId}/statementWithBalance?count=5
```

## Automatic Synchronization

The service includes a scheduler that automatically syncs fund transfers:

- **Every 5 minutes**: Syncs recent fund transfers (configurable)
- **Daily at 2 AM**: Syncs all completed fund transfers

### Configuration Properties

```properties
# Fund Transfer Sync Configuration
fundtransfer.sync.enable-scheduler=true
fundtransfer.sync.sync-interval-minutes=5
fundtransfer.sync.recent-limit=50
fundtransfer.sync.batch-size=100
```

## Data Mapping

Fund transfers are converted to transaction statements as follows:

| Fund Transfer Field | Transaction Statement Field | Notes |
|-------------------|---------------------------|-------|
| transferId | N/A | Not directly mapped |
| fromAccountNumber | senderAccountNo | Direct mapping |
| toAccountNumber | receiverAccountNo | Direct mapping |
| amount | amount | Direct mapping |
| transferType | paymentMethod | IMPS, NEFT, RTGS, UPI, etc. |
| status | status | COMPLETED → SUCCESS, FAILED → FAILED, etc. |
| remarks | remarks | Enhanced with "FT Ref: {referenceNumber}" |
| referenceNumber | remarks | Included in remarks for tracking |
| completedDate/transferDate | transactionDate | Uses completedDate if available |

## Duplicate Prevention

The system prevents duplicate transactions by checking for existing fund transfer reference numbers in the transaction remarks. Each fund transfer reference number can only be imported once.

## Error Handling

- All sync operations include comprehensive error handling
- Failed syncs are logged with detailed error messages
- The system continues processing even if individual fund transfers fail
- Status messages include counts of successful vs. total operations
