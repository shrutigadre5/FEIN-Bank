-- Immediate fix for ORA-01400 error
-- Execute this script directly in Oracle SQL Developer or SQL*Plus

-- Drop sequence if it exists (to recreate with proper settings)
DROP SEQUENCE PAYEE_SEQ;

-- Create the sequence with proper configuration
CREATE SEQUENCE PAYEE_SEQ
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9999999999999999999999999999
    NOCACHE
    NOCYCLE;

-- Verify the sequence exists and works
SELECT sequence_name, min_value, max_value, increment_by, last_number
FROM user_sequences
WHERE sequence_name = 'PAYEE_SEQ';

-- Test the sequence generation
SELECT PAYEE_SEQ.NEXTVAL FROM DUAL;

-- Grant permissions
GRANT SELECT ON PAYEE_SEQ TO C##SAHILP;

-- Optional: Check current table structure
SELECT column_name, data_type, nullable, data_default
FROM user_tab_columns
WHERE table_name = 'PAYEE'
ORDER BY column_id;
