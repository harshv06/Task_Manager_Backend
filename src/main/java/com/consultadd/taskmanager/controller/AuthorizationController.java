package com.consultadd.taskmanager.controller;

import com.consultadd.taskmanager.dto.LoginRequestDTO;
import com.consultadd.taskmanager.dto.RefreshTokenRequestDTO;
import com.consultadd.taskmanager.dto.RegisterRequestDTO;
import com.consultadd.taskmanager.service.AuthorizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthorizationController {
    private final AuthorizationService authorizationService;
    public AuthorizationController(AuthorizationService authorizationService){
        this.authorizationService=authorizationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO registerRequestDTO){
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO loginRequestDTO){
        return ResponseEntity.ok("User logged in successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){
        return ResponseEntity.ok("Refresh Token");
    }
}
