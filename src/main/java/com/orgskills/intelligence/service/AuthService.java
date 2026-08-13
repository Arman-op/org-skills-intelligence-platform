package com.orgskills.intelligence.service;

import com.orgskills.intelligence.dto.auth.AuthResponse;
import com.orgskills.intelligence.dto.auth.ChangePasswordRequest;
import com.orgskills.intelligence.dto.auth.LoginRequest;
import com.orgskills.intelligence.dto.auth.OAuth2GoogleRequest;
import com.orgskills.intelligence.dto.auth.RefreshTokenRequest;
import com.orgskills.intelligence.dto.auth.RegisterRequest;
import com.orgskills.intelligence.dto.auth.UpdateProfileRequest;
import com.orgskills.intelligence.dto.auth.UserProfileResponse;
import com.orgskills.intelligence.entity.RefreshToken;
import com.orgskills.intelligence.entity.User;
import com.orgskills.intelligence.entity.enums.Role;
import com.orgskills.intelligence.exception.ResourceNotFoundException;
import com.orgskills.intelligence.exception.UnauthorizedException;
import com.orgskills.intelligence.exception.ValidationException;
import com.orgskills.intelligence.repository.RefreshTokenRepository;
import com.orgskills.intelligence.repository.UserRepository;
import com.orgskills.intelligence.security.CustomPrincipal;
import com.orgskills.intelligence.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditLogService auditLogService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ValidationException("Email is already registered");
        }

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName().trim());
        user.setRole(request.getRole() != null ? request.getRole() : Role.EMPLOYEE);
        user.setDepartment(request.getDepartment().trim());
        user.setJobTitle(request.getJobTitle().trim());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setActive(true);

        User saved = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
        );
        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = createRefreshToken(saved);

        auditLogService.logEvent(saved.getId(), saved.getEmail(), "REGISTER", "User", saved.getId().toString(), "User registered successfully");

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(toUserProfile(saved))
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
            );
            CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
            User user = userRepository.findById(principal.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Authenticated user no longer exists"));

            if (!Boolean.TRUE.equals(user.getActive())) {
                auditLogService.logEvent(user.getId(), user.getEmail(), "LOGIN_FAILED", "User", user.getId().toString(), "Deactivated account login attempt");
                throw new UnauthorizedException("Account is deactivated. Please contact an administrator.");
            }

            String accessToken = jwtTokenProvider.generateToken(authentication);
            String refreshToken = createRefreshToken(user);

            auditLogService.logEvent(user.getId(), user.getEmail(), "LOGIN_SUCCESS", "User", user.getId().toString(), "User logged in successfully");

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(toUserProfile(user))
                    .build();
        } catch (BadCredentialsException ex) {
            auditLogService.logEvent(null, normalizedEmail, "LOGIN_FAILED", "User", null, "Invalid credentials for email: " + normalizedEmail);
            throw new UnauthorizedException("Invalid email or password");
        }
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (tokenEntity.getRevoked() || tokenEntity.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(tokenEntity);
            throw new UnauthorizedException("Refresh token expired or revoked");
        }

        User user = tokenEntity.getUser();
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new UnauthorizedException("User account is inactive");
        }

        CustomPrincipal principal = new CustomPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        String newAccessToken = jwtTokenProvider.generateToken(auth);
        String newRefreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .user(toUserProfile(user))
                .build();
    }

    @Transactional
    public AuthResponse oauth2GoogleLogin(OAuth2GoogleRequest request) {
        String email = (request.getEmail() != null && !request.getEmail().isBlank()) 
                ? request.getEmail().trim().toLowerCase() 
                : "google_" + UUID.randomUUID().toString().substring(0, 8) + "@oauth.domain";

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            newUser.setFullName(request.getFullName() != null ? request.getFullName().trim() : "Google User");
            newUser.setRole(Role.EMPLOYEE);
            newUser.setDepartment("Engineering");
            newUser.setJobTitle("Software Engineer");
            newUser.setActive(true);
            return userRepository.save(newUser);
        });

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new UnauthorizedException("User account is inactive");
        }

        CustomPrincipal principal = new CustomPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        String accessToken = jwtTokenProvider.generateToken(auth);
        String refreshToken = createRefreshToken(user);

        auditLogService.logEvent(user.getId(), user.getEmail(), "OAUTH2_GOOGLE_LOGIN", "User", user.getId().toString(), "Google OAuth2 login successful");

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(toUserProfile(user))
                .build();
    }

    public UserProfileResponse getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomPrincipal principal)) {
            throw new UnauthorizedException("Not authenticated");
        }
        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toUserProfile(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(Authentication authentication, UpdateProfileRequest request) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomPrincipal principal)) {
            throw new UnauthorizedException("Not authenticated");
        }
        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setFullName(request.getFullName().trim());
        user.setDepartment(request.getDepartment().trim());
        user.setJobTitle(request.getJobTitle().trim());
        user.setAvatarUrl(request.getAvatarUrl());

        auditLogService.logEvent(user.getId(), user.getEmail(), "UPDATE_PROFILE", "User", user.getId().toString(), "User profile updated");
        return toUserProfile(userRepository.save(user));
    }

    @Transactional
    public void changePassword(Authentication authentication, ChangePasswordRequest request) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomPrincipal principal)) {
            throw new UnauthorizedException("Not authenticated");
        }
        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ValidationException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        auditLogService.logEvent(user.getId(), user.getEmail(), "CHANGE_PASSWORD", "User", user.getId().toString(), "User password updated");
    }

    private String createRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60)); // 7 days
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    private UserProfileResponse toUserProfile(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .department(user.getDepartment())
                .jobTitle(user.getJobTitle())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
