package com.example.demo.repos;

import com.example.demo.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByCustomer_CustomerId(Long customerId);

    // Alternative query if the above doesn't work
    @Query("SELECT a FROM Account a JOIN FETCH a.customer c WHERE c.customerId = :customerId")
    List<Account> findAccountsWithCustomerByCustomerId(@Param("customerId") Long customerId);
    
    long countByStatus(String string);
    
    Long countByAccountTypeIgnoreCase( String accountType);
}
