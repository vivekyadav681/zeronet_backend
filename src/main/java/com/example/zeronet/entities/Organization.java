package com.example.zeronet.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "org_name", nullable = false)
    private String orgName;

    @Column(name = "org_type")
    private String orgType;

    @Column(name = "registration_id")
    private String registrationId;

    private String email;
    private String phone;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    private String license;
    
    @Column(name = "contact_person")
    private String contactPerson;
    
    @Column(name = "emergency_contact")
    private String emergencyContact;
    
    private String website;
    
    @Column(name = "start_time", length = 10)
    private String startTime;
    
    @Column(name = "end_time", length = 10)
    private String endTime;
    
    @Column(name = "is_24_hours")
    private Boolean is24Hours;
    
    @Column(name = "total_floors")
    private Integer totalFloors;
    
    @Column(name = "security_staff_count")
    private Integer securityStaffCount;
    
    private String city;
    private String state;
    
    private Double lat;
    private Double lng;
    
    private Double radius;

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
