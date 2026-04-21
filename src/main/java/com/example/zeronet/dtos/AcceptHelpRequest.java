package com.example.zeronet.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AcceptHelpRequest {
    @NotNull(message = "Incident ID is required")
    private Long incidentId;
}
