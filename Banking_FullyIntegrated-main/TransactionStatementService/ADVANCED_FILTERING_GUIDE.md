# Transaction Statement Advanced Filtering

## Overview
Enhanced the Transaction Statement Service to support advanced filtering by both Customer ID and Account Number from URL parameters and form inputs.

## Changes Made

### 1. Backend Changes

#### TransactionStatementController.java
- **Added Advanced Search Endpoint**: `/api/transactions/advanced/statement/search`
  - Accepts both `customerId` and `accountNumber` parameters
  - Validates that the account belongs to the specified customer
  - Returns appropriate filtered results

#### Key Features:
- **Dual Parameter Validation**: When both customerId and accountNumber are provided, verifies they belong to the same customer
- **Flexible Filtering**: Supports filtering by customerId only, accountNumber only, or both
- **Error Handling**: Returns empty list if validation fails

### 2. Frontend Changes

#### index1.html
- **Added Customer ID Field**: New input field for Customer ID alongside Account Number
- **Updated UI**: Added info alert explaining filtering options
- **Enhanced Title**: Changed to "Advanced Search" to reflect new capabilities

#### app.js
- **URL Parameter Parsing**: Automatically reads `accountNo` and `customerId` from URL parameters
- **Smart Endpoint Selection**: 
  - Uses advanced endpoint when both parameters provided
  - Uses customer endpoint when only customerId provided
  - Uses account endpoint when only accountNumber provided
- **Enhanced Error Handling**: More specific error messages for different scenarios
- **Improved UI Feedback**: Shows filtering mode in results metadata

## Usage Examples

### 1. URL with both parameters:
```
http://localhost:8085/index1.html?accountNo=20001&customerId=1001
```

### 2. URL with customer ID only:
```
http://localhost:8085/index1.html?customerId=1001
```

### 3. URL with account number only:
```
http://localhost:8085/index1.html?accountNo=20001
```

### 4. URL with additional filters:
```
http://localhost:8085/index1.html?customerId=1001&type=DEBIT&from=2024-01-01&to=2024-12-31
```

## API Endpoints

### New Advanced Endpoint
```
GET /api/transactions/advanced/statement/search
Parameters:
- customerId (optional): Customer identifier
- accountNumber (optional): Account number
- type (optional): ALL, DEBIT, CREDIT (default: ALL)
- from (optional): Start date (yyyy-MM-dd)
- to (optional): End date (yyyy-MM-dd)
- sortBy (optional): Field to sort by (default: transactionDate)
- direction (optional): ASC, DESC (default: DESC)
- page (optional): Page number (default: 0)
- size (optional): Page size (default: 50)
```

## Validation Logic

1. **Both Parameters Provided**: 
   - Fetches customer account information
   - Verifies the provided account number matches the customer's account
   - Returns transactions for the verified account
   - Returns empty list if validation fails

2. **Customer ID Only**:
   - Uses existing customer-based endpoints
   - Automatically resolves to customer's account

3. **Account Number Only**:
   - Uses existing account-based endpoints
   - Direct account lookup

## Error Handling

- **404 Error**: "The provided Account Number does not belong to the specified Customer ID" (when both provided but don't match)
- **400 Error**: "Invalid request parameters. Please check your input."
- **General Errors**: Displays specific error messages from server

## UI Features

- **Auto-population**: Form fields automatically filled from URL parameters
- **Visual Feedback**: Results metadata shows active filtering mode with 🔍 icon
- **Smart Validation**: Form validation ensures at least one identifier is provided
- **Export Support**: CSV export filename includes both identifiers when available

## Security Benefits

- **Account Ownership Verification**: Prevents unauthorized access to accounts not belonging to a customer
- **Input Validation**: Server-side validation of all parameters
- **Error Masking**: Doesn't reveal sensitive information in error messages
