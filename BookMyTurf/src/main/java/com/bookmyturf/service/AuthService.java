package com.bookmyturf.service;

import com.bookmyturf.entity.User;
import com.bookmyturf.models.JwtResponse;
import com.bookmyturf.models.LoginRequestModel;
import com.bookmyturf.models.RegisterRequestModel;

public interface AuthService {
    String sendOtp(String email);
    User register(RegisterRequestModel registerRequestModel);
    JwtResponse login(LoginRequestModel loginRequestModel);
}
