package com.example.demo.service;

import java.util.List;
import com.example.demo.entities.AccountRequest;

public interface AccountRequestService {
    AccountRequest createRequest(AccountRequest request);
    List<AccountRequest> getAllRequests();
    AccountRequest getRequestById(Long id);
    void deleteRequest(Long id);
}
