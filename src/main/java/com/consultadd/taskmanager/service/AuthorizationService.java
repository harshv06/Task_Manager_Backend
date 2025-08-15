package com.consultadd.taskmanager.service;

import com.consultadd.taskmanager.dto.LoginRequestDTO;
import com.consultadd.taskmanager.dto.RefreshTokenRequestDTO;
import com.consultadd.taskmanager.dto.RegisterRequestDTO;
import com.consultadd.taskmanager.dto.TokenPairDTO;
import com.consultadd.taskmanager.model.User;
import com.consultadd.taskmanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailService userDetailService;

    @Transactional
    public void registerUser(RegisterRequestDTO registerRequest){
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new IllegalArgumentException("Username is already in use");
        }

        User user=User
                .builder()
                .fullName(registerRequest.getFullName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        userRepository.save(user);
        log.info("User registered successfully with email: {}", registerRequest.getEmail());
    }

    public TokenPairDTO login(LoginRequestDTO loginRequestDTO){
        try{
            Authentication authentication=authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getEmail(),
                            loginRequestDTO.getPassword()
                    )
            );
            log.info("User authenticated successfully: {}", loginRequestDTO.getEmail());
            return jwtService.generateTokenPair(authentication);
        }catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", loginRequestDTO.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    public TokenPairDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {
        try {
            String refreshToken = refreshTokenRequestDTO.getRefreshToken();

            if (!jwtService.isTokenValid(refreshToken)) {
                throw new IllegalArgumentException("Invalid or expired refresh token");
            }

            if (!jwtService.isRefreshToken(refreshToken)) {
                throw new IllegalArgumentException("Token is not a refresh token");
            }

            String username = jwtService.extractUsernameFromToken(refreshToken);
            UserDetails userDetails = userDetailService.loadUserByUsername(username);

            if (userDetails == null) {
                throw new UsernameNotFoundException("User not found");
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            String accessToken = jwtService.generateAccessToken(authenticationToken);
            log.info("Token refreshed successfully for user: {}", username);
            return new TokenPairDTO(accessToken, refreshToken);

        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw new RuntimeException("Failed to refresh token: " + e.getMessage());
        }
    }
}
