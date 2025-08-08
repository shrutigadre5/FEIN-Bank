package com.example.demo.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.AccountRequest;
import com.example.demo.repos.AccountRequestRepository;
import com.example.demo.service.AccountRequestService;
import com.example.demo.exception.ResourceNotFoundException;


@Service
public class AccountRequestServiceImpl implements AccountRequestService {

    @Autowired
    private AccountRequestRepository repository;

    @Override
    public AccountRequest createRequest(AccountRequest request) {
        return repository.save(request);
    }

    @Override
    public List<AccountRequest> getAllRequests() {
        return repository.findAll();
    }

    @Override
    public AccountRequest getRequestById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account request not found with ID: " + id));
    }

    @Override
    public void deleteRequest(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Account request not found with ID: " + id);
        }
        repository.deleteById(id);
    }

}
