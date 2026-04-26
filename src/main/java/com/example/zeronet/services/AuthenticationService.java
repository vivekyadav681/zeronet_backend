package com.example.zeronet.services;

import com.example.zeronet.dtos.*;
import com.example.zeronet.entities.Organization;
import com.example.zeronet.entities.User;
import com.example.zeronet.repositories.OrganizationRepository;
import com.example.zeronet.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // POST /auth/login
    public AuthResponse login(LoginRequest request) {
        String identifier = request.getEmail() != null ? request.getEmail().toLowerCase().trim() : request.getPhone();
        Optional<User> maybe = userRepository.findByEmail(identifier);
        if (maybe.isEmpty()) {
             maybe = userRepository.findByPhone(identifier);
        }
        
        if (maybe.isEmpty()) {
            return AuthResponse.builder().error(true).code("USER_NOT_FOUND").message("User not found").build();
        }
        
        User user = maybe.get();
        if (request.getPassword() != null && !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return AuthResponse.builder().error(true).code("INVALID_CREDENTIALS").message("Invalid credentials").build();
        }

        return generateLoginResponse(user);
    }

    // POST /auth/login/otp/request
    public AuthResponse requestLoginOtp(OtpRequest request) {
        String identifier = request.getEmail() != null ? request.getEmail().toLowerCase().trim() : request.getPhone();
        Optional<User> maybe = userRepository.findByEmail(identifier);
        if (maybe.isEmpty()) {
             maybe = userRepository.findByPhone(identifier);
        }
        
        if (maybe.isEmpty()) {
            return AuthResponse.builder().error(true).code("USER_NOT_FOUND").message("User not found").build();
        }

        otpService.sendOtpTo(identifier); // You would adapt your OtpService to handle phone or email
        return AuthResponse.builder().message("OTP sent").expiresIn(30).build();
    }

    // POST /auth/login/otp/verify
    public AuthResponse verifyLoginOtp(OtpVerifyRequest request) {
        String identifier = request.getEmail() != null ? request.getEmail().toLowerCase().trim() : request.getPhone();
        Optional<User> maybe = userRepository.findByEmail(identifier);
        if (maybe.isEmpty()) {
             maybe = userRepository.findByPhone(identifier);
        }
        
        if (maybe.isEmpty()) {
            return AuthResponse.builder().error(true).code("USER_NOT_FOUND").message("User not found").build();
        }
        
        User user = maybe.get();
        
        // Simple OTP verification logic for demonstration (assuming OtpService handles real storage)
        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp()) || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return AuthResponse.builder().error(true).code("INVALID_OTP").message("The OTP entered is incorrect or expired.").build();
        }
        
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        return generateLoginResponse(user);
    }

    // POST /auth/register/send-otp
    public AuthResponse requestRegisterOtp(OtpRequest request) {
        String identifier = request.getEmail() != null ? request.getEmail().toLowerCase().trim() : request.getPhone();
        otpService.sendOtpTo(identifier);
        
        // Temporarily store a user to hold the OTP if you don't have Redis
        Optional<User> maybe = userRepository.findByEmail(identifier);
        User user = maybe.orElse(new User());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        userRepository.save(user);
        
        return AuthResponse.builder().message("OTP sent to email/phone").expiresIn(30).build();
    }

    // POST /auth/register/verify-otp
    public AuthResponse verifyRegisterOtp(OtpVerifyRequest request) {
        String identifier = request.getEmail() != null ? request.getEmail().toLowerCase().trim() : request.getPhone();
        Optional<User> maybe = userRepository.findByEmail(identifier);
        if (maybe.isEmpty()) {
            return AuthResponse.builder().error(true).code("USER_NOT_FOUND").message("User not found").build();
        }
        User user = maybe.get();
        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
            return AuthResponse.builder().error(true).code("INVALID_OTP").message("The OTP entered is incorrect or expired.").build();
        }
        user.setVerified(true);
        userRepository.save(user);
        
        return AuthResponse.builder().verified(true).build();
    }

    // POST /auth/register/organization
    public AuthResponse registerOrganization(Organization org) {
        Organization savedOrg = organizationRepository.save(org);
        
        Optional<User> userOpt = userRepository.findByEmail(org.getEmail());
        User user = userOpt.orElse(new User());
        user.setEmail(org.getEmail());
        user.setPhone(org.getPhone());
        user.setOrganizationId(savedOrg.getId());
        user.setRole("admin");
        user.setName(org.getContactPerson());
        user.setVerified(true); // Since they verified during previous step
        userRepository.save(user);
        
        return generateLoginResponse(user);
    }

    // POST /auth/register/user
    public AuthResponse registerUser(RegisterRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            return AuthResponse.builder().error(true).code("USER_EXISTS").message("User already exists").build();
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("viewer");
        user.setVerified(false);
        userRepository.save(user);
        
        return AuthResponse.builder()
            .message("User registered successfully. Please verify your email/phone.")
            .build();
    }

    private AuthResponse generateLoginResponse(User user) {
        String token = jwtService.generateToken(user.getEmail());
        
        UserDto dto = UserDto.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .organizationId(user.getOrganizationId())
            .role(user.getRole())
            .build();
            
        Organization org = null;
        if (user.getOrganizationId() != null) {
             org = organizationRepository.findById(user.getOrganizationId()).orElse(null);
        }

        return AuthResponse.builder()
            .token(token)
            .user(dto)
            .organization(org)
            .build();
    }
}
