package com.bookmyturf.service.application;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OtpCache {
    private final ConcurrentHashMap<String, OtpData> otpMap = new ConcurrentHashMap<>();

    public void storeOtp(String email, String otp) {
        otpMap.put(email, new OtpData(otp, LocalDateTime.now().plusMinutes(5)));
    }

    public boolean verifyOtp(String email, String otp) {
        var data = otpMap.get(email);
        return data != null
                && data.expiry().isAfter(LocalDateTime.now())
                && data.code().equals(otp);
    }

    public void removeOtp(String email) {
        otpMap.remove(email);
    }

    private record OtpData(String code, LocalDateTime expiry) {}
}

