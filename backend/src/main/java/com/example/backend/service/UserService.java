package com.example.backend.service;

import com.example.backend.dto.RegisterUser;
import com.example.backend.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UserService {
    User registerNewUserAccount(RegisterUser userDto);
}
