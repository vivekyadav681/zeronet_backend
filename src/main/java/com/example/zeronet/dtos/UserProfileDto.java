package com.example.zeronet.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {

    private String name;
    private String phoneNumber;
    private String hometown;
    private LocalDate dateOfBirth;
    private String emergencyContact1Name;
    private String emergencyContact1Phone;
    private String emergencyContact2Name;
    private String emergencyContact2Phone;
    private String emergencyContact3Name;
    private String emergencyContact3Phone;

}
