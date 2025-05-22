package com.example.studentabsencetracker.controller;

import com.example.studentabsencetracker.model.dto.request.LoginRequest;
import com.example.studentabsencetracker.model.dto.request.SignupRequest;
import com.example.studentabsencetracker.model.dto.response.JwtResponse;
import com.example.studentabsencetracker.model.dto.response.UserResponse;
import com.example.studentabsencetracker.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> registerUser(@RequestBody SignupRequest signupRequest) {
        UserResponse userResponse = authService.registerUser(signupRequest);
        return ResponseEntity.ok(userResponse);
    }



}

