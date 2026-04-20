package com.example.zeronet.services;

import com.example.zeronet.dtos.CreateIncidentRequest;
import com.example.zeronet.dtos.IncidentDto;
import com.example.zeronet.entities.Incident;
import com.example.zeronet.entities.User;
import com.example.zeronet.repositories.IncidentRepository;
import com.example.zeronet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;

    @Autowired
    public IncidentService(IncidentRepository incidentRepository, UserRepository userRepository) {
        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
    }

    public IncidentDto createIncident(CreateIncidentRequest req) {
        Optional<User> sender = userRepository.findById(req.getSenderId());
        if (sender.isEmpty()) {
            throw new RuntimeException("Sender not found");
        }

        User victim = null;
        if (req.getVictimId() != null) {
            victim = userRepository.findById(req.getVictimId()).orElse(null);
        }

        Incident incident = new Incident();
        incident.setSender(sender.get());
        incident.setVictim(victim);
        incident.setIncidentDateTime(req.getIncidentDateTime());
        incident.setLatitude(req.getLatitude());
        incident.setLongitude(req.getLongitude());
        incident.setDescription(req.getDescription());
        incident.setSeverity(req.getSeverity());
        // status is "OPEN" by default

        incidentRepository.save(incident);
        return mapToDto(incident);
    }

    public IncidentDto updateIncidentStatus(Long id, String status) {
        Optional<Incident> maybeIncident = incidentRepository.findById(id);
        if (maybeIncident.isEmpty()) {
            throw new RuntimeException("Incident not found");
        }

        Incident incident = maybeIncident.get();
        incident.setStatus(status);
        incidentRepository.save(incident);
        
        return mapToDto(incident);
    }

    public void deleteIncident(Long id) {
        if (!incidentRepository.existsById(id)) {
            throw new RuntimeException("Incident not found");
        }
        incidentRepository.deleteById(id);
    }

    private IncidentDto mapToDto(Incident incident) {
        IncidentDto dto = new IncidentDto();
        dto.setId(incident.getId());
        dto.setSenderId(incident.getSender().getId());
        if (incident.getVictim() != null) {
            dto.setVictimId(incident.getVictim().getId());
        }
        dto.setIncidentDateTime(incident.getIncidentDateTime());
        dto.setLatitude(incident.getLatitude());
        dto.setLongitude(incident.getLongitude());
        dto.setDescription(incident.getDescription());
        dto.setSeverity(incident.getSeverity());
        dto.setStatus(incident.getStatus());
        dto.setCreatedAt(incident.getCreatedAt());
        return dto;
    }
}
