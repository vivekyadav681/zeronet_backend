package com.example.zeronet.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String hometown;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "emergency_contact_1_name")
    private String emergencyContact1Name;

    @Column(name = "emergency_contact_1_phone")
    private String emergencyContact1Phone;

    @Column(name = "emergency_contact_2_name")
    private String emergencyContact2Name;

    @Column(name = "emergency_contact_2_phone")
    private String emergencyContact2Phone;

    @Column(name = "emergency_contact_3_name")
    private String emergencyContact3Name;

    @Column(name = "emergency_contact_3_phone")
    private String emergencyContact3Phone;

    @Column(name = "created_at")
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
