package com.consultadd.taskmanager.controller;

import com.consultadd.taskmanager.dto.LoginRequestDTO;
import com.consultadd.taskmanager.dto.RefreshTokenRequestDTO;
import com.consultadd.taskmanager.dto.RegisterRequestDTO;
import com.consultadd.taskmanager.dto.TokenPairDTO;
import com.consultadd.taskmanager.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthorizationController {
    private final AuthorizationService authorizationService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO registerRequestDTO) {
        try {
            authorizationService.registerUser(registerRequestDTO);
            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Registration failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            TokenPairDTO token = authorizationService.login(loginRequestDTO);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return new ResponseEntity<>("Login failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        try {
            TokenPairDTO tokenPair = authorizationService.refreshToken(refreshTokenRequestDTO);
            return ResponseEntity.ok(tokenPair);
        } catch (Exception e) {
            return new ResponseEntity<>("Token refresh failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }


}
