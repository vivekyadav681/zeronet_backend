package com.example.zeronet.controllers;

import com.example.zeronet.dtos.UserDto;
import com.example.zeronet.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }
}
