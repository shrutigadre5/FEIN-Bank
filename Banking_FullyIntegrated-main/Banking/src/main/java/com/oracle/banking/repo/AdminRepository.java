package com.oracle.banking.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oracle.banking.entities.Admin;

@Repository
public interface AdminRepository 
	extends JpaRepository<Admin, Long>{
	
	Optional<Admin> findByUsername(String username);

	Optional<Admin> findByAdminId(long adminId); 

}
