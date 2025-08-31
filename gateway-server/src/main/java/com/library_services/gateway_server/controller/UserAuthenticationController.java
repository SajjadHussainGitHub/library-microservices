package com.library_services.gateway_server.controller;

import com.library_services.gateway_server.request.AuthRequest;
import com.library_services.gateway_server.request.RefreshRequest;
import com.library_services.gateway_server.response.AuthResponse;
import com.library_services.gateway_server.service.RefreshTokenService;
import com.library_services.gateway_server.service.UserService;
import com.library_services.gateway_server.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class UserAuthenticationController {

    private final UserService userDetailsService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public UserAuthenticationController(UserService userDetailsService, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        // Authenticate user
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        if (!userDetails.getPassword().equals(authRequest.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        // Store refresh token in Redis
        refreshTokenService.storeRefreshToken(userDetails.getUsername(), refreshToken);

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest refreshRequest) {
        String username = jwtUtil.extractUsername(refreshRequest.getRefreshToken(), true);

        if (!jwtUtil.validateToken(refreshRequest.getRefreshToken(), true)) {
            return ResponseEntity.status(403).body("Invalid refresh token");
        }

        // Verify with Redis
        String cachedToken = refreshTokenService.getRefreshToken(username);
        if (cachedToken == null || !cachedToken.equals(refreshRequest.getRefreshToken())) {
            return ResponseEntity.status(403).body("Invalid or expired refresh token");
        }

        // Generate new access token
        String newAccessToken = jwtUtil.generateAccessToken(username);
        return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshRequest.getRefreshToken()));
    }

}
