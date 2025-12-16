package com.bookmyturf.service.implementation;

import com.bookmyturf.constraints.Roles;
import com.bookmyturf.entity.User;
import com.bookmyturf.jparepository.UserJpaRepository;
import com.bookmyturf.models.JwtResponse;
import com.bookmyturf.models.LoginRequestModel;
import com.bookmyturf.models.RegisterRequestModel;
import com.bookmyturf.security.JwtUtil;
import com.bookmyturf.service.AuthService;
import com.bookmyturf.service.application.EmailService;
import com.bookmyturf.service.application.OtpCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserJpaRepository userRepo;
    @Autowired
    private OtpCache otpCache;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    public String sendOtp(String email) {
        String otp = String.valueOf((int) (Math.random() * 9000) + 1000);

        otpCache.storeOtp(email, otp);
        emailService.sendOtp(email, otp);

        return "OTP sent to " + email;
    }

    @Override
    public User register(RegisterRequestModel request) {
        boolean isValid = otpCache.verifyOtp(request.getEmail(), request.getOtp());
        if (!isValid) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setContact(request.getContact());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        // Active status logic
        if (request.getRole() == Roles.USER) {
            user.setActive(true);  // normal user is active immediately
        } else if (request.getRole() == Roles.ADMIN) {
            user.setActive(false); // admin needs approval
        }

        userRepo.save(user);
        otpCache.removeOtp(request.getEmail());

        return user;
    }


    @Override
    public JwtResponse login(LoginRequestModel request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String pass = passwordEncoder.encode("Admin@123");
        if (!user.isActive()) {
            throw new RuntimeException("User is not active. Please contact SUPER_ADMIN for approval.");
        }

        if (!passwordEncoder.matches(request.getPassword(), pass)) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());
        user.setPassword("");
        return new JwtResponse(token,user);
    }

}
