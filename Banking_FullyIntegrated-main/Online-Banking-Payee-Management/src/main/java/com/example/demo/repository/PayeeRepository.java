package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Payee;

@Repository
public interface PayeeRepository extends JpaRepository<Payee, Long> {
    List<Payee> findByCustomerId(Long customerId);
}
