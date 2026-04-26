package com.example.zeronet.repositories;

import com.example.zeronet.entities.Broadcast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BroadcastRepository extends JpaRepository<Broadcast, UUID> {
}
