package com.example.zeronet.controllers;

import com.example.zeronet.entities.Responder;
import com.example.zeronet.repositories.ResponderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/v1/responders")
@RequiredArgsConstructor
public class ResponderController {
    private final ResponderRepository responderRepository;

    @GetMapping
    public ResponseEntity<Map<String, List<Responder>>> getResponders(
            @RequestParam(required = false) UUID organizationId,
            @RequestParam(required = false) String status) {
        
        List<Responder> responders;
        if (organizationId != null && status != null) {
            responders = responderRepository.findByOrganizationIdAndStatus(organizationId, status);
        } else if (organizationId != null) {
            responders = responderRepository.findByOrganizationId(organizationId);
        } else {
            responders = responderRepository.findAll();
        }
        
        Map<String, List<Responder>> resp = new HashMap<>();
        resp.put("responders", responders);
        return ResponseEntity.ok(resp);
    }
}
