package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entities.AccountRequest;
import com.example.demo.service.AccountRequestService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/accountrequest")
public class AccountRequestController {

    @Autowired
    private AccountRequestService service;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid AccountRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            AccountRequest saved = service.createRequest(request);
            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("❌ Failed to create account request: " + e.getMessage());
        }
    }

    @GetMapping
    public List<AccountRequest> getAll() {
        return service.getAllRequests();
    }

    @GetMapping("/{id}")
    public AccountRequest getById(@PathVariable Long id) {
        return service.getRequestById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteRequest(id);
    }
}
