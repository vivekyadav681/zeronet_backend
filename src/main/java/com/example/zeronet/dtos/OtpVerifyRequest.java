package com.example.zeronet.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpVerifyRequest {
    private String email;
    private String phone;
    
    @NotBlank(message = "OTP cannot be blank")
    private String otp;
}
