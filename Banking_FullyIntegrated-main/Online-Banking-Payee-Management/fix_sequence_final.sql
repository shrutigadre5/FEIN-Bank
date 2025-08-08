-- Immediate fix for ORA-01400 error
-- Execute this script directly in Oracle SQL Developer or SQL*Plus

-- Create the sequence (will fail if already exists, which is expected)
CREATE SEQUENCE PAYEE_SEQ
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9999999999999999999999999999
    NOCACHE
    NOCYCLE;

-- Verify the sequence exists
SELECT sequence_name, min_value, max_value, increment_by, last_number
FROM user_sequences
WHERE sequence_name = 'PAYEE_SEQ';

-- Test the sequence generation
SELECT PAYEE_SEQ.NEXTVAL FROM DUAL;
