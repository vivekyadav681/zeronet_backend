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
    private final JwtService jwtService;

    @Autowired
    public AuthenticationService(UserRepository userRepository, OtpService otpService,
            PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
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

        return new AuthResponse(true, "Login successful", token);
    }
}
