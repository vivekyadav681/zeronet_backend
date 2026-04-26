package com.example.zeronet.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private String token;
    private UserDto user;
    private Object organization; // Can map to Organization entity or DTO later
    private String message;
    private Integer expiresIn;
    private Boolean verified;
    private Boolean error;
    private String code;
}
