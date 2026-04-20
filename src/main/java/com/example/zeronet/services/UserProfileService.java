package com.example.zeronet.services;

import com.example.zeronet.dtos.UserProfileDto;
import com.example.zeronet.entities.User;
import com.example.zeronet.entities.UserProfile;
import com.example.zeronet.repositories.UserProfileRepository;
import com.example.zeronet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository, UserRepository userRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    public UserProfileDto getProfile(Long userId) {
        Optional<UserProfile> maybeProfile = userProfileRepository.findByUserId(userId);
        if (maybeProfile.isEmpty()) {
            return null; // Or throw exception
        }
        return mapToDto(maybeProfile.get());
    }

    public UserProfileDto updateProfile(Long userId, UserProfileDto dto) {
        Optional<User> maybeUser = userRepository.findById(userId);
        if (maybeUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = maybeUser.get();
        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(new UserProfile());
        
        if (profile.getId() == null) {
            profile.setUser(user);
        }

        profile.setName(dto.getName());
        profile.setPhoneNumber(dto.getPhoneNumber());
        profile.setHometown(dto.getHometown());
        profile.setDateOfBirth(dto.getDateOfBirth());
        profile.setEmergencyContact1Name(dto.getEmergencyContact1Name());
        profile.setEmergencyContact1Phone(dto.getEmergencyContact1Phone());
        profile.setEmergencyContact2Name(dto.getEmergencyContact2Name());
        profile.setEmergencyContact2Phone(dto.getEmergencyContact2Phone());
        profile.setEmergencyContact3Name(dto.getEmergencyContact3Name());
        profile.setEmergencyContact3Phone(dto.getEmergencyContact3Phone());

        userProfileRepository.save(profile);
        return mapToDto(profile);
    }

    private UserProfileDto mapToDto(UserProfile profile) {
        UserProfileDto dto = new UserProfileDto();
        dto.setName(profile.getName());
        dto.setPhoneNumber(profile.getPhoneNumber());
        dto.setHometown(profile.getHometown());
        dto.setDateOfBirth(profile.getDateOfBirth());
        dto.setEmergencyContact1Name(profile.getEmergencyContact1Name());
        dto.setEmergencyContact1Phone(profile.getEmergencyContact1Phone());
        dto.setEmergencyContact2Name(profile.getEmergencyContact2Name());
        dto.setEmergencyContact2Phone(profile.getEmergencyContact2Phone());
        dto.setEmergencyContact3Name(profile.getEmergencyContact3Name());
        dto.setEmergencyContact3Phone(profile.getEmergencyContact3Phone());
        return dto;
    }
}
