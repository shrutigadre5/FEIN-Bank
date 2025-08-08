package com.oracle.banking.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oracle.banking.BankingApplication;
import com.oracle.banking.controller.AdminPageController;
import com.oracle.banking.entities.Account;
import com.oracle.banking.entities.AccountRequest;
import com.oracle.banking.entities.Admin;
import com.oracle.banking.entities.Customer;
import com.oracle.banking.repo.AccountRepository;
import com.oracle.banking.repo.AccountRequestRepository;
import com.oracle.banking.repo.AdminRepository;
import com.oracle.banking.repo.CustomerRepository;
import com.oracle.banking.utils.generatePasswordUtil;


class AccountAlreadyPresentException extends RuntimeException {
	
    public AccountAlreadyPresentException(String message) {
        super(message);
    }
}

@Service
public class AdminService {
	
	private final BankingApplication bankingApplication;
	
	@Autowired
	AdminRepository adminRepository;
	@Autowired
	AccountRequestRepository accountRequestRepository;
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	AccountRepository accountRepository;

	public AdminService(BankingApplication bankingApplication) {
		this.bankingApplication = bankingApplication;
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
            account.setTransactionPassword(generatePasswordUtil.generateRandomPassword(10));
            account.setCustomer(savedCustomer);

            accountRepository.save(account);

            // ✅ Delete from request table
            accountRequestRepository.deleteById(requestId);

            return "Account approved. Customer ID: " + savedCustomer.getCustomerID();

        } else if ("rejected".equalsIgnoreCase(status)) {
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
	    long salaryCount = accountRepository.countByAccountTypeIgnoreCase("Salary");

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
