package com.example.zeronet.controllers;

import com.example.zeronet.entities.Broadcast;
import com.example.zeronet.repositories.BroadcastRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

@RestController
@RequestMapping("/v1/broadcast")
@RequiredArgsConstructor
public class BroadcastController {
    
    private final BroadcastRepository broadcastRepository;

    @PostMapping("/text")
    public ResponseEntity<Broadcast> textBroadcast(@RequestBody Map<String, String> body) {
        Broadcast b = new Broadcast();
        b.setMessage(body.get("message"));
        if (body.get("organizationId") != null) b.setOrganizationId(UUID.fromString(body.get("organizationId")));
        b.setType("text");
        b.setStatus("active");
        return ResponseEntity.ok(broadcastRepository.save(b));
    }

    @PostMapping("/voice")
    public ResponseEntity<Broadcast> voiceBroadcast(
            @RequestParam("organizationId") String orgId,
            @RequestParam("audio") MultipartFile file) {
        
        Broadcast b = new Broadcast();
        if (orgId != null) b.setOrganizationId(UUID.fromString(orgId));
        b.setType("voice");
        b.setAudioUrl("/uploads/audio/dummy.webm"); // Simplified for now
        b.setStatus("active");
        return ResponseEntity.ok(broadcastRepository.save(b));
    }

    @PostMapping("/emergency")
    public ResponseEntity<Map<String, Object>> triggerEmergency(@RequestBody Map<String, String> body) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("id", UUID.randomUUID().toString());
        resp.put("status", "triggered");
        resp.put("notifiedCount", 142);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/stop")
    public ResponseEntity<Void> stopBroadcast(@RequestBody Map<String, String> body) {
        String id = body.get("broadcastId");
        if (id != null) {
            broadcastRepository.findById(UUID.fromString(id)).ifPresent(b -> {
                b.setStatus("stopped");
                broadcastRepository.save(b);
            });
        }
        return ResponseEntity.ok().build();
    }
}
