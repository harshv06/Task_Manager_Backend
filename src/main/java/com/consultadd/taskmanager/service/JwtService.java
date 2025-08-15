package com.consultadd.taskmanager.service;

import com.consultadd.taskmanager.dto.TokenPairDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long expiration;

    @Value("${app.jwt.refresh-token}")
    private long refreshExpiration;


    // Generate access token
    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, expiration, new HashMap<>());
    }


    public String generateRefreshToken(Authentication authentication) {
        Map<String, String> claims = new HashMap<>();
        claims.put("tokenType", "refresh");
        return generateToken(authentication, refreshExpiration, claims);
    }

    private String generateToken(Authentication authentication, long expirationInMs, Map<String, String> claims) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        Date now = new Date(); // Time of token creation
        Date expiryDate = new Date(now.getTime() + expirationInMs); // Time of token expiration

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(userPrincipal.getUsername())
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSignInKey())
                .compact();
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims != null && !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }


    // Validate token
    public boolean validateTokenForUser(String token, UserDetails userDetails) {
        try {
            final String username = extractUsernameFromToken(token);
            return username != null
                    && username.equals(userDetails.getUsername())
                    && isTokenValid(token);
        } catch (Exception e) {
            log.error("Token validation for user failed: {}", e.getMessage());
            return false;
        }
    }

    public String extractUsernameFromToken(String token) {
        Claims claims = extractAllClaims(token);

        if(claims != null) {
            return claims.getSubject();
        }
        return null;
    }

    public boolean isRefreshToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims != null && "refresh".equals(claims.get("tokenType"));
    }

    public Claims extractAllClaims(String token){
        Claims claims=null;
        try{
            claims=Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        } catch (SecurityException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        }
        return claims;
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public TokenPairDTO generateTokenPair(Authentication authentication) {
        String accessToken = generateAccessToken(authentication);
        String refreshToken = generateRefreshToken(authentication);

        return new TokenPairDTO(accessToken, refreshToken);
    }
}
