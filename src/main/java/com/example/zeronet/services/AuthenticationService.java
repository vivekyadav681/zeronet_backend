package com.example.zeronet.services;

import com.example.zeronet.dtos.AuthResponse;
import com.example.zeronet.dtos.LoginRequest;
import com.example.zeronet.dtos.OtpVerifyRequest;
import com.example.zeronet.dtos.RegisterRequest;
import com.example.zeronet.entities.User;
import com.example.zeronet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(UserRepository userRepository, OtpService otpService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            // If user exists, re-send OTP
            otpService.sendOtpTo(email);
            return new AuthResponse(true, "OTP resent to email");
        }

        // Create a new user and send OTP
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

        // simple token generation (UUID). Replace with JWT if needed.
        String token = UUID.randomUUID().toString();

        return new AuthResponse(true, "Login successful", token);
    }
}
