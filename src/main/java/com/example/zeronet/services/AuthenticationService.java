package com.example.zeronet.services;

import com.example.zeronet.dtos.AuthResponse;
import com.example.zeronet.dtos.LoginRequest;
import com.example.zeronet.dtos.OtpVerifyRequest;
import com.example.zeronet.dtos.RegisterRequest;
import com.example.zeronet.entities.User;
import com.example.zeronet.entities.UserProfile;
import com.example.zeronet.repositories.UserProfileRepository;
import com.example.zeronet.repositories.UserRepository;
import com.example.zeronet.dtos.RefreshTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public AuthenticationService(UserRepository userRepository, UserProfileRepository userProfileRepository,
            OtpService otpService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            User user = existing.get();
            if (user.isVerified()) {
                return new AuthResponse(false, "User already exists and is verified");
            }
            // Update password in case they re-register before verification
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(user);
            
            // If user exists, re-send OTP
            otpService.sendOtpTo(email);
            return new AuthResponse(true, "OTP resent to email");
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(newUser);

        UserProfile profile = new UserProfile();
        profile.setUser(newUser);
        profile.setName(request.getName());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setHometown(request.getHometown());
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setEmergencyContact1Name(request.getEmergencyContact1Name());
        profile.setEmergencyContact1Phone(request.getEmergencyContact1Phone());
        profile.setEmergencyContact2Name(request.getEmergencyContact2Name());
        profile.setEmergencyContact2Phone(request.getEmergencyContact2Phone());
        profile.setEmergencyContact3Name(request.getEmergencyContact3Name());
        profile.setEmergencyContact3Phone(request.getEmergencyContact3Phone());
        userProfileRepository.save(profile);

        // Send OTP
        otpService.sendOtpTo(email);
        return new AuthResponse(true, "OTP sent to email");
    }

    public AuthResponse verifyOtp(OtpVerifyRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        Optional<User> maybe = userRepository.findByEmail(email);
        if (maybe.isEmpty()) {
            return new AuthResponse(false, "User not found");
        }
        User user = maybe.get();
        if (user.getOtp() == null || user.getOtpExpiry() == null) {
            return new AuthResponse(false, "No OTP requested");
        }
        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return new AuthResponse(false, "OTP expired");
        }
        if (!user.getOtp().equals(request.getOtp())) {
            return new AuthResponse(false, "Invalid OTP");
        }

        user.setVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        return new AuthResponse(true, "Email verified");
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        Optional<User> maybe = userRepository.findByEmail(email);
        if (maybe.isEmpty()) {
            return new AuthResponse(false, "User not found");
        }
        User user = maybe.get();
        if (user.getOtp() == null || user.getOtpExpiry() == null) {
            return new AuthResponse(false, "No OTP requested");
        }
        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return new AuthResponse(false, "OTP expired");
        }
        if (!user.getOtp().equals(request.getOtp())) {
            return new AuthResponse(false, "Invalid OTP");
        }

        user.setVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        // Generate JWT Token using JwtService
        String token = jwtService.generateToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new AuthResponse(true, "Login successful", token, refreshToken);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String reqRefreshToken = request.getRefreshToken();
        String username = jwtService.extractUsername(reqRefreshToken);
        if (username != null) {
            Optional<User> maybeUser = userRepository.findByEmail(username);
            if (maybeUser.isPresent() && jwtService.isTokenValid(reqRefreshToken, maybeUser.get().getEmail())) {
                String token = jwtService.generateToken(username);
                String newRefreshToken = jwtService.generateRefreshToken(username);
                return new AuthResponse(true, "Token refreshed successfully", token, newRefreshToken);
            }
        }
        return new AuthResponse(false, "Invalid refresh token");
    }
}
