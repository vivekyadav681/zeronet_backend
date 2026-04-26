package com.example.zeronet.services;

import com.example.zeronet.dtos.IncidentListResponse;
import com.example.zeronet.entities.Incident;
import com.example.zeronet.entities.Responder;
import com.example.zeronet.repositories.IncidentRepository;
import com.example.zeronet.repositories.ResponderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IncidentService {
    private final IncidentRepository incidentRepository;
    private final ResponderRepository responderRepository;

    public IncidentListResponse getIncidents(UUID orgId, String status, String type, String timeRange, int page, int limit) {
        List<Incident> incidents = incidentRepository.findAll();
        return IncidentListResponse.builder()
            .incidents(incidents)
            .total(incidents.size())
            .page(page)
            .build();
    }

    public Incident getIncident(UUID id) {
        return incidentRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }

    public Incident updateStatus(UUID id, String status, String responderId) {
        Incident inc = getIncident(id);
        inc.setStatus(status);
        return incidentRepository.save(inc);
    }

    public Incident escalate(UUID id, String severity, String reason) {
        Incident inc = getIncident(id);
        inc.setSeverity(severity);
        return incidentRepository.save(inc);
    }

    public Incident assignResponder(UUID id, String responderId) {
        Incident inc = getIncident(id);
        if (responderId != null) {
            Optional<Responder> responder = responderRepository.findById(UUID.fromString(responderId));
            responder.ifPresent(r -> inc.getResponders().add(r));
        }
        return incidentRepository.save(inc);
    }

    public Incident resolveIncident(UUID id, String resolvedBy, String notes) {
        Incident inc = getIncident(id);
        inc.setStatus("resolved");
        return incidentRepository.save(inc);
    }
    
    public void deleteIncident(UUID id) {
        incidentRepository.deleteById(id);
    }
}
