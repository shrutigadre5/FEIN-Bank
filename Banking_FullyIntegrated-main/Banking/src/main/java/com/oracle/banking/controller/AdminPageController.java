package com.oracle.banking.controller;

import java.util.List;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.oracle.banking.entities.AccountRequest;
import com.oracle.banking.entities.Admin;
import com.oracle.banking.repo.AdminRepository;
import com.oracle.banking.service.AdminService;

@RestController

@RequestMapping("/bank")
public class AdminPageController {
	
    @Autowired
	AdminService adminService;
    
	
	@GetMapping("/home")
    public RedirectView adminHome() {
        return new RedirectView("/admin-page.html"); // No need for custom method
    }

	@GetMapping("/admins")
	public List<Admin> fetchAdmins(){
		return adminService.fetchAllAdminAccounts();	
	}
	
	@PostMapping("/addadmin")
	public Admin addSingleAdminAccount(@RequestBody Admin adm) {
		return adminService.addSingleAdmin(adm);	
	}
	
	//adminlogin
	@PostMapping("/admin/login")
    public ResponseEntity<?> loginAdmin(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        Map<String, Object> result = adminService.adminLogin(username, password);
        return ResponseEntity.ok(result); 
    }
	
	//reviewrequest
	@GetMapping("/admin/requests")
	public List<AccountRequest> viewAllAccountRequests(){
		return adminService.viewAllPendingRequests();
		
	}
	
	@PostMapping("/admin/handle-request/{requestId}")
	public ResponseEntity<String> handleRequest(
	        @PathVariable Long requestId,
	        @RequestBody Map<String, String> payload) {
	    try {
	        String status = payload.get("status");
	        String remarks = payload.get("remarks");

	        String result = adminService.handleAccountRequest(requestId, status, remarks);
	        return ResponseEntity.ok(result);
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
	    }
	}

	@GetMapping("/admin/request-count")
	public ResponseEntity<Long> countPendingRequests() {
	    long count = adminService.pendingRequestCount();
	    return ResponseEntity.ok(count);
	}

	@GetMapping("/admin/customers/count")
	public ResponseEntity<Long> getCustomerCount() {
	    return ResponseEntity.ok(adminService.countCustomers());
	}
	
	@GetMapping("/admin/accounts/status-count")
	public ResponseEntity<Map<String, Long>> getAccountStatusChartData() {
	    return ResponseEntity.ok(adminService.getAccountStatusCounts());
	}

	@GetMapping("/admin/accounts/type-count")
	public ResponseEntity<Map<String, Long>> getAccountTypeChartData() {
	    return ResponseEntity.ok(adminService.getAccountTypeCounts());
	}

	@GetMapping("/admin/details")
	public ResponseEntity<Admin> getAdminDetails() {
	    Optional<Admin> optionalAdmin = adminService.getAdminName(1001); // Hardcoded for now

	    return optionalAdmin
	            .map(ResponseEntity::ok)
	            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}


}
