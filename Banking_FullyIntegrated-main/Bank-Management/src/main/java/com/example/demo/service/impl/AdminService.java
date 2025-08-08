package com.example.demo.service.impl;

import java.time.LocalDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.BankManagementApplication;
import com.example.demo.controller.AdminPageController;
import com.example.demo.entities.Account;
import com.example.demo.entities.AccountRequest;
import com.example.demo.entities.Admin;
import com.example.demo.entities.Customer;
import com.example.demo.repos.AccountRepository;
import com.example.demo.repos.AccountRequestRepository;
import com.example.demo.repos.AdminRepository;
import com.example.demo.repos.CustomerLoginRepository;
import com.example.demo.utils.generatePasswordUtil;

import ch.qos.logback.core.net.SyslogOutputStream;


class AccountAlreadyPresentException extends RuntimeException {
	
    public AccountAlreadyPresentException(String message) {
        super(message);
    }
}

@Service
public class AdminService {
	
	private final BankManagementApplication bankManagementApplication;
	
	@Autowired
	AdminRepository adminRepository;
	@Autowired
	AccountRequestRepository accountRequestRepository;
	@Autowired
	CustomerLoginRepository customerRepository;
	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	private EmailService emailService;

	public AdminService(BankManagementApplication bankingApplication) {
		this.bankManagementApplication = bankingApplication;
	}
	
	public List<Admin> fetchAllAdminAccounts(){
		return adminRepository.findAll();
		
	}
	
	public Admin addSingleAdmin(Admin admObjToAdd) {
		if (admObjToAdd.getUsername() != null) {
			Optional<Admin> admObjFound = adminRepository.findByUsername(admObjToAdd.getUsername());
			if(admObjFound.isPresent()) {
				throw new AccountAlreadyPresentException("Account already present ");
			}else {
				adminRepository.save(admObjToAdd);}
		}
		return admObjToAdd;
	}
	
	public Map<String, Object> adminLogin(String username, String password) {
		Optional<Admin> admLog = adminRepository.findByUsername(username);
		if (admLog.isPresent()) {
			Admin adm = admLog.get();
			if (adm.getPassword().equals(password)) {
				Map<String, Object> response = new HashMap<>();
	            response.put("message", "Successfully logged in");
	            response.put("adminId", adm.getAdminId());
	            response.put("username", adm.getUsername());
	            response.put("name", adm.getName());
	            return response;
			}else {
	            throw new RuntimeException("Incorrect password");
	        }
	    } else {
	        throw new RuntimeException("Admin not found");
		}
	}
	
	public List<AccountRequest> viewAllPendingRequests(){
		return accountRequestRepository.findAll();
		
	}
	
	public String handleAccountRequest(Long requestId, String status, String remarks) {
        Optional<AccountRequest> optionalRequest = accountRequestRepository.findById(requestId);

        if (optionalRequest.isEmpty()) {
            return "Request not found.";
        }

        AccountRequest req = optionalRequest.get();
        String customerName = req.getFirstName() + " " + req.getLastName();

        if ("approved".equalsIgnoreCase(status)) {
            // ✅ Insert into Customer table
            Customer customer = new Customer();
            customer.setTitle(req.getTitle());
            customer.setFirstName(req.getFirstName());
            customer.setMiddleName(req.getMiddleName());
            customer.setLastName(req.getLastName());
            customer.setMobileNo(req.getMobileNo());
            customer.setEmail(req.getEmail());
            customer.setAadharNo(req.getAadharNo());
            customer.setPanNo(req.getPanNo());
            customer.setDob(req.getDob());
            customer.setResidentialAddress(req.getResidentialAddress());
            customer.setPermanentAddress(req.getPermanentAddress());
            customer.setOccupation(req.getOccupation());
            customer.setAnnualIncome(req.getAnnualIncome());

            String loginPassword = generatePasswordUtil.generateRandomPassword(10);
            customer.setLoginPassword(loginPassword);

            Customer savedCustomer = customerRepository.save(customer);

            // ✅ Insert into Account table
            Account account = new Account();
            account.setAccountType(req.getAccountType());
            account.setApplicationDate(LocalDate.now());
            account.setStatus("Active");
            account.setBalance(0.0);
            String transactionPassword = generatePasswordUtil.generateRandomPassword(10);
	        account.setTransactionPassword(transactionPassword);
            account.setCustomer(savedCustomer);

            accountRepository.save(account);
            
         // ✅ Send approval email
	        try {
	            emailService.sendAccountApprovalEmail(
	                req.getEmail(),
	                customerName,
	                savedCustomer.getCustomerId().toString(),
	                loginPassword,
	                transactionPassword,
	                remarks
	            );
	        } catch (Exception e) {
	            System.err.println("Failed to send approval email: " + e.getMessage());
	            e.printStackTrace();
	            // Don't fail the entire operation if email fails
	        }
	        System.out.println("Attempting to send approval email to " + req.getEmail());

            // ✅ Delete from request table
            accountRequestRepository.deleteById(requestId);

            return "Account approved. Customer ID: " + savedCustomer.getCustomerId();

        } else if ("rejected".equalsIgnoreCase(status)) {
        	
        	// ✅ Send rejection email
	        try {
	            emailService.sendAccountRejectionEmail(
	                req.getEmail(),
	                customerName,
	                remarks
	            );
	        } catch (Exception e) {
	            System.err.println("Failed to send rejection email: " + e.getMessage());
	        }
	        
            accountRequestRepository.deleteById(requestId);
            return "Account request rejected.";
        }

        return "Invalid status. Use 'approved' or 'rejected'.";
    }
	
	public long pendingRequestCount() {
		return accountRequestRepository.count();
	}
	
	public long countCustomers() {
	    return customerRepository.count();
	}
	
	public Map<String, Long> getAccountStatusCounts() {
	    long active = accountRepository.countByStatus("Active");
	    long inactive = accountRepository.countByStatus("Inactive");

	    Map<String, Long> result = new HashMap<>();
	    result.put("Active", active);
	    result.put("Inactive", inactive);
	    return result;
	}

	public Map<String, Long> getAccountTypeCounts() {
	    long savingCount = accountRepository.countByAccountTypeIgnoreCase("Saving") +
	                       accountRepository.countByAccountTypeIgnoreCase("savings");
	    long currentCount = accountRepository.countByAccountTypeIgnoreCase("Current");
	    long salaryCount = accountRepository.countByAccountTypeIgnoreCase("Salary")+accountRepository.countByAccountTypeIgnoreCase("Salaried");

	    Map<String, Long> result = new HashMap<>();
	    result.put("Saving", savingCount);
	    result.put("Current", currentCount);
	    result.put("Salary", salaryCount);
	    return result;
	}
	
	public Optional<Admin> getAdminName(long adminId) {
		return adminRepository.findByAdminId(adminId);
		
	}

	

}
