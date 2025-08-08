-- Add transaction password column to CUSTOMER table (if not already added)
-- This should be run after the entity changes are deployed

-- Update existing customers with sample transaction passwords
-- In production, these should be properly encrypted/hashed

UPDATE CUSTOMER SET TRANSACTION_PASSWORD = 'txn123' WHERE CUSTOMERID = 1001;
UPDATE CUSTOMER SET TRANSACTION_PASSWORD = 'pass456' WHERE CUSTOMERID = 1002;
UPDATE CUSTOMER SET TRANSACTION_PASSWORD = 'secret789' WHERE CUSTOMERID = 1003;

-- If you have other customers, add them here:
-- UPDATE CUSTOMER SET TRANSACTION_PASSWORD = 'your_password' WHERE CUSTOMERID = your_customer_id;

-- To verify the updates:
SELECT CUSTOMERID, FIRSTNAME, LASTNAME, TRANSACTION_PASSWORD FROM CUSTOMER;
