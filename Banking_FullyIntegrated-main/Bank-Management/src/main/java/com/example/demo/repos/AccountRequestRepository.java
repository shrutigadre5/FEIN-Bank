package com.example.demo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entities.AccountRequest;

public interface AccountRequestRepository extends JpaRepository<AccountRequest, Long> {
    boolean existsByEmail(String email);
    boolean existsByMobileNo(String mobileNo);
}

