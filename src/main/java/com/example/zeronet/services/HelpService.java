package com.example.zeronet.services;

import com.example.zeronet.dtos.AcceptHelpRequest;
import com.example.zeronet.dtos.HelpDetailsResponse;
import com.example.zeronet.dtos.SendHelpRequest;
import com.example.zeronet.entities.Incident;
import com.example.zeronet.entities.UserProfile;
import com.example.zeronet.repositories.IncidentRepository;
import com.example.zeronet.repositories.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HelpService {

    private final IncidentRepository incidentRepository;
    private final UserProfileRepository userProfileRepository;
    private final EmailService emailService;

    public HelpDetailsResponse acceptHelp(AcceptHelpRequest request) {
        Optional<Incident> maybeIncident = incidentRepository.findById(request.getIncidentId());
        if (maybeIncident.isEmpty()) {
            throw new RuntimeException("Incident not found");
        }

        Incident incident = maybeIncident.get();
        if ("OPEN".equalsIgnoreCase(incident.getStatus())) {
            incident.setStatus("IN_PROGRESS");
            incidentRepository.save(incident);
        }

        HelpDetailsResponse.HelpDetailsResponseBuilder builder = HelpDetailsResponse.builder()
                .latitude(incident.getLatitude())
                .longitude(incident.getLongitude())
                .incidentDescription(incident.getDescription());

        if (incident.getVictim() != null) {
            Optional<UserProfile> maybeProfile = userProfileRepository.findByUserId(incident.getVictim().getId());
            if (maybeProfile.isPresent()) {
                UserProfile profile = maybeProfile.get();
                builder.victimName(profile.getName())
                       .victimPhone(profile.getPhoneNumber())
                       .emergencyContact1Name(profile.getEmergencyContact1Name())
                       .emergencyContact1Phone(profile.getEmergencyContact1Phone());
            }
        }

        return builder.build();
    }

    public void sendHelp(SendHelpRequest request) {
        Optional<Incident> maybeIncident = incidentRepository.findById(request.getIncidentId());
        if (maybeIncident.isEmpty()) {
            throw new RuntimeException("Incident not found");
        }

        Incident incident = maybeIncident.get();
        String victimName = "Unknown";
        String victimPhone = "Unknown";

        if (incident.getVictim() != null) {
            Optional<UserProfile> maybeProfile = userProfileRepository.findByUserId(incident.getVictim().getId());
            if (maybeProfile.isPresent()) {
                UserProfile profile = maybeProfile.get();
                victimName = profile.getName() != null ? profile.getName() : "Unknown";
                victimPhone = profile.getPhoneNumber() != null ? profile.getPhoneNumber() : "Unknown";
            }
        }

        String message = String.format(
            "An incident has been reported and help is needed!\n\n" +
            "Incident Details:\n" +
            "- Description: %s\n" +
            "- Severity: %s\n" +
            "- Location (Lat, Long): %s, %s\n\n" +
            "Victim Details:\n" +
            "- Name: %s\n" +
            "- Phone: %s\n\n" +
            "Please reach out or dispatch assistance immediately.",
            incident.getDescription() != null ? incident.getDescription() : "N/A",
            incident.getSeverity() != null ? incident.getSeverity() : "N/A",
            incident.getLatitude(), incident.getLongitude(),
            victimName, victimPhone
        );

        emailService.sendHelpNotification(request.getRecipientEmail(), message);
    }
}
