package com.example.zeronet.dtos;

import com.example.zeronet.entities.Incident;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class IncidentListResponse {
    private List<Incident> incidents; // Simplification, could map to DTOs
    private long total;
    private int page;
}
