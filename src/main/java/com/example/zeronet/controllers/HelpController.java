package com.example.zeronet.controllers;

import com.example.zeronet.dtos.AcceptHelpRequest;
import com.example.zeronet.dtos.HelpDetailsResponse;
import com.example.zeronet.dtos.SendHelpRequest;
import com.example.zeronet.services.HelpService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/help")
@RequiredArgsConstructor
public class HelpController {

    private final HelpService helpService;

    @PostMapping("/accept")
    public ResponseEntity<HelpDetailsResponse> acceptHelp(@Valid @RequestBody AcceptHelpRequest request) {
        try {
            HelpDetailsResponse response = helpService.acceptHelp(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendHelp(@Valid @RequestBody SendHelpRequest request) {
        try {
            helpService.sendHelp(request);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
