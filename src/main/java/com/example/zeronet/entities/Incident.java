package com.example.zeronet.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "incidents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "sos_id", unique = true)
    private String sosId;
    
    @Column(name = "case_name")
    private String caseName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String location;
    private Double lat;
    private Double lng;
    private String type;
    private String severity;
    private String status;
    
    @Column(name = "elapsed_time")
    private String elapsedTime;

    @Column(name = "organization_id")
    private UUID organizationId;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "incident_responders",
        joinColumns = @JoinColumn(name = "incident_id"),
        inverseJoinColumns = @JoinColumn(name = "responder_id")
    )
    @Builder.Default
    private List<Responder> responders = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id")
    @Builder.Default
    private List<IncidentTimeline> timeline = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
