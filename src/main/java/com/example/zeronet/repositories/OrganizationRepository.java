package com.example.zeronet.repositories;

import com.example.zeronet.entities.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    boolean existsByRegistrationId(String registrationId);
    boolean existsByEmail(String email);
}
