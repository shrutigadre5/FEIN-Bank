package com.example.demo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.view.RedirectView;
import com.example.demo.entity.Payee;
import com.example.demo.service.PayeeService;
import com.example.demo.dto.AddPayeeRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payees")
@CrossOrigin(origins = "*")
public class PayeeController {
    private static final Logger logger = LoggerFactory.getLogger(PayeeController.class);
    
    @Autowired
    private PayeeService payeeService;
    
    @GetMapping("/id/{id}")
    public ResponseEntity<Payee> getPayeeById(@PathVariable Long id) {
        return ResponseEntity.ok(payeeService.getPayeeById(id));
    }
    
    
    @PostMapping("/add")
    public ResponseEntity<?> addPayee(@Valid @RequestBody Payee payee) {
        try {
            logger.info("Received request to add payee: {}", payee);
            
            Payee savedPayee = payeeService.addPayee(payee);
            logger.info("Payee added successfully: {}", savedPayee.getPayeeId());
            return ResponseEntity.ok(savedPayee);
            
        } catch (RuntimeException e) {
            logger.error("Error adding payee: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error adding payee", e);
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @PostMapping("/add-for-customer/{customerId}")
    public ResponseEntity<?> addPayeeForCustomer(@PathVariable Long customerId, @Valid @RequestBody AddPayeeRequest payeeRequest, HttpSession session) {
        try {
            logger.info("Received request to add payee for customer {}: {}", customerId, payeeRequest);
            
            // Check if session exists and matches the URL customerId
            Long sessionCustomerId = (Long) session.getAttribute("customerId");
            if (sessionCustomerId != null && !sessionCustomerId.equals(customerId)) {
                logger.warn("URL customerId {} doesn't match session customerId {}", customerId, sessionCustomerId);
                return ResponseEntity.badRequest().body("Customer ID mismatch with session");
            }
            
            // Create session if it doesn't exist
            if (sessionCustomerId == null) {
                session.setAttribute("customerId", customerId);
                session.setAttribute("loginTime", System.currentTimeMillis());
                logger.info("Created new session for customer: {}", customerId);
            }
            
            // Create a new Payee entity and populate it from the request
            Payee payee = new Payee();
            logger.info("Setting customerId {} on payee entity", customerId);
            payee.setCustomerId(customerId); // Set customer ID from URL path parameter
            logger.info("After setting customerId, payee.getCustomerId() = {}", payee.getCustomerId());
            
            payee.setPayeeAccountNumber(payeeRequest.getPayeeAccountNumber());
            payee.setPayeeName(payeeRequest.getPayeeName());
            payee.setNickname(payeeRequest.getNickname());
            payee.setBankName(payeeRequest.getBankName());
            payee.setIfscCode(payeeRequest.getIfscCode());
            
            logger.info("Complete payee entity before save: customerId={}, payeeName={}, accountNumber={}", 
                       payee.getCustomerId(), payee.getPayeeName(), payee.getPayeeAccountNumber());
            
            Payee savedPayee = payeeService.addPayee(payee);
            logger.info("Payee added successfully for customer {}: {}", customerId, savedPayee.getPayeeId());
            return ResponseEntity.ok(savedPayee);
            
        } catch (RuntimeException e) {
            logger.error("Error adding payee for customer {}: {}", customerId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error adding payee for customer {}", customerId, e);
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @GetMapping("/customer/{customerId}")
    public List<Payee> getPayeesByCustomerId(@PathVariable Long customerId) {
        return payeeService.getPayeesByCustomerId(customerId);
    }

    @GetMapping("/validate-customer/{customerId}")
    public ResponseEntity<?> validateCustomer(@PathVariable Long customerId) {
        try {
            logger.info("Validating customer ID: {}", customerId);
            boolean isValid = payeeService.isCustomerValid(customerId);
            
            if (isValid) {
                return ResponseEntity.ok().body("Customer ID " + customerId + " is valid");
            } else {
                return ResponseEntity.badRequest().body("Customer ID " + customerId + " is not valid");
            }
            
        } catch (Exception e) {
            logger.error("Error validating customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.status(500).body("Error validating customer: " + e.getMessage());
        }
    }

    // Create session with customerId from URL
    @PostMapping("/login/{customerId}")
    public ResponseEntity<?> loginCustomer(@PathVariable Long customerId, HttpSession session) {
        try {
            logger.info("Creating session for customer ID: {}", customerId);
            
            // Validate customer exists on port 8081
            boolean isValid = payeeService.isCustomerValid(customerId);
            if (!isValid) {
                return ResponseEntity.badRequest().body("Invalid Customer ID: " + customerId);
            }
            
            // Store customerId in session
            session.setAttribute("customerId", customerId);
            session.setAttribute("loginTime", System.currentTimeMillis());
            
            logger.info("Session created successfully for customer: {}", customerId);
            return ResponseEntity.ok().body("Session created for customer ID: " + customerId);
            
        } catch (Exception e) {
            logger.error("Error creating session for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.status(500).body("Error creating session: " + e.getMessage());
        }
    }

    // Get current session customerId
    @GetMapping("/current-customer")
    public ResponseEntity<?> getCurrentCustomer(HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            if (customerId != null) {
                return ResponseEntity.ok().body("Current customer ID: " + customerId);
            } else {
                return ResponseEntity.badRequest().body("No active session found");
            }
        } catch (Exception e) {
            logger.error("Error retrieving current customer from session: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error retrieving session: " + e.getMessage());
        }
    }

    // Logout and redirect to homepage
    @PostMapping("/logout")
    public RedirectView logout(HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            logger.info("Logging out customer: {}", customerId);
            
            // Clear all session attributes
            session.invalidate();
            
            logger.info("Session cleared successfully for customer: {}", customerId);
            
            // Redirect to localhost:8081/homepage.html
            return new RedirectView("http://localhost:8081/homepage.html");
            
        } catch (Exception e) {
            logger.error("Error during logout: {}", e.getMessage());
            // Still redirect to homepage even if there's an error
            return new RedirectView("http://localhost:8081/homepage.html");
        }
    }


    @DeleteMapping("/{payeeId}")
    public ResponseEntity<?> deletePayee(@PathVariable Long payeeId) {
        payeeService.deletePayee(payeeId);
        return ResponseEntity.ok("Deleted successfully");
    }

    // NEW: Store selected payee in session
    @PostMapping("/store-selected")
    public ResponseEntity<String> storeSelectedPayee(@RequestBody Payee payee, HttpSession session) {
        try {
            System.out.println("Storing payee in session: " + payee.getPayeeName());
            session.setAttribute("selectedPayee", payee);
            return ResponseEntity.ok("Payee stored in session successfully");
        } catch (Exception e) {
            System.err.println("Error storing payee in session: " + e.getMessage());
            return ResponseEntity.status(500).body("Error storing payee in session");
        }
    }

    // NEW: Get stored payee from session
    @GetMapping("/get-stored-payee")
    public ResponseEntity<Payee> getStoredPayee(HttpSession session) {
        try {
            Payee payee = (Payee) session.getAttribute("selectedPayee");
            if (payee != null) {
                return ResponseEntity.ok(payee);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error retrieving payee from session: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // NEW: Clear stored payee from session
    @DeleteMapping("/clear-stored-payee")
    public ResponseEntity<String> clearStoredPayee(HttpSession session) {
        try {
            session.removeAttribute("selectedPayee");
            return ResponseEntity.ok("Stored payee cleared from session");
        } catch (Exception e) {
            System.err.println("Error clearing payee from session: " + e.getMessage());
            return ResponseEntity.status(500).body("Error clearing payee from session");
        }
    }
}

