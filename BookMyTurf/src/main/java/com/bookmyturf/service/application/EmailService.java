package com.bookmyturf.service.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;


@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void sendOtp(String to, String otp) {


        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("🔐 BookMyTurf - Verify Your Email");

        String body = """
                Hello,

                Thank you for signing up with BookMyTurf.

                Your One-Time Password (OTP) is: %s

                This OTP is valid for 5 minutes. Please do not share it with anyone.

                Regards,
                BookMyTurf Team
                """.formatted(otp);

        message.setText(body);
        mailSender.send(message);
    }
}

