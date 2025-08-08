-- Simple and correct sequence creation for Oracle
CREATE SEQUENCE PAYEE_SEQ START WITH 1 INCREMENT BY 1;

-- Test the sequence
SELECT PAYEE_SEQ.NEXTVAL FROM DUAL;
