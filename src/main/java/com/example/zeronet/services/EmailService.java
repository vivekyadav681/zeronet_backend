package com.example.zeronet.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    @Nullable
    private JavaMailSender mailSender;

    public void sendOtp(String to, String otp) {
        if (mailSender != null) {
            try {
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setTo(to);
                msg.setSubject("Your authentication OTP");
                msg.setText("Your OTP is: " + otp + " (valid for 5 minutes)");
                mailSender.send(msg);
                log.info("Sent OTP email to {}", to);
                return;
            } catch (Exception ex) {
                log.warn("Failed to send real email, falling back to logger: {}", ex.getMessage());
            }
        }

        // fallback - log the OTP so developer can see it
        log.info("[OTP] to={} otp={}", to, otp);
    }
}
