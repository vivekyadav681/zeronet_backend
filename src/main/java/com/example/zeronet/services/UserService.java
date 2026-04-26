package com.example.zeronet.services;

import com.example.zeronet.dtos.UserDto;
import com.example.zeronet.entities.User;
import com.example.zeronet.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto getUserProfile(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        return UserDto.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .phone(user.getPhone())
            .organizationId(user.getOrganizationId())
            .role(user.getRole())
            .verified(user.isVerified())
            .build();
    }
}
