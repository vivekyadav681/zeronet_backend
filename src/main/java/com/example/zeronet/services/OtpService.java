package com.example.zeronet.services;

import com.example.zeronet.entities.User;
import com.example.zeronet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final Random random = new Random();

    public String generateOtp() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void sendOtpTo(String email) {
        Optional<User> maybe = userRepository.findByEmail(email.toLowerCase().trim());
        User user = maybe.orElseGet(() -> {
            User u = new User();
            u.setEmail(email.toLowerCase().trim());
            return u;
        });

        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        emailService.sendOtp(email, otp);
    }
}
