package com.example.studentabsencetracker.service;

import com.example.studentabsencetracker.model.dto.request.LoginRequest;
import com.example.studentabsencetracker.model.dto.request.SignupRequest;
import com.example.studentabsencetracker.model.dto.response.JwtResponse;
import com.example.studentabsencetracker.model.dto.response.UserResponse;
import com.example.studentabsencetracker.model.entity.User;
import com.example.studentabsencetracker.repository.UserRepository;
import com.example.studentabsencetracker.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    // This is a placeholder for a JWT utility that would generate tokens
    // In a real implementation, you would integrate with JwtUtils
    @Autowired
    private JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new JwtResponse(
                jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().toString()
        );
    }

    public UserResponse registerUser(SignupRequest signupRequest) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        // Create new user's account
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setRole(signupRequest.getRole());
        user.setActive(true);

        userRepository.save(user);

        return new UserResponse(user);
    }
}