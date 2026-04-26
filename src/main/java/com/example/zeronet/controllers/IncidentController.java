package com.example.zeronet.controllers;

import com.example.zeronet.dtos.IncidentListResponse;
import com.example.zeronet.entities.Incident;
import com.example.zeronet.services.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @GetMapping
    public ResponseEntity<IncidentListResponse> getIncidents(
            @RequestParam(required = false) UUID organizationId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String timeRange,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        return ResponseEntity.ok(incidentService.getIncidents(organizationId, status, type, timeRange, page, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incident> getIncident(@PathVariable UUID id) {
        return ResponseEntity.ok(incidentService.getIncident(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Incident> updateStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(incidentService.updateStatus(id, body.get("status"), body.get("responderId")));
    }

    @PatchMapping("/{id}/escalate")
    public ResponseEntity<Incident> escalate(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(incidentService.escalate(id, body.get("severity"), body.get("reason")));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<Incident> assign(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(incidentService.assignResponder(id, body.get("responderId")));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<Incident> resolve(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(incidentService.resolveIncident(id, body.get("resolvedBy"), body.get("notes")));
    }
}
