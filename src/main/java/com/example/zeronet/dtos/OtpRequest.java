package com.example.zeronet.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpRequest {
    private String email;
    private String phone;
    private String registrationId;
}
