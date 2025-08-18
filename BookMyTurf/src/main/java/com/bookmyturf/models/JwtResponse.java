package com.bookmyturf.models;

import com.bookmyturf.entity.User;

public record JwtResponse(String token, User user) {
}
