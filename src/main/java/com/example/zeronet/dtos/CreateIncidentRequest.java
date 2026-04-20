package com.example.zeronet.dtos;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateIncidentRequest {

    @NotNull
    private Long senderId;
    
    private Long victimId;
    
    @NotNull
    private LocalDateTime incidentDateTime;
    
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String description;
    private String severity;

}
