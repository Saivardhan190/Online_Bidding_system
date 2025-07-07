package com.application.example.online_bidding_system.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;



@Service
public class Emailservice {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Set custom "From" name (visible to user)
            helper.setFrom(new InternetAddress("no-reply@gmail.com", "College Bidding System"));

            helper.setTo(toEmail);
            helper.setSubject("OTP Verification - College Bidding");
            helper.setText("Your OTP is: " + otp + "\n\nThis OTP is valid for 5 minutes.");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        } catch (Exception e) {
            throw new RuntimeException("Error setting sender name", e);
        }
    }
}
