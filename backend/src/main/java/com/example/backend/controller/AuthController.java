package com.example.backend.controller;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.example.backend.dto.AuthTokens;
import com.example.backend.dto.ConfirmationToken;
import com.example.backend.dto.LoginUser;
import com.example.backend.dto.RegisterUser;
import com.example.backend.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("login")
    ResponseEntity<Object> login(@Valid @RequestBody LoginUser loginUser)
            throws AuthenticationException, JsonProcessingException, IllegalArgumentException, JWTCreationException {
        AuthTokens tokens;
        try{
            tokens = service.authenticateUser(loginUser);
        }catch(BadCredentialsException e){
            return new ResponseEntity<>("Nieprawidłowy login lub hasło", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(tokens, HttpStatus.OK);
    }

    @PostMapping("signup")
    ResponseEntity<Object> signUp(@Valid @RequestBody RegisterUser userDto) {
        try {
            service.registerNewUserAccount(userDto);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("User registered, but still needs confirmation", HttpStatus.CREATED);
    }

    @PostMapping("confirm_user")
    ResponseEntity<Object> confirm(@Valid @RequestBody ConfirmationToken token) {
        try {
            service.confirmUserAccount(token);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("User confirmed", HttpStatus.CREATED);
    }

    @GetMapping("/refresh_token")
    ResponseEntity<Object> refreshToken(@RequestParam String token){
        if (token != null) {
            try {
                AuthTokens tokens = service.refreshTokens(token);
                return new ResponseEntity<>(tokens, HttpStatus.OK);
            } catch (Exception exception) {
                return new ResponseEntity<>(exception.getMessage(), FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>("refresh token not provided", HttpStatus.BAD_REQUEST);
        }
    }
}
