package com.example.zeronet.dtos;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class HelpDetailsResponse {
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String victimName;
    private String victimPhone;
    private String emergencyContact1Name;
    private String emergencyContact1Phone;
    private String incidentDescription;
}
