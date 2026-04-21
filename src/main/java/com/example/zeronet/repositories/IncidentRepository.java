package com.example.zeronet.repositories;

import com.example.zeronet.entities.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findBySenderIdOrVictimId(Long senderId, Long victimId);
}
