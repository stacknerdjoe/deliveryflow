package com.deliveryflow.orderservice.service;

import com.deliveryflow.orderservice.dto.AuthDto;
import com.deliveryflow.orderservice.model.User;
import com.deliveryflow.orderservice.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final long JWT_EXPIRY_MS = 86400000; // 24 hours

    public AuthDto.Response register(AuthDto.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // In production, use BCrypt. Simplified here for portfolio clarity.
        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(request.getPassword())
            .role(request.getRole() != null ? request.getRole().toUpperCase() : "CUSTOMER")
            .build();

        User saved = userRepository.save(user);
        log.info("New user registered: {}", saved.getEmail());

        String token = generateToken(saved);
        return AuthDto.Response.builder()
            .token(token)
            .userId(saved.getId())
            .name(saved.getName())
            .email(saved.getEmail())
            .role(saved.getRole())
            .build();
    }

    public AuthDto.Response login(AuthDto.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = generateToken(user);
        return AuthDto.Response.builder()
            .token(token)
            .userId(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole())
            .build();
    }

    private String generateToken(User user) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("email", user.getEmail());

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(user.getId())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRY_MS))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }
}
