package com.example.zeronet.controllers;

import com.example.zeronet.dtos.*;
import com.example.zeronet.entities.Organization;
import com.example.zeronet.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authenticationService.login(request);
        return handleResponse(response);
    }

    @PostMapping("/login/otp/request")
    public ResponseEntity<AuthResponse> requestLoginOtp(@RequestBody OtpRequest request) {
        AuthResponse response = authenticationService.requestLoginOtp(request);
        return handleResponse(response);
    }

    @PostMapping("/login/otp/verify")
    public ResponseEntity<AuthResponse> verifyLoginOtp(@RequestBody OtpVerifyRequest request) {
        AuthResponse response = authenticationService.verifyLoginOtp(request);
        return handleResponse(response);
    }

    @PostMapping("/register/organization")
    public ResponseEntity<AuthResponse> registerOrganization(@RequestBody Organization organization) {
        AuthResponse response = authenticationService.registerOrganization(organization);
        return handleResponse(response);
    }

    @PostMapping("/register/user")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterRequest request) {
        AuthResponse response = authenticationService.registerUser(request);
        return handleResponse(response);
    }

    private ResponseEntity<AuthResponse> handleResponse(AuthResponse response) {
        if (response.getError() != null && response.getError()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
