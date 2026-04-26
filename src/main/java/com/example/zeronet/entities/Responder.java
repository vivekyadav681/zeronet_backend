package com.example.zeronet.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "responders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Responder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;
    
    private String role;
    private String status;
    private Double lat;
    private Double lng;
    
    @Column(name = "assigned_incident_id")
    private UUID assignedIncidentId;
    
    @Column(name = "organization_id")
    private UUID organizationId;
}
