package com.example.zeronet.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String phoneNumber;

    private String hometown;

    @NotNull
    private LocalDate dateOfBirth;

    private String emergencyContact1Name;
    private String emergencyContact1Phone;
    private String emergencyContact2Name;
    private String emergencyContact2Phone;
    private String emergencyContact3Name;
    private String emergencyContact3Phone;
}
