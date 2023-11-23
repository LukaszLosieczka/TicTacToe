package com.example.backend.service;

import com.example.backend.dto.AuthTokens;
import com.example.backend.dto.ConfirmationToken;
import com.example.backend.dto.LoginUser;
import com.example.backend.dto.RegisterUser;
import com.example.backend.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.authentication.BadCredentialsException;

public interface AuthService {
    AuthTokens authenticateUser(LoginUser loginUser) throws JsonProcessingException, BadCredentialsException;
    AuthTokens refreshTokens(String refreshToken) throws JsonProcessingException;
    void registerNewUserAccount(RegisterUser userDto);
    void confirmUserAccount(ConfirmationToken token);
}