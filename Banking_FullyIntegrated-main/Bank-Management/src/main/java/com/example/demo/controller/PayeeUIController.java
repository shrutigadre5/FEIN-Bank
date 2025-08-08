package com.example.demo.controller;

import com.example.demo.entities.Payee;
import com.example.demo.service.PayeeServiceCaller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/payee-ui")
@CrossOrigin(origins = "*")
public class PayeeUIController {

    @Autowired
    private PayeeServiceCaller caller;

    @GetMapping("/payees")
    public ResponseEntity<List<Payee>> getPayees(@RequestParam long customerId) {
        try {
            List<Payee> payees = caller.getPayeesByCustomerId(customerId);
            return ResponseEntity.ok(payees);
        } catch (Exception e) {
            System.err.println("Error fetching payees: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Payee> addPayee(@RequestBody Payee payee) {
        try {
            Payee createdPayee = caller.addPayee(payee);
            return ResponseEntity.ok(createdPayee);
        } catch (Exception e) {
            System.err.println("Error adding payee: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/delete/{payeeId}")
    public ResponseEntity<?> deletePayee(@PathVariable Long payeeId) {
        try {
            caller.deletePayee(payeeId);
            return ResponseEntity.ok("Deleted successfully");
        } catch (Exception e) {
            System.err.println("Error deleting payee: " + e.getMessage());
            return ResponseEntity.status(500).body("Error deleting payee");
        }
    }

    // Store selected payee in session
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

    // Get stored payee from session
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

    // Clear stored payee from session
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
