package com.example.zeronet.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendHelpRequest {
    @NotNull(message = "Incident ID is required")
    private Long incidentId;

    @NotNull(message = "Recipient email is required")
    @Email(message = "Valid email is required")
    private String recipientEmail;
}
