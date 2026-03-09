package com.fluentbase.userapi.service;

import com.fluentbase.userapi.dto.*;
import com.fluentbase.userapi.entity.Role;
import com.fluentbase.userapi.entity.User;
import com.fluentbase.userapi.entity.UserProgress;
import com.fluentbase.userapi.exception.ResourceNotFoundException;
import com.fluentbase.userapi.exception.UnauthorizedException;
import com.fluentbase.userapi.repository.UserProgressRepository;
import com.fluentbase.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        user = userRepository.save(user);

        UserProgress progress = UserProgress.builder()
                .user(user)
                .xp(0)
                .streak(0)
                .proficiencyLevel("BEGINNER")
                .build();

        userProgressRepository.save(progress);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return buildAuthResponse(user);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        UUID userId = refreshTokenService.validateRefreshToken(request.getRefreshToken());
        if (userId == null) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        refreshTokenService.deleteRefreshToken(request.getRefreshToken());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenService.storeRefreshToken(refreshToken, user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration() / 1000)
                .build();
    }
}
