package com.example.studentabsencetracker.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {

    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // For simplicity, assuming username is the user ID as string
        // In a real application, you would need to load the user and check the ID
        return authentication.getName().equals(userId.toString());
    }
}