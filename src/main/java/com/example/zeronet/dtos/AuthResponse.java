package com.example.zeronet.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private boolean success;
    private String message;
    private String token;

    public AuthResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
