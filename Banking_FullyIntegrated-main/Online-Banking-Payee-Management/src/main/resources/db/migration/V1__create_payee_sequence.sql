-- Create sequence for payee ID generation
-- This sequence will be used to auto-generate IDs for the PAYEE table

CREATE SEQUENCE PAYEE_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;
    
-- Grant usage to the application user
GRANT SELECT ON PAYEE_SEQ TO C##SAHILP;

-- Test the sequence
SELECT PAYEE_SEQ.NEXTVAL FROM DUAL;
