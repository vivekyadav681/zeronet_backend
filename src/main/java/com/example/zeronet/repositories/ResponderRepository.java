package com.example.zeronet.repositories;

import com.example.zeronet.entities.Responder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResponderRepository extends JpaRepository<Responder, UUID> {
    List<Responder> findByOrganizationId(UUID organizationId);
    List<Responder> findByOrganizationIdAndStatus(UUID organizationId, String status);
}
