package com.example.zeronet.dtos;

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
public class IncidentDto {

    private Long id;
    private Long senderId;
    private Long victimId;
    private LocalDateTime incidentDateTime;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String description;
    private String severity;
    private String status;
    private LocalDateTime createdAt;

}
