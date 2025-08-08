package com.example.demo.service;

import com.example.demo.dto.CustomerLoginRequestDTO;
import com.example.demo.dto.CustomerLoginResponseDTO;

public interface CustomerLoginService {
    CustomerLoginResponseDTO login(CustomerLoginRequestDTO request);
}

