-- Validation script for ORA-01400 fix
-- Run these commands to verify the fix is working

-- 1. Check if sequence exists
SELECT sequence_name, last_number, increment_by 
FROM user_sequences 
WHERE sequence_name = 'PAYEE_SEQ';

-- 2. Test sequence generation
SELECT PAYEE_SEQ.NEXTVAL as next_id FROM DUAL;

-- 3. Check table structure
SELECT column_name, data_type, nullable 
FROM user_tab_columns 
WHERE table_name = 'PAYEE' 
ORDER BY column_id;

-- 4. Check current max ID (if table has data)
SELECT MAX(ID) as max_payee_id FROM PAYEE;

-- 5. Test insert with sequence
-- INSERT INTO PAYEE (ID, CUSTOMER_ID, PAYEE_ACCOUNT_NUMBER, PAYEE_NAME, ADDED_AT) 
-- VALUES (PAYEE_SEQ.NEXTVAL, 1, 1234567890, 'Test Payee', SYSDATE);

-- 6. Verify the insert worked
-- SELECT * FROM PAYEE WHERE PAYEE_NAME = 'Test Payee';
-- ROLLBACK; -- Remove this line if you want to keep the test data
