package com.example.zeronet.controllers;

import com.example.zeronet.dtos.UserProfileDto;
import com.example.zeronet.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getProfile(@PathVariable Long userId) {
        UserProfileDto profile = userProfileService.getProfile(userId);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileDto> updateProfile(
            @PathVariable Long userId,
            @RequestBody UserProfileDto dto) {
        try {
            UserProfileDto updated = userProfileService.updateProfile(userId, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
